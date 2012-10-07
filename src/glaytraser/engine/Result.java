package glaytraser.engine;

import java.util.ArrayList;

import glaytraser.math.*;

public final class Result {
    private static final ArrayList<Result> m_scratchResultList = new ArrayList<Result>();

    public static final Result getResult() {
        synchronized(m_scratchResultList) {
            final int size = m_scratchResultList.size();
            if(size > 0) {
                return m_scratchResultList.remove(size - 1).init();
            } else {
                return new Result();
            }
        }
    }

    public static final void putResult(final Result result) {
        synchronized(m_scratchResultList) {
            m_scratchResultList.add(result);
        }
    }

    private Normal m_normal = new Normal();
    private double m_t;
    private Material m_material;

    public Normal getNormal() {
        return m_normal;
    }

    public double getT() {
        return m_t;
    }

    public void setT(final double t) {
        m_t = t;
    }
    
    public Material getMaterial() {
        return m_material;
    }

    public void setMaterial(final Material material) {
        m_material = material;
    }

    public Result init() {
        m_material = null;
        m_normal.clear();
        m_t = Double.MAX_VALUE;
        return this;
    }

    public void set(final Result result) {
        m_t = result.getT();
        m_normal.set(result.getNormal());
        m_material = result.getMaterial();
    }
}
