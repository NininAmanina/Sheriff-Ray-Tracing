package glaytraser.math;

import java.util.ArrayList;

/**
 * 
 * @author G & S
 *
 */
public class Vector extends AbstractTriple<Vector> {

    private static final ArrayList<Vector> m_scratchVectorList = new ArrayList<Vector>();

    /**
     * Create a null Vector.
     */
    public Vector() {
    }

    /**
     * Create a new Vector as the difference between two Points (p2 - p1).
     *
     * @param p1 The first Point
     * @param p2 The second Point
     */
    public Vector(final Point p1, final Point p2) {
        set(p1, p2);
    }

    /**
     * Create a new Vector using another as a template.
     *
     * @param v The Vector which is being copied
     */
    public Vector(final Vector v) {
        super(v.value[0], v.value[1], v.value[2]);
    }

    public static final Vector getVector() {
        synchronized(m_scratchVectorList) {
            final int size = m_scratchVectorList.size();
            if(size > 0) {
                return m_scratchVectorList.remove(size - 1).set(0, 0, 0);
            } else {
                return new Vector();
            }
        }
    }

    public static final void putVector(final Vector v) {
        synchronized(m_scratchVectorList) {
            m_scratchVectorList.add(v);
        }
    }

    /**
     * Initilaise this Vector using another as a template.
     *
     * @param v The Vector which is being copied
     * @return A reference to this vector, for chaining purposes
     */
    public <U extends Vector> Vector set(final U v) {
        System.arraycopy(v.value, 0, value, 0, 4);
        return this;
    }

    /**
     * Return the dot product of this vector with an AbstractTriple.
     *
     * This may be done only between two Vectors or a Row and a Point.
     *
     * @param t The AbstractTriple with which we are taking the dot product
     * @return The calculated dot product
     * @throws IllegalArgumentException if this is not a Row but t is a Point
     */
    public double dot(final AbstractTriple<?> t) {
        if(t instanceof Point && !(this instanceof Row) && !(this instanceof Column)) {
            throw new IllegalArgumentException(
                "Cannot take the dot product of a Point with anything but a Row or Column!");
        }
        double sum = 0;
        for(int i = 0; i < 4; ++i) {
            sum += t.value[i] * value[i];
        }
        return sum;
    }

    /**
     * Returns the square of the magnitude of this vector.
     * @return
     */
    public double getSquareMagnitude() {
        return dot(this);
    }

    /**
     * Define this Vector to be the difference of two Points (p2 - p1).
     *
     * @param p1 The first Point
     * @param p2 The second Point
     * @return A reference to this vector, for chaining purposes
     */
    public <T extends Vector> T set(final Point p1, final Point p2) {
        return (T) set(p2.value[0] - p1.value[0],
                       p2.value[1] - p1.value[1],
                       p2.value[2] - p1.value[2]);
    }

    /**
     * Take the cross product of this Vector with another (this X v1)
     *
     * @param v1 The other Vector
     * @return The cross product, as a Normal
     */
    public Normal crossProduct(final Vector v1) {
        return crossProduct(v1, Normal.getNormal());
    }

    /**
     * Take the cross product of this Vector with another (this X v1)
     *
     * @param v1 The other Vector
     * @param normal The normal, the values of which shall be set
     * @return <code>normal</code>
     */
    public Normal crossProduct(final Vector v1, final Normal normal) {
        return Normal.getNormal().set(value[1] * v1.value[2] - value[2] * v1.value[1],
                                      value[2] * v1.value[0] - value[0] * v1.value[2],
                                      value[0] * v1.value[1] - value[1] * v1.value[0]);
    }

    /**
     * Multiply a vector by a scalar value.
     *
     * @param scalar is the multiplicative factor
     * @return A reference to this vector, for chaining purposes
     */
    public <T extends Vector> T multiply(final double scalar) {
        for(int i = 0; i < 3; ++i) {
            value[i] *= scalar;
        }
        return (T) this;
    }

    /**
     * Turn this Vector into a unit vector.
     *
     * @return The magnitude of the vector before normalisation
     */
    public double normalize() {
        // TODO: Make this numerically more stable later
        double sum = 0;
        for(int i = 0; i < 3; ++i) {
            sum += value[i]*value[i];
        }

        double length = Math.sqrt(sum);
        if(length < Utils.EPSILON) {
            throw new IllegalStateException("Cannot normalise a zero-length vector");
        }
        for(int i = 0; i < 3; ++i) {
            value[i] /= length;
        }

        return length;
    }
}
