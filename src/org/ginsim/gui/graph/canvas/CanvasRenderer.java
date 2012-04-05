package org.ginsim.gui.graph.canvas;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

public interface CanvasRenderer {

	void render(Graphics2D g, Rectangle area);
	
	Object select(Point p);
}
