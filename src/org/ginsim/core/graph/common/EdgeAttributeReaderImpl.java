package org.ginsim.core.graph.common;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ginsim.common.OptionStore;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.EdgeEnd;
import org.ginsim.core.graph.view.EdgePattern;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.graph.view.ViewHelper;


/**
 * a generic edgeAttributeReader storing data into a dedicated hashmap
 */
public class EdgeAttributeReaderImpl implements EdgeAttributesReader {

	public static final String EDGE_COLOR = "vs.edgecolor";
	
    protected static Map<String, float[]> m_pattern = null;
	
    public static Color default_color = new Color(OptionStore.getOption( EDGE_COLOR, -13395457));
    
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

    public void setLineEnd(EdgeEnd index) {
        if (evsd == null) {
            return;
        }
        evsd.end = index;
    }

    public EdgeEnd getLineEnd() {
        if (evsd == null || evsd.end == null) {
            return EdgeEnd.POSITIVE;
        }
        return evsd.end;
    }

    public List<Point> getPoints() {
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
        protected EdgeEnd end;
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
		setLineEnd(fereader.getLineEnd());
		setLineWidth(fereader.getLineWidth());
		setDash(fereader.getDash());
		
		List<Point> oldPoints = fereader.getPoints();
		if (oldPoints != null) {
			List<Point> points = new ArrayList<Point>(oldPoints.size());
			for (Point point : oldPoints) {
				points.add((Point) point.clone());
			}
			setPoints(points);			
		} else {
			setPoints(null);
		}
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

	@Override
	public void render(NodeAttributesReader nreader, Edge edge, Graphics2D g) {
		setEdge(edge);
		List<Point> points = ViewHelper.getPoints(nreader, this, edge);
		g.setColor(getLineColor());
		Point pt1 = null;

		for (Point pt2: points) {
			if (pt1 != null) {
				g.drawLine(pt1.x, pt1.y, pt2.x, pt2.y);
			}
			pt1 = pt2;
		}
	}

}
