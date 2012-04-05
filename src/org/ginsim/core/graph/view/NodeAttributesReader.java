package org.ginsim.core.graph.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;


/**
 * extract from graph graphic info on a node.
 */
public interface NodeAttributesReader extends AttributesReader {

    /**
     * change the edited node.
     * @param node the node to edit
     */
    void setNode(Object node);

    /**
     * change the edited node, knowing that it is selected
     * @param node
     * @param selected
     */
    void setNode(Object node, boolean selected); 

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
    
//    Rectangle getBounds();
    
    /**
     * set the default background color for vertices.
     * @param color
     */
	void setDefaultNodeBackground(Color color);
    /**
     * set the default foreground color for vertices.
     * @param color
     */
	void setDefaultNodeForeground(Color color);
    /**
     * set the default kind of border for vertices.
     * @param index
     */
	void setDefaultNodeBorder(NodeBorder index);
	/**
	 * set the default size for vertices.
	 * @param w
	 * @param h
	 */
	void setDefaultNodeSize(int w, int h);
	/**
	 * set the default shape for vertices.
	 * @param shape
	 */
	void setDefaultNodeShape(NodeShape shape);
    /**
     * @return the default background color for vertices.
     */
	Color getDefaultNodeBackground();
    /**
     * @return the default foreground color for vertices.
     */
	Color getDefaultNodeForeground();
    /**
     * @return the default kind of border for vertices.
     */
	NodeBorder getDefaultNodeBorder();
	/**
	 * @return the default width for vertices.
	 */
	int getDefaultNodeWidth();
	/**
	 * @return the default height for vertices.
	 */
	int getDefaultNodeHeight();
	/**
	 * @return the default shape for vertices.
	 */
	NodeShape getDefaultNodeShape();

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
     * Set the position and size at once
     * 
     * @param bounds the new bounds
     * @return the old bounds
     */
	Rectangle setBounds(Rectangle bounds);

	/**
	 * Render the current node on a given graphics.
	 * 
	 * @param g
	 */
	void render(Graphics2D g);
}
