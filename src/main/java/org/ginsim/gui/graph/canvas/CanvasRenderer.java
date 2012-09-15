package org.ginsim.gui.graph.canvas;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;

/**
 * Generic interface for Canvas renderer: all drawing and object selection happens here.
 * 
 * @author Aurelien Naldi
 */
public interface CanvasRenderer extends CanvasEventManager {

	/**
	 * Draw on the canvas.
	 * The provided area can be the full visible part of the canvas or a damaged region.
	 * Zooming, clipping... is performed by the canvas before calling this method.
	 * 
	 * @param g the clipped graphics object
	 * @param area the area to redraw
	 */
	void render(Graphics2D g, Rectangle area);

	/**
	 * Draw temporary information on top of the canvas.
	 * It can be used to represent moving objects or other temporary information.
	 * 
	 * @param g the clipped graphics object
	 * @param area the area to redraw
	 */
	void overlay(Graphics2D g, Rectangle area);
	
	/**
	 * Select all objects inside the provided shape (usually a simple selection rectangle).
	 * 
	 * @param s
	 */
	void select(Shape s);
	
	/**
	 * Find the object under the provided point.
	 * 
	 * @param p
	 * @return a found object at this position, or null if none.
	 */
	Object getObjectUnderPoint(Point p);
	
	/**
	 * Get the (virtual) size of the area needed to paint all objects in this renderer.
	 * 
	 * @return the (virtual) size of the rendered canvas.
	 */
	Dimension getBounds();
}
