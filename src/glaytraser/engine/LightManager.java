package glaytraser.engine;

import glaytraser.math.Point;
import glaytraser.math.RGBTriple;
import glaytraser.math.Vector;

import java.util.ArrayList;

public class LightManager {
    // A collection of light sources defined for the scene.
    private static final ArrayList<Light> m_light = new ArrayList<Light>();
    private static final RGBTriple accumulatedColour = new RGBTriple();

    /**
     * This class represents a light object and its properties.
     */
    static abstract class Light {
        // Properties of the light source
        RGBTriple m_colour;
        
        // Scratch colour for calculations
        Vector m_scratchVector = new Vector();
        RGBTriple m_scratchRGB = new RGBTriple();

        abstract public void doLighting(Point intersectionPt, Result result, Node root, RGBTriple colour);

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
        public void doLighting(Point intersectionPt, Result result, Node root, RGBTriple colour) {
            colour.add(((RGBTriple) m_scratchRGB.set(m_colour)).multiply(result.getMaterial().getDiffuse()));
        }
    }

    /**
     * This class represents a light source with a position and its properties.
     */
    static class PointLight extends Light {
        // The position of the light source
        Point m_position;

        Result m_scratchResult = new Result();
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
        public void doLighting(Point intersectionPt, Result result, Node root, RGBTriple colour) {
            // A ray from the intersection point to the light source
            Ray rayToLight = new Ray();
            final double distanceToLight = rayToLight.getVector().set(intersectionPt, m_position).normalize();
            rayToLight.getPoint().set(intersectionPt);

            // If another object occludes this light source, then no need to 
            // do lighting calculations.
            m_scratchResult.init();
            root.rayIntersect(m_scratchResult, rayToLight, false);
            if(m_scratchResult.getT() < distanceToLight) {
                return;
            }

            // Do light calculations (Ref: CS488 course notes)
            // Calculate N = -L, where L is the vector from the intersection point
            // to the light source.
            m_scratchVector.set(rayToLight.getVector());

            final Material material = result.getMaterial();
            double N_dot_L = result.getNormal().dot(m_scratchVector);
            colour.add(((RGBTriple) m_scratchRGB.set(m_colour)).multiply(material.getDiffuse()).multiply(N_dot_L));
            // TODO: For specular colour, incorporate Material.getPhong() and scale the specular colour with it.
            // For now, it is effectively set to 1.
//            colour.add(((RGBTriple) m_scratchRGB.set(m_colour)).multiply(material.getSpecular()));
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
    public static int doLighting(Point intersectionPt, Result result, Node root) {
        accumulatedColour.set(0, 0, 0);
        // Loop through all the light sources, casting rays into the scene.
        for(Light light : m_light) {
            light.doLighting(intersectionPt, result, root, accumulatedColour);
        }

        return accumulatedColour.getInt();
    }

    public static void addPointLightSource(Point p, RGBTriple colour, int attenuation) {
        m_light.add(new PointLight(p, colour, attenuation));
    }

    public static void addAmbientLightSource(RGBTriple colour) {
        m_light.add(new AmbientLight(colour));
    }
}
