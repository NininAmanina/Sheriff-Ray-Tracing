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
    private RGBTriple m_diffuse;
    private RGBTriple m_specular;
    private double m_phong;

    public Material(RGBTriple diffuse, RGBTriple specular, double phong) {
        m_diffuse = diffuse;
        m_specular = specular;
        m_phong = phong;
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
