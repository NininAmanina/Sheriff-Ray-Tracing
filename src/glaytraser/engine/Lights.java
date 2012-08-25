package glaytraser.engine;

import glaytraser.math.Point;

public class Lights {
    /**
     * Do the lighting calculations for all of the lights in the scene for the current pixel.
     *
     * @param p The Point of intersection
     * @param root The root Node of the scene
     * @param m The Material
     * @return
     */
    public static int doLighting(Point p, Node root, Material m) {
        return 255 << 24 | 255 << 16 | 255 << 8 | 255;
        // TODO:  Loop through all the light sources, casting rays into the scene.
    }
}
