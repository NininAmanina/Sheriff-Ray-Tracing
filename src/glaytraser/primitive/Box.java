package glaytraser.primitive;

import glaytraser.engine.Node;
import glaytraser.engine.Ray;
import glaytraser.engine.Result;
import glaytraser.math.Point;
import glaytraser.math.Vector;

public class Box extends Node {
    private double m_length;
    private Point m_centre;

    private Point scratchPoint = new Point();
    private Vector scratchVector = new Vector();

    public Box(Point p, double length) {
        m_centre = p;
        m_length = length;
    }

    // This must be overridden by primitives.
    // @result We expect null for the light-source intersection routine
    public void rayIntersect(Result result, Ray ray, final boolean calcNormal) {
    }
}
