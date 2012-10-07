package glaytraser.math;

import java.util.ArrayList;

/**
 * 
 * @author G & S
 *
 * Defines a Point, as used in graphics packages.
 */
public class Point extends AbstractTriple<Point> {
    private static final ArrayList<Point> m_scratchPointList = new ArrayList<Point>();

    public static final Point getPoint() {
        synchronized(m_scratchPointList) {
            final int size = m_scratchPointList.size();
            if(size > 0) {
                return m_scratchPointList.remove(size - 1).set(0, 0, 0);
            } else {
                return new Point();
            }
        }
    }

    public static final void putPoint(final Point p) {
        synchronized(m_scratchPointList) {
            m_scratchPointList.add(p);
        }
    }

    public Point() {
        value[3] = 1.0;
    }

    public Point(double d0, double d1, double d2) {
        this();
        set(d0, d1, d2);
    }

    public Point(Point p) {
        this(p.value[0], p.value[1], p.value[2]);
    }
}