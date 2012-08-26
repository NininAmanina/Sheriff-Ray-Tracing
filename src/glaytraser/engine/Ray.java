package glaytraser.engine;

import glaytraser.math.*;

public class Ray {
	// Should be a unit vector when the ray is cast
    private Vector m_unitVector = new Vector(); 
    private Point m_point = new Point();

    public Vector getVector() {
        return m_unitVector;
    }

    public Point getPoint() {
        return m_point;
    }

    public void init(Ray ray) {
        m_point.set(ray.getPoint());
        m_unitVector.set(ray.getVector());
    }

    public void transform(Matrix txMatrix) {
        m_point.multiply(txMatrix);
        m_unitVector.multiply(txMatrix);
    }
}
