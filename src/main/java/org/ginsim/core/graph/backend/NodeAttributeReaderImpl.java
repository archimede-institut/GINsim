package org.ginsim.core.graph.backend;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.io.IOException;

import org.ginsim.common.application.LogManager;
import org.ginsim.common.application.OptionStore;
import org.ginsim.common.xml.XMLWriter;
import org.ginsim.core.graph.Edge;
import org.ginsim.core.graph.GraphBackend;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.graph.view.NodeBorder;
import org.ginsim.core.graph.view.NodeShape;
import org.ginsim.core.graph.view.NodeViewInfo;
import org.ginsim.core.graph.view.SimpleStroke;
import org.ginsim.core.graph.view.style.NodeStyle;
import org.ginsim.core.graph.view.style.StyleManager;


/**
 * A generic nodeAttributeReader showing a unified view of default and node-specific styles (from the graph backend).
 */
public class NodeAttributeReaderImpl<V,E extends Edge<V>> implements NodeAttributesReader<V> {

//	public static final boolean SAVE_OLD_VS = false;
	
	private static char ELLIPSIS = '\u2026';
	
	public static final int SW = 6;      // width of the selection mark
	public static final int hSW = SW/2;  // half width of the selection mark

	public static final String VERTEX_BG = "vs.vertexbg";
	public static final String VERTEX_FG = "vs.vertexfg";
	public static final String VERTEX_TEXT = "vs.vertextext";
	public static final String VERTEX_HEIGHT = "vs.vertexheight";
	public static final String VERTEX_WIDTH = "vs.vertexwidth";
	public static final String VERTEX_SHAPE = "vs.vertexshape";
	public static final String VERTEX_BORDER = "vs.vertexborder";

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


	private final GraphBackend<V,E> backend;
	private final StyleManager<V, E> styleManager;
	private final NodeStyle<V> defaultStyle;
	private final Rectangle cachedBounds = new Rectangle();
	
    private V vertex;
    private NodeViewInfo viewInfo = null;
    private NodeStyle<V> style = null;

    private boolean selected;
    private boolean hasChanged = false;
    
    private SimpleStroke stroke = new SimpleStroke();

    /**
     * @param styleManager
     * @param backend
     * @param backend
     */
    public NodeAttributeReaderImpl(StyleManager<V, E> styleManager, GraphBackend<V, E> backend) {
    	this.backend = backend;
    	this.styleManager = styleManager;
        this.defaultStyle = styleManager.getDefaultNodeStyle();
    }

    @Override
    public void setNode(V node) {
    	setNode(node, false);
    }
    
    public void setNode(V node, boolean selected) {
    	this.vertex = node;
    	this.selected = selected;
    	viewInfo = backend.getNodeViewInfo(node);
    	if (viewInfo == null) {
    		style = null;
    		return;
    	}
    	
        style = styleManager.getViewNodeStyle(node);
        refreshBounds();
    	hasChanged = false;
    }
    
    private void refreshBounds() {
        cachedBounds.setLocation(viewInfo.getX(), viewInfo.getY());
    	if (style == null) {
            cachedBounds.setSize(defaultStyle.getWidth(vertex), defaultStyle.getHeight(vertex));
    	} else {
            cachedBounds.setSize(style.getWidth(vertex), style.getHeight(vertex));
    	}
    }

    @Override
    public int getX() {
        return cachedBounds.x;
    }

    @Override
    public int getY() {
        return cachedBounds.y;
    }

    @Override
    public int getHeight() {
        return cachedBounds.height;
    }

    @Override
    public int getWidth() {
        return cachedBounds.width;
    }

    @Override
    public Rectangle getBounds() {
    	return cachedBounds;
    }

    @Override
    public Color getForegroundColor() {
        if (style != null) {
        	return style.getForeground(vertex);
        }
    	return defaultStyle.getForeground(vertex);
    }

    @Override
    public Color getTextColor() {
        if (style != null) {
        	return style.getTextColor(vertex);
        }
    	return defaultStyle.getTextColor(vertex);
    }

    @Override
    public Color getBackgroundColor() {
        if (style != null) {
        	return style.getBackground(vertex);
        }
    	return defaultStyle.getBackground(vertex);
    }

    @Override
    public void refresh() {
    	if (vertex != null && hasChanged) {
    		backend.damage(vertex);
    	}
    }
    @Override
    public void damage() {
    	if (vertex != null) {
    		backend.damage(vertex);
    	}
    }

    @Override
    public void setPos(int x, int y) {
        if (viewInfo == null) {
            return;
        }

        // make sure that the node does not have negative coordinates as it annoys the GUI backend
        if (x < 0) {
        	x = 0;
        }
        if (y < 0) {
        	y = 0;
        }
        
        viewInfo.setPosition(x, y);
        refreshBounds();
        hasChanged = true;
    }
    
    @Override
    public void move(int dx, int dy) {
        if (viewInfo == null) {
            return;
        }

        int x = getX();
        int y = getY();
        setPos(x+dx, y+dy);
    }

    @Override
    public NodeBorder getBorder() {
        if (style != null) {
            return style.getNodeBorder(vertex); 
        }
		return defaultStyle.getNodeBorder(vertex);
    }

    @Override
    public NodeShape getShape() {
        if (style != null) {
        	return style.getNodeShape(vertex); 
        }
		return defaultStyle.getNodeShape(vertex);
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
	public void render(Graphics2D g) {
		if (this.vertex == null) {
			return;
		}
		
		Rectangle bounds = getBounds();
		g.translate( bounds.x, bounds.y);
		doRender(vertex.toString(), g, false);
		g.translate( -bounds.x,-bounds.y);
	}

	@Override
	public void renderMoving(Graphics2D g, int movex, int movey) {
		if (this.vertex == null) {
			return;
		}
		
		Rectangle bounds = getBounds();
		g.translate( movex+bounds.x, movey+bounds.y);
		doRender(vertex.toString(), g, true);
		g.translate( -movex-bounds.x, -movey-bounds.y);
	}

	@Override
	public boolean select(Point p) {
		if (viewInfo == null) {
			return false;
		}
		return getBounds().contains(p);
	}
	
	/**
	 * Render the node, assuming that the node has already been set
	 * and the graphics context has been translated to its position.
	 * i.e. render the current node at (0,0)
	 * 
	 * @param text
	 * @param g
	 * @param moving
	 */
	private void doRender(String text, Graphics2D g, boolean moving) {
		int w = getWidth();
		int h = getHeight();
		int sw = 1;
		
		Shape s = getShape().getShape( sw, sw, w-2*sw,h-2*sw);
		g.setColor(getBackgroundColor());
		stroke.setWidth(sw);
		g.setStroke(stroke);
		g.fill(s);

		if (moving) {
			g.setColor(getForegroundColor());
			g.draw(s);
			return;
		}
		
		// get the text and FontMetric to evaluate its size
		FontMetrics fm = g.getFontMetrics();
		
		// shorten the text if needed
		int textwidth = fm.stringWidth(text);
		int targetWidth = w - 4*sw;
		int i = text.length()-3;
		while ( textwidth > targetWidth && i > 0 ) {
			text = text.substring(0, i) + ELLIPSIS;
			textwidth = fm.stringWidth(text);
			i--;
		}
		
		// center the text
		int textHeight = fm.getHeight();
		int tx = w/2 - textwidth/2;
		int ty = h/2 + textHeight/2 - 2;
		
		// render the text
		g.setColor(getTextColor());
		g.drawString(text, tx, ty);
		
		g.setColor(getForegroundColor());
		g.draw(s);

		if (selected) {
			g.setColor(Color.red);
			g.fillRect(0, 0, SW, SW);
			g.fillRect(0, h-SW, SW, SW);
			g.fillRect(w-SW, h-SW, SW, SW);
			g.fillRect(w-SW, 0, SW, SW);
		}
	}

	@Override
	public NodeStyle getDefaultNodeStyle() {
		return defaultStyle;
	}
	
	@Override
	public void writeGINML(XMLWriter writer) throws IOException {
		if (vertex == null) {
			return;
		}
		
		// save style information
		writer.openTag("nodevisualsetting");
		writer.addAttr("x", ""+getX());
		writer.addAttr("y", ""+getY());

        if (styleManager.isDisabled()) {
            // write old attributes for backward compatibility
            if (getShape() == NodeShape.ELLIPSE) {
                writer.openTag("ellipse");
            } else {
                writer.openTag("rect");
            }

            writer.addAttr("x", ""+getX());
            writer.addAttr("y", ""+getY());
            writer.addAttr("width", ""+getWidth());
            writer.addAttr("height", ""+getHeight());

            Color bg = getBackgroundColor();
            Color fg = getForegroundColor();
            Color txt = getTextColor();
            writer.addAttr("backgroundColor", bg);
            writer.addAttr("foregroundColor", fg);
            if (!txt.equals(fg)) {
                writer.addAttr("textColor", txt);
            }
            writer.closeTag();
        } else if (style != null) {
			writer.addAttr("style", style.getName());
		}
		
		writer.closeTag();
	}

	@Override
	public void setStyle(NodeStyle<V> style) {
		if (viewInfo == null) {
			return;
		}
		this.style = style;
		viewInfo.setStyle(style);
	}


    // TODO: remove setters completely
	public void setForegroundColor(Color color) {}
	public void setTextColor(Color color) {}
    public void setBackgroundColor(Color color) {}
    public void setSize(int w, int h) {}
    public void setBorder(NodeBorder border) {}
    public void setShape(NodeShape shape) {}

}
