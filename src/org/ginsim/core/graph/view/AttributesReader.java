package org.ginsim.core.graph.view;

import java.awt.Point;
import java.awt.Rectangle;

public interface AttributesReader {

	void refresh();
	
	/**
	 * get the boundary rectangle for the selected item.
	 * @return a rectangle representing the bounds
	 */
	Rectangle getBounds();
	
	
	/**
	 * Test if a point can be used to select the current item.
	 * 
	 * @param p
	 * @return true if the item should be selected
	 */
	boolean select(Point p);
}
