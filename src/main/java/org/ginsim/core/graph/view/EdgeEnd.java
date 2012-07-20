package org.ginsim.core.graph.view;

import java.awt.Polygon;
import java.awt.Shape;

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
    	// TODO: real unknown shape
    	return getArrow();
    }

    
    private static Shape getDual() {
    	Shape a = getArrow();
    	Shape b = getT();

    	// TODO: merge shapes?
    	
    	return a;
    }

}
