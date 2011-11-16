package org.ginsim.gui.graph.backend;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import org.ginsim.graph.common.VertexAttributesReader;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexRenderer;
import org.jgrapht.ext.JGraphModelAdapter;

import fr.univmrs.tagc.common.Tools;

/**
 * extract info on vertices for jgraph based graphs.
 */
public class GsJgraphVertexAttribute extends VertexAttributesReader {

	private JGraphModelAdapter m_adaptor;
	private AttributeMap defaultVertexAttr;
	private AttributeMap attr;
	private DefaultGraphCell cell;

    private int idDefaultBorder = 1;
    
    private static Object[] v_shapeRenderer = null;
    private static Object[] v_borderRenderer = null;
    private static Map m_border = new HashMap();
    private static Map m_strongborder = new HashMap();

    private static final int STRONG_BORDER_WIDTH = 5;
    
    static {
        	v_shapeRenderer = new Object[NBSHAPE];
        	v_shapeRenderer[SHAPE_RECTANGLE] = new VertexRenderer();
        	v_shapeRenderer[SHAPE_ELLIPSE] = new GsJgraphEllipseRenderer();
        	
        	
        	v_borderRenderer = new Object[NBBORDER];
        	Border b = BorderFactory.createLineBorder(Color.BLACK);
        	m_border.put(Color.BLACK, b);
        	v_borderRenderer[BORDER_SIMPLE] = b;
        	v_borderRenderer[BORDER_RAISED] = BorderFactory.createRaisedBevelBorder();
        	b = BorderFactory.createLineBorder(Color.BLACK, STRONG_BORDER_WIDTH);
        	v_borderRenderer[BORDER_STRONG] = b;
    }

    public GsJgraphVertexAttribute(JGraphModelAdapter<?,?> adaptor, AttributeMap defaults) {
        this.m_adaptor = adaptor;
        this.defaultVertexAttr = defaults;
	}

	public static void applyDefault(AttributeMap defaultVertexAttr) {
        GraphConstants.setBackground(defaultVertexAttr, VertexAttributesReader.bg);
        GraphConstants.setForeground(defaultVertexAttr, VertexAttributesReader.fg);
        GraphConstants.setBorder(defaultVertexAttr, (Border)v_borderRenderer[VertexAttributesReader.border]);
        GraphConstants.setBounds(defaultVertexAttr, new Rectangle(10,10,VertexAttributesReader.width,VertexAttributesReader.height));
        int shapeIndex = VertexAttributesReader.shape;
        if (shapeIndex >= 0 && shapeIndex < NBSHAPE) {
            defaultVertexAttr.put("RENDERER", v_shapeRenderer[shapeIndex]);
        } else {
            defaultVertexAttr.remove("RENDERER");
        }
    }
    
    public void setVertex(Object vertex) {
        cell = m_adaptor.getVertexCell(vertex);
        attr = cell.getAttributes();
    }
    
    public int getX() {
    	if (attr == null) {
    		return 0;
    	}
        return (int)((Rectangle2D)attr.get("bounds")).getX();
    }

    public int getY() {
    	if (attr == null) {
    		return 0;
    	}
        return (int)((Rectangle2D)attr.get("bounds")).getY();
    }

    public int getHeight() {
    	if (attr == null) {
    		return 0;
    	}
        return (int)((Rectangle2D)attr.get("bounds")).getHeight();
    }

    public int getWidth() {
    	if (attr == null) {
    		return 0;
    	}
        return (int)((Rectangle2D)attr.get("bounds")).getWidth();
    }

    public Color getForegroundColor() {
    	if (attr == null) {
    		return null;
    	}
    	return GraphConstants.getForeground(attr);
    }

    public void setForegroundColor(Color color) {
    	if (attr == null || color == null) {
    		return;
    	}
    	GraphConstants.setForeground(attr, color);
    	Border b = GraphConstants.getBorder(attr);
    	if (b != null && b instanceof LineBorder) {
    	    if (((LineBorder)b).getThickness() == STRONG_BORDER_WIDTH) {
    	        GraphConstants.setBorder(attr, getStrongBorder(color));
    	    } else {
    	        GraphConstants.setBorder(attr, getSimpleBorder(color));
    	    }
    	}
    }

    private Border getSimpleBorder(Color color) {
        Border b = (Border)m_border.get(color);
        if (b == null) {
            b = new LineBorder(color);
            m_border.put(color, b);
        }
        return b;
    }
    private Border getStrongBorder(Color color) {
        Border b = (Border)m_strongborder.get(color);
        if (b == null) {
            b = new LineBorder(color, STRONG_BORDER_WIDTH);
            m_strongborder.put(color, b);
        }
        return b;
    }
    public Color getBackgroundColor() {
    	if (attr == null) {
    		return null;
    	}
        return GraphConstants.getBackground(attr);
    }

    public void setBackgroundColor(Color color) {
    	if (attr == null || color == null) {
    		return;
    	}
    	GraphConstants.setBackground(attr, color);
    }

    public void setPos(int x, int y) {
    	if (attr == null) {
    		return;
    	}
        Rectangle2D rect = (Rectangle2D)attr.get("bounds");
        rect.setFrame(x, y, rect.getWidth(), rect.getHeight());
    }

	public void refresh() {
    	if (attr == null) {
    		return;
    	}
		m_adaptor.cellsChanged(new Object[] {cell});
	}

	public void setSize(int w, int h) {
    	if (attr == null) {
    		return;
    	}
        Rectangle2D rect = (Rectangle2D)attr.get("bounds");
        rect.setFrame(rect.getX(), rect.getY(), w, h);
	}
	
	public void setDefaultVertexBackground(Color color) {
		GraphConstants.setBackground(defaultVertexAttr, color);
		VertexAttributesReader.bg = color;
	}

	public void setDefaultVertexForeground(Color color) {
		GraphConstants.setForeground(defaultVertexAttr, color);
        VertexAttributesReader.fg = color;
	}

	public void setDefaultVertexSize(int w, int h) {
		GraphConstants.setBounds(defaultVertexAttr, new Rectangle(10,10,w,h));
        VertexAttributesReader.height = h;
        VertexAttributesReader.width = w;
	}

	public void setDefaultVertexBorder(int borderIndex) {
		if (attr == null || borderIndex < 0 || borderIndex > v_border.size()) {
			return;
		}
		idDefaultBorder = borderIndex;
		GraphConstants.setBorder(defaultVertexAttr, (Border)v_borderRenderer[borderIndex]);
        VertexAttributesReader.border = borderIndex;
	}

	public void setBorder(int borderIndex) {
		if (attr == null || borderIndex < 0 || borderIndex > v_border.size()) {
			return;
		}
		if (borderIndex == BORDER_SIMPLE) {
		    GraphConstants.setBorder(attr, getSimpleBorder(GraphConstants.getForeground(attr)));
		} else if (borderIndex == BORDER_STRONG) {
		    GraphConstants.setBorder(attr, getStrongBorder(GraphConstants.getForeground(attr)));
		} else {
		    GraphConstants.setBorder(attr, (Border)v_borderRenderer[borderIndex]);
		}
	}

	public int getBorder() {
    	if (attr == null) {
    		return 0;
    	}
    	Border b = GraphConstants.getBorder(attr);
    	if (b != null && b instanceof LineBorder) {
    	    if (((LineBorder)b).getThickness() == STRONG_BORDER_WIDTH) {
    	        return BORDER_STRONG;
    	    }
    	    return BORDER_SIMPLE;
    	}
		int index = Tools.arrayIndexOf(v_borderRenderer,b);
		if (index == -1) {
			index = idDefaultBorder;
		}
		return index;
	}
	
	public int getShape() {
    	if (attr == null) {
    		return 0;
    	}
		int index = Tools.arrayIndexOf(v_shapeRenderer, attr.get("RENDERER"));
		if (index == -1) {
			index = 0;
		}
		return index;
	}
	
	public void setShape(int shapeIndex) {
		if (attr == null) {
			return;
		}
		if (shapeIndex >= 0 && shapeIndex < NBSHAPE) {
			attr.put("RENDERER", v_shapeRenderer[shapeIndex]);
		} else {
			attr.remove("RENDERER");
		}
	}

	public void setDefaultVertexShape(int shapeIndex) {
		if (shapeIndex >= 0 && shapeIndex < NBSHAPE) {
			defaultVertexAttr.put("RENDERER", v_shapeRenderer[shapeIndex]);
		} else {
			defaultVertexAttr.remove("RENDERER");
		}
        VertexAttributesReader.shape = shapeIndex;
	}

    public Color getDefaultVertexBackground() {
        return GraphConstants.getBackground(defaultVertexAttr);
    }

    public Color getDefaultVertexForeground() {
        return GraphConstants.getForeground(defaultVertexAttr);
    }

    public int getDefaultVertexBorder() {
        return idDefaultBorder;
    }

    public int getDefaultVertexWidth() {
        return (int)GraphConstants.getBounds(defaultVertexAttr).getWidth();
    }

    public int getDefaultVertexHeight() {
        return (int)GraphConstants.getBounds(defaultVertexAttr).getHeight();
    }

    public int getDefaultVertexShape() {
		int index = Tools.arrayIndexOf(v_shapeRenderer, defaultVertexAttr.get("RENDERER"));
		if (index == -1) {
			index = 0;
		}
		return index;
    }
}
