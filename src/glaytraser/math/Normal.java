package glaytraser.math;

import java.util.ArrayList;

/**
 * 
 * @author G & S
 */
public class Normal extends Vector {
    private static final ArrayList<Normal> m_scratchNormalList = new ArrayList<Normal>();

    public static final Normal getNormal() {
        synchronized(m_scratchNormalList) {
            final int size = m_scratchNormalList.size();
            if(size > 0) {
                return m_scratchNormalList.remove(size - 1).set(0, 0, 0);
            } else {
                return new Normal();
            }
        }
    }

    public static final void putNormal(final Normal n) {
        synchronized(m_scratchNormalList) {
            m_scratchNormalList.add(n);
        }
    }

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
