package glaytraser.engine;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import glaytraser.math.Pair;
import glaytraser.math.Point;
import glaytraser.math.Vector;

public class Camera {
    private Pair m_size;
    private Point m_cameraPoint;
    private Vector m_cameraDirection;
    private Vector m_cameraUp;
    private double m_fov;
    private String m_file;
    private int [] m_pixel;
    private BufferedImage m_image;
    private static final Camera m_camera = new Camera();

    /**
     * Initialise the camera
     * 
     * @param size The width and height of the image
     * @param cameraPoint The location of the camera (non-null)
     * @param cameraDirection The direction into which the camera will point (non-null)
     * @param cameraUp The up direction of the camera (non-null)
     * @param file The name of the output file
     * @param fov The field of view in degrees
     * @return The Camera singleton
     */
    static Camera init(final Pair size,
                       final Point cameraPoint,
                       final Vector cameraDirection,
                       final Vector cameraUp,
                       final String file,
                       final double fov) {
        synchronized(m_camera) {
            if(size == null) {
                throw new IllegalArgumentException("The image's width and height must not be null");
            }
            if(cameraPoint == null) {
                throw new IllegalArgumentException("The camera's location must not be null");
            }
            if(cameraDirection == null) {
                throw new IllegalArgumentException("The camera's direction vector must not be null");
            }
            if(cameraUp == null) {
                throw new IllegalArgumentException("The camera's up vector must not be null");
            }
            if(file == null || file.length() == 0) {
                throw new IllegalArgumentException("The output filename can be neither null nor empty");
            }
            if(fov < 5) {
                throw new IllegalArgumentException("The field of view (" + fov + ") must be greater than 5 degrees");
            }

            // Do Gram-Schmidt Orthonormalisation -- because the Up vector may not be orthogonal to the
            // camera's direction, we should make it so.
            cameraDirection.normalize();
            cameraUp.normalize();
            Vector right = cameraUp.crossProduct(cameraDirection);
            right.normalize();
            m_camera.setSize(size);
            m_camera.setUp(cameraDirection.crossProduct(right));
            m_camera.setPoint(cameraPoint);
            m_camera.setDirection(cameraDirection);
            m_camera.setFile(file);
            m_camera.setFov(Math.toRadians(0.5 * fov));

            return m_camera;
        }
    }

    private void setFile(final String file) {
        m_file = file;
    }

    private void setFov(final double fov) {
        m_fov = fov;
    }

    /**
     * The field of view in radians
     * 
     * @return The field of view in radians
     */
    public double getFov() {
        return m_fov;
    }

    private void setSize(final Pair size) {
        m_size = size;
    }

    public int getWidth() {
        return (int) m_size.get(0);
    }

    public int getHeight() {
        return (int) m_size.get(1);
    }

    private void setPoint(final Point cameraPoint) {
        m_cameraPoint = cameraPoint;
    }

    public Point getPoint() {
        return m_cameraPoint;
    }

    private void setDirection(final Vector cameraDirection) {
        m_cameraDirection = cameraDirection;
    }

    public Vector getDirection() {
        return m_cameraDirection;
    }

    private void setUp(final Vector cameraUp) {
        m_cameraUp = cameraUp;
    }

    public Vector getUp() {
        return m_cameraUp;
    }

    public void setImage(BufferedImage image) {
        m_image = image;
    }

    public void setPixel(int [] pixel) {
        m_pixel = pixel;
        writeImage();
    }

    private void writeImage() {
        try {
            // retrieve image
            final int width = getWidth();
            final int height = getHeight();
            BufferedImage bi;
            if(m_image != null) {
                bi = m_image; 
            } else {
                bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                bi.setRGB(0, 0, width, height, m_pixel, 0, width);
            }
            File outputfile = new File(m_file);
            ImageIO.write(bi, "png", outputfile);
        } catch (IOException e) {
        }
    }

    public static Camera getCamera() {
        return m_camera;
    }
}
