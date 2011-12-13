package org.ginsim.core.graph.view;

import java.awt.Color;
import java.awt.Point;
import java.util.List;


/**
 * graphic info on an edge.
 * this extension of the GsGraphManager is used to get or change visual info on edges objects.
 * It can also change default value (but they aren't persistant yet).
 */
public interface EdgeAttributesReader extends AttributesReader {

    /**  */
    public static final int NBSTYLE = 2;
    /**  */
    public static final int STYLE_CURVE = 0;
    /**  */
    public static final int STYLE_STRAIGHT = 1;
    
    /**  */
    public static final int NBROUTING = 2;
    /**  */
    public static final int ROUTING_NONE = 0;
    /**  */
    public static final int ROUTING_AUTO = 1;
    /**  */
    public static final int ARROW_POSITIVE = 0;
    /**  */
    public static final int ARROW_NEGATIVE = 1;
    /**  */
    public static final int ARROW_UNKNOWN = 2;
    /**  */
    public static final int ARROW_DOUBLE = 20;


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
     * @param selectedIndex
     */
    void setDefaultStyle(int selectedIndex);
	
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
    int getDefaultStyle();
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
	void setEdge(Object obj);
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
	 * @return the list of available routing.
	 */
	List<String> getRoutingList();
	
	/**
	 * @return the list of available draw style
	 */
	List<String> getStyleList();
	
	/**
	 * @return the list of available draw style
	 */
	List<String> getPatternList();
	
	/**
	 * @return the routing of this edge.
	 */
	int getRouting();
	/**
	 * @return the style of this edge.
	 */
	int getStyle();
	/**
	 * change the routing of this edge.
	 * @param index index of the routing in the list.
	 * @see #getRoutingList()
	 */
	void setRouting(int index);
	/**
	 * change the style of this edge.
	 * @param index index of the style in the list.
	 * @see #getStyleList()
	 */
	void setStyle(int index);
	
	/**
	 * set the end of the line
	 * @param index
	 */
	void setLineEnd(int index);
	
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
    int getLineEnd();
    
    /**
     * set the edge as dashed.
     * @param dashID
     */
    void setDash(String dashID);
    
    /**
     * set the edge as dashed.
     * @return the dash motif
     */
    String getDashID();
    
	float[] getPattern(int i);
	
	float[] getDash();
	
	void setDash(float[] dashArray);
}
