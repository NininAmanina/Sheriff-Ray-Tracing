package glaytraser.math;

import java.util.ArrayList;

public final class Row extends Vector {
    private static final ArrayList<Row> m_scratchRowList = new ArrayList<Row>();

    public static final Row getRow() {
        synchronized(m_scratchRowList) {
            final int size = m_scratchRowList.size();
            if(size > 0) {
                return m_scratchRowList.remove(size - 1).clear();
            } else {
                return new Row();
            }
        }
    }

    public static final void putRow(final Row row) {
        synchronized(m_scratchRowList) {
            m_scratchRowList.add(row);
        }
    }

    public Row() {
    }

    Row(final double d1, final double d2, final double d3, final double d4) {
        set(d1, d2, d3, d4);
    }

    public Row set(final int i, final double d) {
        if(i < 0 || i > 3) {
            throw new IllegalArgumentException("index " + i + "is out of bounds");
        }
        value[i] = d;
        return this;
    }

    public void set(final double d0, final double d1, final double d2, final double d3) {
        value[0] = d0;
        value[1] = d1;
        value[2] = d2;
        value[3] = d3;
    }
}
