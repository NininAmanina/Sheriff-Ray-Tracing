package glaytraser.math;

import glaytraser.math.AbstractTriple;

public class Row extends AbstractTriple {
    Row() {
    }

    public void set(int i, double d) {
        if(i < 0 || i > 3) {
            throw new IllegalArgumentException("index " + i + "is out of bounds");
        }
        value[i] = d;
    }
    
    public void set(double d0, double d1, double d2, double d3) {
        value[0] = d0;
        value[1] = d1;
        value[2] = d2;
        value[3] = d3;
    }
}
