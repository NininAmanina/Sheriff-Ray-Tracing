package glaytraser.primitive;

import glaytraser.engine.Node;
import glaytraser.engine.Ray;
import glaytraser.engine.Result;

public class Union extends Node {
    private Node m_nodeA;
    private Node m_nodeB;
    private Result m_scratchResultA = new Result();
    private Result m_scratchResultB = new Result();

    public Union(final Node nodeA, final Node nodeB) {
        m_nodeA = nodeA;
        m_nodeB = nodeB;
    }

    // This must be overridden by primitives.
    // @result We expect null for the light-source intersection routine
    public void rayIntersect(Result result, Ray ray, final boolean calcNormal) {
        // TODO:  Transform the ray
        final double t = result.getT();
        m_scratchResultA.set(result);
        m_nodeA.rayIntersect(m_scratchResultA, ray, calcNormal);
        m_scratchResultB.set(result);
        m_nodeB.rayIntersect(m_scratchResultB, ray, calcNormal);

        double tA = m_scratchResultA.getT();
        double tB = m_scratchResultB.getT();
        if(t < tA && t < tB) {
            return;
        }
        if(tA == Double.MAX_VALUE || tB < tA) {
            result.set(m_scratchResultB);
        } else {
            result.set(m_scratchResultA);
        }
        if(getMaterial() != null) {
            result.setMaterial(getMaterial());
        }
        // TODO:  Transform the normal
    }
}
