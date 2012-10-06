package glaytraser.engine;

import glaytraser.math.Normal;
import glaytraser.math.Point;
import glaytraser.math.RGBTriple;
import glaytraser.math.Utils;
import glaytraser.math.Vector;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Renderer {
    private static final RGBTriple m_factor = new RGBTriple();
    private static final Point m_scratchPoint = new Point();
    private static final Vector m_scratchVector = new Vector();
    private static final Ray m_scratchRay = new Ray();

    /**
     * @param args
     */
    public static void main(String[] args) {
        Node root = Parser.parseScene("reflect.gr");
    }

    static int [] renderScene(Node root, BufferedImage image) {
        // The main renderer logic
        final Result result = new Result();
        final Ray ray = new Ray();
        final Camera camera = Camera.getCamera();
        final Point cameraPoint = camera.getPoint();
        // Make these unit vectors
        final Vector cameraUp = camera.getUp();
        final Vector verticalVector = new Vector(cameraUp).multiply(-1);
        final Vector horizontalVector = cameraUp.crossProduct(camera.getDirection()).multiply(-1.0);

        final int width = camera.getWidth();
        final int height = camera.getHeight();
        final double halfWidth = (double) (width >> 1);
        final double halfHeight = (double) (height >> 1);
        final double distanceToPixelGrid = halfWidth / Math.tan(camera.getFov());

        Vector temp = new Vector(camera.getDirection()).multiply(distanceToPixelGrid);

        // Find the top-left pixel's centre (move halfwidth - 0.5 pixels to the left,
        // and halfHeight = 0.5 pixels up from the centre of the pixel grid
        final Point scanlineStart = new Point(cameraPoint)
                                        .add(temp)
                                        .add(temp.set(horizontalVector).multiply(-(halfWidth - 0.5)))
                                        .add(temp.set(cameraUp).multiply(halfHeight - 0.5));
        Point scanlinePoint = new Point();

        int [] pixel = new int [width * height];
        final RGBTriple rgbPixel = new RGBTriple();
        for(int r = 0; r < height; ++r) {
            scanlinePoint.set(scanlineStart);
            for(int c = 0; c < width; ++c) {
                m_factor.set(1, 1, 1);
                rgbPixel.set(0, 0, 0);
                ray.getPoint().set(cameraPoint);
                ray.getVector().set(ray.getPoint(), scanlinePoint).normalize();
                if(cast(rgbPixel, result, ray, root, m_factor)) {
                    pixel[r * width + c] = rgbPixel.getInt();
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

    private static boolean cast(final RGBTriple pixel, final Result result, final Ray ray, final Node root, final RGBTriple factor) {
        // Initialise Ray and Result for each pixel
        result.init();

        root.intersect(result, ray, true);
        if(result.getT() < Double.MAX_VALUE) {
            {
                final Normal normal = result.getNormal();
                normal.normalize();
                // In order to make both sides of a surface be shiny, we need to ensure that the normal is in vaguely
                // opposite in direction form the light ray.
                if(normal.dot(ray.getVector()) > 0) {
                    normal.multiply(-1);
                }
            }
            // Lighting calculations
            Point intersectionPt = m_scratchPoint.set(ray.getPoint()).add(m_scratchVector.set(ray.getVector())
                                                                     .multiply(result.getT()));

            // Invert the ray to form the ray from intersection point to the eye
            m_scratchRay.getPoint().set(intersectionPt);
            m_scratchRay.getVector().set(ray.getVector()).multiply(-1);

            LightManager.doLighting(pixel, m_scratchRay, result, root, factor);
            Material m = result.getMaterial();
            if(m.isReflective()) {
                factor.multiply(m.getSpecular()).multiply(0.25);
                if(Utils.REFLECT_EPSILON < factor.get(0) &&
                   Utils.REFLECT_EPSILON < factor.get(1) &&
                   Utils.REFLECT_EPSILON < factor.get(2)) {
                    ray.getPoint().set(m_scratchRay.getPoint());
                    Vector v = m_scratchRay.getVector();
                    // Reflected light vector due to a specular surface.
                    double N_dot_L = result.getNormal().dot(v);
                    m_scratchVector.set(result.getNormal()).multiply(N_dot_L).multiply(2);
                    m_scratchVector.subtract(v);
                    ray.getVector().set(m_scratchVector);
                    cast(pixel, result, ray, root, factor);
                }
            }
            return true;
        }
        return false;
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
