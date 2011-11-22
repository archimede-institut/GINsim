package org.ginsim.graph.common;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Map;

import org.ginsim.graph.backend.GraphViewBackend;


/**
 * a generic nodeAttributeReader storing data into a dedicated hashmap
 */
public class FallbackNodeAttributeReader extends NodeAttributesReader {

	private final GraphViewBackend backend;
    private Map dataMap = null;
    
    private NodeVSdata vvsd;
    private Object vertex;
    
    /**
     * @param map
     */
    public FallbackNodeAttributeReader(GraphViewBackend backend, Map map) {
    	this.backend = backend;
        this.dataMap = map;
    }

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

    public int getX() {
        if (vvsd == null) {
            return 0;
        }
        return (int)vvsd.bounds.getX();
    }

    public int getY() {
        if (vvsd == null) {
            return 0;
        }
        return (int)vvsd.bounds.getY();
    }

    public int getHeight() {
        if (vvsd == null) {
            return 0;
        }
        return (int)vvsd.bounds.getHeight();
    }

    public int getWidth() {
        if (vvsd == null) {
            return 0;
        }
        return (int)vvsd.bounds.getWidth();
    }

    public Color getForegroundColor() {
        if (vvsd == null) {
            return null;
        }
        return vvsd.fgcolor;
    }

    public void setForegroundColor(Color color) {
        if (vvsd == null) {
            return;
        }
        vvsd.fgcolor = color;
    }

    public Color getBackgroundColor() {
        if (vvsd == null) {
            return null;
        }
        return vvsd.bgcolor;
    }

    public void setBackgroundColor(Color color) {
        if (vvsd == null) {
            return;
        }
        vvsd.bgcolor = color;
    }

    public void refresh() {
    	if (vertex != null) {
    		backend.refresh(vertex);
    	}
    }

    public void setPos(int x, int y) {
        if (vvsd == null) {
            return;
        }
        vvsd.bounds.setFrame(x,y, vvsd.bounds.getWidth(), vvsd.bounds.getHeight());
    }

    public void setSize(int w, int h) {
        if (vvsd == null) {
            return;
        }
        vvsd.bounds.setFrame(vvsd.bounds.getX(), vvsd.bounds.getY(), w, h);
    }

    public void setBorder(int index) {
        if (vvsd == null) {
            return;
        }
        vvsd.border = index;
    }

    public int getBorder() {
        if (vvsd == null) {
            return 0;
        }
        return vvsd.border;
    }

    public int getShape() {
        if (vvsd == null) {
            return 0;
        }
        return vvsd.shape;
    }

    public void setShape(int shapeIndex) {
        if (vvsd == null) {
            return;
        }
        vvsd.shape = shapeIndex;
    }

    
    class NodeVSdata {
    	protected Rectangle bounds = new Rectangle();
        protected Color fgcolor;
        protected Color bgcolor;
        
        protected int shape;
        protected int border;
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
}
