package org.ginsim.core.graph.common;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Map;

import org.ginsim.common.OptionStore;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.graph.view.NodeBorder;
import org.ginsim.core.graph.view.NodeShape;
import org.ginsim.core.utils.log.LogManager;


/**
 * a generic nodeAttributeReader storing data into a dedicated hashmap
 */
public class NodeAttributeReaderImpl implements NodeAttributesReader {

    public static Color bg = new Color(((Integer)OptionStore.getOption("vs.vertexbg", new Integer(-26368))).intValue());
    public static Color fg = new Color(((Integer)OptionStore.getOption("vs.vertexfg", new Integer(Color.WHITE.getRGB()))).intValue());
    
    public static NodeShape  shape;
    public static NodeBorder border;
    
    public static int height = ((Integer)OptionStore.getOption("vs.vertexheight", new Integer(30))).intValue();
    public static int width = ((Integer)OptionStore.getOption("vs.vertexwidth", new Integer(55))).intValue();
    
    static {
    	
    	String s = OptionStore.getOption("vs.vertexshape", NodeShape.RECTANGLE.name()).toString();
    	shape = NodeShape.valueOf(s);
    	if (shape == null) {
    		LogManager.error("Invalid shape in option: "+ s);
    		shape = NodeShape.RECTANGLE;
    	}
    	
    	s = OptionStore.getOption("vs.vertexborder", NodeBorder.SIMPLE.name()).toString();
    	border = NodeBorder.valueOf(s);
    	if (border == null) {
    		LogManager.error("Invalid border in option: "+ s);
    		border = NodeBorder.SIMPLE;
    	}
    	
    }


    /**
     * remember default values
     */
    public static void saveOptions() {
        OptionStore.setOption("vs.vertexfg", fg.getRGB());
        OptionStore.setOption("vs.vertexbg", bg.getRGB());
        OptionStore.setOption("vs.vertexshape", shape);
        OptionStore.setOption("vs.vertexborder", border);
        OptionStore.setOption("vs.vertexheight", height);
        OptionStore.setOption("vs.vertexwidth", width);
    }


	private final AbstractGraph backend;
    private Map dataMap = null;
    
    private NodeVSdata vvsd;
    private Object vertex;
    

    /**
     * @param map
     */
    public NodeAttributeReaderImpl(AbstractGraph backend, Map map) {
    	this.backend = backend;
        this.dataMap = map;
    }

    @Override
    public void setNode(Object node) {
    	this.vertex = node;
        vvsd = (NodeVSdata)dataMap.get(vertex);
        if (vvsd == null) {
            vvsd = new NodeVSdata();
            vvsd.bgcolor = bg;
            vvsd.fgcolor = fg;
            vvsd.border = border;
            vvsd.bounds.setFrame(vvsd.bounds.getX(), vvsd.bounds.getY(), width, height);
            vvsd.shape = shape;
            dataMap.put(vertex, vvsd);
        }
    }

    @Override
    public int getX() {
        if (vvsd == null) {
            return 0;
        }
        return (int)vvsd.bounds.getX();
    }

    @Override
    public int getY() {
        if (vvsd == null) {
            return 0;
        }
        return (int)vvsd.bounds.getY();
    }

    @Override
    public int getHeight() {
        if (vvsd == null) {
            return 0;
        }
        return (int)vvsd.bounds.getHeight();
    }

    @Override
    public int getWidth() {
        if (vvsd == null) {
            return 0;
        }
        return (int)vvsd.bounds.getWidth();
    }

    @Override
    public Color getForegroundColor() {
        if (vvsd == null) {
            return null;
        }
        return vvsd.fgcolor;
    }

    @Override
   public void setForegroundColor(Color color) {
        if (vvsd == null) {
            return;
        }
        vvsd.fgcolor = color;
    }

    @Override
    public Color getBackgroundColor() {
        if (vvsd == null) {
            return null;
        }
        return vvsd.bgcolor;
    }

    @Override
    public void setBackgroundColor(Color color) {
        if (vvsd == null) {
            return;
        }
        vvsd.bgcolor = color;
    }

    @Override
    public void refresh() {
    	if (vertex != null) {
    		backend.refresh(vertex);
    	}
    }

    @Override
    public void setPos(int x, int y) {
        if (vvsd == null) {
            return;
        }
        vvsd.bounds.setFrame(x,y, vvsd.bounds.getWidth(), vvsd.bounds.getHeight());
    }

    @Override
    public void setSize(int w, int h) {
        if (vvsd == null) {
            return;
        }
        vvsd.bounds.setFrame(vvsd.bounds.getX(), vvsd.bounds.getY(), w, h);
    }

    @Override
    public void setBorder(NodeBorder border) {
        if (vvsd == null) {
            return;
        }
        vvsd.border = border;
    }

    @Override
    public NodeBorder getBorder() {
        if (vvsd == null) {
            return getDefaultNodeBorder();
        }
        return vvsd.border;
    }

    @Override
    public NodeShape getShape() {
        if (vvsd == null) {
            return getDefaultNodeShape();
        }
        return vvsd.shape;
    }

    @Override
    public void setShape(NodeShape shape) {
        if (vvsd == null) {
            return;
        }
        vvsd.shape = shape;
    }

    
    class NodeVSdata {
    	protected Rectangle bounds = new Rectangle();
        protected Color fgcolor;
        protected Color bgcolor;
        
        protected NodeShape shape;
        protected NodeBorder border;
    }

	@Override
	public Rectangle getBounds() {
		if (vvsd != null) {
			return vvsd.bounds;
		}
		return null;
	}
	@Override
	public Rectangle setBounds(Rectangle bounds) {
		if (vvsd != null) {
			Rectangle old = vvsd.bounds;
			vvsd.bounds = bounds;
			return old;
		}
		return null;
	}
	
	
	@Override
	public void setDefaultNodeBackground(Color color) {
		bg = color;
	}
	@Override
	public void setDefaultNodeForeground(Color color) {
		fg = color;
	}
	@Override
	public void setDefaultNodeBorder(NodeBorder border) {
		this.border = border;
	}
	/**
	 * set the default size for vertices.
	 * @param w
	 * @param h
	 */
	public void setDefaultNodeSize(int w, int h) {
		width = w;
		height = h;
	}
	/**
	 * set the default shape for vertices.
	 * @param shape
	 */
	public void setDefaultNodeShape(NodeShape shape) {
		this.shape = shape;
	}

    /**
     * @return the default background color for vertices.
     */
	public Color getDefaultNodeBackground() {
		return bg;
	}
    /**
     * @return the default foreground color for vertices.
     */
	public Color getDefaultNodeForeground() {
		return fg;
	}
    /**
     * @return the default kind of border for vertices.
     */
	public NodeBorder getDefaultNodeBorder() {
		return border;
	}
	/**
	 * @return the default width for vertices.
	 */
	public int getDefaultNodeWidth() {
		return width;
	}
	/**
	 * @return the default height for vertices.
	 */
	public int getDefaultNodeHeight() {
		return height;
	}
	/**
	 * @return the default shape for vertices.
	 */
	public NodeShape getDefaultNodeShape() {
		return shape;
	}

	@Override
    public void copyFrom(NodeAttributesReader fvreader) {
        setPos(fvreader.getX(), fvreader.getY());
        setSize(fvreader.getWidth(), fvreader.getHeight());
        setShape(fvreader.getShape());
        setBackgroundColor(fvreader.getBackgroundColor());
        setForegroundColor(fvreader.getForegroundColor());
        setBorder(fvreader.getBorder());
    }

}
