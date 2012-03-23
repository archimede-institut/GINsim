package org.ginsim.core.graph.common;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.Map;

import org.ginsim.common.OptionStore;
import org.ginsim.common.utils.log.LogManager;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.graph.view.NodeBorder;
import org.ginsim.core.graph.view.NodeShape;


/**
 * a generic nodeAttributeReader storing data into a dedicated hashmap
 */
public class NodeAttributeReaderImpl implements NodeAttributesReader {

	private static char ELLIPSIS = '\u2026';
	
	public static final String VERTEX_BG = "vs.vertexbg";
	public static final String VERTEX_FG = "vs.vertexfg";
	public static final String VERTEX_TEXT = "vs.vertextext";
	public static final String VERTEX_HEIGHT = "vs.vertexheight";
	public static final String VERTEX_WIDTH = "vs.vertexwidth";
	public static final String VERTEX_SHAPE = "vs.vertexshape";
	public static final String VERTEX_BORDER = "vs.vertexborder";

    public static Color bg = new Color(OptionStore.getOption( VERTEX_BG, -26368));
    public static Color fg = new Color(OptionStore.getOption( VERTEX_FG, Color.WHITE.getRGB()));
    public static Color text = new Color(OptionStore.getOption( VERTEX_TEXT, Color.BLACK.getRGB()));
    
    public static int height = OptionStore.getOption( VERTEX_HEIGHT, 25);
    public static int width = OptionStore.getOption( VERTEX_WIDTH, 50);
    
    public static NodeShape  shape;
    public static NodeBorder border;
    
    static {
    	
    	String s = OptionStore.getOption( VERTEX_SHAPE, NodeShape.RECTANGLE.name());
    	try {
    		shape = NodeShape.valueOf(s);
    	} catch (IllegalArgumentException e) {
    		LogManager.error("Invalid shape in option: "+ s);
    		shape = NodeShape.RECTANGLE;
    		OptionStore.setOption( VERTEX_SHAPE, shape);
    	}
    	
    	s = OptionStore.getOption( VERTEX_BORDER, NodeBorder.SIMPLE.name());
    	try {
        	border = NodeBorder.valueOf(s);
    	} catch (IllegalArgumentException e) {
    		LogManager.error("Invalid border in option: "+ s);
    		border = NodeBorder.SIMPLE;
    		OptionStore.setOption( VERTEX_BORDER, NodeBorder.SIMPLE.name());
    	}
    	
    }


	private final AbstractGraph backend;
    private Map<Object, NodeVSdata> dataMap = null;
    
    private NodeVSdata vvsd;
    private Object vertex;
    

    /**
     * @param map
     */
    public NodeAttributeReaderImpl(AbstractGraph backend, Map<Object, NodeVSdata> map) {
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
            vvsd.textcolor = text;
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
    public Color getTextColor() {
        if (vvsd == null) {
            return null;
        }
        return vvsd.textcolor;
    }

    @Override
   public void setTextColor(Color color) {
        if (vvsd == null) {
            return;
        }
        vvsd.textcolor = color;
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

        // make sure that the node does not have negative coordinates as it annoys the GUI backend
        if (x < 0) {
        	x = 0;
        }
        if (y < 0) {
        	y = 0;
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
        protected Color textcolor;
        protected Color bgcolor;
        
        protected NodeShape shape;
        protected NodeBorder border;
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
		
		OptionStore.setOption( VERTEX_BG, color.getRGB());
		bg = color;
	}
	
	@Override
	public void setDefaultNodeForeground(Color color) {
		
		OptionStore.setOption( VERTEX_FG, color.getRGB());
		fg = color;
	}
	
	@Override
	public void setDefaultNodeBorder(NodeBorder border) {
		
		OptionStore.setOption( VERTEX_BORDER, border.name());
		this.border = border;
	}
	/**
	 * set the default size for vertices.
	 * @param w
	 * @param h
	 */
	public void setDefaultNodeSize(int w, int h) {
		
		OptionStore.getOption( VERTEX_HEIGHT, h);
		OptionStore.getOption( VERTEX_WIDTH, w);
		width = w;
		height = h;
	}
	/**
	 * set the default shape for vertices.
	 * @param shape
	 */
	public void setDefaultNodeShape(NodeShape shape) {
		
		OptionStore.setOption( VERTEX_SHAPE, shape.name());
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

	@Override
	public void render(Object node, Graphics2D g) {
		setNode(node);
		int x = getX();
		int y = getY();
		g.translate( x,y);
		doRender(node.toString(), g);
		g.translate( -x,-y);
	}
	
	/**
	 * Render the node, assuming that the node has already been set
	 * and the graphics context has been translated to its position.
	 * i.e. render the current node at (0,0)
	 * 
	 * @param node
	 * @param g
	 */
	private void doRender(String text, Graphics2D g) {
		int w = getWidth();
		int h = getHeight();
		
		Shape s = getShape().getShape( 0,0, w,h);
		g.setColor(getBackgroundColor());
		g.fill(s);
		g.setColor(getForegroundColor());
		g.draw(s);

		// get the text and FontMetric to evaluate its size
		FontMetrics fm = g.getFontMetrics();
		
		// shorten the text if needed
		int textwidth = fm.stringWidth(text);
		int targetWidth = w - 2;
		int i = text.length()-3;
		while ( textwidth > targetWidth && i > 0 ) {
			text = text.substring(0, i) + ELLIPSIS;
			textwidth = fm.stringWidth(text);
			i--;
		}
		
		// center the text
		int textHeight = fm.getHeight();
		int tx = w/2 - textwidth/2;
		int ty = h/2 + textHeight/2;
		
		// render the text
		g.setColor(getTextColor());
		g.drawString(text, tx, ty);
	}

}
