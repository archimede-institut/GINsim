package org.ginsim.core.graph.view;

import java.awt.Rectangle;
import java.awt.Shape;

public enum NodeShape {

	RECTANGLE, ELLIPSE;
	
	public Shape getShape(int x, int y, int width, int height) {
		// FIXME: support more shapes
		return new Rectangle(x, y, width, height);
	}
}
