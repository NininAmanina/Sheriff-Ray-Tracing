package glaytraser.engine;

import glaytraser.math.*;

public class Node {
    private static final double EPSILON = 0.0000001;

    Matrix m_txMatrix = new Matrix();
    Ray m_txRay = new Ray();
    Normal m_scratchNormal = new Normal();
    Node [] m_child = new Node [0];
    Material m_material;
    
    public Node() {
    }

    // This must be overridden by primitives.
    // @result We expect null for the light-source intersection routine
    public boolean rayIntersect(Result result, Ray ray, final boolean calcNormal) {
        /*
        Each subclass of Node must have something like the following code within it --
        the t value, the material properties and the normal must be set at the same time.
        We recommend calculating the normal at any time an intersection is detected.
        We recommend transformimg the normal
        
        double t = 
        if(t > EPSILON && t < result.getT()) {
            result.setT(t)
            result.setNormal(transformedCalculatedNormal);
            result.setMaterial(m_material);
        }
        */
        return false;
    }

    public final boolean intersect(final Result result, final Ray ray, final boolean calcNormal) {
        // Handle all of the children firstly
        for(int i = 0, ii = m_child.length; i < ii; ++i) {
            if(m_child[i].intersect(result, ray, calcNormal) && !calcNormal) {
                return true;
            }
        }
        
        // Now do our transformation
        m_txRay.init(ray);
        m_txRay.transform(m_txMatrix);
        
        return rayIntersect(result, m_txRay, calcNormal);
    }
}