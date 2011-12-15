package org.ginsim.core.graph.common;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import org.ginsim.common.OptionStore;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.EdgePattern;


/**
 * a generic edgeAttributeReader storing data into a dedicated hashmap
 */
public class EdgeAttributeReaderImpl implements EdgeAttributesReader {

	public static final String EDGE_COLOR = "vs.edgecolor";
	
    protected static Map<String, float[]> m_pattern = null;
	
    public static Color default_color = new Color(((Integer)OptionStore.getOption( EDGE_COLOR)).intValue());
    
    protected float defaultLineWidth;
    
	private final AbstractGraph graph;
    private final Map dataMap;
    
    private float defaultsize = 1;
    private boolean defaultfill = true;
    
    private Edge<?> edge;
    private EdgeVSdata evsd = null;

    private boolean defaultcurve;
    
    
    /**
     * @param dataMap
     */
    public EdgeAttributeReaderImpl(AbstractGraph backend, Map dataMap) {
    	this.graph = backend;
        this.dataMap = dataMap;
    }
    
    public void setDefaultEdgeColor(Color color) {
    	
    	OptionStore.getOption( EDGE_COLOR, color.getRGB());
    	this.default_color = color;
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
    	edge = (Edge)obj;
        evsd = (EdgeVSdata)dataMap.get(obj);
        if (evsd == null && obj instanceof Edge) {
            evsd = (EdgeVSdata)dataMap.get(obj);
        }
        if (evsd == null) {
            evsd = new EdgeVSdata();
            
            evsd.color = default_color;
            evsd.fill = defaultfill;
            evsd.curve = defaultcurve;
            evsd.size = defaultsize;
            
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
    	if (edge != null) {
    		graph.refresh(edge);
    	}
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

    public List getPoints() {
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

    class EdgeVSdata {
        protected Color color;
        protected float size;
        protected int end;
        protected boolean fill;
        protected boolean curve;
        protected List points;
		protected EdgePattern dash;
    }

    public float getDefaultEdgeSize() {
        return defaultsize;
    }

    public boolean getDefaultEdgeEndFill() {
        return defaultfill;
    }

    @Override
	public void setDash(EdgePattern pattern) {
		if (evsd == null) {
			return;
		}
		evsd.dash = pattern;
	}

    @Override
	public EdgePattern getDash() {
		if (evsd == null) {
			return null;
		}
		return evsd.dash;
	}
	
	@Override
	public Color getDefaultEdgeColor() {
		return default_color;
	}

	@Override
	public void copyFrom(EdgeAttributesReader fereader) {
		setLineColor(fereader.getLineColor());
		setCurve(fereader.isCurve());
		setPoints(fereader.getPoints());
		setLineEnd(fereader.getLineEnd());
		setLineWidth(fereader.getLineWidth());
		setDash(fereader.getDash());
 }
 
	@Override
	public void copyDefaultFrom(EdgeAttributesReader fvreader) {
		setDefaultEdgeSize(fvreader.getDefaultEdgeSize());
		setDefaultEdgeEndFill(fvreader.getDefaultEdgeEndFill());
		setDefaultCurve(fvreader.getDefaultCurve());
	}

	@Override
	public void setDefaultCurve(boolean b) {
		defaultcurve = b;
	}

	@Override
	public boolean isCurve() {
		if (evsd == null) {
			return false;
		}
		return evsd.curve;
	}

	@Override
	public void setCurve(boolean curve) {
		if (evsd == null) {
			return;
		}
		evsd.curve = curve;
	}

	@Override
	public boolean getDefaultCurve() {
		return defaultcurve;
	}

	
}
