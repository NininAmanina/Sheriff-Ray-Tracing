package glaytraser.engine;

import glaytraser.math.*;

public class Ray {
	// Should be a unit vector when the ray is cast
    private Vector m_vector = new Vector(); 
    private Point m_point = new Point();

    public Vector getVector() {
        return m_vector;
    }

    public Point getPoint() {
        return m_point;
    }

    public void init(final Ray ray) {
        m_point.set(ray.getPoint());
        m_vector.set(ray.getVector());
    }

    public void transform(final Matrix txMatrix) {
        m_point.multiply(txMatrix);
        m_vector.multiply(txMatrix);
    }
}
