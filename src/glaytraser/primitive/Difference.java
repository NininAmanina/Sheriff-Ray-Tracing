package glaytraser.primitive;

import java.util.ArrayList;

import glaytraser.engine.Node;
import glaytraser.engine.Ray;
import glaytraser.engine.Result;
import glaytraser.math.Normal;
import glaytraser.math.Vector;

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
    public boolean rayIntersect(final Result result, final Ray ray, final boolean calcNormal) {
        final Result r1 = Result.getResult();
        final Result r2 = Result.getResult();
        final Result r = Result.getResult().set(result);
        try {
            m_nodeA.intersect(r1, ray, calcNormal);
            m_nodeB.intersect(r2, ray, calcNormal);
            mergeResults(r, r1, r2, ray.getVector());
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
            Result.putResult(r);
        }
    }

    private void mergeResults(final Result r, final Result r1, final Result r2, final Vector v) {
        final ArrayList<Normal> n1 = r1.getNormalList();
        final ArrayList<Normal> n2 = r2.getNormalList();
        final ArrayList<Normal> n = r.getNormalList();
        final ArrayList<Double> t1 = r1.getTList();
        final ArrayList<Double> t2 = r2.getTList();
        final ArrayList<Double> t = r.getTList();

        int i1 = 0, i2 = 0, s1 = n1.size(), s2 = n2.size();
        
    }
}
