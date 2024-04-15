public class BoundingBox {
    double xmin = Double.NaN;
    double ymin = Double.NaN;
    double xmax = Double.NaN;
    double ymax = Double.NaN;

    /**
     * Powiększa BB tak, aby zawierał punkt (x,y)
     * Jeżeli był wcześniej pusty - wówczas ma zawierać wyłącznie ten punkt
     * @param x - współrzędna x
     * @param y - współrzędna y
     */
    void addPoint(double x, double y) {
        if(isEmpty()) {
            xmin = x; xmax = x;
            ymin = y; ymax = y;
        }
        else {
            xmin = Math.min(xmin, x);
            ymin = Math.min(ymin, y);
            xmax = Math.max(xmax, x);
            ymax = Math.max(ymax, y);
        }
    }

    /**
     * Sprawdza, czy BB zawiera punkt (x,y)
     * @param x
     * @param y
     * @return
     */
    boolean contains(double x, double y) {
        if(isEmpty()) return false;
        return (x >= xmin && x <= xmax && y >= ymin && y <= ymax);
    }

    /**
     * Sprawdza czy dany BB zawiera bb
     * @param bb
     * @return
     */
    boolean contains(BoundingBox bb) {
        if(isEmpty()) return false;
        return (xmin <= bb.xmin && xmax >= bb.xmax && ymin <= bb.ymin && ymax >= bb.ymax);
    }

    /**
     * Sprawdza, czy dany BB przecina się z bb
     * @param bb
     * @return
     */
    boolean intersects(BoundingBox bb) {
        if(isEmpty() || bb.isEmpty()) return false;
        return !(bb.xmin > xmax || bb.xmax < xmin || bb.ymin > ymax || bb.ymax < ymin);
    }
    /**
     * Powiększa rozmiary tak, aby zawierał bb oraz poprzednią wersję this
     * Jeżeli był pusty - po wykonaniu operacji ma być równy bb
     * @param bb
     * @return
     */
    BoundingBox add(BoundingBox bb) {
        if (bb.isEmpty()) {
            return this;
        } else if (isEmpty()) {
            xmin = bb.xmin;
            ymin = bb.ymin;
            xmax = bb.xmax;
            ymax = bb.ymax;
        } else {
            xmin = Math.min(xmin, bb.xmin);
            ymin = Math.min(ymin, bb.ymin);
            xmax = Math.max(xmax, bb.xmax);
            ymax = Math.max(ymax, bb.ymax);
        }
        return this;
    }
    /**
     * Sprawdza czy BB jest pusty
     * @return
     */
    boolean isEmpty() {
        return Double.isNaN(xmin) && Double.isNaN(ymin) && Double.isNaN(xmax) && Double.isNaN(ymax);
    }

    /**
     * Sprawdza czy
     * 1) typem o jest BoundingBox
     * 2) this jest równy bb
     * @return
     */
    public boolean equals(Object o) {
        if(o == this) return true;
        if(o == null || getClass() != o.getClass()) return false;

        BoundingBox res = (BoundingBox) o;
        return res.xmin == xmin && res.xmax == xmax && res.ymin == ymin && res.ymax == ymax;
    }

    /**
     * Oblicza i zwraca współrzędną x środka
     * @return if !isEmpty() współrzędna x środka else wyrzuca wyjątek
     * (sam dobierz typ)
     */
    double getCenterX() throws IllegalAccessException {
        if(isEmpty()) throw new IllegalAccessException("Bounding Box is empty");
        return (xmin + xmax) / 2;
    }
    /**
     * Oblicza i zwraca współrzędną y środka
     * @return if !isEmpty() współrzędna y środka else wyrzuca wyjątek
     * (sam dobierz typ)
     */
    double getCenterY() throws IllegalAccessException {
        if(isEmpty()) throw new IllegalAccessException("Bounding Box is empty");
        return (ymin + ymax) / 2;
    }

    /**
     * Oblicza odległość pomiędzy środkami this bounding box oraz bbx
     * @param bbx prostokąt, do którego liczona jest odległość
     * @return if !isEmpty odległość, else wyrzuca wyjątek lub zwraca maksymalną możliwą wartość double
     * Ze względu na to, że są to współrzędne geograficzne, zamiast odległości użyj wzoru haversine
     * (ang. haversine formula)
     *
     * Gotowy kod można znaleźć w Internecie...
     */
    double distanceTo(BoundingBox bbx) throws IllegalAccessException {
        if(isEmpty() || bbx.isEmpty()) {
            throw new IllegalStateException("BoundingBox is empty");
        }
        double dLat = Math.toRadians(bbx.getCenterY() - getCenterY());
        double dLon = Math.toRadians(bbx.getCenterX() - getCenterX());

        double centerY1 = Math.toRadians(getCenterY());
        double centerY2 = Math.toRadians(bbx.getCenterY());

        double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.pow(Math.sin(dLon / 2), 2) *
                        Math.cos(centerY1) *
                        Math.cos(centerY2);
        double rad = 6371;
        double c = 2 * Math.asin(Math.sqrt(a));
        return rad * c;
    }

    public String toString() {
        return " " + xmin + " " + ymin + " " + xmax + " " + ymax;
    }

}