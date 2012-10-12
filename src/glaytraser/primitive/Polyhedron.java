package glaytraser.primitive;

import java.util.ArrayList;

import glaytraser.engine.Node;
import glaytraser.engine.Ray;
import glaytraser.engine.Result;
import glaytraser.math.Normal;
import glaytraser.math.Point;
import glaytraser.math.Utils;
import glaytraser.math.Vector;

public class Polyhedron extends Node {
    private Point [] m_point;
    private ArrayList <Integer []> m_polygon;
    private ArrayList <Normal> m_normal;

    /**
     * Required for subclass
     */
    Polyhedron() {
    }

    /**
     * Create a polyhedron.
     * 
     * @param point List of Point objects which are referenced in <code>polygon</code>.
     * @param polygon List of convex polygons, each of which is defined by a set of Point objects from <code>point</code>.
     */
    public Polyhedron(final ArrayList<Point> point, final ArrayList<Integer []> polygon) {
        init(point, polygon);
    }

    protected final void init(final ArrayList<Point> point, final ArrayList<Integer []> polygon) {
        System.out.println("Creating polyhedron with points " + point + " and faces " + polygon);
        if(point == null) {
            throw new IllegalArgumentException("The polyhedron must have vertices.");
        }
        m_point = point.toArray(new Point [0]);
        if(polygon == null) {
            throw new IllegalArgumentException("The polyhedron must have faces.");
        } else {
            for(Integer [] poly : polygon) {
                if(poly == null || poly.length < 3) {
                    throw new IllegalArgumentException("Polygon does not have enough sides.");
                }
            }
        }
        m_polygon = polygon;
        setNormals();
    }

    private void setNormals() {
        m_normal = new ArrayList<Normal>();
        final Vector v1 = Vector.getVector();
        final Vector v2 = Vector.getVector();
        try {
            // TODO:  For now, assume that no sequence of three points is collinear.
            for(Integer [] polygon : m_polygon) {
                Normal n = v1.set(m_point[polygon[0]], m_point[polygon[1]]).crossProduct(
                           v2.set(m_point[polygon[1]], m_point[polygon[2]]));
                n.normalize();
                m_normal.add(n);
            }
        } finally {
            Vector.putVector(v1);
            Vector.putVector(v2);
        }
    }

    // This must be overridden by primitives.
    // @result We expect null for the light-source intersection routine
    public boolean rayIntersect(Result result, Ray ray, final boolean calcNormal) {
        // Note that the Point at index 0 in a polygon acts as the equivalent of the Point at m_polygon.length.
        // SUGGESTION:  At certain points, use % m_polygon[i].length for indexing.
        boolean success = false;
        final Vector v1 = Vector.getVector();
        final Vector v2 = Vector.getVector();
        final Point pt = Point.getPoint();
        final Normal norm = Normal.getNormal();
        final Result newResult = Result.getResult();
        newResult.setMaterial(getMaterial());
        try {
            outer: for(int j = 0, jj = m_polygon.size(); j < jj; ++j) {
                Integer [] poly = m_polygon.get(j);
                // Get intersection point with each plane
                final Normal normal = m_normal.get(j);
                final Point p = ray.getPoint();
                final Vector v = ray.getVector();
//                double [] t = Utils.insersect(0, 0, 0, 0, 0, 0, normal.get(0), normal.get(1), normal.get(2), -normal.dot(m_point[poly[0]]));
                final double A = normal.get(0), B = normal.get(1), C = normal.get(2),
                    D = -(normal.get(0) * m_point[poly[0]].get(0) +
                          normal.get(1) * m_point[poly[0]].get(1) +
                          normal.get(2) * m_point[poly[0]].get(2));
                final double denom = A * v.get(0) + B * v.get(1) + C * v.get(2);
                if(Math.abs(denom) < Utils.EPSILON) {
                    continue outer;
                }
                double t = -(D + A * p.get(0) + B * p.get(1) + C * p.get(2)) / denom;
                if(t < Utils.EPSILON) {
                    continue outer;
                }
                // Calculate the intersection point
                pt.set(p).add(v1.set(v).multiply(t));
                for(int i = 0, ii = poly.length; i < ii; ++i) {
                    v1.set(pt, m_point[poly[i]]);
                    v2.set(pt, m_point[poly[(i + 1) % ii]]);
                    if(normal.dot(v1.crossProduct(v2, norm)) < 0) {
                        continue outer;
                    }
                }
                // By this step in the algorithm, we've gotten an intersection inside the polygon.
                if(t > Utils.EPSILON) {
                    newResult.addIntersection(t, normal);
                    success = true;
                }
            }
            if(success && newResult.getT() < result.getT()) {
                result.set(newResult);
                return true;
            }
            return false;
        } finally {
            Vector.putVector(v1);
            Vector.putVector(v2);
            Point.putPoint(pt);
            Normal.putNormal(norm);
            Result.putResult(newResult);
        }
    }
}
