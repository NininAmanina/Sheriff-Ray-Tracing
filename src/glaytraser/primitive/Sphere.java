package glaytraser.primitive;

import glaytraser.engine.Node;
import glaytraser.engine.Ray;
import glaytraser.engine.Result;
import glaytraser.math.Point;
import glaytraser.math.Utils;
import glaytraser.math.Vector;

public class Sphere extends Node {
    private double m_radius;
    private Point m_center;
    
    private Point scratchPoint = new Point();
    private Vector scratchVector = new Vector();
    
    public Sphere(Point p, double radius) {
        super(p);
        this.m_radius = radius; 
    }

    // This must be overridden by primitives.
    // @result We expect null for the light-source intersection routine
    public boolean rayIntersect(Result result, Ray ray, final boolean calcNormal) {
        double a = ray.getVector().dot(ray.getVector());
        Vector pc = new Vector(m_center, ray.getPoint());
        double b = 2*ray.getVector().dot(pc);
        double c = pc.dot(pc);
        
        double discriminant = b*b - 4*a*c;
        if(discriminant < 0.0) {
            return false;
        }
        
        double t1 = (b - Math.sqrt(discriminant)) / (2 * a);
        double t2 = (b + Math.sqrt(discriminant)) / (2 * a);
        double t = Math.min(Math.max(t1, 0), Math.max(t2, 0));
        if(t > Utils.EPSILON && t < result.getT()) {
            result.setT(t);
            scratchPoint.set(ray.getPoint());
            scratchVector.set(ray.getVector());
            scratchVector.multiply(t);
            scratchPoint.add(scratchVector);
            // TODO: Transform the normal into the world space
            result.getNormal().set(m_center, scratchPoint);
            // TODO: Set the material property for the primitive
            return true;
        }
        return false;
    }
    
    public Point getCenter() {
        return m_center;
    }

    public void setCenter(Point center) {
        this.m_center = center;
    }

    public double getRadius() {
        return m_radius;
    }

    public void setRadius(double radius) {
        this.m_radius = radius;
    }
}
