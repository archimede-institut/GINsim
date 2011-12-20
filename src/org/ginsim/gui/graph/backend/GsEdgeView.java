package org.ginsim.gui.graph.backend;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.List;

import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.view.EdgeEnd;
import org.ginsim.core.graph.view.EdgePattern;
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

	private static boolean CUSTOMBOUNDS = false;
	
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
		float[] dash = null;
		EdgePattern pattern = renderer.reader.getDash();
		if (pattern != null) {
			dash = pattern.getPattern();
		}
		if (dash != null) {
			GraphConstants.setDashPattern(attributes, dash);
		}
		GraphConstants.setLineWidth(attributes, renderer.reader.getLineWidth());

		if (renderer.reader.isCurve()) {
			GraphConstants.setLineStyle(attributes, GraphConstants.STYLE_SPLINE);
		}
		
		int lineEnd = GraphConstants.ARROW_CLASSIC;
		EdgeEnd end = renderer.reader.getLineEnd();
		switch (end) {
		case POSITIVE:
			lineEnd = GraphConstants.ARROW_TECHNICAL;
			break;
		case NEGATIVE:
			lineEnd = GraphConstants.ARROW_LINE;
			break;
		case UNKNOWN:
			lineEnd = GraphConstants.ARROW_CIRCLE;
			break;
		case DUAL:
			lineEnd = GraphConstants.ARROW_DIAMOND;
			break;
		}
		GraphConstants.setLineEnd(attributes, lineEnd);
		GraphConstants.setEndFill(attributes, true);
		
		return attributes;
	}
	
	public Rectangle2D getBounds() {
		if (!CUSTOMBOUNDS) {
			return super.getBounds();
		}
		
		List<Point> points = ViewHelper.getPoints(renderer.nodeReader, renderer.reader, edge);
		int delta = 5*(int)renderer.reader.getLineWidth();
		
		Rectangle rect = new Rectangle(points.get(0));
		for (Point p: points) {
			rect.add(p);
		}
		
		
		// add a rectange arround the last point
		Point last = points.get(points.size()-1);
		Rectangle2D arrow = new Rectangle2D.Double(last.x-delta, last.y-delta, 2*delta, 2*delta);
		//rect.add(arrow);
		
		// extend the bounding box to include selection marks and edge end
		rect.setBounds(rect.x-delta, rect.y-delta, rect.width+2*delta, rect.height+2*delta);
		
		return rect;
	}
}
