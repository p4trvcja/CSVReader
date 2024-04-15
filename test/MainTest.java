import org.junit.Assert;
import org.junit.Test;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Locale;

import static org.junit.Assert.*;

public class MainTest {

    @Test
    public void testNeighbors() throws Exception {
        AdminUnitList adminUnits = new AdminUnitList();
        adminUnits.read("admin-units.csv");

        AdminUnit chosenUnit = adminUnits.units.get(9645);
        System.out.println("chosen unit: " + chosenUnit.name);
        double maxDistance = 15.0;
        double t1 = System.nanoTime() / 1e6;
        AdminUnitList neighbors = adminUnits.getNeighbors(chosenUnit, maxDistance);
        neighbors.list(System.out);
        double t2 = System.nanoTime() / 1e6;
        System.out.printf(Locale.US, "Potrzebny czas na znalezienie sąsiadów: %f milisekund/y\n", t2 - t1);
    }

    @Test
    public void testWithHeader() throws Exception {
        CSVReader reader = new CSVReader(new FileReader("with-header.csv", Charset.forName("Cp1250")), ";", true);
        while(reader.next()) {
            int id = reader.getInt("id");
            String name = reader.get("imię");
            String surname = reader.get("nazwisko");
            String street = reader.get("ulica");
            int numHouse = reader.getInt("nrdomu");
            int numApartment = reader.getInt("nrmieszkania");

            System.out.printf(Locale.US, "%d %s %s %s %d %d", id, name, surname, street, numHouse, numApartment);
            System.out.println();
        }
    }

    @Test
    public void testWithoutHeader() throws IOException {
        CSVReader reader = new CSVReader(new FileReader("no-header.csv", Charset.forName("Cp1250")), ";", false);

        while (reader.next()) {
            try {
                int id = reader.getInt(0);
                String name = reader.get(1);
                String surname = reader.get(2);
                String street = reader.get(3);
                int numHouse = reader.getInt(4);
                int numApartment = reader.getInt(5);

                System.out.printf(Locale.US, "%d %s %s %s %d %d", id, name, surname, street, numHouse, numApartment);
                System.out.println();
            } catch (Exception e) {
                System.out.println("empty values");
            }
        }
    }

    @Test
    public void testMissingValues() throws IOException {
        CSVReader reader = new CSVReader(new FileReader("missing-values.csv", Charset.forName("Cp1250")), ";",true);
        while (reader.next()) {
            for (int i = 0; i < reader.getRecordLength(); i++) {
                if (reader.isMissing(i)) {
                    System.out.print("EMPTY VALUE at column " + i + " ");
                } else {
                    System.out.print(reader.get(i) + " ");
                }
            }
            System.out.println();
        }
    }

    @Test
    public void testTitanicPart() throws Exception {
        CSVReader reader = new CSVReader("titanic-part.csv", ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)",  true);
        while (reader.next()) {
            for (int i = 0; i < reader.getRecordLength(); i++) {
                if (reader.isMissing(i)) {
                    System.out.print("EMPTY VALUE at column " + i + " ");
                } else {
                    System.out.print(reader.get(i) + " ");
                }
            }
            System.out.println();
        }
    }

    @Test
    public void testDifferentSource() throws Exception {
        String text = "a,b,c\n123.4,567.8,91011.12";
        CSVReader reader = new CSVReader(new StringReader(text),",",true);
        while(reader.next()) {
            double a = reader.getDouble("a");
            double b = reader.getDouble("b");
            double c = reader.getDouble("c");
            System.out.printf(Locale.US, "%f %f %f", a, b, c);
            Assert.assertEquals(123.4, a, 0.0001);
            Assert.assertEquals(567.8, b, 0.0001);
            Assert.assertEquals(91011.12, c, 0.0001);
        }
    }

    @Test
    public void testAdminUnitQuery() throws Exception {
        AdminUnitList list = new AdminUnitList();
        list.read("admin-units.csv");
        AdminUnitQuery query = new AdminUnitQuery()
                .selectFrom(list)
                .where(a -> a.area > 1000)
                .or(a -> a.name.startsWith("Sz"))
                .sort((a, b) -> Double.compare(a.area, b.area))
                .limit(100);
        query.execute().list(System.out);
        System.out.println();
    }

    @Test
    public void testQuery1() throws Exception {
        AdminUnitList list = new AdminUnitList();
        list.read("admin-units.csv");
        // 1 propozycja zapytania
        AdminUnitQuery query1 = new AdminUnitQuery()
                .selectFrom(list)
                .where(a -> a.population > 80000)
                .and(a -> a.name.startsWith("No"))
                .sort((a, b) -> Double.compare(a.area, b.area))
                .limit(100);
        query1.execute().list(System.out);
        System.out.println();
    }


    @Test
    public void testQuery2() throws Exception {
        AdminUnitList list = new AdminUnitList();
        list.read("admin-units.csv");
        // 2 propozycja zapytania
        AdminUnitQuery query2 = new AdminUnitQuery()
                .selectFrom(list)
                .where(a -> a.area < 10000)
                .and(a -> a.name.endsWith("awa"))
                .sort((a, b) -> Double.compare(a.area, b.area))
                .limit(100);
        query2.execute().list(System.out);
        System.out.println();
    }

    @Test
    public void testQuery3() throws Exception {
        AdminUnitList list = new AdminUnitList();
        list.read("admin-units.csv");
        // 3 propozycja zapytania
        AdminUnitQuery query3 = new AdminUnitQuery()
                .selectFrom(list)
                .where(a -> a.name.startsWith("A"))
                .sort((a, b) -> Double.compare(a.area, b.area))
                .limit(100);
        query3.execute().list(System.out);
    }

    @Test
    public void testFilter() throws Exception {
        AdminUnitList list = new AdminUnitList();
        list.read("admin-units.csv");
        list.filter(a->a.name.startsWith("Ż")).sortInplaceByArea().list(System.out);
    }
}