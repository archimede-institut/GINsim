package org.ginsim.gui.graph.backend;

import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.view.ViewHelper;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphModel;


/**
 * Edge view for jgraph.
 * It extract visual parameters from the attribute reader into jgraph's (convoluted) rendering system.
 */
public class GsEdgeView extends EdgeView {
	private static final long serialVersionUID = 875785889768955L;

	protected final GsEdgeRenderer renderer;
	protected final Edge<?> edge;
	
	/**
	 * create a new EdgeView.
	 * 
	 * @param cell the EdgeCell
	 * @param renderer the renderer for this edge
	 */
	public GsEdgeView(Object cell, GsEdgeRenderer renderer) {
		super(cell);
		this.edge = (Edge<?>)((DefaultGraphCell)cell).getUserObject();
		this.renderer = renderer;
	}

	@Override
	public CellViewRenderer getRenderer() {
		return renderer;
	}

	protected AttributeMap getCellAttributes(GraphModel model) {
		AttributeMap attributes = new AttributeMap();
		
		// copy visual settings to the attribute map
		renderer.reader.setEdge(edge);
		GraphConstants.setPoints(attributes, ViewHelper.getPoints(renderer.nodeReader, renderer.reader, edge));
		GraphConstants.setLineColor(attributes, renderer.reader.getLineColor());
		float[] dash = renderer.reader.getDash();
		if (dash != null) {
			GraphConstants.setDashPattern(attributes, dash);
		}
		GraphConstants.setLineWidth(attributes, renderer.reader.getLineWidth());

		int lineEnd = GraphConstants.ARROW_CLASSIC;
		switch (renderer.reader.getLineEnd()) {
		case 0:
			lineEnd = GraphConstants.ARROW_TECHNICAL;
			break;
		case 1:
			lineEnd = GraphConstants.ARROW_LINE;
			break;
		case 2:
			lineEnd = GraphConstants.ARROW_CIRCLE;
			break;
		}
		GraphConstants.setLineEnd(attributes, lineEnd);
		GraphConstants.setEndFill(attributes, true);
		
		return attributes;
	}
}
