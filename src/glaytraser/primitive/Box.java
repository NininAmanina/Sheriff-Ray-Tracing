package glaytraser.primitive;

import java.util.ArrayList;

import glaytraser.math.Point;
import glaytraser.math.Vector;

public class Box extends Polyhedron {

    /**
     * Create a cube.
     * 
     * @param p The point at which the box is anchored.
     * @param length The length of each edge of the box.
     */
    public Box(Point p, double length) {
        ArrayList<Point> point = new ArrayList<Point>();
        // Define the vertices on the Box.
        final Vector scratchVector = new Vector();
        point.add(new Point(p).add(scratchVector.set(     0,      0,      0)));
        point.add(new Point(p).add(scratchVector.set(length,      0,      0)));
        point.add(new Point(p).add(scratchVector.set(     0, length,      0)));
        point.add(new Point(p).add(scratchVector.set(length, length,      0)));
        point.add(new Point(p).add(scratchVector.set(     0,      0, length)));
        point.add(new Point(p).add(scratchVector.set(length,      0, length)));
        point.add(new Point(p).add(scratchVector.set(     0, length, length)));
        point.add(new Point(p).add(scratchVector.set(length, length, length)));

        // Define the faces of the Box.
        ArrayList<Integer []> polygon = new ArrayList<Integer []>();
        polygon.add(new Integer [] { 0, 1, 3, 2 });
        polygon.add(new Integer [] { 0, 4, 5, 1 });
        polygon.add(new Integer [] { 0, 2, 6, 4 });
        polygon.add(new Integer [] { 7, 5, 1, 3 });
        polygon.add(new Integer [] { 7, 3, 2, 6 });
        polygon.add(new Integer [] { 7, 6, 4, 5 });

        // Store the Box as a Polyhedron
        init(point, polygon);
    }
}
