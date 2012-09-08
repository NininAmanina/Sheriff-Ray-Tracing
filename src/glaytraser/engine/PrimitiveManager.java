package glaytraser.engine;

import glaytraser.math.Point;
import glaytraser.primitive.Box;
import glaytraser.primitive.Intersection;
import glaytraser.primitive.Polyhedron;
import glaytraser.primitive.Sphere;
import glaytraser.primitive.Torus;
import glaytraser.primitive.Union;

import java.util.ArrayList;
import java.util.HashMap;

class PrimitiveManager {
    private static final HashMap<String, Node> m_nodes = new HashMap<String, Node>();
    private static final String ROOT = ":";

    static final Node createRoot() {
        return addNode(null, ROOT);
    }

    /**
     * Create a new Node.
     * 
     * Create a new Node (basically a co-ordinate system) and insert it in the tree of Nodes.
     * 
     * @param name The name includes its parent's name, delimited by ":"
     * @return The new node
     */
    static final Node addNode(final String parent, final String name) {
        return insertNode(parent, name, new Node());
    }

    static final Node addUnion(final String parent, final String name, final String A, final String B) {
        return insertNode(parent, name, new Union(getNode(A), getNode(B)));
    }

    static final Node addIntersection(final String parent, final String name, final String A, final String B) {
        return insertNode(parent, name, new Intersection(getNode(A), getNode(B)));
    }

    static final Sphere addSphere(final String parent, final String name, final Point point, final double radius) {
        return (Sphere) insertNode(parent, name, new Sphere(point, radius));
    }

    static Box addBox(String parent, String name, Point point, double length) {
        return (Box) insertNode(parent, name, new Box(point, length));
    }

    static Node createTorus(final String parent, final String name, final Point point, final double toroidal,
                            final double polaroidal) {
        return (Torus) insertNode(parent, name, new Torus(point, toroidal, polaroidal));
    }

    static Polyhedron addPolyhedron(final String parent,
                                    final String name,
                                    final ArrayList <Point> point,
                                    final ArrayList <Integer []> polygon) {
        return (Polyhedron) insertNode(parent, name, new Polyhedron(point, polygon));
    }

    /**
     * Insert a node into the hierarchy
     * 
     * @param parentName The name of the parent of the Node to be inserted.  If null, then name must be ":".
     * @param name The name of the current node.
     * @param node Reference to the Node object.
     * @return
     */
    static final Node insertNode(final String parentName, final String name, final Node node) {
        synchronized(m_nodes) {
            if(m_nodes.containsKey(name)) {
                throw new IllegalStateException("Already have node named \"" + name + "\"; please choose another name");
            }
            if(parentName == null && !ROOT.equals(name)) {
                throw new IllegalArgumentException("Root node must be named \"" + ROOT + "\"");
            }
            if(parentName != null) {
                if(!m_nodes.containsKey(parentName)) {
                    throw new IllegalStateException("No parent by the name \"" + parentName + "\" exists");
                }
                Node parent = m_nodes.get(parentName);
                parent.addChild(node);
            }
            m_nodes.put(parentName == null ? ROOT : (parentName.equals(ROOT) ? "" : parentName) + ROOT + name, node);
            return node;
        }
    }

    public static Node getNode(final String node) {
        synchronized(m_nodes) {
            if(!m_nodes.containsKey(node)) {
                throw new IllegalArgumentException("No such node as \"" + node);
            }
            return m_nodes.get(node);
        }
    }
}
