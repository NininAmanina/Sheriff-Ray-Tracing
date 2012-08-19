package glaytraser.engine;

import glaytraser.math.Matrix;
import glaytraser.math.Point;
import glaytraser.math.Vector;

public class Renderer {
    private static Node root = new Node();
    private static Point camera = new Point();
    private static Matrix cameraGnomon = new Matrix();
    private static int width = 1024;
    private static int height = 768;
    private static double fov = 120.0;

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
        // TODO:  move some of this into the scene definition file

        Result result = new Result();
        Ray ray = new Ray();
        Point scanlineStart = new Point();
        Point scanlinePoint = new Point();
        // Make these unit vectors
        // TODO:  Convert the unit vectors into the co-ordinate system of the camera.
        Vector verticalVector = new Vector(0.0, -1.0, 0.0);
        Vector horizontalVector = new Vector(1.0, 0.0, 0.0);

        double halfWidth = (double) (width >> 1);
        double halfHeight = (double) (height >> 1);
        double distanceToPixelGrid = halfWidth / Math.tan(fov / 2.0);
        // TODO:  Find the top-left pixel's centre (move halfwidth - 0.5 pixels to the left,
        // and halfHeight = 0.5 pixels up from the centre of the pixel grid
        // TODO:  initialise transformation matrices at each level
        // TODO:  initialise Ray for each pixel
        for(int i = 0; i < height; ++i) {
            scanlinePoint.set(scanlineStart);
            for(int j = 0; j < width; ++j) {
                result.init();
                ray.getVector().set(scanlinePoint, ray.getPoint());
                root.intersect(result, ray, true);
                if(result.getT() < Double.MAX_VALUE) {
                    // TODO:  Lighting calculations using
                }
                scanlinePoint.add(horizontalVector);
            }
            scanlineStart.add(verticalVector);
        }
        return false;
    }
}
