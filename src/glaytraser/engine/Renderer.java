package glaytraser.engine;

import glaytraser.math.Point;
import glaytraser.math.RGBTriple;
import glaytraser.math.Vector;
import glaytraser.primitive.Sphere;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.MemoryImageSource;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Renderer {
    // TODO:  Move these to a scene file once the parser is done
    // Define some basic scene properties for a scene with a few spheres.
    private static final RGBTriple ambient = new RGBTriple(0, 0, 0);
    private static final RGBTriple diffuse = new RGBTriple(0.7, 1.0, 0.7);
    private static final RGBTriple specular = new RGBTriple(0.5, 0.7, 0.5);

    private static final RGBTriple ptLightColour = new RGBTriple(0.7, 0.0, 0.7);
    private static final RGBTriple amLightColour = new RGBTriple(0.4, 0.4, 0.4);

    static {
        // Define a single point light source
        LightManager.addPointLightSource(new Point(-100, 150, 400), ptLightColour);
        LightManager.addAmbientLightSource(amLightColour);
    }

    private static final Material material = Material.createMaterial("first", diffuse, specular, 25);

    // The view location
    private static Point camera = new Point(0, 0, -800);
    private static Vector cameraDirection = new Vector(0, 0, 1);
    private static Vector cameraUp = new Vector(0, 1, 0);

    //    private static Matrix cameraGnomon = new Matrix();
    private static int width = 1024;
    private static int height = 768;
    private static final double fovDegrees = 50.0;
    private static double fov = Math.PI * (0.5 * fovDegrees / 180.0);

    /**
     * @param args
     */
    public static void main(String[] args) {
//        Node root = Parser.parseScene(args[0]);
        Node root = PrimitiveManager.createRoot();
        PrimitiveManager.createSphere(":sphere1", new Point(0, 0, 400), 50).setMaterial(material);
        PrimitiveManager.createSphere(":sphere2", new Point(10, -40, -100), 25).setMaterial(material);
        PrimitiveManager.createSphere(":sphere3", new Point(-110, 160, 410), 10).setMaterial(material);

        Renderer.renderScene(root);
    }

    static boolean renderScene(Node root) {
        // The main renderer logic
        Point scratchPoint = new Point();
        Vector scratchVector = new Vector();
        Result result = new Result();
        Ray ray = new Ray();
        ray.getPoint().set(camera);
        // Make these unit vectors
        Vector verticalVector = new Vector(cameraUp).multiply(-1);
        Vector horizontalVector = cameraUp.crossProduct(cameraDirection).multiply(-1.0);

        double halfWidth = (double) (width >> 1);
        double halfHeight = (double) (height >> 1);
        double distanceToPixelGrid = halfWidth / Math.tan(fov);

        Vector temp = new Vector(cameraDirection).multiply(distanceToPixelGrid);

        // Find the top-left pixel's centre (move halfwidth - 0.5 pixels to the left,
        // and halfHeight = 0.5 pixels up from the centre of the pixel grid
        // TODO: Use generics to avoid the cast below
        Point scanlineStart = (Point) new Point(camera)
            .add(temp)
            .add(temp.set(horizontalVector).multiply(-(halfWidth - 0.5)))
            .add(temp.set(cameraUp).multiply(halfHeight - 0.5));
        Point scanlinePoint = new Point();

        int [] pixel = new int [width * height];
        // TODO:  initialise transformation matrices at each level
        for(int r = 0; r < height; ++r) {
            scanlinePoint.set(scanlineStart);
            for(int c = 0; c < width; ++c) {
                // Initialise Ray and Result for each pixel
                result.init();
                ray.getVector().set(ray.getPoint(), scanlinePoint).normalize();

                root.intersect(result, ray, true);
                if(result.getT() < Double.MAX_VALUE) {
                    result.getNormal().normalize();
                    // Lighting calculations
                    pixel[r * width + c] = LightManager.doLighting(
                        (Point) scratchPoint.set(ray.getPoint())
                                            .add(scratchVector.set(ray.getVector())
                                                              .multiply(result.getT())),
                        result,
                        root);
                } else {
                    pixel[r * width + c] = 255 << 24 | (r % 256) << 16 | (c % 256) << 8;
                }

                scanlinePoint.add(horizontalVector);
            }
            scanlineStart.add(verticalVector);
        }
        // Push a screen with the rendered scene
        JFrame frame = new JFrame("GLaytraSer");
        frame.setSize(width, height);
        frame.add(new RayTracerPanel(pixel, width, height));
        frame.setVisible(true);
        return true;
    }
}

class RayTracerPanel extends JPanel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    Image m_image;

    RayTracerPanel(int [] pixel, int width, int height) {
        m_image = createImage(new MemoryImageSource(width, height, pixel, 0, width));
        setSize(width, height);
    }

    public void paintComponent(Graphics g) {
        g.drawImage(m_image, 0, 0, null);
    }
}
