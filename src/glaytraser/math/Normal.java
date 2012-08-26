package glaytraser.math;

/**
 * 
 * @author G & S
 * TODO: Implement
 */
public class Normal extends Vector {
    // Initialize the normal to be the unit vector so that if the surface normal is 
    // undefined, the lighting can still be partially calculated.
    public Normal() {
        super(1, 1, 1);
    }
}
