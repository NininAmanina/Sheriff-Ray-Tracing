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
        row[0] = new Row(1, 0, 0, 0);
        row[1] = new Row(0, 1, 0, 0);
        row[2] = new Row(0, 0, 1, 0);
        row[3] = new Row(0, 0, 0, 1);
    }

    public Row getRow(int i) {
        return row[i];
    }
}
