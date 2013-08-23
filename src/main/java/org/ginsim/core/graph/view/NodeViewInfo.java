package org.ginsim.core.graph.view;


/**
 * Store for basic view information on nodes.
 * This interface defines the mandatory information on nodes.
 * Each node must have a position (x,y) and a style (can be null).
 * 
 * @author Aurelien Naldi
 */
public interface NodeViewInfo {

	/**
	 * Get the position of the node on the x axis
	 * @return the position on the x axis
	 */
	int getX();
	
	/**
	 * Get the position of the node on the y axis
	 * @return the position on the y axis
	 */
	int getY();
	
	/**
	 * Get the style used to draw this node.
	 * If
	 * @return the style for this node, or null to use the default.
	 */
	SimpleNodeStyle getStyle();

	/**
	 * Set the style to use for this node.
	 * 
	 * @param style the new style
	 * @return true if the style was updated
	 */
	boolean setStyle(SimpleNodeStyle style);
	
	/**
	 * Set the position of this node.
	 * Note that negative positions are not accepted.
	 * 
	 * @param x the new x value
	 * @param y the new y value
	 * 
	 * @return true if the position was updated.
	 */
	boolean setPosition(int x, int y);
}
