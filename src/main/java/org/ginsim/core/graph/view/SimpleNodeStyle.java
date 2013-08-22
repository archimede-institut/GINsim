package org.ginsim.core.graph.view;

import java.awt.Color;

public class SimpleNodeStyle<V> implements NodeStyle<V> {

	private Color bg, fg, txt;
	
	private int width, height;
	
	private NodeShape shape;
	private NodeBorder border;
	
	@Override
	public Color getBackground(V obj) {
		return bg;
	}

	@Override
	public Color getForeground(V obj) {
		return fg;
	}

	@Override
	public Color getTextColor(V obj) {
		return txt;
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
	public NodeShape getNodeShape(V obj) {
		return shape;
	}

	@Override
	public NodeBorder getNodeBorder(V obj) {
		return border;
	}

	@Override
	public boolean setBackground(Color bg) {
		this.bg = bg;
		return true;
	}

	@Override
	public boolean setForeground(Color fg) {
		this.fg = fg;
		return true;
	}

	@Override
	public boolean setTextColor(Color txt) {
		this.txt = txt;
		return true;
	}

	@Override
	public boolean setDimension(int w, int h) {
		this.width = w;
		this.height = h;
		return true;
	}

	@Override
	public boolean setNodeShape(NodeShape shape) {
		if (this.shape == shape) {
			return false;
		}
		this.shape = shape;
		return true;
	}

	@Override
	public boolean setNodeBorder(NodeBorder border) {
		if (this.border == border) {
			return false;
		}
		this.border = border;
		return true;
	}
}
