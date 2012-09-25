package glaytraser.primitive;

import glaytraser.engine.Node;
import glaytraser.engine.Ray;
import glaytraser.engine.Result;

public class Intersection extends Node {
    private Node m_nodeA;
    private Node m_nodeB;
    private Result m_scratchResultA = new Result();
    private Result m_scratchResultB = new Result();

    public Intersection(final Node nodeA, final Node nodeB) {
        m_nodeA = nodeA;
        m_nodeB = nodeB;
    }

    // This must be overridden by primitives.
    // @result We expect null for the light-source intersection routine
    public boolean rayIntersect(final Result result, final Ray ray, final boolean calcNormal) {
        m_scratchResultA.init();
        m_nodeA.intersect(m_scratchResultA, ray, calcNormal);
        m_scratchResultB.init();
        m_nodeB.intersect(m_scratchResultB, ray, calcNormal);
        double tA = m_scratchResultA.getT();
        double tB = m_scratchResultB.getT();
        if(tA == Double.MAX_VALUE || tB == Double.MAX_VALUE) {
            return false;
        } else if(tA < tB) {
            result.set(m_scratchResultB);
        } else {
            result.set(m_scratchResultA);
        }
        if(getMaterial() != null) {
            result.setMaterial(getMaterial());
        }
        return true;
    }
}
