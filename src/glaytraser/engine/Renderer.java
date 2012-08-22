package glaytraser.engine;

import glaytraser.math.Point;
import glaytraser.math.Vector;
import glaytraser.primitive.Sphere;

public class Renderer {
    private static Node root = new Sphere(new Point(0, 0, 800), 400);
    
    // The view location
    private static Point camera = new Point(0, 0, -800);
    private static Vector cameraDirection = new Vector(0, 0, 1);
    private static Vector cameraUp = new Vector(0, 1, 0);
    
//    private static Matrix cameraGnomon = new Matrix();
    private static int width = 1024;
    private static int height = 768;
    private static double fov = 50.0;

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
        // Make these unit vectors
        // TODO:  Convert the unit vectors into the co-ordinate system of the camera.
        Vector verticalVector = new Vector(cameraUp);
        verticalVector.multiply(-1);
        
        Vector horizontalVector = cameraDirection.crossProduct(cameraUp);

        double halfWidth = (double) (width >> 1);
        double halfHeight = (double) (height >> 1);
        double distanceToPixelGrid = halfWidth / Math.tan(fov);
        
        Vector temp = new Vector(cameraDirection);
        temp.multiply(distanceToPixelGrid);
        
        // Find the top-left pixel's centre (move halfwidth - 0.5 pixels to the left,
        // and halfHeight = 0.5 pixels up from the centre of the pixel grid
        Point scanlineStart = new Point(camera);
        scanlineStart.add(temp);
        temp.set(horizontalVector);
        temp.multiply(-(halfWidth - 0.5));
        scanlineStart.add(temp);
        temp.set(cameraUp);
        temp.multiply(halfHeight - 0.5);
        scanlineStart.add(temp);
        
        Point scanlinePoint = new Point();
        
        // TODO:  initialise transformation matrices at each level
        for(int i = 0; i < height; ++i) {
            scanlinePoint.set(scanlineStart);
            for(int j = 0; j < width; ++j) {
                // Initialise Ray and Result for each pixel
                result.init();
                ray.getVector().set(scanlinePoint, ray.getPoint());
                ray.getVector().normalize();
                
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
