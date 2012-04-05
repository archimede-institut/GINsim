package org.ginsim.gui.graph.canvas;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import org.ginsim.core.GraphEventCascade;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.backend.GraphViewListener;
import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.common.GraphChangeType;
import org.ginsim.core.graph.common.GraphListener;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.NodeAttributesReader;

public class GraphCanvasRenderer implements CanvasRenderer, GraphListener<Graph> {

	private final Graph<?, ?> graph;
	private final NodeAttributesReader nreader;
    private final EdgeAttributesReader ereader;
	private final SimpleCanvas canvas;
    
    public GraphCanvasRenderer(Graph<?,?> graph, SimpleCanvas canvas) {
    	this.graph = graph;
    	this.nreader = graph.getNodeAttributeReader();
    	this.ereader = graph.getEdgeAttributeReader();
    	this.canvas = canvas;
    	
    	canvas.setRenderer(this);
    	GraphManager.getInstance().addGraphListener(graph, this);
    }
	
	@Override
	public void render(Graphics2D g, Rectangle area) {

		// FIXME: get selection info from the graph
		boolean selected = false;
		
    	for (Object node: graph.getNodes()) {
    		selected = !selected;
    		nreader.setNode(node, selected);
    		Rectangle bounds = nreader.getBounds();
    		if (true || bounds.intersects(area)) {
    			nreader.render(g);
    		}
    	}
    	
    	for (Edge edge: graph.getEdges()) {
    		ereader.setEdge(edge);
    		Rectangle bounds = ereader.getBounds();
    		if (true || bounds.intersects(area)) {
    			ereader.render(g);
    		}
    	}
	}

	@Override
	public Object select(Point p) {

    	for (Object node: graph.getNodes()) {
    		nreader.setNode(node);
    		if (nreader.select(p)) {
    			return node;
    		}
    	}
    	
    	for (Edge edge: graph.getEdges()) {
    		ereader.setEdge(edge);
    		if (ereader.select(p)) {
    			return edge;
    		}
    	}
    	
    	return null;
	}

	@Override
	public GraphEventCascade graphChanged(Graph g, GraphChangeType type, Object data) {
		
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

}
