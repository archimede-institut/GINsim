package org.ginsim.core.graph.view;

import java.awt.Color;
import java.awt.Dimension;

/**
 * Implementation of NodeStyle to store default visual settings in a graph.
 * Each graph can tweak its own instance or extend the class
 * to make the default depend on the node properties.
 * 
 * @author Aurelien Naldi
 *
 * @param <V>
 */
public class DefaultNodeStyle<V extends Object> implements NodeStyle<V> {

	private static NodeShape DEFAULT_SHAPE = NodeShape.RECTANGLE;
	
	private Color background = Color.WHITE;
	private Color foreground = Color.BLACK;
	private Color textColor  = Color.BLACK;
	
	private Dimension size = new Dimension(40, 25);
	
	private NodeShape shape = DEFAULT_SHAPE;

	@Override
	public Color getBackground(V obj) {
		return background;
	}

	@Override
	public Color getForeground(V obj) {
		return foreground;
	}

	@Override
	public Color getTextColor(V obj) {
		return textColor;
	}

	@Override
	public void setBackground(Color bg) {
		this.background = bg;
	}

	@Override
	public void setForeground(Color fg) {
		this.foreground = fg;
	}

	@Override
	public void setTextColor(Color txt) {
		this.textColor = txt;
	}

	@Override
	public Dimension getDimension(V obj) {
		return size;
	}

	@Override
	public void setDimension(int w, int h) {
		this.setDimension(new Dimension(w,h));
	}

	@Override
	public void setDimension(Dimension size) {
		this.size = size;
	}
	
	@Override
	public NodeShape getNodeShape(V obj) {
		return shape;
	}
	
	@Override
	public void setNodeShape(NodeShape shape) {
		if (shape == null) {
			this.shape = DEFAULT_SHAPE;
		} else {
			this.shape = shape;
		}
	}
}
