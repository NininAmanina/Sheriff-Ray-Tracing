package glaytraser.math;

/**
 * 
 * @author devfloater3
 *
 * Defines a Point, as used in graphics packages.
 */
public class Point extends AbstractTriple {
    public Point() {
        value[3] = 1.0;
    }

    public void add(Vector v) {
        for(int i = 0; i < 4; ++i) {
            value[i] += v.value[i];
        }
    }
}