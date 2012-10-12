package glaytraser.engine;

import java.util.ArrayList;

import glaytraser.math.*;

public final class Result {
    private Material m_material;
    // These lists are used for CSG
    private ArrayList<Normal> m_normalList = new ArrayList<Normal>();
    private ArrayList<Double> m_tList = new ArrayList<Double>();

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

    public Normal getNormal() {
        return m_normalList.get(0);
    }

    public double getT() {
        if(m_tList.size() > 0) {
            return m_tList.get(0);
        } else {
            return Double.MAX_VALUE;
        }
    }

    public Material getMaterial() {
        return m_material;
    }

    public void setMaterial(final Material material) {
        m_material = material;
    }

    public void addIntersection(final double t, final Normal normal) {
        if(t < Utils.EPSILON) {
            return;
        }
        final int size = m_normalList.size();
        if(size == 0) {
            m_tList.add(t);
            m_normalList.add(normal);
            return;
        }
        int i = 0;
        for(;i < size && t > m_tList.get(i); ++i) {
        }
        m_tList.add(i, t);
        final Normal newNormal = Normal.getNormal();
        newNormal.set(normal);
        m_normalList.add(i, newNormal);
    }

    public Result init() {
        m_material = null;
        clearList();
        return this;
    }

    private void clearList() {
        for(final Normal n : m_normalList) {
            Normal.putNormal(n);
        }
        m_normalList.clear();
        m_tList.clear();
    }
    
    public void transformNormals(final Matrix tx) {
        for(final Normal n : m_normalList) {
            n.multiply(tx);
        }
    }

    public Result set(final Result result) {
        m_material = result.getMaterial();
        m_tList.clear();
        m_tList.addAll(result.m_tList);
        clearList();
        for(int i = 0, ii = result.m_tList.size(); i < ii; ++i) {
            addIntersection(result.m_tList.get(i), result.m_normalList.get(i));
        }
        return this;
    }

    public final ArrayList<Double> getTList() {
        return m_tList;
    }

    public final ArrayList<Normal> getNormalList() {
        return m_normalList;
    }
}
