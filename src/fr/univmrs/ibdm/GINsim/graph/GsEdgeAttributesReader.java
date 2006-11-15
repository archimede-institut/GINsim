package fr.univmrs.ibdm.GINsim.graph;

import java.awt.Color;
import java.util.List;
import java.util.Vector;

import fr.univmrs.ibdm.GINsim.global.GsOptions;
import fr.univmrs.ibdm.GINsim.manageressources.Translator;

/**
 * graphic info on an edge.
 * this extension of the GsGraphManager is used to get or change visual info on edges objects.
 * It can also change default value (but they aren't persistant yet).
 */
public abstract class GsEdgeAttributesReader {

    protected static Color color = new Color(((Integer)GsOptions.getOption("vs.edgecolor", new Integer(-13395457))).intValue());
    // we don't want to remember other options for now
    
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

    /** simple dash pattern */
	public static float[] DASH_PATTERN = new float[] {10, 4, 3, 5};
	

    
    protected static Vector v_style = null;
	protected static Vector v_routing = null;
	
    static {
        v_style = new Vector();
    	    v_style.add(Translator.getString("STR_styleCurve"));
        v_style.add(Translator.getString("STR_styleStraight"));
    	    v_routing = new Vector();
    	    v_routing.add(Translator.getString("STR_routingNone"));
        v_routing.add(Translator.getString("STR_routingAuto"));
    }

    /**
     * remember default values
     */
    public static void saveOptions() {
        GsOptions.setOption("vs.edgecolor", new Integer(color.getRGB()));
    }
    
    protected float defaultLineWidth;
    
    /**
     * set the default color for edges
     * @param color
     */
	abstract public void setDefaultEdgeColor(Color color);
	/**
	 * set the default width for edges
	 * @param s
	 */
	abstract public void setDefaultEdgeSize(float s);
	/**
	 * change default for filling edge's end. 
	 * @param b if true, edges end will be filled
	 */
	abstract public void setDefaultEdgeEndFill(boolean b);
    /**
     * @param selectedIndex
     */
    public abstract void setDefaultStyle(int selectedIndex);
	
	/**
	 * @return the default edge color
	 */
	public Color getDefaultEdgeColor() {
		return color;
	}
	/**
	 * @return the default edge size
	 */
	abstract public float getDefaultEdgeSize();
	/**
	 * @return if the default end is filled
	 */
	abstract public boolean getDefaultEdgeEndFill();
	/**
	 * @return the default line style
	 *
	 */
    public abstract int getDefaultStyle();
	/**
	 * @return the width of this edge
	 */
	abstract public float getLineWidth();
	/**
	 * change the width of this edge
	 * @param w
	 */
	abstract public void setLineWidth(float w);
	/**
	 * set the edge on which we work
	 * @param obj
	 */
	public abstract void setEdge(Object obj);
	/**
	 * change this edge's line color.
	 * @param color the new color.
	 */
	public abstract void setLineColor(Color color);
	/**
	 * @return the color of this edge
	 */
	public abstract Color getLineColor();
	/**
	 * apply pending changes (refresh display)
	 */
	public abstract void refresh();
	/**
	 * @return the list of avaible routing.
	 */
	public Vector getRoutingList() {
	    return v_routing;
	}
	/**
	 * @return the list of avaible draw style
	 */
	public Vector getStyleList() {
	    return v_style;
	}
	/**
	 * @return the routing of this edge.
	 */
	public abstract int getRouting();
	/**
	 * @return the style of this edge.
	 */
	public abstract int getStyle();
	/**
	 * change the routing of this edge.
	 * @param index index of the routing in the list.
	 * @see #getRoutingList()
	 */
	public abstract void setRouting(int index);
	/**
	 * change the style of this edge.
	 * @param index index of the style in the list.
	 * @see #getStyleList()
	 */
	public abstract void setStyle(int index);
	
	/**
	 * set the end of the line
	 * @param index
	 */
	public abstract void setLineEnd(int index);
	
	/**
	 * @param border if true, returns corrected points instead of real ones
	 * @return the list of points used by this edge.
	 */
	public abstract List getPoints(boolean border);
    /**
     * set points used by this edge
     * @param l
     */
    public abstract void setPoints(List l);
    /**
     * @param fereader
     */
    public void copyFrom(GsEdgeAttributesReader fereader) {
        setLineColor(fereader.getLineColor());
        setStyle(fereader.getStyle());
        setRouting(fereader.getRouting());
        setPoints(fereader.getPoints(false));
        setLineEnd(fereader.getLineEnd());
        setLineWidth(fereader.getLineWidth());
        setDash(fereader.getDash());
    }
    
    /**
     * @param fvreader
     */
    public void copyDefaultFrom(GsEdgeAttributesReader fvreader) {
        setDefaultEdgeSize(fvreader.getDefaultEdgeSize());
        setDefaultEdgeEndFill(fvreader.getDefaultEdgeEndFill());
        setDefaultStyle(fvreader.getDefaultStyle());
    }
    /**
     * @return the lineEnd
     */
    abstract public int getLineEnd();
    
    /**
     * set the edge as dashed.
     * @param dashArray 
     */
    abstract public void setDash(float[] dashArray);
    /**
     * set the edge as dashed.
     * @return the dash motif
     */
    abstract public float[] getDash();
}
