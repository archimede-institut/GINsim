package fr.univmrs.ibdm.GINsim.graph;

import java.awt.Color;
import java.util.Vector;

import fr.univmrs.ibdm.GINsim.global.GsOptions;
import fr.univmrs.ibdm.GINsim.manageressources.Translator;

/**
 * extract from graph graphic info on a vertex.
 */
public abstract class GsVertexAttributesReader {

    public static Color bg = new Color(((Integer)GsOptions.getOption("vs.vertexbg", new Integer(-26368))).intValue());
    public static Color fg = new Color(((Integer)GsOptions.getOption("vs.vertexfg", new Integer(Color.WHITE.getRGB()))).intValue());
    public static int shape = ((Integer)GsOptions.getOption("vs.vertexshape", new Integer(0))).intValue();
    public static int border = 0;
    public static int height = ((Integer)GsOptions.getOption("vs.vertexheight", new Integer(30))).intValue();
    public static int width = ((Integer)GsOptions.getOption("vs.vertexwidth", new Integer(55))).intValue();
    
    protected static Vector v_shape = null;
    protected static Vector v_border = null;
    
    /**     */
    public static final int NBSHAPE = 2;
    /**     */
    public static final int SHAPE_RECTANGLE = 0;
    /**     */
    public static final int SHAPE_ELLIPSE = 1;
    
    /**     */
    public static final int NBBORDER = 3;
    /**   simple line border  */
    public static final int BORDER_SIMPLE = 0;
    /**  jgraph's default border: 3D effect   */
    public static final int BORDER_RAISED = 1;
    /**   like the simple border but larger  */
    public static final int BORDER_STRONG = 2;
    
    
    static {
        	v_shape = new Vector();
        	v_shape.add(Translator.getString("STR_shapeRectangle"));
        	v_shape.add(Translator.getString("STR_shapeEllipse"));
        	
        	v_border = new Vector();
        	v_border.add(Translator.getString("STR_borderSimple"));
        	v_border.add(Translator.getString("STR_borderRB"));
        	v_border.add(Translator.getString("STR_borderStrong"));
    }

    /**
     * remember default values
     */
    public static void saveOptions() {
        GsOptions.setOption("vs.vertexfg", new Integer(fg.getRGB()));
        GsOptions.setOption("vs.vertexbg", new Integer(bg.getRGB()));
        GsOptions.setOption("vs.vertexshape", new Integer(shape));
        GsOptions.setOption("vs.vertexborder", new Integer(border));
        GsOptions.setOption("vs.vertexheight", new Integer(height));
        GsOptions.setOption("vs.vertexwidth", new Integer(width));
    }

    /**
     * change the edited vertex.
     * @param vertex the vertex to edit
     */
    abstract public void setVertex(Object vertex); 

    /**
     * @return the horizontal position of the vertex.
     */
    abstract public int getX();
    /**
     * @return the vertical position of the vertex.
     */
    abstract public int getY();
    /**
     * @return the height of the vertex.
     */
    abstract public int getHeight();
    /**
     * @return the width of the vertex.
     */
    abstract public int getWidth();
    /**
     * @return the foreground (text) color of the vertex.
     */
    abstract public Color getForegroundColor();
    /**
     * change the foreground color of the vertex.
     * @param color the new color.
     */
    abstract public void setForegroundColor(Color color);
    /**
     * @return the background color of the vertex.
     */
    abstract public Color getBackgroundColor();
    /**
     * change the background color of the vertex.
     * @param color the new color.
     */
    abstract public void setBackgroundColor(Color color);

    /**
     * apply pending changes (refresh display).
     *
     */
    abstract public void refresh();
    
    /**
     * change the vertex's position.
     * @param x
     * @param y
     */
    abstract public void setPos(int x, int y);
    /**
     * change the vertex's size.
     * @param w
     * @param h
     */
    abstract public void setSize(int w, int h);
    /**
     * set the default background color for vertices.
     * @param color
     */
	public void setDefaultVertexBackground(Color color) {
		bg = color;
	}
    /**
     * set the default foreground color for vertices.
     * @param color
     */
	public void setDefaultVertexForeground(Color color) {
		fg = color;
	}
    /**
     * set the default kind of border for vertices.
     * @param index
     */
	public void setDefaultVertexBorder(int index) {
		border = index;
	}
	/**
	 * set the default size for vertices.
	 * @param w
	 * @param h
	 */
	public void setDefaultVertexSize(int w, int h) {
		width = w;
		height = h;
	}
	/**
	 * set the default shape for vertices.
	 * @param shape
	 */
	public void setDefaultVertexShape(int shape) {
		GsVertexAttributesReader.shape = shape;
	}

    /**
     * @return the default background color for vertices.
     */
	public Color getDefaultVertexBackground() {
		return bg;
	}
    /**
     * @return the default foreground color for vertices.
     */
	public Color getDefaultVertexForeground() {
		return fg;
	}
    /**
     * @return the default kind of border for vertices.
     */
	public int getDefaultVertexBorder() {
		return border;
	}
	/**
	 * @return the default width for vertices.
	 */
	public int getDefaultVertexWidth() {
		return width;
	}
	/**
	 * @return the default height for vertices.
	 */
	public int getDefaultVertexHeight() {
		return height;
	}
	/**
	 * @return the default shape for vertices.
	 */
	public int getDefaultVertexShape() {
		return shape;
	}

	/**
	 * change the kind of border for this vertex
	 * @param index
	 * @see #getBorderList()
	 */
	abstract public void setBorder(int index);
	/**
	 * @return the border of the vertex.
	 */
	abstract public int getBorder();
	/**
	 * @return the list of avaible borders.
	 */
	public Vector getBorderList() {
	    return v_border;
	}
	
	/**
	 * @return the list of avaible shapes.
	 */
	public Vector getShapeList() {
	    return v_shape;
	}
	/**
	 * @return the shape of the vertex
	 */
	abstract public int getShape();
	/**
	 * change the shape of the vertex.
	 * @param shapeIndex
	 * @see #getShapeList()
	 */
	abstract public void setShape(int shapeIndex);

    /**
     * @param fvreader
     */
    public void copyFrom(GsVertexAttributesReader fvreader) {
        setPos(fvreader.getX(), fvreader.getY());
        setSize(fvreader.getWidth(), fvreader.getHeight());
        setShape(fvreader.getShape());
        setBackgroundColor(fvreader.getBackgroundColor());
        setForegroundColor(fvreader.getForegroundColor());
        setBorder(fvreader.getBorder());
    }
    
}
