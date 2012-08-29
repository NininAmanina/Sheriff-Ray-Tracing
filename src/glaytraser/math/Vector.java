package glaytraser.math;

/**
 * 
 * @author G & S
 *
 */
public class Vector extends AbstractTriple {
    /**
     * Create a null Vector.
     */
    public Vector() {
    }

    /**
     * Create a new Vector, initalised with x, y, and z components.
     *
     * @param x
     * @param y
     * @param z
     */
    public Vector(double x, double y, double z) {
        super(x, y, z);
    }

    /**
     * Create a new Vector as the difference between two Points (p2 - p1).
     *
     * @param p1 The first Point
     * @param p2 The second Point
     */
    public Vector(Point p1, Point p2) {
        set(p1, p2);
    }

    /**
     * Create a new Vector using another as a template.
     *
     * @param v The Vector which is being copied
     */
    public Vector(Vector v) {
        super(v.value[0], v.value[1], v.value[2]);
    }

    /**
     * Initilaise this Vector using another as a template.
     *
     * @param v The Vector which is being copied
     * @return A reference to this vector, for chaining purposes
     */
    public Vector set(Vector v) {
        for(int i = 0; i < 3; ++i) {
            value[i] = v.value[i];
        }
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
    public double dot(AbstractTriple t) {
        if(t instanceof Point && !(this instanceof Row)) {
            throw new IllegalArgumentException(
                "Cannot take the dot product of a Point with anything but a Row!");
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
    public Vector set(Point p1, Point p2) {
        set(p2.value[0] - p1.value[0],
            p2.value[1] - p1.value[1],
            p2.value[2] - p1.value[2]);
        return this;
    }

    /**
     * Take the cross product of this Vector with another (this X v1)
     *
     * @param v1 The other vector
     * @return The cross product, as a vector
     */
    public Vector crossProduct(Vector v1) {
        return new Vector(value[1] * v1.value[2] - value[2] * v1.value[1], 
                          value[2] * v1.value[0] - value[0] * v1.value[2],
                          value[0] * v1.value[1] - value[1] * v1.value[0]);
    }

    /**
     * Multiply a vector by a scalar value.
     *
     * @param scalar is the multiplicative factor
     * @return A reference to this vector, for chaining purposes
     */
    public Vector multiply(double scalar) {
        for(int i = 0; i < 3; ++i) {
            value[i] *= scalar;
        }
        return this;
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
        if(Math.abs(length) < Utils.EPSILON) {
            throw new IllegalStateException("Cannot normalise a zero-length vector");
        }
        if(length > 0) {
            for(int i = 0; i < 3; ++i) {
                value[i] /= length;
            }
        }

        return length;
    }
}
