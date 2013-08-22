package org.ginsim.core.graph.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;


/**
 * extract from graph graphic info on a node.
 */
public interface NodeAttributesReader<V> extends AttributesReader {

	/**
	 * Get the default style for the nodes in this graph.
	 * @return the default node style.
	 */
	DefaultNodeStyle getDefaultNodeStyle();
	
    /**
     * change the edited node.
     * @param node the node to edit
     */
    void setNode(V node);

    /**
     * change the edited node, knowing that it is selected
     * @param node
     * @param selected
     */
    void setNode(V node, boolean selected); 

    /**
     * @return the horizontal position of the node.
     */
    int getX();
    /**
     * @return the vertical position of the node.
     */
    int getY();
    
    /**
     * @return the height of the node.
     */
    int getHeight();
    /**
     * @return the width of the node.
     */
    int getWidth();
    
    /**
     * @return the foreground (border) color of the node.
     */
    Color getForegroundColor();
    /**
     * change the foreground color of the node.
     * @param color the new color.
     */
    void setForegroundColor(Color color);
    /**
     * @return the text color of the node.
     */
    Color getTextColor();
    /**
     * change the text color of the node.
     * @param color the new color.
     */
    void setTextColor(Color color);
    /**
     * @return the background color of the node.
     */
    Color getBackgroundColor();
    /**
     * change the background color of the node.
     * @param color the new color.
     */
    void setBackgroundColor(Color color);

    /**
     * change the node's position.
     * @param x
     * @param y
     */
    void setPos(int x, int y);
    
    /**
     * change the node's size.
     * @param w
     * @param h
     */
    void setSize(int w, int h);
    
	/**
	 * change the kind of border for this node
	 * @param index
	 * @see #getBorderList()
	 */
	void setBorder(NodeBorder index);
	/**
	 * @return the border of the node.
	 */
	NodeBorder getBorder();
	/**
	 * @return the shape of the node
	 */
	NodeShape getShape();
	/**
	 * change the shape of the node.
	 * @param shapeIndex
	 * @see #getShapeList()
	 */
	void setShape(NodeShape shapeIndex);

    /**
     * @param fvreader
     */
    void copyFrom(NodeAttributesReader fvreader);

	/**
	 * Render the current node on a given graphics.
	 * 
	 * @param g
	 */
	void render(Graphics2D g);
	
	/**
	 * Render the current node while moving it.
	 * 
	 * @param g
	 * @param movex
	 * @param movey
	 */
	void renderMoving(Graphics2D g, int movex, int movey);
}
