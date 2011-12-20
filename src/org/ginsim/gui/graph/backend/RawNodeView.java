package org.ginsim.gui.graph.backend;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import org.ginsim.common.utils.log.LogManager;
import org.ginsim.core.graph.view.ViewHelper;
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

	private FakeAttributeMap attributes = null;
	
	private Rectangle bounds = null;
	
	public RawNodeView(Object cell, RawNodeRenderer vertexRenderer) {
		this.cell = (DefaultGraphCell)cell;
		this.user = ((DefaultGraphCell)cell).getUserObject();
		this.renderer = vertexRenderer;
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
		return renderer.getBounds(user);
	}

	@Override
	public boolean intersects(JGraph g, Rectangle2D rect) {
		return getBounds().intersects(rect);
	}

	@Override
	public Point2D getPerimeterPoint(EdgeView edge, Point2D source, Point2D p) {
		Rectangle bounds = getBounds();
		Point target = new Point((int)p.getX(), (int)p.getY());
		return ViewHelper.getIntersection(bounds, target);
	}

	@Override
	public Map changeAttributes(GraphLayoutCache cache, Map map) {
		// just ignore it, we don't support undo anyway
		return getAttributes();
	}

	@Override
	public AttributeMap getAttributes() {
		if (attributes == null) {
			attributes = new FakeAttributeMap(this);
		}
		return attributes;
	}

	@Override
	public AttributeMap getAllAttributes() {
		return getAttributes();
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
		LogManager.error( "Trying to get a renderer through the abstractcellview method, should not happen");
		return null;
	}
	
	public void translate(double dx, double dy) {
		renderer.translate(user, dx, dy);
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
