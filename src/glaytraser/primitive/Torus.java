package glaytraser.primitive;

import glaytraser.engine.Node;
import glaytraser.engine.Ray;
import glaytraser.engine.Result;
import glaytraser.math.Point;
import glaytraser.math.PolyRoots;
import glaytraser.math.Utils;
import glaytraser.math.Vector;

public class Torus extends Node {
    private double m_toroidal;
    private double m_polaroidal;
    private Point m_centre;

    private Point m_scratchPoint = new Point();
    private Vector m_scratchVector = new Vector();

    public Torus(final Point p, final double toroidal, final double polaroidal) {
        m_centre = p;
        m_toroidal = toroidal;
        m_polaroidal = polaroidal;
    }

    // This must be overridden by primitives.
    // @result We expect null for the light-source intersection routine
    public void rayIntersect(Result result, Ray ray, final boolean calcNormal) {
        Vector rayVector = ray.getVector();
        double adC[] = new double [4];

        double a = m_toroidal;
        double b = m_polaroidal * m_polaroidal;
        m_scratchVector.set(m_centre, ray.getPoint());
        double x = m_scratchVector.get(0);
        double y = m_scratchVector.get(1);
        double z = m_scratchVector.get(2);
        double c = rayVector.get(0) * x;
        double d = rayVector.get(1) * y;
        double e = rayVector.get(2) * z;
        double g = rayVector.get(0) * rayVector.get(0);
        double h = rayVector.get(1) * rayVector.get(1);
        double i = rayVector.get(2) * rayVector.get(2);
        double f = a * a;
        double l = x * x;
        double m = y * y;
        double n = z * z;

        double temp2 = rayVector.dot(rayVector);
        double temp3 = temp2 * temp2;
        double temp4 = m_scratchVector.dot(m_scratchVector) + f - b;
        double temp5 = m_scratchVector.dot(rayVector);
        double temp6 = 4 * temp5;

        adC[0] = temp6 / temp2;
        adC[1] = 4 * ( temp5 * temp5 - f * ( g + h ) ) / temp3 + 2 * temp4 / temp2;
        adC[2] = ( 4 * ( temp5 * temp4 - 2 * f * ( c + d ) ) ) / temp3;
        adC[3] = ( temp4 * temp4 - 4 * f * ( l + m ) ) / temp3;

        // Retrieve the smallest positive root of the quadratic.
        double [] roots = PolyRoots.quarticRoots(adC[0], adC[1], adC[2], adC[3]);
        double dRoot = Double.MAX_VALUE;
        for(int index = 0, ii = roots.length; i < ii; ++i) {
            dRoot = Math.min(dRoot, Math.max(0, roots[index]));
        }

        // If there is a real root to this quadratic, return the intersection
        // point of the ray with the surface.
        double tempInter = result.getT();
        if(Utils.EPSILON < dRoot && (tempInter < Utils.EPSILON || tempInter > dRoot)) {
            result.setT(dRoot);
            m_scratchVector.set(ray.getVector());
            m_scratchVector.multiply(dRoot);
            m_scratchPoint.set(ray.getPoint());
            m_scratchPoint.add(m_scratchVector);
            m_scratchVector.set(m_centre, m_scratchPoint);

            g = b;
            a = m_scratchVector.get(0);
            b = m_scratchVector.get(1);
            c = m_scratchVector.get(2);
            d = m_scratchVector.dot(m_scratchVector) - f - g;
            e = d + f + f;
            result.getNormal().set(d * a, d * b, e * c);
            result.setMaterial( getMaterial() );
        }
    }

    public Point getCentre() {
        return m_centre;
    }

    public void setCentre(Point centre) {
        m_centre = centre;
    }
}
