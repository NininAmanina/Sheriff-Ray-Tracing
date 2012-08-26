package glaytraser.engine;

import glaytraser.math.Point;
import glaytraser.math.RGBTriple;
import glaytraser.math.Vector;

import java.util.ArrayList;

public class LightManager {
    // A collection of light sources defined for the scene.
    static ArrayList<Light> m_light = new ArrayList<Light>();

    /**
     * This class represents a light object and its properties.
     */
    abstract class Light {
        // Properties of the light source
        RGBTriple m_ambient;
        RGBTriple m_diffuse;
        RGBTriple m_specular;

        public Light() {
        }

        abstract public void doLighting(Point intersectionPt, Result result, Node root, 
                Material material, RGBTriple colour);

        public Light(RGBTriple ambient, RGBTriple diffuse, RGBTriple specular) {
            m_ambient = ambient;
            m_diffuse = diffuse;
            m_specular = specular;
        }

        public RGBTriple getAmbient() {
            return m_ambient;
        }

        public RGBTriple getDiffuse() {
            return m_diffuse;
        }

        public RGBTriple getSpecular() {
            return m_specular;
        }
    }

    /**
     * This class represents an ambient light object and its properties.
     */
    class AmbientLight extends Light {
        public AmbientLight(RGBTriple ambient) {
            super(ambient, null, null);
        }

        @Override
        public void doLighting(Point intersectionPt, Result result, Node root,
                Material material, RGBTriple colour) {
            colour =  m_ambient.multiply(material.getAmbient());
        }
    }

    /**
     * This class represents a light source with a position and its properties.
     */
    class PointLight extends Light {
        private Vector m_scratchVector;

        // The position of the light source
        Point m_position;

        public PointLight(Point p) {
            super(null, null, null);
            m_position = p;
        }

        public PointLight(Point p, RGBTriple diffuse, RGBTriple specular) {
            super(null, diffuse, specular);
            m_position = p;
        }


        // Returns the point position of the light source
        public Point getPosition() {
            return m_position;
        }

        @Override
        public void doLighting(Point intersectionPt, Result result, Node root, 
                Material material, RGBTriple colour) {
            // A ray from the intersection point to the light source
            Ray rayToLight = new Ray();
            rayToLight.getVector().set(intersectionPt, m_position);
            rayToLight.getPoint().set(intersectionPt);

            // If the object does not intersect with a light source, then no need to 
            // do lighting calculations.
            if(root.rayIntersect(null, rayToLight, false)) {
                return;
            }

            // Do light calculations (Ref: CS488 course notes)
            // Calculate N = -L, where L is the vector from the intersection point
            // to the light source.
            m_scratchVector = rayToLight.getVector();
            m_scratchVector.multiply(-1);

            double N_dot_L = result.getNormal().dot(m_scratchVector);
            RGBTriple diffuseColour = m_diffuse.multiply(material.getDiffuse()).multiply(N_dot_L);
            // TODO: For specular colour, determine the Phong Factor and scale the specular colour with it.
            // For now, it is effectively set to 1.
            RGBTriple specularColour = m_specular.multiply(material.getSpecular());

            // Set the color of the pixel
            colour.set(diffuseColour.add(specularColour));
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
    public static int doLighting(Point intersectionPt, Result result, Node root, 
            Material material) {
        // Loop through all the light sources, casting rays into the scene.
        RGBTriple accumulatedColour = new RGBTriple(0, 0, 0);
        for(Light light : m_light) {
            RGBTriple colourFromLight = new RGBTriple(0, 0, 0);
            light.doLighting(intersectionPt, result, root, material, colourFromLight);
            accumulatedColour.add(colourFromLight);
        }

        return accumulatedColour.getInt();
    }

    public void addLightSource(Light light) {
        m_light.add(light);
    }
}
