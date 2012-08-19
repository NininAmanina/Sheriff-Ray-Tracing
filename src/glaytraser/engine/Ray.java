package glaytraser.engine;

import glaytraser.math.*;

public class Ray {
    private Vector m_vector; // should be a unit vector
    private Point m_point;
    
    public Vector getVector() {
        return m_vector;
    }
    
    public Point getPoint() {
        return m_point;
    }

    public void init(Ray ray) {
        m_point.set(ray.getPoint());
        m_vector.set(ray.getVector());
    }

    public void transform(Matrix txMatrix) {
        m_point.multiply(txMatrix);
        m_vector.multiply(txMatrix);
    }
}
