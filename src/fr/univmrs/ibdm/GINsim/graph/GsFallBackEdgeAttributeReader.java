package fr.univmrs.ibdm.GINsim.graph;

import java.awt.Color;
import java.util.List;
import java.util.Map;

/**
 * a generic edgeAttributeReader storing data into a dedicated hashmap
 */
public class GsFallBackEdgeAttributeReader extends GsEdgeAttributesReader {

    private Map dataMap;
    
    private float defaultsize = 1;
    private boolean defaultfill = true;
    
    private EdgeVSdata evsd = null;

    private int defaultstyle;
    private int defaultRouting; 
    
    
    /**
     * @param dataMap
     */
    public GsFallBackEdgeAttributeReader(Map dataMap) {
        this.dataMap = dataMap;
    }
    
    public void setDefaultEdgeColor(Color color) {
    	GsEdgeAttributesReader.color = color;
    }

    public void setDefaultEdgeSize(float s) {
        defaultsize = s;
    }

    public void setDefaultEdgeEndFill(boolean b) {
        defaultfill = b;
    }

    public float getLineWidth() {
        if (evsd == null) {
            return 0;
        }
        return evsd.size;
    }

    public void setLineWidth(float w) {
        if (evsd == null) {
            return;
        }
        evsd.size = w;
    }

    public void setEdge(Object obj) {
        evsd = (EdgeVSdata)dataMap.get(obj);
        if (evsd == null) {
            evsd = new EdgeVSdata();
            
            evsd.color = color;
            evsd.fill = defaultfill;
            evsd.routing = defaultRouting;
            evsd.size = defaultsize;
            evsd.style = defaultstyle;
            
            dataMap.put(obj, evsd);
        }
    }

    public void setLineColor(Color color) {
        if (evsd != null) {
            evsd.color = color;
        }
    }

    public Color getLineColor() {
        if (evsd == null) {
            return null;
        }
        return evsd.color;
    }

    public void refresh() {
        // nothing to do here!
    }

    public int getRouting() {
        if (evsd == null) {
            return 0;
        }
        return evsd.routing;
    }

    public int getStyle() {
        if (evsd == null) {
            return 0;
        }
        return evsd.style;
    }

    public void setRouting(int index) {
        if (evsd == null) {
            return;
        }
        evsd.routing = index;
    }

    public void setStyle(int index) {
        if (evsd == null) {
            return;
        }
        evsd.style = index;
    }

    public void setLineEnd(int index) {
        if (evsd == null) {
            return;
        }
        evsd.end = index;
    }

    public int getLineEnd() {
        if (evsd == null) {
            return 0;
        }
        return evsd.end;
    }

    public List getPoints(boolean border) {
        if ( evsd == null ) {
            return null;
        }
        return evsd.points;
    }

    public void setPoints(List l) {
        if (evsd == null) {
            return;
        }
        evsd.points = l;
    }

    public void setDefaultStyle(int selectedIndex) {
        defaultstyle = selectedIndex;
    }
    
    private class EdgeVSdata {
        protected Color color;
        protected float size;
        protected int end;
        protected boolean fill;
        protected int routing;
        protected int style;
        protected List points;
		protected float[] dash;
    }

    public float getDefaultEdgeSize() {
        return defaultsize;
    }

    public boolean getDefaultEdgeEndFill() {
        return defaultfill;
    }

    public int getDefaultStyle() {
        return defaultstyle;
    }

	public void setDash(float[] dashArray) {
		evsd.dash = dashArray;
	}

	public float[] getDash() {
		return evsd.dash;
	}
}
