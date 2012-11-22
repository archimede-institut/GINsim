package org.ginsim.gui.graph.canvas;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.common.GraphChangeType;
import org.ginsim.core.graph.common.GraphEventCascade;
import org.ginsim.core.graph.common.GraphListener;
import org.ginsim.core.graph.common.GraphModel;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.gui.graph.EditActionManager;
import org.ginsim.gui.graph.GraphSelection;
import org.ginsim.gui.graph.canvas.events.AddEdgeInGraphCanvasEventManager;
import org.ginsim.gui.graph.canvas.events.AddNodeInGraphCanvasEventManager;
import org.ginsim.gui.graph.canvas.events.DragStatus;
import org.ginsim.gui.graph.canvas.events.GraphSelectionCanvasEventManager;

@SuppressWarnings({"rawtypes", "unchecked"})
public class GraphCanvasRenderer implements CanvasRenderer, GraphListener {

	private final NodeAttributesReader nreader;
    private final EdgeAttributesReader ereader;
	private final SimpleCanvas canvas;
	private final GraphSelection selection;
	
	private final CanvasEventManager selectEventManager, addNodeEventManager, addEdgeEventManager;

	public final Graph graph;
	public final EditActionManager amanager;
	
	private boolean nodesOnTop = true;
	
	private Dimension bounds = new Dimension();
	
	int movex=0, movey=0;
	
	/**
	 * Cache all selected items for faster access and memory: allows to detect items needing to be redrawn
	 */
	public Set selectionCache = new HashSet();

	DragStatus dragstatus = DragStatus.NODRAG;
	
	public GraphCanvasRenderer(Graph<?,?> graph, SimpleCanvas canvas, GraphSelection selection, EditActionManager amanager) {
    	this.graph = graph;
    	this.nreader = graph.getNodeAttributeReader();
    	this.ereader = graph.getEdgeAttributeReader();
    	this.canvas = canvas;
    	this.selection = selection;
    	
    	canvas.setRenderer(this);
    	GraphManager.getInstance().addGraphListener(graph, this);

    	this.amanager = amanager;
    	selectEventManager = new GraphSelectionCanvasEventManager(graph, this, selection);
    	// TODO: dedicated event managers to add nodes and edges
    	addNodeEventManager = new AddNodeInGraphCanvasEventManager(this);
    	addEdgeEventManager = new AddEdgeInGraphCanvasEventManager(this);
    }

	private CanvasEventManager getEventManager() {
		switch (amanager.getSelectedAction().getMode()) {
		case NODE:
			return addNodeEventManager;
		case EDGE:
			return addEdgeEventManager;

		default:
			return selectEventManager;
		}
	}
	
	@Override
	public void render(Graphics2D g, Rectangle area) {

		SimpleDimension dim = new SimpleDimension();

		if (nodesOnTop) {
			renderEdges(g, area, dim);
			renderNodes(g, area, dim);
		} else {
			renderNodes(g, area, dim);
			renderEdges(g, area, dim);
		}
		// update global bounds
		bounds.width = dim.getWidth() + 5;
		bounds.height = dim.getHeight() + 5;
	}

	private void renderNodes(Graphics2D g, Rectangle area, SimpleDimension dim) {
    	for (Object node: graph.getNodes()) {
    		nreader.setNode(node, selectionCache.contains(node));
    		Rectangle bounds = nreader.getBounds();
    		dim.extend(bounds);
    		if (bounds.intersects(area)) {
    			nreader.render(g);
    		}
    	}
	}

	private void renderEdges(Graphics2D g, Rectangle area, SimpleDimension dim) {
    	for (Object edge: graph.getEdges()) {
    		ereader.setEdge((Edge)edge, selectionCache.contains(edge));
    		Rectangle bounds = ereader.getBounds();
    		dim.extend(bounds);
    		if (bounds.intersects(area)) {
    			ereader.render(g);
    		}
    	}
	}
	
	@Override
	public void overlay(Graphics2D g, Rectangle area) {
		getEventManager().overlay(g, area);
	}

	@Override
	public void helpOverlay(Graphics2D g, Rectangle area) {
		getEventManager().helpOverlay(g, area);
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
	public void damageItem(Object item) {
		Rectangle bounds = null;
		if (item instanceof Edge) {
			// force a reset of the ereader
			ereader.setEdge(null);
			ereader.setEdge((Edge)item);
			bounds = ereader.getBounds();
		} else {
			// force a reset of the nreader
			nreader.setNode(null);
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

		Object sel = null;
		
    	for (Object node: graph.getNodes()) {
    		nreader.setNode(node);
    		if (nreader.select(p)) {
    			sel = node;
    		}
    	}
    	if (sel != null) {
    		return sel;
    	}
    	
    	for (Object edge: graph.getEdges()) {
    		ereader.setEdge((Edge)edge);
    		if (ereader.select(p)) {
    			sel = edge;
    		}
    	}
    	
    	return sel;
	}

	@Override
	public GraphEventCascade graphChanged(GraphModel g, GraphChangeType type, Object data) {
		
		switch (type) {
		case EDGEDAMAGED:
			ereader.setEdge((Edge)data);
			canvas.damageCanvas(ereader.getBounds());
			ereader.setEdge(null);
			return null;
		case NODEDAMAGED:
			nreader.setNode(data);
			canvas.damageCanvas(nreader.getBounds());
			nreader.setNode(null);
			return null;
		
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
		case GRAPHVIEWCHANGED:
			canvas.clearOffscreen();
		default:
			break;
		}

		canvas.repaint();
		return null;
	}


	public void repaintCanvas() {
		canvas.repaint();
	}

	
	/*
	 * forward events from the canvas to the apropriate event manager
	 */
	
	@Override
	public void click(Point p, boolean alternate) {
		getEventManager().click(p, alternate);
	}

	@Override
	public void pressed(Point p, boolean alternate) {
		getEventManager().pressed(p, alternate);
	}

	@Override
	public void released(Point p) {
		getEventManager().released(p);
	}

	@Override
	public void dragged(Point p) {
		getEventManager().dragged(p);
	}

	@Override
	public void cancel() {
		getEventManager().cancel();
	}

	@Override
	public Dimension getBounds() {
		return bounds;
	}
}

