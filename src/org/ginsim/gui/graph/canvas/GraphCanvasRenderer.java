package org.ginsim.gui.graph.canvas;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import org.ginsim.core.graph.backend.GraphViewListener;
import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.NodeAttributesReader;

public class GraphCanvasRenderer implements CanvasRenderer {

	private final Graph<?, ?> graph;
	private final NodeAttributesReader nreader;
    private final EdgeAttributesReader ereader;
	
    public GraphCanvasRenderer(Graph<?,?> graph) {
    	this.graph = graph;
    	this.nreader = graph.getNodeAttributeReader();
    	this.ereader = graph.getEdgeAttributeReader();

    }
	
	@Override
	public void render(Graphics2D g, Rectangle area) {
		
    	for (Object node: graph.getNodes()) {
    		nreader.render(node, g);
    	}
    	
    	for (Edge edge: graph.getEdges()) {
    		ereader.render(nreader, edge, g);
    	}

		
		// TODO Auto-generated method stub

	}

}
