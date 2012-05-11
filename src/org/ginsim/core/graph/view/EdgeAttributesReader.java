package org.ginsim.core.graph.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;

import org.ginsim.core.graph.common.Edge;


/**
 * graphic info on an edge.
 * this extension of the GsGraphManager is used to get or change visual info on edges objects.
 * It can also change default value (but they aren't persistant yet).
 */
public interface EdgeAttributesReader extends AttributesReader {

    /**
     * set the default color for edges
     * @param color
     */
	void setDefaultEdgeColor(Color color);
	/**
	 * set the default width for edges
	 * @param s
	 */
	void setDefaultEdgeSize(float s);
	/**
	 * change default for filling edge's end. 
	 * @param b if true, edges end will be filled
	 */
	void setDefaultEdgeEndFill(boolean b);
    /**
     * Change the default curved flag
     * @param b
     */
    void setDefaultCurve(boolean b);
	
	/**
	 * @return the default edge color
	 */
	Color getDefaultEdgeColor();
	/**
	 * @return the default edge size
	 */
	float getDefaultEdgeSize();
	/**
	 * @return if the default end is filled
	 */
	boolean getDefaultEdgeEndFill();
	/**
	 * @return the default line style
	 *
	 */
    boolean getDefaultCurve();
	/**
	 * @return the width of this edge
	 */
	float getLineWidth();
	/**
	 * change the width of this edge
	 * @param w
	 */
	void setLineWidth(float w);
	/**
	 * set the edge on which we work
	 * @param obj
	 */
	void setEdge(Edge<?> obj);

	/**
	 * Set the edge on which we work, knowing that it is selected.
	 * 
	 * @param obj
	 * @param selected
	 */
	void setEdge(Edge<?> obj, boolean selected);
	
	/**
	 * change this edge's line color.
	 * @param color the new color.
	 */
	void setLineColor(Color color);
	/**
	 * @return the color of this edge
	 */
	Color getLineColor();

	/**
	 * Is this edge curve?
	 * This only affects edges with intermediate points.
	 * 
	 * @return true if the edge is curve
	 */
	boolean isCurve();

	/**
	 * Set the edge as curve property.
	 * 
	 * @param curve
	 */
	void setCurve(boolean curve);
	
	/**
	 * set the end of the line
	 * @param index
	 */
	void setLineEnd(EdgeEnd index);
	
	/**
	 * @return the list of points used by this edge.
	 */
	List<Point> getPoints();

    /**
     * set points used by this edge
     * @param l
     */
    void setPoints(List l);
    /**
     * @param fereader
     */
    void copyFrom(EdgeAttributesReader fereader);

     /**
     * @param fvreader
     */
    void copyDefaultFrom(EdgeAttributesReader fvreader);
    
    /**
     * @return the lineEnd
     */
    EdgeEnd getLineEnd();
    
    /**
     * set the edge as dashed.
     * @param dashID
     */
    void setDash(EdgePattern dashID);
    
    /**
     * set the edge as dashed.
     * @return the dash motif
     */
    EdgePattern getDash();
    
    /**
     * Render an edge on a given graphics.
     * 
     * @param g
     */
	void render(Graphics2D g);

	/**
	 * Render an edge while the edge or at least one of its connected node is moved.
	 * 
	 * @param g
	 * @param type
	 * @param movex
	 * @param movey
	 */
	void renderMoving(Graphics2D g, MovingEdgeType type, int movex, int movey);
	
	void renderMovingPoint(Graphics2D g, int idx, int movex, int movey);

}
