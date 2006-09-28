package fr.univmrs.ibdm.GINsim.graph;

import java.awt.Color;
import java.util.Map;

/**
 * a generic vertexAttributeReader storing data into a dedicated hashmap
 */
public class GsFallbackVertexAttributeReader extends GsVertexAttributesReader {

    private Map dataMap = null;
    private VertexVSdata vvsd;
    
    protected int w = 60;
    protected int h = 25;
    protected Color fgcolor = Color.WHITE;
    protected Color bgcolor = new Color(255, 150, 0);
    
    protected int border = BORDER_RAISED;
    protected int shape;

    
    /**
     * @param map
     */
    public GsFallbackVertexAttributeReader(Map map) {
        this.dataMap = map;
    }

    public void setVertex(Object vertex) {
        vvsd = (VertexVSdata)dataMap.get(vertex);
        if (vvsd == null) {
            vvsd = new VertexVSdata();
            vvsd.bgcolor = bgcolor;
            vvsd.fgcolor = fgcolor;
            vvsd.border = border;
            vvsd.w = w;
            vvsd.h = h;
            vvsd.shape = shape;
            dataMap.put(vertex, vvsd);
        }
    }

    public int getX() {
        if (vvsd == null) {
            return 0;
        }
        return vvsd.x;
    }

    public int getY() {
        if (vvsd == null) {
            return 0;
        }
        return vvsd.y;
    }

    public int getHeight() {
        if (vvsd == null) {
            return 0;
        }
        return vvsd.h;
    }

    public int getWidth() {
        if (vvsd == null) {
            return 0;
        }
        return vvsd.w;
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
    }

    public void setPos(int x, int y) {
        if (vvsd == null) {
            return;
        }
        vvsd.x = x;
        vvsd.y = y;
    }

    public void setSize(int w, int h) {
        if (vvsd == null) {
            return;
        }
        vvsd.w = w;
        vvsd.h = h;
    }

    public void setDefaultVertexBackground(Color color) {
        bgcolor = color;
    }

    public void setDefaultVertexForeground(Color color) {
        fgcolor = color;
    }

    public void setDefaultVertexBorder(int index) {
        border = index;
    }

    public void setDefaultVertexSize(int w, int h) {
        this.w = w;
        this.h = h;
    }

    public void setDefaultVertexShape(int shape) {
        this.shape = shape;
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

    
    private class VertexVSdata {
        protected int x, y, w, h;
        protected Color fgcolor;
        protected Color bgcolor;
        
        protected int shape;
        protected int border;
    }


    public Color getDefaultVertexBackground() {
        return bgcolor;
    }

    public Color getDefaultVertexForeground() {
        return fgcolor;
    }

    public int getDefaultVertexBorder() {
        return border;
    }

    public int getDefaultVertexWidth() {
        return w;
    }

    public int getDefaultVertexHeight() {
        return h;
    }

    public int getDefaultVertexShape() {
        return shape;
    }
}
