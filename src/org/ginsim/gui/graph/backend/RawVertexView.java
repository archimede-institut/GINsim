package org.ginsim.gui.graph.backend;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import org.jgraph.JGraph;
import org.jgraph.graph.AbstractCellView;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.CellHandle;
import org.jgraph.graph.CellMapper;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphCellEditor;
import org.jgraph.graph.GraphContext;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;

public class RawVertexView implements CellView {

	private final RawVertexRenderer renderer;
	private final DefaultGraphCell cell;

	public final Object user;
	
	private final AttributeMap attributes = new AttributeMap();
	
	private static RawVertexView FIRST = null;
	private static boolean first = true;
	
	private Rectangle bounds = null;
	private CellView parent = null;
	
	public RawVertexView(Object cell, RawVertexRenderer vertexRenderer) {
		this.cell = (DefaultGraphCell)cell;
		this.user = this.cell.getUserObject();
		this.renderer = vertexRenderer;
		
		System.out.println("new view: "+ user);
		System.out.println(vertexRenderer.getBounds(user));
		if (first) {
			first = false;
			FIRST = new RawVertexView(cell, vertexRenderer);
		}
	}

	@Override
	public Object getCell() {
		return cell;
	}

	@Override
	public void refresh(GraphLayoutCache cache, CellMapper mapper, boolean createDependentViews) {
		
		if (FIRST == this) {
			System.out.println("refresh");
		}

		// Re-read global attributes
		GraphModel model = cache.getModel();
		// Cache Parent View
		if (mapper != null && model != null) {
			// Create parent only if it's visible in the graph
			Object par = model.getParent(cell);
			CellView tmp = mapper.getMapping(par, createDependentViews);
			if (tmp != parent) {
				removeFromParent();
			}
			parent = tmp;
		}
		// Cache Cell Attributes in View
		update(cache);
		
	}

	@Override
	public void update(GraphLayoutCache cache) {
		childUpdated();
	}

	@Override
	public void childUpdated() {
		if (FIRST == this) {
			System.out.println("updated");
		}
		bounds = null;
		if (parent != null) {
			parent.childUpdated();
		}
	}

	@Override
	public CellView getParentView() {
		if (FIRST == this) {
			System.out.println("parent");
		}
		return parent;
	}

	@Override
	public CellView[] getChildViews() {
		return new CellView[] {};
	}

	@Override
	public void removeFromParent() {
		if (FIRST == this) {
			System.out.println("remove from parent");
		}
		if (parent instanceof AbstractCellView) {
			// TODO: how can I do this?
			// ((AbstractCellView) parent).childViews.remove(this);
		}
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	@Override
	public Rectangle getBounds() {
		if (bounds == null) {
			bounds = renderer.getBounds(user);
		}
		return bounds;
	}

	@Override
	public boolean intersects(JGraph g, Rectangle2D rect) {
		return getBounds().intersects(rect);
	}

	@Override
	public Point2D getPerimeterPoint(EdgeView edge, Point2D source, Point2D p) {
		Rectangle2D bounds = getBounds();
		double x = bounds.getX();
		double y = bounds.getY();
		double width = bounds.getWidth();
		double height = bounds.getHeight();
		double xCenter = x + width / 2;
		double yCenter = y + height / 2;
		double dx = p.getX() - xCenter; // Compute Angle
		double dy = p.getY() - yCenter;
		double alpha = Math.atan2(dy, dx);
		double xout = 0, yout = 0;
		double pi = Math.PI;
		double pi2 = Math.PI / 2.0;
		double beta = pi2 - alpha;
		double t = Math.atan2(height, width);
		if (alpha < -pi + t || alpha > pi - t) { // Left edge
			xout = x;
			yout = yCenter - width * Math.tan(alpha) / 2;
		} else if (alpha < -t) { // Top Edge
			yout = y;
			xout = xCenter - height * Math.tan(beta) / 2;
		} else if (alpha < t) { // Right Edge
			xout = x + width;
			yout = yCenter + width * Math.tan(alpha) / 2;
		} else { // Bottom Edge
			yout = y + height;
			xout = xCenter + height * Math.tan(beta) / 2;
		}
		return new Point2D.Double(xout, yout);
	}

	@Override
	public Map changeAttributes(GraphLayoutCache cache, Map map) {
		if (FIRST == this) {
			System.out.println("change attributes");
		}
		return attributes;
	}

	@Override
	public AttributeMap getAttributes() {
		return attributes;
	}

	@Override
	public AttributeMap getAllAttributes() {
		return attributes;
	}

	@Override
	public Component getRendererComponent(JGraph graph, boolean selected, boolean focus, boolean preview) {
		renderer.setView(this);
		return renderer;
	}

	@Override
	public CellHandle getHandle(GraphContext context) {
		// TODO Auto-generated method stub
		if (FIRST == this) {
			System.out.println("handle");
		}
		return null;
	}

	@Override
	public GraphCellEditor getEditor() {
		return null;
	}

	public String toString() {
		return user.toString();
	}
}
