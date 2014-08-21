package org.ginsim.core.graph.view;

import org.ginsim.core.graph.view.shapes.SVGEllipse;
import org.ginsim.core.graph.view.shapes.SVGRectangle;

public enum NodeShape {

	RECTANGLE, ELLIPSE;
	
	public SVGShape getShape(int x, int y, int width, int height) {
		
		switch (this) {
		case ELLIPSE:
			return new SVGEllipse(x, y, width, height);
		}
		return new SVGRectangle(x, y, width, height);
	}
}
