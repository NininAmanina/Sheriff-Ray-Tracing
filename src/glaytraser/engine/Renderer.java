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
    private static final Vector m_subpixelHorizontalVector = new Vector();
    private static final Vector m_subpixelVerticalVector = new Vector();
    private static final int MAX_SAMPLES = 15;
    private static int m_sampleCount = 1;
    private static double m_sampleFactor = 1.0;
    private static double m_sampleFactorSquared = 1.0;

    /**
     * @param args
     */
    public static void main(final String [] args) {
        Node root = Parser.parseScene("simple.gr");
    }

    static int [] renderScene(final Node root, final BufferedImage image) {
        // The main renderer logic
        Result result = new Result();
        Ray ray = new Ray();
        Camera camera = Camera.getCamera();
        final Point cameraPoint = camera.getPoint();
        // Make these unit vectors
        final Vector cameraUp = camera.getUp();
        final Vector verticalVector = new Vector(cameraUp).multiply(-1);
        final Vector horizontalVector = cameraUp.crossProduct(camera.getDirection()).multiply(-1.0);
        m_subpixelHorizontalVector.set(horizontalVector).multiply(m_sampleFactor);
        m_subpixelVerticalVector.set(verticalVector).multiply(m_sampleFactor);

        int width = camera.getWidth();
        int height = camera.getHeight();
        double halfWidth = (double) (width >> 1);
        double halfHeight = (double) (height >> 1);
        double distanceToPixelGrid = halfWidth / Math.tan(camera.getFov());

        Vector temp = new Vector(camera.getDirection()).multiply(distanceToPixelGrid);

        // Find the top-left pixel's centre (move halfwidth - 0.5 pixels to the
        // left, and halfHeight - 0.5 pixels up from the centre of the pixel
        // grid.
        Point scanlineStart = new Point(cameraPoint).add(temp)
                .add(temp.set(horizontalVector).multiply(-(halfWidth - 0.5))).add(temp.set(cameraUp).multiply(halfHeight - 0.5));
        Point scanlinePoint = new Point();

        int [] pixel = new int [width * height];
        for (int r = 0; r < height; ++r) {
            scanlinePoint.set(scanlineStart);
            for (int c = 0; c < width; ++c) {
                ray.getPoint().set(cameraPoint);
                ray.getVector().set(ray.getPoint(), scanlinePoint).normalize();
                castRayIntoScene(root, result, ray, horizontalVector, verticalVector, width, pixel, r, c);

                if (image != null) {
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

    private static void castRayIntoScene(final Node root, final Result result, final Ray ray, final Vector left,
            final Vector down, final int width, final int [] pixel, final int row, final int column) {
        final RGBTriple pixel1 = RGBTriple.getRGBTriple();
        final RGBTriple pixel2 = RGBTriple.getRGBTriple();
        final Ray subpixelRay = Ray.getRay();
        final Vector temp = Vector.getVector();
        final Point scanlinePoint = Point.getPoint();
        final double factor = 0.5 * (1 - m_sampleCount);
        final Point scanlineStart = Point.getPoint().set(ray.getPoint())
                .add(temp.set(m_subpixelHorizontalVector).multiply(factor))
                .add(temp.set(m_subpixelVerticalVector).multiply(factor));
        try {
            pixel1.set(0, 0, 0);
            subpixelRay.init(ray);
            for (int i = 0; i < m_sampleCount; ++i) {
                scanlinePoint.set(scanlineStart);
                for (int j = 0; j < m_sampleCount; ++j) {
                    pixel2.set(0, 0, 0);
                    m_factor.set(1, 1, 1);
                    subpixelRay.getPoint().set(scanlinePoint);
                    // Jitter the ray by a random amount in a subpixel if we have a sample rate of more than 1.
                    if(m_sampleCount > 1) {
                        jitter(subpixelRay);
                    }
                    if (!cast(pixel2, result, subpixelRay, root, m_factor)) {
                        pixel2.set(((double) (row % 256)) / 256.0, ((double) (column % 256)) / 256.0, 0);
                    }
                    pixel1.add(pixel2);
                    scanlinePoint.add(m_subpixelHorizontalVector);
                }
                scanlineStart.add(m_subpixelVerticalVector);
            }
            pixel[row * width + column] = pixel1.multiply(m_sampleFactorSquared).getInt();
        } finally {
            RGBTriple.putRGBTriple(pixel1);
            RGBTriple.putRGBTriple(pixel2);
            Ray.putRay(subpixelRay);
            Vector.putVector(temp);
            Point.putPoint(scanlinePoint);
            Point.putPoint(scanlineStart);
        }
    }

    private static void jitter(Ray subpixelRay) {
        final Vector horzVec = Vector.getVector().set(m_subpixelHorizontalVector).multiply(Math.random() - 0.5);
        final Vector vertVec = Vector.getVector().set(m_subpixelVerticalVector).multiply(Math.random() - 0.5);

        subpixelRay.getPoint().add(horzVec).add(vertVec);
        Vector.putVector(horzVec);
        Vector.putVector(vertVec);
    }

    private static boolean cast(final RGBTriple pixel, final Result result, final Ray ray, final Node root, final RGBTriple factor) {
        // Initialise Ray and Result for each pixel
        result.init();

        root.intersect(result, ray, true);
        if (result.getT() < Double.MAX_VALUE) {
            {
                final Normal normal = result.getNormal();
                normal.normalize();
                // In order to make both sides of a surface be shiny, we need to
                // ensure that the normal is in vaguely
                // opposite in direction form the light ray.
                if (normal.dot(ray.getVector()) > 0) {
                    normal.multiply(-1);
                }
            }
            final Vector v1 = Vector.getVector();
            final Point p1 = Point.getPoint();
            final Ray recastRay = Ray.getRay();
            try {
                // Lighting calculations
                Point intersectionPt = p1.set(ray.getPoint()).add(
                        v1.set(ray.getVector()).multiply(result.getT()));

                // Invert the ray to form the ray from intersection point to the eye
                recastRay.getPoint().set(intersectionPt);
                recastRay.getVector().set(ray.getVector()).multiply(-1);

                LightManager.doLighting(pixel, recastRay, result, root, factor);
                Material m = result.getMaterial();
                if (m.isReflective()) {
                    factor.multiply(m.getSpecular()).multiply(0.25);
                    if (Utils.REFLECT_EPSILON < factor.get(0) && Utils.REFLECT_EPSILON < factor.get(1)
                            && Utils.REFLECT_EPSILON < factor.get(2)) {
                        ray.getPoint().set(recastRay.getPoint());
                        Vector v = recastRay.getVector();
                        // Reflected light vector due to a specular surface.
                        double N_dot_L = result.getNormal().dot(v);
                        v1.set(result.getNormal()).multiply(N_dot_L).multiply(2);
                        v1.subtract(v);
                        ray.getVector().set(v1);
                        cast(pixel, result, ray, root, factor);
                    }
                }
            } finally {
                Vector.putVector(v1);
                Point.putPoint(p1);
                Ray.putRay(recastRay);
            }
            return true;
        }
        return false;
    }

    public static void setSampleCount(int sampleCount) {
        if (sampleCount < 1 || sampleCount > MAX_SAMPLES) {
            throw new IllegalArgumentException("The number of samples per axis (" + sampleCount + ") must lie in [1,"
                    + MAX_SAMPLES + "]");
        }
        m_sampleCount = sampleCount;
        m_sampleFactor = 1.0 / (double) (m_sampleCount);
        m_sampleFactorSquared = m_sampleFactor * m_sampleFactor;
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
