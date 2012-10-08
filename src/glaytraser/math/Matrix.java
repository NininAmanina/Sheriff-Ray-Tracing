package glaytraser.math;

import java.util.ArrayList;

/**
 * 
 * @author G & S
 *
 * This class represents a matrix for premultiplying vectors and points and post-multiplying Normals.
 */
public final class Matrix {
    private final Row [] m_row = new Row [4];
    private final Column [] m_column = new Column [4];
    private static final ArrayList<Matrix> m_scratchMatrixList = new ArrayList<Matrix>();

    public static final Matrix getMatrix() {
        synchronized(m_scratchMatrixList) {
            final int size = m_scratchMatrixList.size();
            if(size > 0) {
                return m_scratchMatrixList.remove(size - 1).identity();
            } else {
                return new Matrix();
            }
        }
    }

    public static final void putMatrix(final Matrix matrix) {
        synchronized(m_scratchMatrixList) {
            m_scratchMatrixList.add(matrix);
        }
    }

    public Matrix() {
        m_row[0] = new Row(); 
        m_row[1] = new Row();
        m_row[2] = new Row();
        m_row[3] = new Row();
        m_column[0] = new Column(); 
        m_column[1] = new Column();
        m_column[2] = new Column();
        m_column[3] = new Column();

        identity();
    }

    /*package*/ Row getRow(final int i) {
        return m_row[i];
    }

    public Column getColumn(final int i) {
        return m_column[i];
    }

    public double get(final int row, final int column) {
        return m_row[row].get(column);
    }

    public void set(final int row, final int column, final double value) {
        m_row[row].set(column, value);
        m_column[column].set(row, value);
    }

    public Matrix identity() {
        m_row[0].set(1, 0, 0, 0);
        m_row[1].set(0, 1, 0, 0);
        m_row[2].set(0, 0, 1, 0);
        m_row[3].set(0, 0, 0, 1);
        m_column[0].set(1, 0, 0, 0);
        m_column[1].set(0, 1, 0, 0);
        m_column[2].set(0, 0, 1, 0);
        m_column[3].set(0, 0, 0, 1);
        return this;
    }

    public void postMultiply(final Matrix m) {
        final Row scratchRow = Row.getRow();
        try {
            for(int r = 0; r < 4; ++r) {
                final Row row = m_row[r];
                for(int c = 0; c < 4; ++c) {
                    scratchRow.set(c, row.dot(m.getColumn(c)));
                }
                row.set(scratchRow);
            }
            copyRowsIntoColumns();
        } finally {
            Row.putRow(scratchRow);
        }
    }

    public void preMultiply(final Matrix m) {
        final Row scratchRow = Row.getRow();
        try {
            for(int r = 0; r < 4; ++r) {
                final Row row = m.getRow(r);
                for(int c = 0; c < 4; ++c) {
                    scratchRow.set(c, row.dot(m_column[c]));
                }
                m_row[r].set(scratchRow);
            }
            copyRowsIntoColumns();
        } finally {
            Row.putRow(scratchRow);
        }
    }

    private void copyRowsIntoColumns() {
        for(int c = 0; c < 4; ++c) {
            m_column[c].set(m_row[0].get(c), m_row[1].get(c), m_row[2].get(c), m_row[3].get(c));
        }
    }
    
    public String toString() {
        return new StringBuilder(getClass().toString()).append(m_row[0]).append(m_row[1]).append(m_row[2]).append(m_row[3]).toString();
    }
}
