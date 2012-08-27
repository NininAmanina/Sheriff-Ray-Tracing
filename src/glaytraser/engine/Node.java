package glaytraser.engine;

import java.util.ArrayList;

import glaytraser.math.*;

public class Node {
    Matrix m_txMatrix = new Matrix();
    Ray m_txRay = new Ray();
    Normal m_scratchNormal = new Normal();
    ArrayList<Node> m_child = new ArrayList<Node>();
    Material m_material;

    public Node() {
    }

    public Node(Point p) {
    }

    // This must be overridden by primitives.
    // @result We expect null for the light-source intersection routine
    public void rayIntersect(Result result, Ray ray, final boolean calcNormal) {
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
    }

    /**
     * Determine whether a Ray intersects either with this Node or one of its children.
     *
     * @param result The t-value (from p + v * t) and the Normal to the surface at the point of intersection.
     * @param ray The incoming Ray, possibly transformed by the parent to this Node.
     * @param calcNormal Whether or not to calculate the normal to the surface of this Node.  If false,
     *        then result is ignored (and may be null).
     * @return Whether this node or any of its children intersects with this Ray.
     */
    public final void intersect(final Result result, final Ray ray, final boolean calcNormal) {
        // Handle all of the children firstly
        for(Node child : m_child) {
            child.intersect(result, ray, calcNormal);
        }

        // Now do our transformation
        m_txRay.init(ray);
        m_txRay.transform(m_txMatrix);

        rayIntersect(result, m_txRay, calcNormal);
    }

    public void addChild(Node node) {
        m_child.add(node);
    }

    public Node setMaterial(Material material) {
        m_material = material;
        return this;
    }

    public Material getMaterial() {
        return m_material;
    }
}