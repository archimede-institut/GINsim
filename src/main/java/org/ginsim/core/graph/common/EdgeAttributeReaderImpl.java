package org.ginsim.core.graph.common;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ginsim.common.application.OptionStore;
import org.ginsim.core.graph.view.Bezier;
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

	private static final int MAX_EDGE_SIZE = 15;
	
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
    private Shape cachedPath = null;
    private List<Point> cachedPoints = null;
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
        	Shape path = getPath();
        	Rectangle b = path.getBounds();
        	int arrowmargin = 5;
        	cachedBounds = new Rectangle(b.x-arrowmargin, b.y-arrowmargin, b.width+2*arrowmargin, b.height+2*arrowmargin);
        }
        return cachedBounds;
    }

    
    public void setDefaultEdgeSize(float s) {
        if (s > MAX_EDGE_SIZE) {
        	s = MAX_EDGE_SIZE;
        } else if (s < 1) {
        	s = 1;
        }
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
        if (w > MAX_EDGE_SIZE) {
        	w = MAX_EDGE_SIZE;
        } else if (w < 1) {
        	w = 1;
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
    	cachedPath = null;
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
		dorender(g, null);
	}
	
	@Override
	public void renderMoving(Graphics2D g, MovingEdgeType type, int movex, int movey) {
		List<Point> points = ViewHelper.getMovingPoints(type, movex, movey, nreader, this, edge);
		dorender(g, points);
	}
	
	@Override
	public void renderMovingPoint(Graphics2D g, int idx, int movex, int movey) {
		List<Point> points = new ArrayList<Point>(getPoints());
		Point pt = points.get(idx);
		points.set(idx, new Point(pt.x+movex, pt.y+movey));
		dorender(g, ViewHelper.getModifiedPoints(nreader, this, edge, points));
	}



	private void dorender(Graphics2D g, List<Point> points) {
		g.setColor(getLineColor());
		stroke.setWidth(getLineWidth());
		g.setStroke(stroke);
		if (selected) {
			// TODO: better selection markup
			g.setColor(Color.PINK);
		}
		
		Shape s;
		if (points == null) {
			s = getPath();
			points = cachedPoints;
		} else {
			s = createPath(points, isCurve());
		}
		g.draw(s);

		// get the angle for the edge end
		int n = points.size()-1;
		Point pt1 = points.get(n);
		Point ptsrc = points.get(n-1);
		double theta = ViewHelper.getRotationAngle(pt1.x-ptsrc.x, pt1.y-ptsrc.y);;

		// draw the arrow end
		g.translate(pt1.x, pt1.y);
		g.rotate(theta);

		g.fill(getLineEnd().getShape());

		g.rotate(-theta);
		g.translate(-pt1.x, -pt1.y);

		if (selected) {
			// draw points
			g.setColor(Color.RED);
			for (Point pt: points) {
				g.fillRect(pt.x-2, pt.y-2, 4, 4);
			}
		}
	}
	
	@Override
	public boolean select(Point p) {
		if (evsd == null) {
			return false;
		}
		
		if (getBounds().contains(p)) {
			Shape path = getPath();
			return path.intersects(p.x-2, p.y-2, 5, 5);
		}
		
		return false;
	}

	private Shape createPath(List<Point> points, boolean curve) {

		Path2D path = new Path2D.Float();

		if (curve) {
			Point pt = points.get(0);
			path.moveTo(pt.x, pt.y);

			pt = points.get(1);
			Point2D[] b = new Bezier(points).getPoints();
			path.quadTo((float) b[0].getX(), 	(float) b[0].getY(), (float) pt.getX(), (float) pt.getY());
			int n = points.size();
			for (int i = 2; i < n - 1; i++) {
				Point2D b0 = b[2 * i - 3];
				Point2D b1 = b[2 * i - 2];
				pt = points.get(i);
				path.curveTo(
						(float) b0.getX(), (float) b0	.getY(),
						(float) b1.getX(), (float) b1.getY(),
						(float) pt.getX(), (float) pt.getY());
			}
			pt = points.get(n-1);
			path.quadTo(
					(float) b[b.length - 1].getX(),
					(float) b[b.length - 1].getY(),
					(float) pt.getX(), (float) pt.getY());

		} else {
			// just iterate a path with existing points
			Point pt_prev = null;
			for (Point pt: points) {
				if (pt_prev == null) {
					path.moveTo(pt.x, pt.y);
				} else {
					path.lineTo(pt.x, pt.y);
				}
				pt_prev = pt;
			}
		}
		
		return stroke.createStrokedShape(path);
	}

	
	private Shape getPath() {
		if (cachedPath == null) {
			cachedPoints = ViewHelper.getPoints(nreader, this, edge);
			cachedPath = createPath(cachedPoints, isCurve());
		}
		return cachedPath;
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
