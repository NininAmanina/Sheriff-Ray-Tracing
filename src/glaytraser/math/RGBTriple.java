package glaytraser.math;

/**
 * This class represents color expressed in RGB coordinates.
 */
public class RGBTriple extends AbstractTriple {
    /**
     * Constructor for RGBTriple
     * @param r is the red component
     * @param g is the green component
     * @param b is the blue component
     */
    public RGBTriple(double r, double g, double b) {
        super(r, g, b);
    }

    /**
     * Defines an addition operation on two colours.
     * @param rgb is the colour to be added.
     * @return The sum of the two colours.
     */
    public RGBTriple add(RGBTriple rgb) {
        for(int i = 0; i < 3; ++i) {
            value[i] += rgb.value[i];
        }
        return this;
    }

    /**
     * Component-wise multiplication operation for colours.
     * @param rgb is the other colour with which we "mixed" together this colour.
     * @return The colour resulting from the multiplication.
     */
    public RGBTriple multiply(RGBTriple rgb){
        for(int i=0; i<3; i++) {
            value[i] *= rgb.value[i];
        }
        return this;
    }

    /**
     * Multiply a RGBTriple by a scalar value.
     *
     * @param scalar is the multiplicative factor
     * @return A reference to this RGBTriple, for chaining purposes
     */
    public RGBTriple multiply(double scalar) {
        for(int i = 0; i < 3; ++i) {
            value[i] *= scalar;
        }
        return this;
    }

    /**
     * Returns the RGB color as an integer
     */
    public int getInt() {
        return 255 << 24 | (int) (value[0] * 255) << 16 
                | (int) (value[1] * 255) << 8 
                | (int) (value[2] * 255);
    }
}
