package org.ginsim.gui.graph.backend;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.view.ViewHelper;
import org.ginsim.core.utils.log.LogManager;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
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

	/**
	 * Sets the point at <code>index</code> to <code>p</code>.
	 */
	public void setPoint(int index, Point2D p) {
		LogManager.info( "Set point: "+index+" "+p);
		super.setPoint(index, p);
	}

	/**
	 * Adds <code>p</code> at position <code>index</code>.
	 */
	public void addPoint(int index, Point p) {
		List<Point> realPoints = getRealPoints(true);
		realPoints.add(index-1, p);
		super.addPoint(index, p);
	}

	/**
	 * Removes the point at position <code>index</code>.
	 */
	public void removePoint(int index) {
		List realPoints = getRealPoints();
		if (realPoints != null && realPoints.size() > index) {
			realPoints.remove(index-1);
			super.removePoint(index);
		}
	}

	public void setPoints(List points) {
		renderer.reader.setEdge(edge);
		renderer.reader.setPoints(points);
	}
	public List<Point> getRealPoints() {
		return getRealPoints(false);
	}
	public List<Point> getRealPoints(boolean create) {
		renderer.reader.setEdge(edge);
		List<Point> points = renderer.reader.getPoints();
		if (create && points == null) {
			points = new ArrayList<Point>();
			renderer.reader.setPoints(points);
		}
		return points;
	}

	protected AttributeMap getCellAttributes(GraphModel model) {
		AttributeMap attributes = new AttributeMap();
		
		GraphConstants.setRouting(attributes, GsEdgeRouter.router);
		
		// copy visual settings to the attribute map
		renderer.reader.setEdge(edge);
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


class GsEdgeRouter extends DefaultEdge.DefaultRouting {

	public static final GsEdgeRouter router = new GsEdgeRouter();

	@Override
	public List<Point> route(GraphLayoutCache cache, EdgeView edge) {
		
		if (!(edge instanceof GsEdgeView)) {
			return null;
		}
		GsEdgeView view = (GsEdgeView)edge;
		return ViewHelper.getPoints(view.renderer.nodeReader, view.renderer.reader, view.edge);
	}

	@Override
	protected int getLoopStyle() {
		return GraphConstants.STYLE_BEZIER;
	}

	@Override
	protected int getEdgeStyle() {
		// TODO: return correct style
		return GraphConstants.STYLE_ORTHOGONAL;
	}

}
