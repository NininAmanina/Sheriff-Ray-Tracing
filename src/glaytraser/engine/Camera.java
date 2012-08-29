package glaytraser.engine;

import glaytraser.math.Point;
import glaytraser.math.Vector;

public class Camera {
    private static boolean m_initialised;
    private static Point m_cameraPoint;
    private static Vector m_cameraDirection;
    private static Vector m_cameraUp;
    private static final Camera m_camera = new Camera();
    
    /**
     * Initialise the camera
     * 
     * @param cameraPoint The location of the camera (non-null)
     * @param cameraDirection The direction into which the camera will point (non-null)
     * @param cameraUp The up direction of the camera (non-null)
     * @return The Camera singleton
     */
    static Camera init(final Point cameraPoint, final Vector cameraDirection, final Vector cameraUp) {
        synchronized(m_camera) {
            if(m_initialised) {
                throw new IllegalStateException("Camera already initialised");
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
            
            // Do Gram-Schmidt Orthonormalisation -- because the Up vector may not be orthogonal to the
            // camera's direction, we should make it so.
            cameraDirection.normalize();
            cameraUp.normalize();
            Vector right = cameraUp.crossProduct(cameraDirection);
            right.normalize();
            m_camera.setUp(cameraDirection.crossProduct(right));
            m_camera.setPoint(cameraPoint);
            m_camera.setDirection(cameraDirection);
            m_initialised = true;

            return m_camera;
        }
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
}
