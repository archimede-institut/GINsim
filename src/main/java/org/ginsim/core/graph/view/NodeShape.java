package org.ginsim.core.graph.view;

import org.ginsim.core.graph.view.shapes.SVGEllipse;
import org.ginsim.core.graph.view.shapes.SVGRectangle;
import org.ginsim.service.export.image.SVGWriter;

import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.io.IOException;

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
