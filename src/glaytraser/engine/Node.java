package glaytraser.engine;

import java.util.ArrayList;

import glaytraser.math.*;

public class Node {
    // M^-1, which is also M^T
    private final Matrix m_txToNode = new Matrix();
    private final Ray m_txRay = new Ray();
    private final ArrayList<Node> m_child = new ArrayList<Node>();
    private Material m_material;

    public Node() {
    }

    // This must be overridden by primitives.
    // @result We expect null for the light-source intersection routine
    protected boolean rayIntersect(Result result, Ray ray, final boolean calcNormal) {
        /*
        Each subclass of Node must have something like the following code within it --
        the t value, the material properties and the (untransformed) normal must be set at the same time.
        */
        return false;
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
    public final boolean intersect(final Result result, final Ray ray, final boolean calcNormal) {
        // Now do our transformation
        m_txRay.init(ray);
        m_txRay.transform(m_txToNode);
        boolean success = false;

        // Handle all of the children firstly
        for(Node child : m_child) {
            success |= child.intersect(result, m_txRay, calcNormal);
        }

        success |= rayIntersect(result, m_txRay, calcNormal);
        if(success) {
            result.transformNormals(m_txToNode);
        }
        return success;
    }

    /**
     * Translate this co-ordinate system by some Vector offset.
     * 
     * @param translate The Vector by which to translate this Node.
     */
    public final void translate(final Vector translate) {
        // To Node tx
        final Matrix mat = Matrix.getMatrix();
        for(int i = 0; i < 3; ++i) {
            mat.set(i, 3, -translate.get(i));
        }
        m_txToNode.postMultiply(mat);
        Matrix.putMatrix(mat);
    }

    /**
     * Scaling in x, y, and z
     * 
     * @param scalePoint The point around which to scale in x, y, and z.
     * @param scaleFactor The amount to scale in x, y, and z.
     */
    public final void scale(final Point scalePoint, final Vector scaleFactor) {
        final Vector v1 = Vector.getVector();
        // Firstly translate to the scalePoint
        v1.set(scalePoint).multiply(-1).add(m_txToNode.getColumn(3));
        translate(v1);
        // To Node tx
        final Matrix mat = Matrix.getMatrix();
        mat.identity();
        for(int i = 0; i < 3; ++i) {
            mat.set(i, i, 1.0 / scaleFactor.get(i));
        }
        m_txToNode.postMultiply(mat);
        Matrix.putMatrix(mat);
        // Translate back
        translate(v1.multiply(-1));
        Vector.putVector(v1);
    }

    /**
     * Rotate around an axis.
     * 
     * @param axis The axis around which to rotate (0 = x, 1 = y, 2 = z)
     * @param angle The angle, in radians, which we need to rotate
     */
    public final void rotate(final int axis, final double angle) {
        // Firstly translate to the origin
        final Vector v1 = Vector.getVector();
        v1.set(m_txToNode.getColumn(3));
        translate(v1);
        final Matrix mat = Matrix.getMatrix();
        mat.identity();
        final int axisP = (axis + 1) % 3;
        final int axisPP = (axis + 2) % 3;
        final double cos = Math.cos(angle);
        final double sin = Math.sin(angle);
        // To Node tx
        mat.set(axisP, axisP, cos);
        mat.set(axisP, axisPP, sin);
        mat.set(axisPP, axisP, -sin);
        mat.set(axisPP, axisPP, cos);
        m_txToNode.postMultiply(mat);
        Matrix.putMatrix(mat);
        // Translate back
        translate(v1.multiply(-1));
        Vector.putVector(v1);
    }

    public void addChild(final Node node) {
        m_child.add(node);
    }

    public Node setMaterial(final Material material) {
        m_material = material;
        return this;
    }

    public Material getMaterial() {
        return m_material;
    }
}