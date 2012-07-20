package org.ginsim.gui.graph.canvas.events;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ginsim.common.application.LogManager;
import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.common.GraphChangeType;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.MovingEdgeType;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.graph.view.ViewHelper;
import org.ginsim.gui.graph.GraphSelection;
import org.ginsim.gui.graph.canvas.CanvasEventManager;
import org.ginsim.gui.graph.canvas.GraphCanvasRenderer;
import org.ginsim.gui.shell.editpanel.SelectionType;

public class GraphSelectionCanvasEventManager implements CanvasEventManager {

	private final GraphCanvasRenderer renderer;
	private final GraphSelection selection;
	private final Graph graph;
	private final NodeAttributesReader nreader;
	private final EdgeAttributesReader ereader;
	
	private DragStatus dragstatus = DragStatus.NODRAG;
	private MovingPoint movingPoint = null;
	
	private Point startPoint=null;
	int movex=0, movey=0;
	
	/**
	 * Edges that have to be moved as either them or their attached nodes are selected
	 */
	private Map<Edge, MovingEdgeType> movingEdges = new HashMap<Edge, MovingEdgeType>();

	
	public GraphSelectionCanvasEventManager(Graph<?,?> graph, GraphCanvasRenderer renderer, GraphSelection selection) {
		this.renderer = renderer;
		this.selection = selection;
		
		this.graph = graph;
		this.nreader = graph.getNodeAttributeReader();
		this.ereader = graph.getEdgeAttributeReader();
	}
	
	@Override
	public void click(Point p, boolean alternate) {
		dragstatus = DragStatus.NODRAG;
	}

	@Override
	public void pressed(Point p, boolean alternate) {
		dragstatus = DragStatus.NODRAG;
		startPoint = p;
		
		Object o = renderer.getObjectUnderPoint(p);
		
		if (alternate) {
			if (o == null) {
				// do nothing
			} else if (renderer.selectionCache.contains(o)) {
				// remove it from selection
				if (o instanceof Edge) {
					if (switchEdgePoint((Edge)o, p)) {
						// nothing to do here
					} else {
						selection.unselectEdge((Edge)o);
					}
				} else {
					selection.unselectNode(o);
				}
			} else {
				// extend selection
				if (o instanceof Edge) {
					selection.addEdgeToSelection((Edge)o);
				} else {
					selection.addNodeToSelection(o);
				}
			}
		} else if (detectMovingPoint(p)) {
			// nothing to do here
		} else {
			// reset the selection
			if (o == null) {
				selection.unselectAll();
			} else if (o instanceof Edge) {
				selection.selectEdge((Edge)o);
			} else {
				selection.selectNode(o);
			}
		}
	}

	@Override
	public void released(Point p) {
		if (startPoint != null) {
			movex = p.x-startPoint.x;
			movey = p.y-startPoint.y;
		} else {
			movex = movey = 0;
		}
		
		switch (dragstatus) {
		case MOVEPOINT:
			if (movingPoint == null) {
				LogManager.error("MOVEPOINT without an actual moving point");
				break;
			}
			renderer.damageItem(movingPoint.edge);
			ereader.setEdge(movingPoint.edge);
			List<Point> points = ereader.getPoints();
			Point pt = points.get(movingPoint.pointIdx);
			points.set(movingPoint.pointIdx, new Point(pt.x+movex, pt.y + movey));
			graph.fireGraphChange(GraphChangeType.EDGEUPDATED, movingPoint.edge);
			renderer.damageItem(movingPoint.edge);
			movingPoint = null;
			dragstatus = DragStatus.NODRAG;
			movingEdges.clear();
			renderer.repaintCanvas();
			break;
		case MOVE:
			// first mark all moving items as damaged
			for (Object o: movingEdges.keySet()) {
				renderer.damageItem(o);
			}
			for (Object o: renderer.selectionCache) {
				renderer.damageItem(o);
			}
			
			// then move them
			for (Object o: renderer.selectionCache) {
				if (o instanceof Edge) {
					ereader.setEdge((Edge)o);
					ereader.move(movex, movey);
				} else {
					nreader.setNode(o);
					nreader.move(movex, movey);
				}
				renderer.damageItem(o);
			}

			// finally mark them as damaged again and repaint
			for (Object o: movingEdges.keySet()) {
				renderer.damageItem(o);
			}
			for (Object o: renderer.selectionCache) {
				renderer.damageItem(o);
			}
			dragstatus = DragStatus.NODRAG;
			movingEdges.clear();
			renderer.repaintCanvas();
			break;

		case SELECT:
			
			Rectangle selectionClip = ViewHelper.getRectangle(startPoint.x, startPoint.y, startPoint.x+movex, startPoint.y+movey);
			renderer.select(selectionClip);
			dragstatus = DragStatus.NODRAG;
			renderer.repaintCanvas();
			break;
		default:
			break;
		}
		
		startPoint = null;
		movex = movey = 0;
		movingPoint = null;
	}

	@Override
	public void dragged(Point p) {
		if (startPoint == null) {
			return;
		}
		if (dragstatus == DragStatus.NODRAG) {
			if (renderer.selectionCache.size() > 0) {
				dragstatus = DragStatus.MOVE;
				
				if (movingPoint != null) {
					dragstatus = DragStatus.MOVEPOINT;
				} else {
					detectMovingEdges();
				}
			} else {
				dragstatus = DragStatus.SELECT;
			}
		}
		
		movex = p.x - startPoint.x;
		movey = p.y - startPoint.y;
		renderer.repaintCanvas();
	}

	/**
	 * Detect if the point is suitable to trigger a edge point move.
	 * if it returns true, <code>movingPoint</code> will hold the selected edge point.
	 * Otherwise, it will be null.
	 * 
	 * @param p
	 * @return
	 */
	private boolean detectMovingPoint(Point p) {
		movingPoint = null;
		if (selection.getSelectionType() != SelectionType.SEL_EDGE) {
			return false;
		}
		// look for edge point selection
		Edge e = (Edge)selection.getSelectedEdges().get(0);
		ereader.setEdge(e);
		
		List<Point> points = ereader.getPoints();
		if (points == null || points.size() < 2) {
			return false;
		}
		
		int i=0;
		for (Point pt: points) {
			if (Math.abs(pt.x-p.x) < 3 && Math.abs(pt.y-p.y) < 3) {
				movingPoint = new MovingPoint(e, i);
				return true;
			}
			i++;
		}
		return false;
	}

	/**
	 * Test if the given point can be used to add or remove an intermediate point
	 * in the selected edge.
	 * If the point matches an existing point, it will be removed.
	 * Otherwise a new point will be added and movingPoint will point to this new point.
	 * It will not be perfect for curved edges but should work well enough in most cases...
	 * 
	 * @param under the edge detected under the point. It may not be the selected edge (will then return immediately)
	 * @param p the clicked point
	 * @return true if a point was added or removed
	 */
	private boolean switchEdgePoint(Edge under, Point p) {
		movingPoint = null;
		if (selection.getSelectionType() != SelectionType.SEL_EDGE) {
			return false;
		}
		// look for edge point selection
		Edge e = (Edge)selection.getSelectedEdges().get(0);
		if (e != under) {
			return false;
		}
		
		ereader.setEdge(e);
		
		List<Point> points = ereader.getPoints();
		if (points == null) {
			points = new ArrayList<Point>();
			points.add(p);
			renderer.damageItem(e);
			ereader.setPoints(points);
			movingPoint = new MovingPoint(e, 0);
			return true;
		}
		
		int i=0;
		for (Point pt: points) {
			if (Math.abs(pt.x-p.x) < 3 && Math.abs(pt.y-p.y) < 3) {
				renderer.damageItem(e);
				points.remove(i);
				return true;
			}
			i++;
		}

		int idx = points.size();
		double best = Double.MAX_VALUE;
		Point prev = null;
		i = -1;
		for (Point pt: ViewHelper.getPoints(nreader, ereader, e)) {
			if (prev != null) {
				double delta = scorePointSegment(p, prev, pt);
				if (delta < best) {
					best = delta;
					idx = i;
				}
			}
			i++;
			prev = pt;
		}
		
		points.add(idx, p);
		movingPoint = new MovingPoint(e, idx);
		return true;
	}
	
	/**
	 * Ugly hack to estimate how far a point is from a segment.
	 * It is NOT a real distance, but a small value means that it is very close.
	 * 
	 * principle: if P is on [A,B] then |AB| = |AP| + |PB|
	 * 
	 * @param pt
	 * @param start
	 * @param end
	 * @return
	 */
	private double scorePointSegment(Point pt, Point start, Point end) {
		int dx = end.x-start.x;
		int dy = end.y-start.y;
		double d = Math.sqrt(dx*dx + dy*dy);
		
		dx = pt.x-start.x;
		dy = pt.y-start.y;
		double d1 = Math.sqrt(dx*dx + dy*dy);

		dx = end.x-pt.x;
		dy = end.y-pt.y;
		double d2 = Math.sqrt(dx*dx + dy*dy);

		return Math.abs(d1+d2 - d);
	}
	
	private void detectMovingEdges() {
		movingEdges.clear();
		for (Object o: renderer.selectionCache) {
			if (o instanceof Edge) {
				Edge e = (Edge)o;
				boolean source = renderer.selectionCache.contains(e.getSource());
				boolean target = renderer.selectionCache.contains(e.getTarget());
				movingEdges.put(e, MovingEdgeType.getMovingType(true, source, target));
				continue;
			}
			
			boolean source = true;
			boolean target = false;
			for (Object oe: graph.getOutgoingEdges(o)) {
				if (renderer.selectionCache.contains(oe)) {
					continue;
				}
				Edge e = (Edge)oe;
				target = renderer.selectionCache.contains(e.getTarget());
				movingEdges.put(e, MovingEdgeType.getMovingType(false, source, target));
			}
			target = true;
			for (Object oe: graph.getIncomingEdges(o)) {
				if (renderer.selectionCache.contains(oe)) {
					continue;
				}
				Edge e = (Edge)oe;
				source = renderer.selectionCache.contains(e.getSource());
				movingEdges.put(e, MovingEdgeType.getMovingType(false, source, target));
			}
		}
	}
	
	@Override
	public void cancel() {
		// TODO Auto-generated method stub
	}

	
	@Override
	public void overlay(Graphics2D g, Rectangle area) {
		
		if (dragstatus == null || dragstatus == DragStatus.NODRAG) {
			return;
		}
		
		switch (dragstatus) {
		case MOVEPOINT:
		case MOVE:
			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f); 
			g.setComposite(ac);
			
			if (movingPoint != null) {
				ereader.renderMovingPoint(g, movingPoint.pointIdx, movex, movey);
				break;
			}

			for (Object o: renderer.selectionCache) {
		    	if (o instanceof Edge) {
		    		// nothing to do here?
		    	} else {
		    		nreader.setNode(o, false);
		    		Rectangle bounds = nreader.getBounds();
		    		if (bounds.intersects(area)) {
		    			nreader.renderMoving(g, movex, movey);
		    		}
		    	}
			}
			for (Edge e: movingEdges.keySet()) {
				MovingEdgeType type = movingEdges.get(e);
				ereader.setEdge(e);
				Rectangle bounds = ereader.getBounds();
				if (bounds.intersects(area)) {
					ereader.renderMoving(g, type, movex, movey);
				}
			}
			
			break;

		case SELECT:
			// just draw a selection rectangle
			g.draw(ViewHelper.getRectangle(startPoint.x, startPoint.y, startPoint.x+movex, startPoint.y+movey));
			break;
		}
	}

	@Override
	public void helpOverlay(Graphics2D g, Rectangle area) {
		g.setColor(Color.GRAY);
		g.fill(area);
		System.out.println("TODO: help...");
		// TODO: help overlay
	}

}

class MovingPoint {
	final Edge<?> edge;
	final int pointIdx;
	
	public MovingPoint(Edge edge, int point) {
		this.edge = edge;
		this.pointIdx = point;
	}
}
