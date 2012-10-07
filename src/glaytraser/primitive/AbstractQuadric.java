package glaytraser.primitive;

import glaytraser.engine.Node;
import glaytraser.engine.Ray;
import glaytraser.engine.Result;
import glaytraser.math.Normal;
import glaytraser.math.Point;
import glaytraser.math.Utils;
import glaytraser.math.Vector;

public abstract class AbstractQuadric extends Node {
    private Point m_centre;
    private double m_A, m_B, m_C, m_D, m_E, m_F, m_G, m_H, m_I, m_J;

    /**
     * Generic quadric surface: Ax^2 + Bxy + Cxz + Dy^2 + Eyz + Fz^2 + Gx + Hy + Iz + J = 0
     * @param p
     * @param A
     * @param B
     * @param C
     * @param D
     * @param E
     * @param F
     * @param G
     * @param H
     * @param I
     * @param J
     */
    public AbstractQuadric(final Point p,
                           final double A,
                           final double B,
                           final double C,
                           final double D,
                           final double E,
                           final double F,
                           final double G,
                           final double H,
                           final double I,
                           final double J) {
        m_centre = p;
        m_A = A;
        m_B = B;
        m_C = C;
        m_D = D;
        m_E = E;
        m_F = F;
        m_G = G;
        m_H = H;
        m_I = I;
        m_J = J;
    }

    // This must be overridden by primitives.
    // @result We expect null for the light-source intersection routine
    public final boolean rayIntersect(Result result, Ray ray, final boolean calcNormal) {
        /*
         * m_A * pc_x * pc_x + m_A * 2 * v_x * pc_x * t + m_A * v_x * v_x * t^2 +
         * m_B * pc_x * pc_y + m_B * (v_x * pc_y + v_y * pc_x) * t + m_B * v_x * v_y * t^2 +
         * m_C * pc_x * pc_z + m_C * (v_x * pc_z + v_z * pc_x) * t + m_C * v_x * v_z * t^2 +
         * m_D * pc_y * pc_y + m_D * 2 * v_y * pc_y * t + m_D * v_y * v_y * t^2 +
         * m_E * pc_y * pc_z + m_E * (v_y * pc_z + v_z * pc_y) * t + m_E * v_y * v_z * t^2 +
         * m_F * pc_z * pc_z + m_F * 2 * v_z * pc_z * t + m_F * v_z * v_z * t^2 +
         * m_G * pc_x + m_G * v_x * t +
         * m_H * pc_y + m_H * v_y * t +
         * m_I * pc_z + m_I * v_z * t +
         * m_J
         */
        final Vector vec = Vector.getVector();
        final Point pt = Point.getPoint();
        try {
            vec.set(m_centre, ray.getPoint());
            final double pc_x = vec.get(0);
            final double pc_y = vec.get(1);
            final double pc_z = vec.get(2);
            final Vector v = ray.getVector();
            final double v_x = v.get(0);
            final double v_y = v.get(1);
            final double v_z = v.get(2);
            final double a = m_A * v_x * v_x + m_B * v_x * v_y + m_C * v_x * v_z + m_D * v_y * v_y + m_E * v_y * v_z + m_F * v_z * v_z;
            final double b = 0.5 * (m_A * 2 * v_x * pc_x  + m_B * (v_x * pc_y + v_y * pc_x) + m_C * (v_x * pc_z + v_z * pc_x) +
                             m_D * 2 * v_y * pc_y + m_E * (v_y * pc_z + v_z * pc_y) + m_F * 2 * v_z * pc_z +
                             m_G * v_x + m_H * v_y + m_I * v_z);
            final double c = m_A * pc_x * pc_x + m_B * pc_x * pc_y + m_C * pc_x * pc_z + m_D * pc_y * pc_y + m_E * pc_y * pc_z +
                             m_F * pc_z * pc_z + m_G * pc_x + m_H * pc_y + m_I * pc_z + m_J;
            final double [] tArray = Utils.quadraticRoot(a, b, c);
            if(tArray == null) {
                return false;
            }

            double t;
            if(tArray[0] < Utils.EPSILON) {
                t = tArray[1];
            } else if(tArray[1] < Utils.EPSILON) {
                t = tArray[0];
            } else {
                t = Math.min(tArray[0], tArray[1]);
            }
            if(t > Utils.EPSILON && t < result.getT()) {
                result.setT(t);
                if(calcNormal) {
                    pt.set(ray.getPoint()).add(
                            vec.set(ray.getVector()).multiply(t));
                    computeQuadricNormal(result.getNormal(), vec.set(m_centre, pt));

                    // Set the material property for the primitive
                    result.setMaterial(getMaterial());
                }
            }
            return true;
        }  finally {
            Vector.putVector(vec);
            Point.putPoint(pt);
        }
    }

    /**
     * Compute the normal to Ax^2 + Bxy + Cxz + Dy^2 + Eyz + Fz^2 + Gx + Hy + Iz + J = 0
     * at v (a hack 'til translations are checked-in).
     */
    public final void computeQuadricNormal(final Normal n, final Vector v) {
        final double x = v.get(0);
        final double y = v.get(1);
        final double z = v.get(2);
        n.set(2 * m_A * x + m_B * y + m_C * z + m_G,
              m_B * x + 2 * m_D * y + m_E * z + m_H,
              m_C * x + m_E * y + 2 * m_F * z + m_I);
    }
    
    public final Point getCentre() {
        return m_centre;
    }

    public final void setCentre(final Point centre) {
        m_centre = centre;
    }
}
