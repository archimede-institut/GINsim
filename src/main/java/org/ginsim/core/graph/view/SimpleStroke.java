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

	float width = 1;
	
	int cap = BasicStroke.CAP_SQUARE;
	int join = BasicStroke.JOIN_MITER;
	int miterlimit = 10;
	EdgePattern pattern = EdgePattern.SIMPLE;
	
	BasicStroke cachedStroke = null;
	BasicStroke cachedSimpleStroke = null;

	public void setWidth(float width) {
		if (width < 1) {
			width = 1;
		}
		
		if (this.width != width) {
			this.width = width;
			cachedStroke = null;
			cachedSimpleStroke = null;
		}
	}

	public void setDashPattern(EdgePattern pattern) {
		if (pattern == this.pattern) {
			return;
		}
		
		if (pattern == null) {
			if (this.pattern == EdgePattern.SIMPLE) {
				return;
			}
			this.pattern = EdgePattern.SIMPLE;
		} else {
			this.pattern = pattern;
		}
		cachedStroke = null;
	}
	
	@Override
	public Shape createStrokedShape(Shape s) {
        if (cachedStroke == null) {
        	cachedStroke = new BasicStroke(width, cap, join, miterlimit, pattern.getPattern(), 0);
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
