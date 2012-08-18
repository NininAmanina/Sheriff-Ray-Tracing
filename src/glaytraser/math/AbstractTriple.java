package glaytraser.math;

/**
 * This class is the superclass of all triples which we wish to use:  Points, Vectors, and Rows in a Matrix
 */
public abstract class AbstractTriple {
    // The data for the triple
    /*package*/ final double [] value = new double [4];

    // Scratch space for multiplying matrices
    private final double [] scratch = new double [4];

    public void premultiply(Matrix m) {
        // Copy our current values into the scratch space for the multiplication
        for(int i = 0; i < 3; ++i) {
            scratch[i] = dot(m.getRow(i));
        }
        System.arraycopy(scratch, 0, value, 0, 4);
    }

    public void set(int i, double d) {
        if(i < 0 || i > 2) {
            throw new IllegalArgumentException("index " + i + "is out of bounds");
        }
        value[i] = d;
    }

    public void set(double d0, double d1, double d2) {
        value[0] = d0;
        value[1] = d1;
        value[2] = d2;
    }

    public double dot(AbstractTriple t) {
        return t.value[0] * value[0] +
               t.value[1] * value[1] +
               t.value[2] * value[2] +
               t.value[3] * value[3]; 
    }
}
