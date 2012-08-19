package glaytraser.math;

/**
 * 
 * @author G & S
 *
 * This class represents a matrix for premultiplying vectors and points and post-multiplying Normals.
 */
public class Matrix {
    private Row [] row = new Row [4];
    
    public Matrix() {
    }
    
    public Row getRow(int i) {
        return row[i];
    }
}
