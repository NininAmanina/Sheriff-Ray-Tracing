package glaytraser.primitive;

import glaytraser.engine.Node;
import glaytraser.math.Point;

public class Sphere extends Node {
    private double m_radius;
    private Point m_center;
    
    public Sphere(Point p, double radius) {
        super(p);
        this.m_radius = radius; 
    }

    public Point getCenter() {
        return m_center;
    }

    public void setCenter(Point center) {
        this.m_center = center;
    }

    public double getRadius() {
        return m_radius;
    }

    public void setRadius(double radius) {
        this.m_radius = radius;
    }
}
