package glaytraser.engine;

import glaytraser.math.RGBTriple;

/**
 * 
 * @author G & S
 *
 * This class contains the Material properties of a primitive.
 */
public class Material {
    // Material properties
    RGBTriple m_ambient;
    RGBTriple m_diffuse;
    RGBTriple m_specular;

    public Material() {
    }

    public Material(RGBTriple ambient, RGBTriple diffuse, RGBTriple specular) {
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
