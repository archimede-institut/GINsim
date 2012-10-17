package org.ginsim.gui.graph.canvas;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;

/**
 * Define methods to integrate a view into a VirtualScrollPane.
 * This interface enables collaboration between the scrollpane and the view. 
 * 
 * @author Aurelien Naldi
 */
public interface VirtualScrollable {

	/**
	 * Get the component for this view.
	 * 
	 * @return a component to add in the scrollPane
	 */
	Component getComponent();

	/**
	 * Associate the view with a scrollPane. The view will then fire events upon view changes.
	 * @param virtualScrollPane
	 */
	void setScrollPane(VirtualScrollPane virtualScrollPane);
	
	/**
	 * Get the full virtual size of this widget.
	 * This is used by the scrollpane to define the ranges of the scrollbars.
	 * 
	 * @return the Dimension of the space in which the visibleArea can move
	 */
	Dimension getVirtualDimension();

	/**
	 * Get the part of the virtual size that is currently visible.
	 * This allows the scrollpane to define the positions of the scrollbars.
	 * 
	 * @return a rectangle providing the bounds of the visible area.
	 */
	Rectangle getVisibleArea();

	/**
	 * Scroll this view to a specified position.
	 * 
	 * @param x
	 * @param y
	 */
	void setScrollPosition(int x, int y);
	
}
