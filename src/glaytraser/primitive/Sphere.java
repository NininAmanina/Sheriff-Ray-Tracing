package glaytraser.primitive;

import glaytraser.math.Point;

public class Sphere extends AbstractQuadric {
    public Sphere(final Point p, final double radius) {
        super(p, 1 / radius, 0, 0, 1 / radius, 0, 1 / radius, 0, 0, 0, -radius);
//        super(p, 1 / (radius * radius), 0, 0, 1 / (radius * radius), 0, 1 / (radius * radius), 0, 0, 0, -1);
    }
}
