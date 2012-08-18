package glaytraser.engine;

import glaytraser.math.*;

public class Renderer {
    private static Node root = new Node();
    private static Point camera = new Point();
    private static Matrix cameraGnomon = new Matrix();
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Invoke the ray tracer with the scene file
        if(args.length < 2) {
            System.exit(1);
        }

//        if(SceneParser.createScene(args[0], root, camera, cameraGnomon) &&
//           Renderer.renderScene()) {
//        }
    }
    
    static boolean renderScene() {
        Result result = new Result();
        Ray ray = new Ray();
        // TODO:  initialise transformation matrices at each level
        // TODO:  initialise Ray for each pixel
//        for() {
            result.init();
            root.intersect(result, ray, true);
            if(result.getT() < Integer.MAX_VALUE) {
                // TODO:  Lighting calculations using
            }
//        }
        return false;
    }
}
