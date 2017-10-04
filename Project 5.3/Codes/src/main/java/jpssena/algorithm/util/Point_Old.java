package jpssena.algorithm.util;

/**
 * Created by João Paulo on 30/08/2017.
 */
public class Point_Old {
    public double x;
    public double y;

    public Point_Old(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return "[" + x + ":" + y + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Point_Old) {
            Point_Old p = (Point_Old) o;
            return nearlyEquals(p.getX(), getX(), 0.000000000001) && nearlyEquals(p.getY(), getY(), 0.000000000001);
        }
        return false;
    }

    private boolean nearlyEquals(double a, double b, double precision) {
        return (a - b) < precision;
    }
}
