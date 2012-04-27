package org.ginsim.gui.graph.canvas.events;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.MovingEdgeType;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.graph.view.ViewHelper;
import org.ginsim.gui.graph.GraphSelection;
import org.ginsim.gui.graph.canvas.CanvasEventManager;
import org.ginsim.gui.graph.canvas.GraphCanvasRenderer;

public class GraphSelectionCanvasEventManager implements CanvasEventManager {

	private final GraphCanvasRenderer renderer;
	private final GraphSelection selection;
	private final Graph graph;
	private final NodeAttributesReader nreader;
	private final EdgeAttributesReader ereader;
	
	private DragStatus dragstatus = DragStatus.NODRAG;
	
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
	}

	@Override
	public void dragged(Point p) {
		if (startPoint == null) {
			return;
		}
		if (dragstatus == DragStatus.NODRAG) {
			if (renderer.selectionCache.size() > 0) {
				dragstatus = DragStatus.MOVE;
				
				// detect moving edges
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
			} else {
				dragstatus = DragStatus.SELECT;
			}
		}
		
		movex = p.x - startPoint.x;
		movey = p.y - startPoint.y;
		renderer.repaintCanvas();
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
		case MOVE:
			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f); 
			g.setComposite(ac); 

			for (Object o: renderer.selectionCache) {
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

}
