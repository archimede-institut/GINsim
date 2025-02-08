package org.ginsim.gui.graph.canvas;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

public interface CanvasEventManager {

	void click(Point p, boolean alternate);

	void pressed(Point p, boolean alternate);
	
	void released(Point p);
	
	void dragged(Point p);
	
	void cancel();
	
	void overlay(Graphics2D g, Rectangle area);

	/**
	 * help function
	 * @param g  the graph
	 * @param area  the area
	 */
	void helpOverlay(Graphics2D g, Rectangle area);
}
