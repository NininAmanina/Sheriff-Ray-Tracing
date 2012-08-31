package glaytraser.math;

public class Pair extends AbstractTriple {
    /**
     * Set the ith component of this AbstractTriple.
     *
     * @param i The index ([0..1])
     * @param d The value
     * @return A reference to this vector, for chaining purposes
     */
    public AbstractTriple set(int i, double d) {
        if(i < 0 || i > 1) {
            throw new IllegalArgumentException("index " + i + "is out of bounds");
        }
        value[i] = d;
        return this;
    }
}
