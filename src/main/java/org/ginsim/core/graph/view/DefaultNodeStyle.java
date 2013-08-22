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
	private static NodeBorder DEFAULT_BORDER = NodeBorder.SIMPLE;

	private static final int MAX_SIZE = 500;
	private static final int MIN_SIZE = 15;
	
	private Color background = Color.WHITE;
	private Color foreground = Color.BLACK;
	private Color textColor  = Color.BLACK;
	
	private int width = 40;
	private int height = 25;
	
	private NodeShape shape = DEFAULT_SHAPE;
	private NodeBorder border = DEFAULT_BORDER;

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
	public boolean setBackground(Color bg) {
		if (bg != this.background) {
			this.background = bg;
			return true;
		}
		return false;
	}

	@Override
	public boolean setForeground(Color fg) {
		this.foreground = fg;
		return true;
	}

	@Override
	public boolean setTextColor(Color txt) {
		this.textColor = txt;
		return true;
	}

	@Override
	public int getWidth(V obj) {
		return width;
	}

	@Override
	public int getHeight(V obj) {
		return height;
	}

	@Override
	public boolean setDimension(int w, int h) {
		// TODO: implement min/max width/height
		this.width = w;
		this.height = h;
		return true;
	}

	@Override
	public NodeShape getNodeShape(V obj) {
		return shape;
	}
	
	@Override
	public boolean setNodeShape(NodeShape shape) {
		if (shape == this.shape) {
			return false;
		}
		if (shape == null) {
			this.shape = DEFAULT_SHAPE;
		} else {
			this.shape = shape;
		}
		return true;
	}

	@Override
	public NodeBorder getNodeBorder(V obj) {
		return border;
	}

	@Override
	public boolean setNodeBorder(NodeBorder border) {
		if (border == this.border) {
			return false;
		}
		if (border == null) {
			this.border = DEFAULT_BORDER;
		} else {
			this.border = border;
		}
		return true;
	}
}
