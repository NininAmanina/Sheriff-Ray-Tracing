package glaytraser.engine;

import glaytraser.math.Point;
import glaytraser.primitive.Sphere;

import java.util.HashMap;

class PrimitiveManager {
    private static final HashMap<String, Node> m_nodes = new HashMap<String, Node>();
    private static final char DELIM = ':';
    private static final String ROOT = "";

    static final Node createRoot() {
        return createNode(ROOT, null);
    }

    /**
     * Create a new Node, using its fully qualified name.
     * 
     * Create a new Node (basically a co-ordinate system) and insert it in the tree of Nodes.
     * 
     * @param name The name includes its parent's name, delimited by ":"
     * @param point The point at which the Node is located (ignored if this is the root)
     * @return The new node
     */
    static final Node createNode(String name, Point point) {
        Node node;
        if(ROOT.equals(name)) {
            node = new Node(new Point(0, 0, 0));
        } else {
            node = new Node(point);
        }

        return insertNode(name, node);
    }

    static final Sphere createSphere(String name, Point point, double radius) {
        return (Sphere) insertNode(name, new Sphere(point, radius));
    }

    static final Node insertNode(String name, Node node) {
        synchronized(m_nodes) {
            if(m_nodes.containsKey(name)) {
                throw new IllegalStateException("Already have node named \"" + name + "\"; please choose another name");
            }
            if(!ROOT.equals(name)) {
                int last = name.lastIndexOf(DELIM);
                if(last < 0) {
                    throw new IllegalArgumentException("Invalid node name:  \"" + name + "\"");
                }
                String parentName = name.substring(0, last);
                if(!m_nodes.containsKey(parentName)) {
                    throw new IllegalStateException("No parent by the name \"" + parentName + "\" exists");
                }
                Node parent = m_nodes.get(parentName);
                parent.addChild(node);
            }
            m_nodes.put(name, node);

            return node;
        }
    }
}
