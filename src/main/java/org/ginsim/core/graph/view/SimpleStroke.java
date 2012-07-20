package org.ginsim.core.graph.view;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Stroke;

/**
 * Simple stroke implementation, inspired by BasicStroke
 * but for which we can change the width and dash pattern.
 * 
 * @author Aurelien Naldi
 */
public class SimpleStroke implements Stroke {

	private final float[] DASHPATTERN = {4, 2};
	
	float width = 1;
	float[] dash = null;
	
	int cap = BasicStroke.CAP_SQUARE;
	int join = BasicStroke.JOIN_MITER;
	int miterlimit = 10;
	int dash_phase = 0;
	
	BasicStroke cachedStroke = null;
	BasicStroke cachedSimpleStroke = null;

	public void setWidth(float width) {
		if (width < 1) {
			width = 1;
		} else if (this.width != width) {
			this.width = width;
			cachedStroke = null;
			cachedSimpleStroke = null;
		}
	}

	public void setDashed(boolean dashed) {
		if (dashed) {
			this.dash = DASHPATTERN;
		} else {
			this.dash = null;
			cachedStroke = null;
			cachedSimpleStroke = null;
		}
	}
	
	@Override
	public Shape createStrokedShape(Shape s) {
        if (cachedStroke == null) {
        	cachedStroke = new BasicStroke(width, cap, join, miterlimit, dash, dash_phase);
        }
        return cachedStroke.createStrokedShape(s);
	}

	public Shape createSimpleStrokedShape(Shape s) {
        if (cachedSimpleStroke == null) {
        	cachedSimpleStroke = new BasicStroke(width, cap, join, miterlimit);
        }
        return cachedSimpleStroke.createStrokedShape(s);
	}

}
