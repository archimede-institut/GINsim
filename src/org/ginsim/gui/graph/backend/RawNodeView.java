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
import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphContext;
import org.jgraph.graph.GraphLayoutCache;

import fr.univmrs.tagc.common.Debugger;

/**
 * Cell view that will use data from the attribute readers to draw nodes.
 * It a perfect world it would only implement CellView, but jgraph can only interactively move items
 * which extend AbstractCellView (using the translate method).
 * It contains a fake attribute map, but really relies on the attribute reader.
 * 
 * @author Aurelien Naldi
 */
public class RawNodeView extends AbstractCellView implements CellView {

	private final RawNodeRenderer renderer;
	public final Object user;

	private final FakeAttributeMap attributes;
	
	private Rectangle bounds = null;
	
	public RawNodeView(Object cell, RawNodeRenderer vertexRenderer) {
		this.cell = (DefaultGraphCell)cell;
		this.user = ((DefaultGraphCell)cell).getUserObject();
		this.renderer = vertexRenderer;
		
		attributes = new FakeAttributeMap(this);
	}

	@Override
	public void update(GraphLayoutCache cache) {
		childUpdated();
	}

	@Override
	public CellView[] getChildViews() {
		return new CellView[] {};
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
		// just ignore it, we don't support undo anyway
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
		renderer.setView(this, selected, preview);
		return renderer;
	}

	@Override
	public CellHandle getHandle(GraphContext context) {
		return null;
	}

	public String toString() {
		return user.toString();
	}

	@Override
	public CellViewRenderer getRenderer() {
		Debugger.log("get renderer through abstractcellview method");
		return null;
	}
}

class FakeAttributeMap extends AttributeMap {

	private final RawNodeView view;
	
	public FakeAttributeMap(RawNodeView view) {
		this.view = view;
	}

	@Override
	public synchronized Object get(Object key) {
		if (GraphConstants.BOUNDS.equals(key)) {
			return view.getBounds(); 
		}
		return null;
	}

	@Override
	public synchronized Object put(Object key, Object value) {
		if (GraphConstants.BOUNDS.equals(key) && value instanceof Rectangle) {
			view.setBounds((Rectangle)value);
		}
		return get(key);
	}

	@Override
	public Object clone() {
		// as far as I can tell, cloning is useless as this object is empty
		return this;
	}
}
