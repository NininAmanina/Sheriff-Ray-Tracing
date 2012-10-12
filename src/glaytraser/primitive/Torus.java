package glaytraser.primitive;

import glaytraser.engine.Node;
import glaytraser.engine.Ray;
import glaytraser.engine.Result;
import glaytraser.math.Normal;
import glaytraser.math.Point;
import glaytraser.math.PolyRoots;
import glaytraser.math.Utils;
import glaytraser.math.Vector;

public class Torus extends Node {
    private double m_toroidal;
    private double m_polaroidal;
    private Point m_centre;

    public Torus(final Point p, final double toroidal, final double polaroidal) {
        m_centre = p;
        m_toroidal = toroidal;
        m_polaroidal = polaroidal;
    }

    // This must be overridden by primitives.
    // @result We expect null for the light-source intersection routine
    public boolean rayIntersect(final Result result, final Ray ray, final boolean calcNormal) {
        final Vector v1 = Vector.getVector();
        final Point pt = Point.getPoint();
        final Result newResult = Result.getResult();
        newResult.setMaterial(getMaterial());
        try {
            final Vector rayVector = ray.getVector();
            final double [] adC = new double [4];

            final double a = m_toroidal;
            final double b = m_polaroidal * m_polaroidal;
            v1.set(m_centre, ray.getPoint());
            final double x = v1.get(0);
            final double y = v1.get(1);
//            final double z = v1.get(2);
            final double c = rayVector.get(0) * x;
            final double d = rayVector.get(1) * y;
//            final double e = rayVector.get(2) * z;
            final double g = rayVector.get(0) * rayVector.get(0);
            final double h = rayVector.get(1) * rayVector.get(1);
//            final double i = rayVector.get(2) * rayVector.get(2);
            final double f = a * a;
            final double l = x * x;
            final double m = y * y;
//            final double n = z * z;

            final double temp2 = rayVector.dot(rayVector);
            final double temp3 = temp2 * temp2;
            final double temp4 = v1.dot(v1) + f - b;
            final double temp5 = v1.dot(rayVector);
            final double temp6 = 4 * temp5;

            adC[0] = temp6 / temp2;
            adC[1] = 4 * ( temp5 * temp5 - f * ( g + h ) ) / temp3 + 2 * temp4 / temp2;
            adC[2] = ( 4 * ( temp5 * temp4 - 2 * f * ( c + d ) ) ) / temp3;
            adC[3] = ( temp4 * temp4 - 4 * f * ( l + m ) ) / temp3;

            // Retrieve the smallest positive root of the quadratic.
            final double [] roots = PolyRoots.quarticRoots(adC[0], adC[1], adC[2], adC[3]);
            if(roots == null || roots.length == 0) {
                return false;
            }
            for(int index = 0, ii = roots.length; index < ii; ++index) {
                if(roots[index] > Utils.EPSILON) {
                    newResult.addIntersection(roots[index], calculateTorusNormal(result, ray, b, f, roots[index]));
                }
            }

            // If there is a real root to this quadratic, return the intersection
            // point of the ray with the surface.
            if(result.getT() > newResult.getT()) {
                result.set(newResult);
                result.setMaterial(getMaterial());
                return true;
            }
            return false;
        } finally {
            Vector.putVector(v1);
            Point.putPoint(pt);
            Result.putResult(newResult);
        }
    }

    private Normal calculateTorusNormal(final Result result, final Ray ray, final double b,
            final double f, double dRoot) {
        final Vector v1 = Vector.getVector().set(ray.getVector()).multiply(dRoot);
        final Point pt = Point.getPoint().set(ray.getPoint()).add(v1);
        v1.set(m_centre, pt);

        final double g2 = b;
        final double a2 = v1.get(0);
        final double b2 = v1.get(1);
        final double c2 = v1.get(2);
        final double d2 = v1.dot(v1) - f - g2;
        final double e2 = d2 + f + f;
        Vector.putVector(v1);
        Point.putPoint(pt);

        return Normal.getNormal().set(d2 * a2, d2 * b2, e2 * c2);
    }

    public Point getCentre() {
        return m_centre;
    }

    public void setCentre(final Point centre) {
        m_centre = centre;
    }
}
