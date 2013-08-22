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
import org.ginsim.core.graph.backend.GraphBackend;
import org.ginsim.core.graph.view.Bezier;
import org.ginsim.core.graph.view.DefaultEdgeStyle;
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
public class EdgeAttributeReaderImpl<V, E extends Edge<V>> implements EdgeAttributesReader<V,E> {

	private static final int MAX_EDGE_SIZE = 7;
	
	public static final String EDGE_COLOR = "vs.edgecolor";
	
    protected static Map<String, float[]> m_pattern = null;
	
	private final GraphBackend<V, E> graph;
    private final Map<E, EdgeVSdata> dataMap;
    private final DefaultEdgeStyle<V,E> defaultStyle;
    
    private E edge;
    private EdgeVSdata evsd = null;
    private boolean selected = false;
    private boolean hasChanged = false;
    
    private final NodeAttributesReader nreader;
    
    private Rectangle cachedBounds = null;
    private Shape cachedPath = null;
    private List<Point> cachedPoints = null;
    private SimpleStroke stroke = new SimpleStroke();
    
    /**
     * @param dataMap
     */
    public EdgeAttributeReaderImpl(DefaultEdgeStyle defaultStyle, GraphBackend<V, E> backend, Map dataMap, NodeAttributesReader nreader) {
    	this.graph = backend;
        this.dataMap = dataMap;
        this.nreader = nreader;
        this.defaultStyle = defaultStyle;
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
        	float w = getLineWidth();
        	if (w > 2) {
        		arrowmargin = (int) ((1 + arrowmargin*w) / 2);
        	}
        	cachedBounds = new Rectangle(b.x-arrowmargin, b.y-arrowmargin, b.width+2*arrowmargin, b.height+2*arrowmargin);
        }
        return cachedBounds;
    }
    
    @Override
    public float getLineWidth() {
        if (evsd == null) {
            return 0;
        }
        return evsd.size;
    }

    @Override
    public void setLineWidth(float w) {
        if (evsd == null) {
            return;
        }
        if (w > MAX_EDGE_SIZE) {
        	w = MAX_EDGE_SIZE;
        } else if (w < 1) {
        	w = 1;
        }
        if (evsd.size != w) {
        	evsd.size = w;
        	hasChanged = true;
        }
    }

    @Override
    public void setEdge(E obj) {
    	setEdge(obj, false);
    }
    @Override
    public void setEdge(E obj, boolean selected) {
    	this.selected = selected;
    	if (obj == this.edge) {
    		return;
    	}
    	this.edge = obj;
    	cachedBounds = null;
    	cachedPath = null;
        evsd = dataMap.get(obj);
        if (evsd == null) {
            evsd = new EdgeVSdata();
            
            evsd.color = null;
            evsd.curve = false;
            evsd.size = defaultStyle.getWidth(edge);
            
            dataMap.put(obj, evsd);
        }
    	hasChanged = false;
    }

    @Override
    public void setLineColor(Color color) {
        if (evsd != null && evsd.color != color) {
            evsd.color = color;
            hasChanged = true;
        }
    }

    @Override
    public Color getLineColor() {
        if (evsd == null || evsd.color == null) {
            return defaultStyle.getColor(edge);
        }
        return evsd.color;
    }

    @Override
    public void refresh() {
    	if (edge != null && hasChanged) {
    		graph.damage(edge);
    	}
    }
    @Override
    public void damage() {
    	if (edge != null) {
    		graph.damage(edge);
    	}
    }

    @Override
    public void setLineEnd(EdgeEnd index) {
        if (evsd != null && evsd.end != index) {
        	evsd.end = index;
        	hasChanged = true;
        }
    }

    @Override
    public EdgeEnd getLineEnd() {
        if (evsd == null || evsd.end == null) {
            return EdgeEnd.POSITIVE;
        }
        return evsd.end;
    }

    @Override
    public List<Point> getPoints() {
        if ( evsd == null ) {
            return null;
        }
        return evsd.points;
    }

    @Override
    public void setPoints(List l) {
        if (evsd == null) {
            return;
        }
        evsd.points = l;
        hasChanged = true;
    }

    class EdgeVSdata {
        protected Color color;
        protected float size;
        protected EdgeEnd end;
        protected boolean curve;
        protected List points;
		protected EdgePattern dash;
    }

    @Override
	public void setDash(EdgePattern pattern) {
		if (evsd != null && evsd.dash != pattern) {
			evsd.dash = pattern;
			hasChanged = true;
		}
	}

    @Override
	public EdgePattern getDash() {
		if (evsd == null) {
			return null;
		}
		return evsd.dash;
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
	public boolean isCurve() {
		if (evsd == null) {
			return false;
		}
		return evsd.curve;
	}

	@Override
	public void setCurve(boolean curve) {
		if (evsd != null && evsd.curve != curve) {
			evsd.curve = curve;
			hasChanged = true;
		}
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
		float width = getLineWidth();
		stroke.setWidth(width);
		stroke.setDashPattern(getDash());
		g.setStroke(stroke);
		
		Shape s;
		if (points == null) {
			s = getPath();
			points = cachedPoints;
		} else {
			s = createPath(points, isCurve());
		}
		if (selected) {
			g.setColor(Color.PINK);
			g.draw(stroke.createStrokedShape(s));
		}
		g.setColor(getLineColor());
		g.fill(stroke.createStrokedShape(s));
		


		// get the angle for the edge end
		int n = points.size()-1;
		Point pt1 = points.get(n);
		Point ptsrc = points.get(n-1);
		double theta = ViewHelper.getRotationAngle(pt1.x-ptsrc.x, pt1.y-ptsrc.y);;

		// draw the arrow end
		g.translate(pt1.x, pt1.y);
		g.rotate(theta);

		Shape lineEnd = getLineEnd().getShape();
		if (width > 2) {
			float scale = width / 2;
			g.scale(scale, scale);
			g.fill(lineEnd);
			scale = 1/scale;
			g.scale(scale, scale);
		} else {
			g.fill(lineEnd);
		}

		g.rotate(-theta);
		g.translate(-pt1.x, -pt1.y);

		if (selected) {
			// draw points
			int maxpoint = points.size()-1;
			for (int i=0 ; i<= maxpoint ; i++) {
				Point pt = points.get(i);
				if (i==0 || i==maxpoint) {
					g.setColor(Color.GRAY);
					g.fillRect(pt.x-2, pt.y-2, 4, 4);
				} else {
					g.setColor(Color.RED);
					g.fillRect(pt.x-3, pt.y-3, 6, 6);
				}
			}
		}
	}
	
	@Override
	public boolean select(Point p) {
		if (evsd == null) {
			return false;
		}
		
		if (getBounds().contains(p)) {
			Shape path = stroke.createSimpleStrokedShape(getPath());
			return path.intersects(p.x-2, p.y-2, 5, 5);
		}
		
		return false;
	}

	private Shape createPath(List<Point> points, boolean curve) {

		Path2D path = new Path2D.Float();

		if (curve && points != null && points.size() > 2) {
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
		
		return path;
	}

	
	private Shape getPath() {
		if (cachedPath == null) {
			stroke.setWidth(getLineWidth());
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

	@Override
	public DefaultEdgeStyle<V, E> getDefaultEdgeStyle() {
		return defaultStyle;
	}
}
