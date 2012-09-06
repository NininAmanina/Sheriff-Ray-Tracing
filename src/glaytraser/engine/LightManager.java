package glaytraser.engine;

import glaytraser.math.Point;
import glaytraser.math.RGBTriple;
import glaytraser.math.Vector;

import java.util.ArrayList;

public class LightManager {
    // A collection of light sources defined for the scene.
    private static final ArrayList<Light> m_light = new ArrayList<Light>();
    private static final RGBTriple m_pixel = new RGBTriple();
    private static final Result m_scratchResult = new Result();
    private static final Vector m_scratchVector = new Vector();
    private static final RGBTriple m_scratchRGB = new RGBTriple();

    /**
     * This class represents a light object and its properties.
     */
    static abstract class Light {
        // Properties of the light source
        RGBTriple m_colour;

        // Scratch colour for calculations
        Vector m_scratchVector = new Vector();
        RGBTriple m_scratchRGB = new RGBTriple();

        abstract public void doLighting(RGBTriple pixel, Ray invertedRay, Result result, Node root);

        public Light(RGBTriple colour) {
            m_colour = colour;
        }

        public RGBTriple getColour() {
            return m_colour;
        }
    }

    /**
     * This class represents an ambient light object and its properties.
     */
    static class AmbientLight extends Light {
        private AmbientLight(RGBTriple colour) {
            super(colour);
        }

        @Override
        public void doLighting(RGBTriple pixel, Ray invertedRay, Result result, Node root) {
            pixel.add(((RGBTriple) m_scratchRGB.set(m_colour)).multiply(result.getMaterial().getDiffuse()));
        }
    }

    /**
     * This class represents an ambient light object and its properties.
     */
    static class DirectionalLight extends Light {
        private final Ray m_scratchRay = new Ray();
        private Vector m_direction;

        /**
         * Add a directional light source.
         *
         * @param colour The RGB values of the light
         * @param direction The direction into which the light is travelling
         */
        private DirectionalLight(Vector direction, RGBTriple colour) {
            super(colour);
            m_direction = direction;
        }

        @Override
        public void doLighting(RGBTriple pixel, Ray invertedRay, Result result, Node root) {
            // A ray from the intersection point to the light source
            Ray rayToLight = m_scratchRay;
            rayToLight.getVector().set(m_direction);
            rayToLight.getPoint().set(invertedRay.getPoint());
            LightManager.doLighting(pixel, rayToLight, Double.MAX_VALUE, invertedRay, result, root, m_colour);
        }
    }

    /**
     * This class represents a light source with a position and its properties.
     */
    static class PointLight extends Light {
        // The position of the light source
        private Point m_position;
        private final Ray m_scratchRay = new Ray();
        private int m_attenuation;

        /**
         * Create a point light at a position with a certain colour and a given attenuation exponent.
         * @param p The location of the point light.
         * @param colour The colour of the light.
         * @param attenuation The attenuation exponent.  It must be 0, 1, or 2.
         */
        private PointLight(Point p, RGBTriple colour, int attenuation) {
            super(colour);
            m_position = p;
            if(attenuation < 0 || attenuation > 2) {
                throw new IllegalArgumentException("Attenuation must be in [0..2], but requested: " + attenuation);
            }
            m_attenuation = attenuation;
        }

        // Returns the point position of the light source
        public Point getPosition() {
            return m_position;
        }
        
        public int getAttenuation() {
            return m_attenuation;
        }

        @Override
        public void doLighting(RGBTriple pixel, Ray invertedRay, Result result, Node root) {
            // A ray from the intersection point to the light source
            Ray rayToLight = m_scratchRay;
            final double distanceToLight = rayToLight.getVector().set(invertedRay.getPoint(), m_position).normalize();
            rayToLight.getPoint().set(invertedRay.getPoint());
            LightManager.doLighting(pixel, rayToLight, distanceToLight, invertedRay, result, root, m_colour);
        }
    }

    /**
     * Do the lighting calculations for all of the lights in the scene for the current pixel.
     *
     * @param p The Point of intersection
     * @param root The root Node of the scene
     * @param m The Material
     * @return The colour of the pixel as an integer.
     */
    public static int doLighting(Ray invertedRay, Result result, Node root) {
        m_pixel.set(0, 0, 0);
        // Loop through all the light sources, casting rays into the scene.
        for(Light light : m_light) {
            light.doLighting(m_pixel, invertedRay, result, root);
        }

        return m_pixel.getInt();
    }

    private static final void doLighting(RGBTriple pixel, Ray rayToLight, double distanceToLight, Ray invertedRay, Result result, Node root, RGBTriple colour) {
        // If another object occludes this light source, then no need to 
        // do lighting calculations.
        m_scratchResult.init();
        root.rayIntersect(m_scratchResult, rayToLight, false);
        if(m_scratchResult.getT() < distanceToLight) {
            return;
        }

        final Material material = result.getMaterial();
        double N_dot_L = result.getNormal().dot(rayToLight.getVector());

        // Reflected light vector due to a specular surface.
        m_scratchVector.set(result.getNormal()).multiply(N_dot_L).multiply(2);
        m_scratchVector.subtract(rayToLight.getVector());

        // Vector from the intersection point to the eye (camera) position.
        double R_dot_V = Math.max(0, m_scratchVector.dot(invertedRay.getVector()));
        double phongSpecularTerm = Math.pow(R_dot_V, material.getPhong());

        // Uses Phong shading for materials with a specular property.
        pixel.add(((RGBTriple) m_scratchRGB.set(colour)).multiply(material.getSpecular()).multiply(phongSpecularTerm));
        pixel.add(((RGBTriple) m_scratchRGB.set(colour)).multiply(material.getDiffuse()).multiply(Math.max(0, N_dot_L)));

    }

    public static void addPointLightSource(final Point p, final RGBTriple colour, final int attenuation) {
        m_light.add(new PointLight(p, colour, attenuation));
    }

    public static void addDirectionalLightSource(final Vector direction, final RGBTriple colour) {
        m_light.add(new DirectionalLight(direction, colour));
    }

    public static void addAmbientLightSource(final RGBTriple colour) {
        m_light.add(new AmbientLight(colour));
    }
}
