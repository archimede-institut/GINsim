package org.ginsim.core.graph.view;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

public enum EdgeEnd {

    POSITIVE(getArrow()),
    NEGATIVE(getT()),
    UNKNOWN(getUnknown()),
    DUAL(getDual());

    private final Shape shape;
    
    private EdgeEnd(Shape shape) {
		this.shape = shape;
	}
    
    public Shape getShape() {
    	return shape;
    }
    
    
    private static Shape getArrow() {
    	Polygon poly = new Polygon();
    	poly.addPoint(-4,-5);
    	poly.addPoint(-2, 0);
    	poly.addPoint(-4, 5);
    	poly.addPoint( 2, 0);
    	return poly;
    }
    
    private static Shape getT() {
    	Polygon poly = new Polygon();
    	poly.addPoint(-1, -4);
    	poly.addPoint(-1, 4);
    	poly.addPoint(1, 4);
    	poly.addPoint(1, -4);
    	return poly;
    }

    private static Shape getUnknown() {
    	return new Ellipse2D.Double(-5, -4, 8, 8);
    }

    
    private static Shape getDual() {
    	Polygon poly = new Polygon();
    	poly.addPoint(-6,-5);
    	poly.addPoint(-6, 5);
    	poly.addPoint(-5, 5);
    	poly.addPoint(-5, 0);
    	poly.addPoint(-2, 0);
    	poly.addPoint(-4, 5);
    	poly.addPoint( 2, 0);
    	poly.addPoint(-4,-5);
    	poly.addPoint(-2, 0);
    	poly.addPoint(-5, 0);
    	poly.addPoint(-5,-5);
    	return poly;
    }

}
