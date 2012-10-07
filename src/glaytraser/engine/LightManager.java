package glaytraser.engine;

import glaytraser.math.Point;
import glaytraser.math.RGBTriple;
import glaytraser.math.Vector;

import java.util.ArrayList;

public class LightManager {
    // A collection of light sources defined for the scene.
    private static final ArrayList<Light> m_light = new ArrayList<Light>();
    private static final RGBTriple m_pixel = new RGBTriple();

    /**
     * This class represents a light object and its properties.
     */
    static abstract class Light {
        // Properties of the light source
        RGBTriple m_colour;

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
        private AmbientLight(final RGBTriple colour) {
            super(colour);
        }

        @Override
        public void doLighting(final RGBTriple pixel, final Ray invertedRay, final Result result, final Node root) {
            final RGBTriple rgb  = RGBTriple.getRGBTriple();
            pixel.add(((RGBTriple) rgb.set(m_colour)).multiply(result.getMaterial().getDiffuse()));
            RGBTriple.putRGBTriple(rgb);
        }
    }

    /**
     * This class represents an ambient light object and its properties.
     */
    static class DirectionalLight extends Light {
        private Vector m_direction;

        /**
         * Add a directional light source.
         *
         * @param colour The RGB values of the light
         * @param direction The direction into which the light is travelling
         */
        private DirectionalLight(final Vector direction, final RGBTriple colour) {
            super(colour);
            m_direction = direction;
        }

        @Override
        public void doLighting(final RGBTriple pixel, final Ray invertedRay, final Result result, final Node root) {
            // A ray from the intersection point to the light source
            final Ray rayToLight = Ray.getRay();
            rayToLight.getVector().set(m_direction);
            rayToLight.getPoint().set(invertedRay.getPoint());
            LightManager.doLighting(pixel, rayToLight, Double.MAX_VALUE, invertedRay, result, root, m_colour);
            Ray.putRay(rayToLight);
        }
    }

    /**
     * This class represents a light source with a position and its properties.
     */
    static class PointLight extends Light {
        // The position of the light source
        private Point m_position;
        private int m_attenuation;

        /**
         * Create a point light at a position with a certain colour and a given attenuation exponent.
         * @param p The location of the point light.
         * @param colour The colour of the light.
         * @param attenuation The attenuation exponent.  It must be 0, 1, or 2.
         */
        private PointLight(final Point p, final RGBTriple colour, final int attenuation) {
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
        public void doLighting(final RGBTriple pixel, final Ray invertedRay, final Result result, final Node root) {
            // A ray from the intersection point to the light source
            final Ray rayToLight = Ray.getRay();
            final double distanceToLight = rayToLight.getVector().set(invertedRay.getPoint(), m_position).normalize();
            rayToLight.getPoint().set(invertedRay.getPoint());
            LightManager.doLighting(pixel, rayToLight, distanceToLight, invertedRay, result, root, m_colour);
            Ray.putRay(rayToLight);
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
    public static void doLighting(final RGBTriple pixel,
                                  final Ray invertedRay,
                                  final Result result,
                                  final Node root,
                                  final RGBTriple factor) {
        m_pixel.set(0, 0, 0);
        // Loop through all the light sources, casting rays into the scene.
        for(final Light light : m_light) {
            light.doLighting(m_pixel, invertedRay, result, root);
        }

        m_pixel.multiply(factor);
        pixel.add(m_pixel);
    }

    private static final void doLighting(final RGBTriple pixel,
                                         final Ray rayToLight,
                                         final double distanceToLight,
                                         final Ray invertedRay,
                                         final Result result,
                                         final Node root,
                                         final RGBTriple colour) {
        // If another object occludes this light source, then no need to do lighting calculations.
        final Result lightResult = Result.getResult();
        lightResult.init();
        root.intersect(lightResult, rayToLight, false);
        if(lightResult.getT() < distanceToLight) {
            return;
        }
        Result.putResult(lightResult);

        final Material material = result.getMaterial();
        double N_dot_L = result.getNormal().dot(rayToLight.getVector());

        final Vector v = Vector.getVector();
        // Reflected light vector due to a specular surface.
        v.set(result.getNormal()).multiply(N_dot_L).multiply(2);
        v.subtract(rayToLight.getVector());

        // Vector from the intersection point to the eye (camera) position.
        final double R_dot_V = Math.max(0, v.dot(invertedRay.getVector()));
        final double phongSpecularTerm = Math.pow(R_dot_V, material.getPhong());
        Vector.putVector(v);

        // Uses Phong shading for materials with a specular property.
        final RGBTriple rgb = RGBTriple.getRGBTriple();
        pixel.add(rgb.set(colour).multiply(material.getSpecular()).multiply(phongSpecularTerm));
        pixel.add(rgb.set(colour).multiply(material.getDiffuse()).multiply(Math.max(0, N_dot_L)));
        RGBTriple.putRGBTriple(rgb);
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