import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.function.Predicate;

public class AdminUnitList {
    List<AdminUnit> units = new ArrayList<>();
    public void read(String filename) throws Exception {
        Map<Long, AdminUnit> idToAdminUnit = new HashMap<>();
        Map<AdminUnit, Long> adminUnitToParentId = new HashMap<>();
        CSVReader csvReader = new CSVReader(filename, ",", true);

        while(csvReader.next()) {
            AdminUnit unit = new AdminUnit();

            unit.name = csvReader.get("name");
            long id = csvReader.getLong("id");
            long parent = 0;
            int adminLevel=0;
//          id,parent,name,admin_level,population,area,density,x1,y1,x2,y2,x3,y3,x4,y4,x5,y5

            double population=0, area=0, density=0, x1=0,
                    y1=0, x2=0, y2=0, x3=0, y3=0,
                    x4=0, y4=0, x5=0, y5 = 0;

            if(!csvReader.isMissing("parent")) parent = csvReader.getLong("parent");
            if(!csvReader.isMissing("admin_level")) adminLevel = csvReader.getInt("admin_level");
            if(!csvReader.isMissing("population")) population = csvReader.getDouble("population");
            if(!csvReader.isMissing("area")) area = csvReader.getDouble("area");
            if(!csvReader.isMissing("density")) density = csvReader.getDouble("density");

            if(!csvReader.isMissing("x1")) x1 = csvReader.getDouble("x1");
            if(!csvReader.isMissing("y1")) y1 = csvReader.getDouble("y1");
            if(!csvReader.isMissing("x2")) x2 = csvReader.getDouble("x2");
            if(!csvReader.isMissing("y2")) y2 = csvReader.getDouble("y2");
            if(!csvReader.isMissing("x3")) x3 = csvReader.getDouble("x3");
            if(!csvReader.isMissing("y3")) y3 = csvReader.getDouble("y3");
            if(!csvReader.isMissing("x4")) x4 = csvReader.getDouble("x4");
            if(!csvReader.isMissing("y4")) y4 = csvReader.getDouble("y4");
            if(!csvReader.isMissing("x5")) x5 = csvReader.getDouble("x5");
            if(!csvReader.isMissing("y5")) y5 = csvReader.getDouble("y5");

            BoundingBox bBox = new BoundingBox();
            bBox.addPoint(x1, y1);
            bBox.addPoint(x2, y2);
            bBox.addPoint(x3, y3);
            bBox.addPoint(x4, y4);
            bBox.addPoint(x5, y5);

            unit.setAdminLevel(adminLevel);
            unit.setArea(area);
            unit.setPopulation(population);
            unit.setDensity(density);
            unit.setBbox(bBox);

            units.add(unit);
            idToAdminUnit.put(id, unit);
            adminUnitToParentId.put(unit, parent);
        }

        for(AdminUnit unit : units) {
            long parentId = adminUnitToParentId.getOrDefault(unit, (long) Double.NaN);
            AdminUnit parent = idToAdminUnit.getOrDefault(parentId, null);
            unit.setParent(parent);
        }

        for (AdminUnit unit : units) {
            fixMissingValues(unit);
        }
    }

    /**
     * Wypisuje zawartość korzystając z AdminUnit.toString()
     * @param out
     */
    void list(PrintStream out) {
        for(AdminUnit unit : units) {
            out.println(unit.toString());
        }
    }
    /**
     * Wypisuje co najwyżej limit elementów począwszy od elementu o indeksie offset
     * @param out - strumień wyjsciowy
     * @param offset - od którego elementu rozpocząć wypisywanie
     * @param limit - ile (maksymalnie) elementów wypisać
     */
    void list(PrintStream out,int offset, int limit ) {
        for(int i = offset; i < Math.min(offset + limit, units.size()); i++) {
            out.println(units.get(i).toString());
        }
    }
    /**
     * Zwraca nową listę zawierającą te obiekty AdminUnit, których nazwa pasuje do wzorca
     * @param pattern - wzorzec dla nazwy
     * @param regex - jeśli regex=true, użyj finkcji String matches(); jeśli false użyj funkcji contains()
     * @return podzbiór elementów, których nazwy spełniają kryterium wyboru
     */
    AdminUnitList selectByName(String pattern, boolean regex) {
        AdminUnitList ret = new AdminUnitList();
        for(AdminUnit unit : units) {
            if(regex) {
                if(unit.name.matches(pattern)) ret.units.add(unit);
            } else {
                if(unit.name.contains(pattern)) ret.units.add(unit);
            }
        }
        return ret;
    }

    private void fixMissingValues(AdminUnit adminUnit) {
        if (adminUnit.parent != null && adminUnit.density == 0) {
            fixMissingValues(adminUnit.parent);
            adminUnit.setDensity(adminUnit.parent.density);
        }

        if (adminUnit.area != 0 && adminUnit.population == 0) {
            adminUnit.setPopulation(adminUnit.area * adminUnit.density);
        }
    }

    /**
     * Zwraca listę jednostek sąsiadujących z jendostką unit na tym samym poziomie hierarchii admin_level.
     * Czyli sąsiadami wojweództw są województwa, powiatów - powiaty, gmin - gminy, miejscowości - inne miejscowości
     * @param unit - jednostka, której sąsiedzi mają być wyznaczeni
     * @param maxdistance - parametr stosowany wyłącznie dla miejscowości, maksymalny promień odległości od środka unit,
     *                    w którym mają sie znaleźć punkty środkowe BoundingBox sąsiadów
     * @return lista wypełniona sąsiadami
     */
    AdminUnitList getNeighbors(AdminUnit unit, double maxdistance) throws IllegalAccessException {
        AdminUnitList neighbors = new AdminUnitList();
        int adminLevel_ = unit.adminLevel;

        if(adminLevel_ >= 8) {
            for(AdminUnit adminUnit : units) {
                if(adminUnit.adminLevel == adminLevel_ && !adminUnit.equals(unit) && unit.bbox.distanceTo(adminUnit.bbox) < maxdistance)
                    neighbors.units.add(adminUnit);
            }
        } else {
            for(AdminUnit adminUnit : units) {
                if(adminUnit.adminLevel == adminLevel_ && !adminUnit.equals(unit) && adminUnit.bbox.intersects(unit.bbox))
                    neighbors.units.add(adminUnit);
            }
        }

        return neighbors;
    }

    /**
     * Sortuje daną listę jednostek (in place = w miejscu)
     * @return this
     */
    public AdminUnitList sortInplaceByName() {
        class NameComparator implements Comparator<AdminUnit> {
            @Override
            public int compare(AdminUnit t, AdminUnit t1) {
                return String.CASE_INSENSITIVE_ORDER.compare(t.name, t1.name);
            }
        }
        NameComparator comparator = new NameComparator();
        units.sort(comparator);
        return this;
    }

    /**
     * Sortuje daną listę jednostek (in place = w miejscu)
     * @return this
     */
    AdminUnitList sortInplaceByArea() {
        units.sort(new Comparator<AdminUnit>() {
            @Override
            public int compare(AdminUnit o1, AdminUnit o2) {
                return Double.compare(o1.area, o2.area);
            }
        });
        return this;
    }
    /**
     * Sortuje daną listę jednostek (in place = w miejscu)
     * @return this
     */
    AdminUnitList sortInplaceByPopulation() {
        units.sort((o1, o2) -> Double.compare(o1.population, o2.population));
        return this;
    }

    AdminUnitList sortInplace(Comparator<AdminUnit> cmp) {
        units.sort(cmp);
        return this;
    }

    AdminUnitList sort(Comparator<AdminUnit> cmp) {
        AdminUnitList res = new AdminUnitList();
        res.units.addAll(this.units);
        res.sortInplace(cmp);
        return res;
    }

    /**
     *
     * @param pred referencja do interfejsu Predicate
     * @return nową listę, na której pozostawiono tylko te jednostki,
     * dla których metoda test() zwraca true
     */
    AdminUnitList filter(Predicate<AdminUnit> pred) {
        AdminUnitList res = new AdminUnitList();
        for (AdminUnit unit : units) {
            if (pred.test(unit)) {
                res.units.add(unit);
            }
        }
        return res;
    }

    /**
     * Zwraca co najwyżej limit elementów spełniających pred
     * @param pred - predykat
     * @param limit - maksymalna liczba elementów
     * @return nową listę
     */
    AdminUnitList filter(Predicate<AdminUnit> pred, int limit) {
        AdminUnitList res = new AdminUnitList();
        int count = 0;
        for(AdminUnit unit : units) {
            if(pred.test(unit)) {
                res.units.add(unit);
                count++;
            }
            if(count >= limit) break;
        }
        return res;
    }

    /**
     * Zwraca co najwyżej limit elementów spełniających pred począwszy od offset
     * Offest jest obliczany po przefiltrowaniu
     * @param pred - predykat
     * @param offset - od którego elementu
     * @param limit - maksymalna liczba elementów
     * @return nową listę
     */
    AdminUnitList filter(Predicate<AdminUnit> pred, int offset, int limit) {
        if(offset < 0 || limit < 0)
            throw new IllegalStateException("params offset or limit cannot be negative");
        AdminUnitList res = new AdminUnitList();
        int count = 0;
        for(AdminUnit unit : units) {
            if(pred.test(unit)) {
                if(count >= offset) {
                    res.units.add(unit);
                    count++;
                    if(count >= offset+limit) break;
                } else {
                    count++;
                }
            }
        }
        return res;
    }

}