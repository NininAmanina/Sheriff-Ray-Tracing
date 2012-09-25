package glaytraser.primitive;

import glaytraser.engine.Node;
import glaytraser.engine.Ray;
import glaytraser.engine.Result;

public class Instance extends Node {
    private Node m_instance;

    public Instance(final Node instance) {
        m_instance = instance;
    }

    // This must be overridden by primitives.
    // @result We expect null for the light-source intersection routine
    public boolean rayIntersect(final Result result, final Ray ray, final boolean calcNormal) {
        final double t = result.getT();
        final boolean success = m_instance.intersect(result, ray, calcNormal);
        if(getMaterial() != null) {
            result.setMaterial(getMaterial());
        }
        return success;
    }
}
