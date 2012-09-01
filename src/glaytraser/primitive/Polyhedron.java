package glaytraser.primitive;

import java.util.ArrayList;

import glaytraser.engine.Node;
import glaytraser.engine.Ray;
import glaytraser.engine.Result;
import glaytraser.math.Normal;
import glaytraser.math.Point;
import glaytraser.math.Vector;

public class Polyhedron extends Node {
    private Point scratchPoint = new Point();
    private Vector scratchVector1 = new Vector();
    private Vector scratchVector2 = new Vector();
    private ArrayList <Point> m_point;
    private ArrayList <int []> m_polygon;
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
    public Polyhedron(final ArrayList<Point> point, final ArrayList<int []> polygon) {
        init(point, polygon);
    }

    protected final void init(final ArrayList<Point> point, final ArrayList<int []> polygon) {
        System.out.println("Creating polyhedron with points " + point + " and faces " + polygon);
        if(point == null) {
            throw new IllegalArgumentException("The polyhedron must have vertices.");
        }
        m_point = point;
        if(polygon == null) {
            throw new IllegalArgumentException("The polyhedron must have faces.");
        } else {
            for(int [] poly : polygon) {
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
        // TODO:  For now, assume that no sequence of three points is collinear.
        for(int [] polygon : m_polygon) {
            Normal n = (Normal) scratchVector1.set(m_point.get(polygon[0]), m_point.get(polygon[1])).crossProduct(
                                scratchVector2.set(m_point.get(polygon[1]), m_point.get(polygon[2])));
            n.normalize();
            m_normal.add(n);
        }
    }

    // This must be overridden by primitives.
    // @result We expect null for the light-source intersection routine
    public void rayIntersect(Result result, Ray ray, final boolean calcNormal) {
        // TODO:  Note that the Point at index 0 in a polygon acts as the equivalent of the Point at m_polygon.length.
        // SUGGESTION:  At certain points, use % m_polygon.length for indexing.
    }
}
