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

    private Point scratchPoint = new Point();
    private Vector scratchVector = new Vector();

    public Sphere(Point p, double radius) {
        super(p);
        m_centre = p;
        m_radiusSquared = radius * radius;
    }

    // This must be overridden by primitives.
    // @result We expect null for the light-source intersection routine
    public void rayIntersect(Result result, Ray ray, final boolean calcNormal) {
        // Simplify the math by dividing each of the arguments by 2.
        // This makes it slightly faster _and_ slightly more stable.
        double a = ray.getVector().getSquareMagnitude();
        Vector pc = new Vector(m_centre, ray.getPoint());
        double b = ray.getVector().dot(pc); // * 2;
        double c = pc.getSquareMagnitude() - m_radiusSquared;

        double discriminant = b * b - a * c;// * 4;
        if(discriminant < -Utils.EPSILON) {
            return;
        }
        // If the discriminant's magnitude is less than EPSILON, then make it 0.
        // Otherwise, take its square root and cache the value.
        double sqrtDisc;
        if(discriminant < Utils.EPSILON) {
            sqrtDisc = 0;
        } else {
            sqrtDisc = Math.sqrt(discriminant);
        }
        // TODO:  Make the math below a bit more numerically stable.
        double t1 = (-b - sqrtDisc) / a; // * 0.5;
        double t2 = (-b + sqrtDisc) / a; // * 0.5;
        double t = Math.min(Math.max(t1, 0), Math.max(t2, 0));
        if(t > Utils.EPSILON && t < result.getT()) {
            result.setT(t);
            if(calcNormal) {
                // TODO: Transform the normal into the world space.
                // TODO: Use generics to avoid the explicit cast below.
                result.getNormal().set(m_centre,
                    (Point) scratchPoint.set(ray.getPoint()).add(
                        scratchVector.set(ray.getVector()).multiply(t)));
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
