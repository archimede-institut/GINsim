package org.ginsim.gui.graph.canvas;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ginsim.core.GraphEventCascade;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.common.GraphChangeType;
import org.ginsim.core.graph.common.GraphListener;
import org.ginsim.core.graph.common.GraphModel;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.MovingEdgeType;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.graph.view.ViewHelper;
import org.ginsim.gui.graph.GraphSelection;

@SuppressWarnings({"rawtypes", "unchecked"})
public class GraphCanvasRenderer implements CanvasRenderer, GraphListener {

	private final Graph graph;
	private final NodeAttributesReader nreader;
    private final EdgeAttributesReader ereader;
	private final SimpleCanvas canvas;
	private final GraphSelection selection;
	
	private Point startPoint=null;
	int movex=0, movey=0;
	
	/**
	 * Cache all selected items for faster access and memory: allows to detect items needing to be redrawn
	 */
	private Set selectionCache = new HashSet();
	/**
	 * Edges that have to be moves as either them or their attached nodes are selected
	 */
	private Map<Edge, MovingEdgeType> movingEdges = new HashMap<Edge, MovingEdgeType>();

	DragStatus dragstatus = DragStatus.NODRAG;
	
	public GraphCanvasRenderer(Graph<?,?> graph, SimpleCanvas canvas, GraphSelection selection) {
    	this.graph = graph;
    	this.nreader = graph.getNodeAttributeReader();
    	this.ereader = graph.getEdgeAttributeReader();
    	this.canvas = canvas;
    	this.selection = selection;
    	
    	canvas.setRenderer(this);
    	GraphManager.getInstance().addGraphListener(graph, this);
    }
	
	@Override
	public void render(Graphics2D g, Rectangle area) {

    	for (Object node: graph.getNodes()) {
    		nreader.setNode(node, selectionCache.contains(node));
    		Rectangle bounds = nreader.getBounds();
    		if (bounds.intersects(area)) {
    			nreader.render(g);
    		}
    	}
    	
    	for (Object edge: graph.getEdges()) {
    		ereader.setEdge((Edge)edge, selectionCache.contains(edge));
    		Rectangle bounds = ereader.getBounds();
    		if (bounds.intersects(area)) {
    			ereader.render(g);
    		}
    	}
	}

	@Override
	public void overlay(Graphics2D g, Rectangle area) {
		
		if (dragstatus == null || dragstatus == DragStatus.NODRAG) {
			return;
		}
		
		switch (dragstatus) {
		case MOVE:
			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f); 
			g.setComposite(ac); 

			for (Object o: selectionCache) {
		    	if (o instanceof Edge) {
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

	
	public void updateSelectionCache() {
		Set<?> newSelection = buildSelectionCache();
		for (Object o: newSelection) {
			if (!selectionCache.contains(o)) {
				damageItem(o);
			}
		}
		
		for (Object o: selectionCache) {
			if (!newSelection.contains(o)) {
				damageItem(o);
			}
		}
		
		selectionCache.clear();
		selectionCache = newSelection;
		
		// full repaint to test
		//canvas.clearOffscreen();
		canvas.repaint();
	}
	
	/**
	 * Mark an item as needing redraw.
	 * This will call damageCanvas according to the item's bounds.
	 * Note: It does NOT call repaint() to let several calls happen before the actual redraw.
	 * @param item
	 */
	private void damageItem(Object item) {
		Rectangle bounds = null;
		if (item instanceof Edge) {
			ereader.setEdge((Edge)item);
			bounds = ereader.getBounds();
		} else {
			nreader.setNode(item);
			bounds = nreader.getBounds();
		}
		
		if (bounds != null) {
			canvas.damageCanvas(bounds);
		}
	}
	
	private Set buildSelectionCache() {
		Set newset = new HashSet();
		List selected = selection.getSelectedNodes();
		if (selected != null) {
			newset.addAll(selected);
		}
		selected = selection.getSelectedEdges();
		if (selected != null) {
			newset.addAll(selected);
		}
		return newset;
	}
	
	@Override
	public void select(Shape s) {
		List edges = new ArrayList();
		List nodes = new ArrayList();
		
    	for (Object node: graph.getNodes()) {
    		nreader.setNode(node);
    		if (s.contains(nreader.getBounds())) {
    			nodes.add(node);
    		}
    	}

    	for (Object edge: graph.getEdges()) {
    		ereader.setEdge((Edge)edge);
    		if (s.contains(ereader.getBounds())) {
    			edges.add(edge);
    		}
    	}

    	selection.setSelection(nodes, edges);
	}

	public Object getObjectUnderPoint(Point p) {

    	for (Object node: graph.getNodes()) {
    		nreader.setNode(node);
    		if (nreader.select(p)) {
    			return node;
    		}
    	}
    	
    	for (Object edge: graph.getEdges()) {
    		ereader.setEdge((Edge)edge);
    		if (ereader.select(p)) {
    			return edge;
    		}
    	}
    	
    	return null;
	}

	@Override
	public GraphEventCascade graphChanged(GraphModel g, GraphChangeType type, Object data) {
		
		switch (type) {
		case EDGEADDED:
		case EDGEUPDATED:
		case EDGEREMOVED:
			ereader.setEdge((Edge)data);
			canvas.damageCanvas(ereader.getBounds());
			break;
		case NODEADDED:
		case NODEUPDATED:
		case NODEREMOVED:
			nreader.setNode(data);
			canvas.damageCanvas(nreader.getBounds());
			break;
		default:
			break;
		}

		return null;
	}

	
	/*
	 * manage events from the canvas
	 */

	
	@Override
	public void click(Point p, boolean alternate) {
		dragstatus = DragStatus.NODRAG;
	}

	@Override
	public void pressed(Point p, boolean alternate) {
		dragstatus = DragStatus.NODRAG;
		startPoint = p;
		
		Object o = getObjectUnderPoint(p);
		
		if (alternate) {
			if (o == null) {
				// do nothing
			} else if (selectionCache.contains(o)) {
				// remove it from selection
				if (o instanceof Edge) {
					selection.unselectEdge((Edge)o);
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
		case MOVE:
			// first mark all moving items as damaged
			for (Object o: movingEdges.keySet()) {
				damageItem(o);
			}
			for (Object o: selectionCache) {
				damageItem(o);
			}
			
			// then move them
			for (Object o: selectionCache) {
				if (o instanceof Edge) {
					ereader.setEdge((Edge)o);
					ereader.move(movex, movey);
				} else {
					nreader.setNode(o);
					nreader.move(movex, movey);
				}
				damageItem(o);
			}

			// finally mark them as damaged again and repaint
			for (Object o: movingEdges.keySet()) {
				damageItem(o);
			}
			for (Object o: selectionCache) {
				damageItem(o);
			}
			dragstatus = DragStatus.NODRAG;
			movingEdges.clear();
			canvas.repaint();
			break;

		case SELECT:
			
			Rectangle selectionClip = ViewHelper.getRectangle(startPoint.x, startPoint.y, startPoint.x+movex, startPoint.y+movey);
			select(selectionClip);
			dragstatus = DragStatus.NODRAG;
			canvas.repaint();
			break;
		default:
			break;
		}
		
		startPoint = null;
		movex = movey = 0;
	}

	@Override
	public void dragged(Point p) {
		if (startPoint == null) {
			return;
		}
		if (dragstatus == DragStatus.NODRAG) {
			if (selectionCache.size() > 0) {
				dragstatus = DragStatus.MOVE;
				
				// detect moving edges
				movingEdges.clear();
				for (Object o: selectionCache) {
					if (o instanceof Edge) {
						Edge e = (Edge)o;
						boolean source = selectionCache.contains(e.getSource());
						boolean target = selectionCache.contains(e.getTarget());
						movingEdges.put(e, MovingEdgeType.getMovingType(true, source, target));
						continue;
					}
					
					boolean source = true;
					boolean target = false;
					for (Object oe: graph.getOutgoingEdges(o)) {
						if (selectionCache.contains(oe)) {
							continue;
						}
						Edge e = (Edge)oe;
						target = selectionCache.contains(e.getTarget());
						movingEdges.put(e, MovingEdgeType.getMovingType(false, source, target));
					}
					target = true;
					for (Object oe: graph.getIncomingEdges(o)) {
						if (selectionCache.contains(oe)) {
							continue;
						}
						Edge e = (Edge)oe;
						source = selectionCache.contains(e.getSource());
						movingEdges.put(e, MovingEdgeType.getMovingType(false, source, target));
					}
				}
			} else {
				dragstatus = DragStatus.SELECT;
			}
		}
		
		movex = p.x - startPoint.x;
		movey = p.y - startPoint.y;
		canvas.repaint();
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
	}

}

enum DragStatus {
	NODRAG, SELECT, MOVE;
}
