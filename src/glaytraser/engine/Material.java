package glaytraser.engine;

import java.util.HashMap;

import glaytraser.math.RGBTriple;

/**
 * 
 * @author G & S
 *
 * This class contains the Material properties of a primitive.
 */
class Material {
    // A place to store all of the Material objects
    private static final HashMap<String, Material> m_materialMap = new HashMap<String, Material>();

    // Material properties
    private RGBTriple m_diffuse;
    private RGBTriple m_specular;
    private double m_phong;

    private Material(final RGBTriple diffuse, final RGBTriple specular, final double phong) {
        m_diffuse = diffuse;
        m_specular = specular;
        m_phong = phong;
    }

    static final Material addMaterial(
        final String name,
        final RGBTriple diffuse,
        final RGBTriple specular,
        final double phong) {
        synchronized(m_materialMap) {
            if(m_materialMap.containsKey(name)) {
                throw new IllegalStateException("Already have material named \"" + name + "\"; please choose another name");
            }
            Material m = new Material(diffuse, specular, phong);
            m_materialMap.put(name,  m);
            return m;
        }
    }

    public static final Material getMaterial(final String name) {
        if(!m_materialMap.containsKey(name)) {
            throw new IllegalArgumentException("No material with name \"" + name);
        }
        return m_materialMap.get(name);
    }

    public double getPhong() {
        return m_phong;
    }

    public RGBTriple getDiffuse() {
        return m_diffuse;
    }

    public RGBTriple getSpecular() {
        return m_specular;
    }
}
