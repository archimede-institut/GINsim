package org.ginsim.core.graph.view;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.ginsim.common.utils.log.LogManager;
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
	 * @param bounds
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
	
	private static PointList getPoints(Rectangle srcBounds, Rectangle targetBounds, List<Point> middlePoints, int w) {

		if (middlePoints == null || middlePoints.size() < 1) {
			return getPoints(srcBounds, targetBounds, w);
		}
		
		PointList points = new PointList();

		points.add(new Point());
		for (Point p: middlePoints) {
			points.add(p);
		}
		points.add(getIntersection(targetBounds, middlePoints.get(middlePoints.size()-1), true, w));
		
		// replace the first point
		points.set(0, getIntersection(srcBounds, middlePoints.get(0), true, w));
		
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

	private static PointList getPoints(NodeAttributesReader nodeReader, Object src) {
		Rectangle bounds = getBounds(nodeReader, src);
		return getPoints(bounds);
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
}

class PointList extends ArrayList<Point> {
	
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
		
		LogManager.debug("Adding point to "+ edge + " -- ("+index+") --> "+p);
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
		LogManager.debug("Remove point: "+index);
		List<Point> points = reader.getPoints();
		if (points != null) {
			points.remove(index-1);
		}
		return super.remove(index);
	}
}
