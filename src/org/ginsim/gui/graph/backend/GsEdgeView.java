package org.ginsim.gui.graph.backend;

import org.ginsim.graph.common.Edge;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.EdgeView;


/**
 * a jgraph edge viewer
 */
public class GsEdgeView extends EdgeView {
	private static final long serialVersionUID = 875785889768955L;
	
	private final GsEdgeRenderer renderer;
	protected final Edge<?> edge;
	
	/**
	 * create a new EdgeView.
	 * 
	 * @param cell the EdgeCell
	 * @param renderer the renderer for this edge
	 */
	public GsEdgeView(Object cell, GsEdgeRenderer renderer) {
		super(cell);
		this.edge = (Edge)((DefaultGraphCell)cell).getUserObject();
		this.renderer = renderer;
	}

	@Override
	public CellViewRenderer getRenderer() {
		return renderer;
	}
}
