package glaytraser.math;

/**
 * 
 * @author G & S
 *
 * Defines a Point, as used in graphics packages.
 */
public class Point extends AbstractTriple<Point> {
    public Point() {
        value[3] = 1.0;
    }

    public Point(double d0, double d1, double d2) {
        this();
        set(d0, d1, d2);
    }

    public Point(Point p) {
        this(p.value[0], p.value[1], p.value[2]);
    }
}