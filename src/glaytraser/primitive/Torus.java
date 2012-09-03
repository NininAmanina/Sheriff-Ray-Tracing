package glaytraser.primitive;

import glaytraser.engine.Node;
import glaytraser.engine.Ray;
import glaytraser.engine.Result;
import glaytraser.math.Point;
import glaytraser.math.Utils;
import glaytraser.math.Vector;

public class Torus extends Node {
    private double m_toroidal;
    private double m_polaroidal;
    private Point m_centre;

    private Point scratchPoint = new Point();
    private Vector scratchVector = new Vector();

    public Torus(final Point p, final double toroidal, final double polaroidal) {
        m_centre = p;
        m_toroidal = toroidal;
        m_polaroidal = polaroidal;
    }

    // This must be overridden by primitives.
    // @result We expect null for the light-source intersection routine
    public void rayIntersect(Result result, Ray ray, final boolean calcNormal) {
    }

    public Point getCentre() {
        return m_centre;
    }

    public void setCentre(Point centre) {
        m_centre = centre;
    }
}
