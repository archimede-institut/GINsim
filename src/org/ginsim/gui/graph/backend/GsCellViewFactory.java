package org.ginsim.gui.graph.backend;

import org.ginsim.core.graph.common.Graph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphModel;

/**
 * custom cellViewFactory.
 * needed to get custom renderer for edges to be able to show/hide edge labels.
 */
public class GsCellViewFactory extends DefaultCellViewFactory {
	
	/** */
	private static final long serialVersionUID = 7666430579571102018L;
	private final GsEdgeRenderer edgeRenderer;
	private final RawNodeRenderer rawNodeRenderer;

	
	/**
	 * @param jgraph
	 * FIXME: parallel edge routing requires a graphmanager, this constructor may not fully work yet
	 * @param g 
	 */
	public GsCellViewFactory(GsJgraph jgraph, Graph<?, ?> g) {
		this.edgeRenderer = new GsEdgeRenderer(jgraph, g.getEdgeAttributeReader(), g.getNodeAttributeReader());
		this.rawNodeRenderer = new RawNodeRenderer(jgraph, g.getNodeAttributeReader());
	}

	public CellView createView(GraphModel model, Object cell) {
		CellView view = null;
		if (model.isPort(cell)) {
			view = createPortView(cell);
		} else if (model.isEdge(cell)) {
			view = createEdgeView(cell);
		} else {
			view = createRawNodeView(cell);
		}
		return view;
	}

	
	/**
	 * Constructs an EdgeView view for the specified object.
	 * @param cell
	 * @return the new EdgeView.
	 */
	protected EdgeView createEdgeView(Object cell) {
		return new GsEdgeView(cell, edgeRenderer);
	}

	protected CellView createRawNodeView(Object cell) {
		return new RawNodeView(cell, rawNodeRenderer);
	}
}