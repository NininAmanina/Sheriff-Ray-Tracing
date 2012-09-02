package glaytraser.engine;

import glaytraser.math.Normal;
import glaytraser.math.Point;
import glaytraser.math.Vector;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Renderer {
    /**
     * @param args
     */
    public static void main(String[] args) {
        Node root = Parser.parseScene("simple.gr");
    }

    static int [] renderScene(Node root, BufferedImage image) {
        // The main renderer logic
        Point scratchPoint = new Point();
        Vector scratchVector = new Vector();
        Result result = new Result();
        Ray ray = new Ray();
        Ray scratchRay = new Ray();
        Camera camera = Camera.getCamera();
        final Point cameraPoint = camera.getPoint();
        ray.getPoint().set(cameraPoint);
        // Make these unit vectors
        final Vector cameraUp = camera.getUp();
        Vector verticalVector = new Vector(cameraUp).multiply(-1);
        Vector horizontalVector = cameraUp.crossProduct(camera.getDirection()).multiply(-1.0);

        int width = camera.getWidth();
        int height = camera.getHeight();
        double halfWidth = (double) (width >> 1);
        double halfHeight = (double) (height >> 1);
        double distanceToPixelGrid = halfWidth / Math.tan(camera.getFov());

        Vector temp = new Vector(camera.getDirection()).multiply(distanceToPixelGrid);

        // Find the top-left pixel's centre (move halfwidth - 0.5 pixels to the left,
        // and halfHeight = 0.5 pixels up from the centre of the pixel grid
        // TODO: Use generics to avoid the cast below
        Point scanlineStart = (Point) new Point(cameraPoint)
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
                    {
                        final Normal normal = result.getNormal();
                        normal.normalize();
                        // In order to make both sides of a surface be shint, we need to ensure that the normal is in vaguely
                        // opposite in direction form the light ray.
                        if(normal.dot(ray.getVector()) > 0) {
                            normal.multiply(-1);
                        }
                    }
                    // Lighting calculations
                    Point intersectionPt = (Point) scratchPoint.set(ray.getPoint())
                                                               .add(scratchVector.set(ray.getVector())
                                                                       .multiply(result.getT()));

                    // Invert the ray to form the ray from intersection point to the eye
                   	scratchRay.getPoint().set(intersectionPt);
                    scratchRay.getVector().set(ray.getVector()).multiply(-1);

                    pixel[r * width + c] = LightManager.doLighting(scratchRay, result, root);
                } else {
                    pixel[r * width + c] = 255 << 24 | (r % 256) << 16 | (c % 256) << 8;
                }
                if(image != null) {
                    image.setRGB(c, r, pixel[r * width + c]);
                }

                scanlinePoint.add(horizontalVector);
            }
            scanlineStart.add(verticalVector);
        }
        // Push a screen with the rendered scene
        JFrame frame = new JFrame("GLaytraSer");
        frame.setSize(width, height);
        frame.add(new RayTracerPanel(image, width, height));
        frame.setVisible(true);
        return pixel;
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

    RayTracerPanel(Image image, int width, int height) {
        m_image = image;
        setSize(width, height);
    }

    public void paintComponent(Graphics g) {
        g.drawImage(m_image, 0, 0, null);
    }
}
