package org.ginsim.core.graph.view;

import java.awt.Color;

public class SimpleNodeStyle<V> implements NodeStyle<V> {

	private Color bg, fg, txt;
	
	private int width=-1, height=-1;
	
	private NodeShape shape;
	private NodeBorder border;
	
	private DefaultNodeStyle<V> d;
	
	public SimpleNodeStyle(DefaultNodeStyle<V> d) {
		this.d = d;
	}
	
	@Override
	public Color getBackground(V obj) {
		if (bg == null) {
			return d.getBackground(obj);
		}
		return bg;
	}

	@Override
	public Color getForeground(V obj) {
		if (fg == null) {
			return d.getForeground(obj);
		}
		return fg;
	}

	@Override
	public Color getTextColor(V obj) {
		if (txt == null) {
			return d.getTextColor(obj);
		}
		return txt;
	}

	@Override
	public int getWidth(V obj) {
		if (width < 0) {
			return d.getWidth(obj);
		}
		return width;
	}

	@Override
	public int getHeight(V obj) {
		if (height < 0) {
			return d.getHeight(obj);
		}
		return height;
	}

	@Override
	public NodeShape getNodeShape(V obj) {
		if (shape == null) {
			return d.getNodeShape(obj);
		}
		return shape;
	}

	@Override
	public NodeBorder getNodeBorder(V obj) {
		if (border == null) {
			return d.getNodeBorder(obj);
		}
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
