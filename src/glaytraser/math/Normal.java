package glaytraser.math;

/**
 * 
 * @author G & S
 */
public class Normal extends Vector {
    /**
     * Postmultiply this Normal by a Matrix.
     * @param m The matrix.
     */
    public void multiply(final Matrix m) {
        // Copy our current values into the scratch space for the multiplication
        for(int i = 0; i < 3; ++i) {
            scratch[i] = m.getColumn(i).dot(this);
        }
        System.arraycopy(scratch, 0, value, 0, 3);
    }
}
