package org.ginsim.core.graph.view;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

public enum NodeShape {

	RECTANGLE, ELLIPSE;
	
	public Shape getShape(int x, int y, int width, int height) {
		
		switch (this) {
		case ELLIPSE:
			return new Ellipse2D.Double(x, y, width, height);
		}
		return new Rectangle(x, y, width, height);
	}
}
