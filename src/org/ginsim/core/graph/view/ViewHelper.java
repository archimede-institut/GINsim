package org.ginsim.core.graph.view;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.ginsim.core.graph.common.Edge;

/**
 * Common logics to manage bounding boxes, anchor points and related tricks for edge routing.
 * 
 * This should be used by the view renderer and the SVG export, which should provide more flexibility and consistency.
 * 
 * @author Aurelien Naldi
 */
public class ViewHelper {

	/**
	 * Construct intermediate points for loops.
	 * 
	 * @param bounds the bounds of the item with the loop
	 * @return the list of points for a loop.
	 */
	private static PointList getPoints(Rectangle bounds) {

		PointList points = new PointList();
		
		int x = (int)bounds.getCenterX();
		int y = (int)bounds.getMinY();
		points.add(new Point(x-10, y));
		points.add(new Point(x-3, y-20));
		points.add(new Point(x+3, y-20));
		points.add(new Point(x+10, y));
		return points;
	}
	
	public static PointList getPoints(Rectangle srcBounds, Rectangle targetBounds, List<Point> middlePoints, int w) {

		if (middlePoints == null || middlePoints.size() < 1) {
			return getPoints(srcBounds, targetBounds, w);
		}
		
		PointList points = new PointList();

		points.add(new Point());
		for (Point p: middlePoints) {
			points.add(p);
		}
		points.add(getIntersection(targetBounds, middlePoints.get(middlePoints.size()-1), false, w));
		
		// replace the first point
		points.set(0, getIntersection(srcBounds, middlePoints.get(0), false, w));
		
		return points;
	}

	private static PointList getPoints(Rectangle srcBounds, Rectangle targetBounds, int w) {
		PointList points = new PointList();

		Point p = new Point((int)targetBounds.getCenterX(), (int)targetBounds.getCenterY());
		p = getIntersection(srcBounds, p, true, w);
		points.add(p);
		p = getIntersection(targetBounds, p, true, w);
		points.add(p);
		
		return points;
	}

	
    /**
     * Get the bounding box point to connect a box with an external point.
     * 
     * @param box
     * @param point
     * @param intersect
     * @param width
     * @return
     */
    public static Point getIntersection(Rectangle box, Point point, boolean intersect, float width) {
        if (box == null || box.contains(point)) {
            return point;
        }
        
        double minx, miny, maxx, maxy, px, py, resultx, resulty;
        minx = box.getMinX();
        miny = box.getMinY();
        maxx = box.getMaxX();
        maxy = box.getMaxY();
        px = point.getX();
        py = point.getY();
        double offset = width/2 + 2;

        if (intersect) {        // compute intersection of the box with the line from its center to the point
            double centerx = box.getCenterX(), centery = box.getCenterY();
            double dx = px-centerx, dy = py-centery;
            if (dy == 0) {
                resulty = centery;
                resultx = dx > 0 ? minx : maxx;
            }
            double ratio = dx/dy;
            double boxRatio = box.getWidth() / box.getHeight();
            if (Math.abs(ratio) > boxRatio) {     // crosses on one of the vertical sides
                if (dx > 0) {
                    resultx = maxx + offset;
                    resulty = centery + (maxx-centerx)/ratio;
                } else {
                    resultx = minx - offset;
                    resulty = centery - (maxx-centerx)/ratio;
                }
            } else {                    // crosses on one of the horizontal sides
                if (dy > 0) {
                    resulty = maxy + offset;
                    resultx = centerx + (maxy-centery)*ratio;
                } else {
                    resulty = miny - offset;
                    resultx = centerx - (maxy-centery)*ratio;
                }
            }
            
        } else {                        // find the closest point in the bounding box
            if (px > maxx) {
                resultx = maxx + offset;
            } else if (px < minx) {
                resultx = minx - offset;
            } else {
                resultx = px;
            }
    
            if (py > maxy) {
                resulty = maxy + offset;
            } else if (py < miny) {
                resulty = miny - offset;
            } else {
                resulty = py;
            }
        }
        Point r = new Point((int)resultx, (int)resulty);
        return r;
    }

    public static Point2D getIntersection(Rectangle bounds, Point target) {
		return getIntersection(bounds, target, 2);
	}

	private static Point2D getIntersection(Rectangle bounds, Point target, int w) {
		return getIntersection(bounds, target, true, w);
	}

	/**
	 * Get the bounding box of a node.
	 * 
	 * @param nodeReader
	 * @param node
	 * @return
	 */
	private static Rectangle getBounds(NodeAttributesReader nodeReader, Object node) {
		
		nodeReader.setNode(node);
		int x = nodeReader.getX();
		int y = nodeReader.getY();
		int w = nodeReader.getWidth();
		int h = nodeReader.getHeight();
		
		return new Rectangle(x, y, w, h);
	}

	/**
	 * Route an edge: add automated intermediate points if needed, compute the points on the nodes bounding boxes.
	 * 
	 * @param nodeReader
	 * @param edgeReader
	 * @param edge
	 * 
	 * @return the list of points forming the edge
	 */
	public static List<Point> getPoints(NodeAttributesReader nodeReader, EdgeAttributesReader edgeReader, Edge<?> edge) {
		PointList points = doGetPoints(nodeReader, edgeReader, edge);
		points.setEdge(edgeReader, edge);
		return points;
	}

	public static List<Point> getMovingPoints(MovingEdgeType type, int movex, int movey, NodeAttributesReader nreader, EdgeAttributesReader ereader, Edge<?> edge) {
		Object source = edge.getSource();
		Object target = edge.getTarget();
		
		if (source == target) {
			Rectangle box = getBounds(nreader, source);
			if (type.source) {
				box = translateRectangle(box, movex, movey);
			}
			return getPoints(box);
		}
		
		Rectangle b1 = getBounds(nreader, source);
		if (type.source) {
			b1 = translateRectangle(b1, movex, movey);
		}
		Rectangle b2 = getBounds(nreader, target);
		if (type.target) {
			b2 = translateRectangle(b2, movex, movey);
		}

		List<Point> realPoints = ereader.getPoints();
		int w = (int)ereader.getLineWidth();
		
		if (realPoints == null || realPoints.size() == 0) {
			return getPoints(b1, b2, w);
		}
		
		if (type.edge) {
			// move points
		}
		return getPoints(b1, b2, realPoints, w);
	}

	private static Rectangle translateRectangle(Rectangle box, int movex, int movey) {
		return new Rectangle(box.x+movex, box.y+movey, box.width, box.height);
	}
	
	public static PointList doGetPoints(NodeAttributesReader nodeReader, EdgeAttributesReader edgeReader, Edge<?> edge) {
		Object source = edge.getSource();
		Object target = edge.getTarget();
		
		if (source == target) {
			Rectangle box = getBounds(nodeReader, source);
			return getPoints(box);
		}
		
		Rectangle b1 = getBounds(nodeReader, source);
		Rectangle b2 = getBounds(nodeReader, target);
		
		List<Point> realPoints = edgeReader.getPoints();
		int w = (int)edgeReader.getLineWidth();
		
		if (realPoints == null || realPoints.size() == 0) {
			return getPoints(b1, b2, w);
		}
		
		return getPoints(b1, b2, realPoints, w);
	}

	/**
	 * Remove the first and last points if they are inside the source and target bounding box
	 * 
	 * @param nreader
	 * @param ereader
	 * @param edge
	 */
	public static void trimPoints(Edge edge, NodeAttributesReader nreader, EdgeAttributesReader ereader) {
		
		Rectangle b1 = getBounds(nreader, edge.getSource());
		Rectangle b2 = getBounds(nreader, edge.getTarget());
		
		ereader.setEdge(edge);
		List<Point> points = ereader.getPoints();
		if (points == null || points.size() < 1) {
			return;
		}
		
		if (b1.contains(points.get(0))) {
			points.remove(0);
		}
		int last = points.size()-1;
		if (b2.contains(points.get(last))) {
			points.remove(last);
		}
	}

	/**
	 * Get the rotation angle between a vector and the horizontal axis.
	 * 
	 * @param dx
	 * @param dy
	 * @return
	 */
	public static double getRotationAngle(int dx, int dy) {

		// fast answer for simple cases (and avoid dividing by 0)
		if (dy == 0) {
			if (dx < 0) {
				return Math.PI;
			}
			return 0;
		}
		if (dx == 0) {
			if (dy > 0) {
				return Math.PI/2;
			}
			return -Math.PI/2;
		}

		// estimate angle using inverse tangent function
		// Note: the tangent is the ratio dy/dx (height reached for dx=1)
		double theta = Math.atan(dy / (double)dx);
		
		// find the proper angle when dx < 0
		if (dx < 0) {
			theta += Math.PI;
		}
		return theta;
	}

	/**
	 * Get a rectangle given its two angle points.
	 * 
	 * @param lastPoint
	 * @param draggedPoint
	 * @return
	 */
	public static Rectangle getRectangle(Point lastPoint, Point draggedPoint) {
		int x1 = lastPoint.x;
		int x2 = draggedPoint.x;
		int y1 = lastPoint.y;
		int y2 = draggedPoint.y;
		
		return getRectangle(x1, y1, x2, y2);
	}
	/**
	 * Get a rectangle given its two angle points.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public static Rectangle getRectangle(int x1, int y1, int x2, int y2) {
		int tmp;
		if (x2 < x1) {
			tmp = x1;
			x1 = x2;
			x2 = tmp;
		}
		if (y2 < y1) {
			tmp = y1;
			y1 = y2;
			y2 = tmp;
		}
		return new Rectangle(x1, y1, x2-x1, y2-y1);
	}

}

class PointList extends ArrayList<Point> {
	private static final long serialVersionUID = 6183889721767644621L;
	
	private EdgeAttributesReader reader;
	private Edge<?> edge;
	
	protected void setEdge(EdgeAttributesReader reader, Edge<?> edge) {
		this.reader = reader;
		this.edge = edge;
	}
	
	public void add(int index, Point p) {
		if (edge == null) {
			super.add(index, p);
			return;
		}
		
		reader.setEdge(edge);
		List<Point> points = reader.getPoints();
		if (points == null) {
			points = new ArrayList<Point>();
			reader.setPoints(points);
		}
		points.add(index-1, p);
		super.add(index, p);
	}

	public Point remove(int index) {
		List<Point> points = reader.getPoints();
		if (points != null) {
			points.remove(index-1);
		}
		return super.remove(index);
	}
}
