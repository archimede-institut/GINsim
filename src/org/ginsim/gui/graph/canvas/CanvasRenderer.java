package org.ginsim.gui.graph.canvas;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;

public interface CanvasRenderer extends CanvasEventManager {

	void render(Graphics2D g, Rectangle area);
	
	void overlay(Graphics2D g, Rectangle area);
	
	void select(Shape s);
	
	Object getObjectUnderPoint(Point p);
}
