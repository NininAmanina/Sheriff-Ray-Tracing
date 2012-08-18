package glaytraser.engine;

import glaytraser.math.*;

public class Result {
    private Normal m_normal;
    private double m_t;
    private Material m_material;
    
    public Normal getNormal() {
        return m_normal;
    }
    
    public double getT() {
        return m_t;
    }

    public void setT(double t) {
        m_t = t;
    }
    
    public Material getMaterial() {
        return m_material;
    }

    public void setMaterial(Material material) {
        m_material = material;
    }

    public void init() {
        m_material = null;
        m_normal.clear();
        m_t = Integer.MAX_VALUE;
    }
}