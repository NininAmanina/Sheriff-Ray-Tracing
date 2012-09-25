package glaytraser.math;

/**
 * This class is the superclass of all triples which we wish to use:  Points, Vectors, and Rows in a Matrix
 */
public abstract class AbstractTriple {
    // The data for the triple
    /*package*/ final double [] value = new double [4];

    // Scratch space for multiplying matrices
    /*package*/ final double [] scratch = new double [4];

    /**
     * Declare a null AbstractTriple.
     */
    public AbstractTriple() {
    }

    /**
     * Define a triple of some sort via x, y, and z.
     * @param x
     * @param y
     * @param z
     */
    public AbstractTriple(final double x, final double y, final double z) {
        set(x, y, z);
    }

    /**
     * Multiply this AbstractTriple by a Matrix.
     *
     * By default, it is premultiplied.  This behaviour may be overridden if necessary.
     * @param m The matrix.
     */
    public void multiply(final Matrix m) {
        // Copy our current values into the scratch space for the multiplication
        for(int i = 0; i < 4; ++i) {
            scratch[i] = m.getRow(i).dot(this);
        }
        System.arraycopy(scratch, 0, value, 0, 4);
    }

    /**
     * Set the ith component of this AbstractTriple.
     *
     * @param i The index ([0..2])
     * @param d The value
     * @return A reference to this vector, for chaining purposes
     */
    public AbstractTriple set(final int i, final double d) {
        if(i < 0 || i > 2) {
            throw new IllegalArgumentException("index " + i + "is out of bounds");
        }
        value[i] = d;
        return this;
    }

    // Note that this is not type-safe -- we can initialise a Point with the XYZ values of
    // a vector, et al.
    public AbstractTriple set(final AbstractTriple t) {
        return set(t.value[0], t.value[1], t.value[2]);
    }

    public double get(final int index) {
        return value[index];
    }

    public AbstractTriple set(final double d0, final double d1, final double d2) {
        value[0] = d0;
        value[1] = d1;
        value[2] = d2;
        return this;
    }

    public AbstractTriple subtract(final Vector v) {
        for(int i = 0; i < 3; ++i) {
            value[i] -= v.value[i];
        }
        return this;
    }
    
    public AbstractTriple add(final Vector v) {
        for(int i = 0; i < 3; ++i) {
            value[i] += v.value[i];
        }
        return this;
    }

    public void clear() {
        for(int i = 0; i < 3; ++i) {
            value[i] = 0.0;
        }
    }

    public String toString() {
        return new StringBuilder(getClass().toString())
                                .append(' ')
                                .append('[')
                                .append(value[0])
                                .append(' ')
                                .append(value[1])
                                .append(' ')
                                .append(value[2])
                                .append(']')
                                .toString();
    }
}
