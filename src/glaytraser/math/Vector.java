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
        super(d0, d1, d2);
    }

    public Vector(Point p1, Point p2) {
        set(p1, p2);
    }

    public Vector(Vector v) {
        super(v.value[0], v.value[1], v.value[2]);
    }
    
    public void set(Vector v) {
        for(int i=0; i<3; i++) {
            value[i] = v.value[i];
        }
    }
    
    public void set(Point p1, Point p2) {
        set(p2.value[0] - p1.value[0],
            p2.value[1] - p1.value[1],
            p2.value[2] - p1.value[2]);
    }
    
    public Vector crossProduct(Vector v1) {
        return new Vector(value[1] * v1.value[2] - value[2] * v1.value[1], 
                          value[2] * v1.value[0] - value[0] * v1.value[2],
                          value[0] * v1.value[1] - value[1] * v1.value[0]);
    }
    
    public void multiply(double scalar) {
        for(int i=0; i<3; i++) {
            value[i] *= scalar;
        }
    }
    
    // TODO: Make this more numerically stable later
    public double normalize() {
        double sum = 0;
        for(int i=0; i<3; i++) {
            sum += value[i]*value[i];
        }
        
        double length = Math.sqrt(sum);
        if (length > 0) {
            for(int i=0; i<3; i++) {
                value[i] /= length;
            }
        }
        
        return length;
    }
}
