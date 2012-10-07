package glaytraser.primitive;

import glaytraser.engine.Node;
import glaytraser.engine.Ray;
import glaytraser.engine.Result;

public class Intersection extends Node {
    private Node m_nodeA;
    private Node m_nodeB;

    public Intersection(final Node nodeA, final Node nodeB) {
        m_nodeA = nodeA;
        m_nodeB = nodeB;
    }

    // This must be overridden by primitives.
    // @result We expect null for the light-source intersection routine
    public boolean rayIntersect(final Result result, final Ray ray, final boolean calcNormal) {
        final Result r1 = Result.getResult();
        final Result r2 = Result.getResult();
        try {
            r1.set(result);
            m_nodeA.intersect(r1, ray, calcNormal);
            r2.set(result);
            m_nodeB.intersect(r2, ray, calcNormal);
            double tA = r1.getT();
            double tB = r2.getT();
            if(tA == Double.MAX_VALUE || tB == Double.MAX_VALUE) {
                return false;
            } else if(tA < tB) {
                result.set(r2);
                result.setMaterial(r1.getMaterial());
            } else {
                result.set(r1);
                result.setMaterial(r2.getMaterial());
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
