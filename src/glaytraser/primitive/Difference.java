package glaytraser.primitive;

import glaytraser.engine.Node;
import glaytraser.engine.Ray;
import glaytraser.engine.Result;

public class Difference extends Node {
    private Node m_nodeA;
    private Node m_nodeB;

    /**
     * Subtract <code>nodeB,/code> from <code>nodeA</code>
     * @param nodeA
     * @param nodeB
     */
    public Difference(final Node nodeA, final Node nodeB) {
        m_nodeA = nodeA;
        m_nodeB = nodeB;
    }

    // This must be overridden by primitives.
    // @result We expect null for the light-source intersection routine
    public boolean rayIntersect(Result result, Ray ray, final boolean calcNormal) {
        final Result r1 = Result.getResult();
        final Result r2 = Result.getResult();
        try {
            m_nodeA.intersect(r1, ray, calcNormal);
            m_nodeB.intersect(r2, ray, calcNormal);
            double tA = r1.getT();
            double tB = r2.getT();
            if(tA == Double.MAX_VALUE) {
                return false;
            } else if(tA < tB) {
                result.set(r1);
            } else {
                result.set(r2);
                result.setMaterial(r1.getMaterial());
            }
            if(getMaterial() != null) {
                result.setMaterial(getMaterial());
            }
            return true;
        } finally {
            Result.putResult(r1);
            Result.putResult(r2);
        }
    }
}
