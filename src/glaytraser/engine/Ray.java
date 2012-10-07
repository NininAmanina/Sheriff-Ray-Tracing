package glaytraser.engine;

import java.util.ArrayList;

import glaytraser.math.*;

public final class Ray {
    private static final ArrayList<Ray> m_scratchRayList = new ArrayList<Ray>();

    public static final Ray getRay() {
        synchronized(m_scratchRayList) {
            final int size = m_scratchRayList.size();
            if(size > 0) {
                return m_scratchRayList.remove(size - 1).init();
            } else {
                return new Ray();
            }
        }
    }

    public static final void putRay(final Ray ray) {
        synchronized(m_scratchRayList) {
            m_scratchRayList.add(ray);
        }
    }

    // Should be a unit vector when the ray is cast
    private Vector m_vector = new Vector(); 
    private Point m_point = new Point();

    public Vector getVector() {
        return m_vector;
    }

    public Point getPoint() {
        return m_point;
    }

    public Ray init() {
        m_point.clear();
        m_vector.clear();
        return this;
    }

    public Ray init(final Ray ray) {
        m_point.set(ray.getPoint());
        m_vector.set(ray.getVector());
        return this;
    }

    public void transform(final Matrix txMatrix) {
        m_point.multiply(txMatrix);
        m_vector.multiply(txMatrix);
    }
}
