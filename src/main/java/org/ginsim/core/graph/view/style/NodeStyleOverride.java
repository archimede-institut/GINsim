package org.ginsim.core.graph.view.style;

import java.awt.Color;

import org.ginsim.core.graph.view.NodeBorder;
import org.ginsim.core.graph.view.NodeShape;

public class NodeStyleOverride<V> extends BaseStyleOverride<NodeStyle<V>> implements NodeStyle<V> {

	public NodeStyleOverride(NodeStyle<V> style) {
		super(style);
	}
	
	@Override
	public Color getBackground(V obj) {
		return baseStyle.getBackground(obj);
	}

	@Override
	public Color getForeground(V obj) {
		return baseStyle.getBackground(obj);
	}

	@Override
	public Color getTextColor(V obj) {
		return baseStyle.getTextColor(obj);
	}

	@Override
	public int getWidth(V obj) {
		return baseStyle.getWidth(obj);
	}

	@Override
	public int getHeight(V obj) {
		return baseStyle.getHeight(obj);
	}

	@Override
	public NodeShape getNodeShape(V obj) {
		return baseStyle.getNodeShape(obj);
	}

	@Override
	public NodeBorder getNodeBorder(V obj) {
		return baseStyle.getNodeBorder(obj);
	}

	@Override
	public boolean enforceColors() {
		return false;
	}

	@Override
	public boolean enforceShape() {
		return false;
	}

	@Override
	public boolean enforceSize() {
		return false;
	}

	@Override
	public boolean enforceBorder() {
		return false;
	}

	@Override
	public boolean matches(NodeShape shape, Color bg, Color fg, Color text,	int w, int h) {
		return false;
	}
}
