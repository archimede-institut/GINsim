package fr.univmrs.tagc.GINsim.jgraph;

import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.VertexView;

/**
 * custom cellViewFactory.
 * needed to get custom renderer for edges to be able to show/hide edge labels.
 */
public class GsCellViewFactory extends DefaultCellViewFactory {
	
	/** */
	private static final long serialVersionUID = 7666430579571102018L;
	private GsEdgeRenderer renderer;

	
	/**
	 * @param graph
	 */
	public GsCellViewFactory(GsJgraphtGraphManager graph) {
		this.renderer = new GsEdgeRenderer(graph);
	}

	/**
	 * Constructs an EdgeView view for the specified object.
	 * @param cell
	 * @return the new EdgeView.
	 */
	protected EdgeView createEdgeView(Object cell) {
		return new GsEdgeView(cell, renderer);
	}

	protected VertexView createVertexView(Object cell) {
		return new GsJgraphVertexView(cell);
	}
}