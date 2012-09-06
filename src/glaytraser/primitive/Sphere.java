package glaytraser.primitive;

import glaytraser.engine.Node;
import glaytraser.engine.Ray;
import glaytraser.engine.Result;
import glaytraser.math.Point;
import glaytraser.math.Utils;
import glaytraser.math.Vector;

public class Sphere extends Node {
    private double m_radiusSquared;
    private Point m_centre;

    private Point m_scratchPoint = new Point();
    private Vector m_scratchVector = new Vector();

    public Sphere(Point p, double radius) {
        m_centre = p;
        m_radiusSquared = radius * radius;
    }

    // This must be overridden by primitives.
    // @result We expect null for the light-source intersection routine
    public void rayIntersect(Result result, Ray ray, final boolean calcNormal) {
        // Simplify the math by dividing each of the arguments by 2.
        // This makes it slightly faster _and_ slightly more stable.
        Vector pc = new Vector(m_centre, ray.getPoint());
        double a = ray.getVector().getSquareMagnitude();
        double b = ray.getVector().dot(pc); // * 2;
        double c = pc.getSquareMagnitude() - m_radiusSquared;
        double [] tArray = Utils.quadraticRoot(a, b, c);
        if(tArray == null) {
            return;
        }

        double t = Math.min(Math.max(tArray[0], 0), Math.max(tArray[1], 0));
        if(t > Utils.EPSILON && t < result.getT()) {
            result.setT(t);
            if(calcNormal) {
                // TODO: Transform the normal into the world space.
                // TODO: Use generics to avoid the explicit cast below.
                result.getNormal().set(m_centre,
                    (Point) m_scratchPoint.set(ray.getPoint()).add(
                        m_scratchVector.set(ray.getVector()).multiply(t)));
                // Set the material property for the primitive
                result.setMaterial(getMaterial());
            }
        }
    }

    public Point getCentre() {
        return m_centre;
    }

    public void setCentre(Point centre) {
        m_centre = centre;
    }
}
