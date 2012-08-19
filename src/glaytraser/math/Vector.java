package glaytraser.math;

/**
 * 
 * @author G & S
 *
 */
public class Vector extends AbstractTriple {
    public Vector() {
    }

    public Vector(double d0, double d1, double d2) {
        set(d0, d1, d2);
    }

    public Vector(Point p1, Point p2) {
        set(p1, p2);
    }

    public void set(Point p1, Point p2) {
        set(p2.value[0] - p1.value[0],
            p2.value[1] - p1.value[1],
            p2.value[2] - p1.value[2]);
    }

    public void add(Vector v) {
        for(int i = 0; i < 3; ++i) {
            value[i] += v.value[i];
        }
    }
}
