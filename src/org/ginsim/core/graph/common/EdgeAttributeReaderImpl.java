package org.ginsim.core.graph.common;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ginsim.common.OptionStore;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.EdgeEnd;
import org.ginsim.core.graph.view.EdgePattern;
import org.ginsim.core.graph.view.MovingEdgeType;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.graph.view.SimpleStroke;
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
    private final Map<Edge<?>, EdgeVSdata> dataMap;
    
    private float defaultsize = 1;
    private boolean defaultfill = true;
    
    private Edge<?> edge;
    private EdgeVSdata evsd = null;
    private boolean selected = false;
    
    private boolean defaultcurve;
    
    private final NodeAttributesReader nreader;
    
    private Rectangle cachedBounds = null;
    private SimpleStroke stroke = new SimpleStroke();
    
    /**
     * @param dataMap
     */
    public EdgeAttributeReaderImpl(AbstractGraph backend, Map dataMap, NodeAttributesReader nreader) {
    	this.graph = backend;
        this.dataMap = dataMap;
        this.nreader = nreader;
    }
    
    public void setDefaultEdgeColor(Color color) {
    	
    	OptionStore.getOption( EDGE_COLOR, color.getRGB());
    	this.default_color = color;
    }

    @Override
    public Rectangle getBounds() {
        if (evsd == null) {
            return null;
        }
    	
        if (cachedBounds == null) {
        	List<Point> cachedPoints = ViewHelper.getPoints(nreader, this, edge);
        	Point p = cachedPoints.get(cachedPoints.size()-1);
        	// take into account some extra space for the arrow
        	int arrowmargin = 5;
        	int minx = p.x-arrowmargin;
        	int miny = p.y-arrowmargin;
        	int maxx = p.x+arrowmargin;
        	int maxy = p.y+arrowmargin;
        	
        	for (Point p1: cachedPoints) {
        		if (p1.x < minx) {
        			minx = p1.x;
        		} else if (p1.x > maxx) {
        			maxx = p1.x;
        		}
        		
        		if (p1.y < miny) {
        			miny = p1.y;
        		} else if (p1.y > maxy) {
        			maxy = p1.y;
        		}
        	}
        	
        	cachedBounds = new Rectangle(minx, miny, maxx-minx, maxy-miny);
        }
        return cachedBounds;
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

    public void setEdge(Edge obj) {
    	setEdge(obj, false);
    }
    public void setEdge(Edge obj, boolean selected) {
    	this.selected = selected;
    	if (obj == this.edge) {
    		return;
    	}
    	this.edge = obj;
    	cachedBounds = null;
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
	public void render(Graphics2D g) {
		List<Point> points = ViewHelper.getPoints(nreader, this, edge);
		dorender(g, points);
	}
	
	@Override
	public void renderMoving(Graphics2D g, MovingEdgeType type, int movex, int movey) {
		List<Point> points = ViewHelper.getMovingPoints(type, movex, movey, nreader, this, edge);
		dorender(g, points);
	}

	private void dorender(Graphics2D g, List<Point> points) {
		g.setColor(getLineColor());
		stroke.setWidth(getLineWidth());
		g.setStroke(stroke);
		if (selected) {
			// TODO: better selection markup
			g.setColor(Color.PINK);
		}
		double theta;
		if (isCurve()) {
			theta = dorender_curved(g, points);
		} else {
			theta = dorender_straight(g, points);
		}
		
		// draw the arrow end
		Point pt1 = points.get(points.size()-1);
		g.translate(pt1.x, pt1.y);
		g.rotate(theta);
		
		g.fill(getLineEnd().getShape());

		g.rotate(-theta);
		g.translate(-pt1.x, -pt1.y);
	}
	
	private double dorender_curved(Graphics2D g, List<Point> points) {
		
		// TODO: support curved edges as well
		return dorender_straight(g, points);
	}

	private double dorender_straight(Graphics2D g, List<Point> points) {
		Point pt1 = null;

		for (Point pt2: points) {
			if (pt1 != null) {
				g.drawLine(pt1.x, pt1.y, pt2.x, pt2.y);
			}
			pt1 = pt2;
		}
		
		Point ptsrc = points.get(points.size()-2);
		return ViewHelper.getRotationAngle(pt1.x-ptsrc.x, pt1.y-ptsrc.y);
	}

	@Override
	public boolean select(Point p) {
		if (evsd == null) {
			return false;
		}
		
		if (getBounds().contains(p)) {
			// TODO: finer check
		}
		
		return false;
	}

	@Override
	public void move(int dx, int dy) {
		if (evsd == null) {
			return;
		}
		List<Point> points = getPoints();
		if (points == null) {
			return;
		}
		for (Point p: points) {
			p.x += dx;
			p.y += dy;
		}
	}

}
