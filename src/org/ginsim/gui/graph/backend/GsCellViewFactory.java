package org.ginsim.gui.graph.backend;

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
	 * @param jgraph
	 * FIXME: parallel edge routing requires a graphmanager, this constructor may not fully work yet
	 */
	public GsCellViewFactory(GsJgraph jgraph) {
		this.renderer = new GsEdgeRenderer(jgraph);
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