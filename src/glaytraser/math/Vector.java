package glaytraser.math;

/**
 * 
 * @author devfloater3
 *
 */
public class Vector extends AbstractTriple {
    public Vector() {
    }
    
    public Vector(Point p1, Point p2) {
        set(p2.value[0] - p1.value[0],
            p2.value[1] - p1.value[1],
            p2.value[2] - p1.value[2]);
    }

    public void add(Vector v) {
        for(int i = 0; i < 4; ++i) {
            value[i] += v.value[i];
        }
    }
}
