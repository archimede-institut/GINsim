package org.ginsim.core.graph.view;

import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.ginsim.core.graph.Edge;

/**
 * Common logics to manage bounding boxes, anchor points and related tricks for edge routing.
 * 
 * This should be used by the view renderer and the SVG export, which should provide more flexibility and consistency.
 * 
 * @author Aurelien Naldi
 */
public class ViewHelper {

    public static final Font GRAPHFONT;

    static {
        // TODO: pick a better font??
        GRAPHFONT = new Font( "SansSerif", Font.PLAIN, 12 );
    }


    /**
	 * Construct intermediate points for loops.
	 * 
	 * @param bounds the bounds of the item with the loop
	 * @return the list of points for a loop.
	 */
	private static PointList getPoints(Rectangle bounds, EdgeAnchor anchor) {

		PointList points = new PointList();

		int h = (int)bounds.getHeight();
		int w = (int)bounds.getWidth();
		
		int md = (int)bounds.getWidth();
		int d = (int)bounds.getHeight();
		if (d < md) {
			md = d/2;
		} else {
			md = md/2;
		}
		d = 15;
		if (md > d) {
			md = d;
		} else if (d > md*2) {
			d = md*2;
		}
		
		int x = (int)bounds.getMaxX();
		int y = (int)bounds.getMinY();
		switch (anchor) {
		case SE:
			points.add(new Point(x+2, y+md));
			points.add(new Point(x+d, y+md));
			points.add(new Point(x+d, y+h+md));
			points.add(new Point(x-md, y+h+md));
			points.add(new Point(x-md, y+h+2));
			break;
		case SW:
			points.add(new Point(x-w-2, y+md));
			points.add(new Point(x-w-d, y+md));
			points.add(new Point(x-w-d, y+h+md));
			points.add(new Point(x-w+md, y+h+md));
			points.add(new Point(x-w+md, y+h+2));
			break;
		case NW:
			points.add(new Point(x-w+md, y));
			points.add(new Point(x-w+md, y-d));
			points.add(new Point(x-w-d, y-d));
			points.add(new Point(x-w-d, y+md));
			points.add(new Point(x-w-2, y+md));
			break;
		default:
			points.add(new Point(x-md, y));
			points.add(new Point(x-md, y-d));
			points.add(new Point(x+d, y-d));
			points.add(new Point(x+d, y+md));
			points.add(new Point(x+2, y+md));
			break;
		}
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
		points.set(0, getIntersection(srcBounds, middlePoints.get(0), false, 0));
		
		return points;
	}

	/**
	 * Pick points on the borders of two nodes to connect them
	 *
	 * @param srcBounds
	 * @param targetBounds
	 * @param w
	 * @return
	 */
	private static PointList getPoints(Rectangle srcBounds, Rectangle targetBounds, int w) {
		PointList points = new PointList();

		int srcX = (int)srcBounds.getCenterX();
		int srcY = (int)srcBounds.getCenterY();
		int tgtX = (int)targetBounds.getCenterX();
		int tgtY = (int)targetBounds.getCenterY();

		if (srcX > targetBounds.x && tgtX > srcBounds.x) {
			int useX = (srcX + tgtX) / 2;
			if (srcY < tgtY) {
				// Move vertically downward
				points.add( new Point(useX, srcBounds.y + srcBounds.height));
				points.add( new Point(useX, targetBounds.y));
				return points;
			}

			// Move vertically upward
			points.add( new Point(useX, srcBounds.y));
			points.add( new Point(useX, targetBounds.y + targetBounds.height));
			return points;
		}

		if (srcY > targetBounds.y && tgtY > srcBounds.y) {
			int useY = (srcY + tgtY) / 2;
			if (srcX < tgtX) {
				// Move horizontally rightward
				points.add( new Point(srcBounds.x + srcBounds.width, useY));
				points.add( new Point(targetBounds.x, useY));
				return points;
			}

			// Move horizontally leftward
			points.add( new Point(srcBounds.x, useY));
			points.add( new Point(targetBounds.x + targetBounds.width, useY));
			return points;
		}

		Point p = new Point(tgtX, tgtY);
		p = getIntersection(srcBounds, p, true, 0);
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
		
		return getBounds(nodeReader, node, 0);
	}

	/**
	 * Get the bounding box of a node, with a custom margin.
	 * 
	 * @param nodeReader
	 * @param node
	 * @param margin
	 * @return
	 */
	private static Rectangle getBounds(NodeAttributesReader nodeReader, Object node, int margin) {
		
		nodeReader.setNode(node);
		int x = nodeReader.getX()-margin;
		int y = nodeReader.getY()-margin;
		int w = nodeReader.getWidth() +2*margin;
		int h = nodeReader.getHeight() + 2*margin;
		
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
        Object source = edge.getSource();
        Object target = edge.getTarget();

        List<Point> realPoints = getRealPoints(edge, edgeReader);

        if (source == target) {
            if (realPoints == null) {
                Rectangle box = getBounds(nodeReader, source);
                return getPoints(box, edgeReader.getAnchor());
            }
        }

        Rectangle b1 = getBounds(nodeReader, source);
        Rectangle b2 = getBounds(nodeReader, target);

        int w = (int)edgeReader.getLineWidth();

        if (realPoints == null && edgeReader.hasReverseEdge()) {
            realPoints = getReversedShift(b1, b2);
        }

        if (realPoints == null) {
            return getPoints(b1, b2, w);
        }

        PointList points = getPoints(b1, b2, realPoints, w);
		points.setEdge(edgeReader, edge);
		return points;
	}

    /**
     * Get the routing points for an edge which is affected by a move preview.
     * It is similar to the normal getPoint method, but some parts of the edge
     * (the start or the end or the points) can be shifted independently.
     *
     * @param type
     * @param movex
     * @param movey
     * @param nreader
     * @param ereader
     * @param edge
     * @return
     */
	public static List<Point> getMovingPoints(MovingEdgeType type, int movex, int movey, NodeAttributesReader nreader, EdgeAttributesReader ereader, Edge<?> edge) {
		Object source = edge.getSource();
		Object target = edge.getTarget();
		
		List<Point> realPoints = getRealPoints(edge, ereader);

		if (source == target && realPoints == null) {
			Rectangle box = getBounds(nreader, source);
			if (type.source) {
				box = translateRectangle(box, movex, movey);
			}
			return getPoints(box, ereader.getAnchor());
		}
		
		Rectangle b1 = getBounds(nreader, source);
		if (type.source) {
			b1 = translateRectangle(b1, movex, movey);
		}
		Rectangle b2 = getBounds(nreader, target);
		if (type.target) {
			b2 = translateRectangle(b2, movex, movey);
		}

		int w = (int)ereader.getLineWidth();


        if (realPoints == null && ereader.hasReverseEdge()) {
            realPoints = getReversedShift(b1, b2);
        }

        if (realPoints == null) {
			return getPoints(b1, b2, w);
		}
		
		if (type.edge) {
            realPoints = translatePoints(realPoints, movex, movey);
		}
		return getPoints(b1, b2, realPoints, w);
	}

    private static List<Point> getRealPoints(Edge<?> edge, EdgeAttributesReader ereader) {
        List<Point> realPoints = ereader.getPoints();
        Object source = edge.getSource();
        Object target = edge.getTarget();

        if (source == target) {
            if (realPoints != null && realPoints.size() < 3) {
                realPoints = null;
                ereader.setPoints(realPoints);
            }
        } else if (realPoints != null && realPoints.size() < 1) {
            realPoints = null;
            ereader.setPoints(realPoints);
        }

        return realPoints;
    }

    private static Rectangle translateRectangle(Rectangle box, int movex, int movey) {
        return new Rectangle(box.x+movex, box.y+movey, box.width, box.height);
    }
    private static List<Point> translatePoints(List<Point> points, int movex, int movey) {
        List<Point> result = new ArrayList<Point>(points.size());
        for (Point p: points) {
            result.add(new Point(p.x+movex, p.y+movey));
        }
        return result;
    }

    private static List<Point> getReversedShift(Rectangle b1, Rectangle b2) {
        if (b1.intersects(b2)) {
            return null;
        }

        int x1 = (int)b1.getCenterX();
        int y1 = (int)b1.getCenterY();

        int x2 = (int)b2.getCenterX();
        int y2 = (int)b2.getCenterY();

        int dx = (x2-x1)/3;
        int dy = (y2-y1)/3;

        double d = Math.sqrt(dx * dx + dy * dy) / 15;

        int x = x1 + dx + (int)(dy/d);
        int y = y1 + dy - (int)(dx/d);

        List<Point> points = new ArrayList<Point>(1);
        points.add(new Point(x+dx,y+dy));
        return points;
    }

	public static PointList getModifiedPoints(NodeAttributesReader nodeReader, EdgeAttributesReader edgeReader, Edge<?> edge, List<Point> modifiedPoints) {
		Object source = edge.getSource();
		Object target = edge.getTarget();
		
		if (source == target && modifiedPoints == null) {
			Rectangle box = getBounds(nodeReader, source);
			return getPoints(box, edgeReader.getAnchor());
		}
		
		Rectangle b1 = getBounds(nodeReader, source);
		Rectangle b2 = getBounds(nodeReader, target);
		
		int w = (int)edgeReader.getLineWidth();
		
		if (modifiedPoints == null || modifiedPoints.size() == 0) {
			return getPoints(b1, b2, w);
		}
		
		return getPoints(b1, b2, modifiedPoints, w);
	}

	/**
	 * Remove the first and last points if they are inside the source and target bounding box
	 * 
	 * @param nreader
	 * @param ereader
	 * @param edge
	 */
	public static void trimPoints(Edge edge, List<Point> points, NodeAttributesReader nreader, EdgeAttributesReader ereader) {
		
		ereader.setEdge(edge);
		int margin = (int)ereader.getLineWidth()+1;
		Rectangle b1 = getBounds(nreader, edge.getSource(), margin);
		Rectangle b2 = getBounds(nreader, edge.getTarget(), margin);
		
		if (points == null || points.size() < 1) {
			return;
		}
		
		if ( contained(points.get(0), b1)) {
			points.remove(0);
		}
		int last = points.size()-1;
		if (contained(points.get(last), b2)) {
			points.remove(last);
		}
	}

	private static boolean contained(Point p, Rectangle rect) {
		if (p.x < rect.x - 3 || p.x > rect.x+rect.width+3 || p.y < rect.y - 3 || p.y > rect.y + rect.height + 3) {
			return false;
		}
		return true;
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
