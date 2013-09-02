package org.ginsim.core.graph.view.style;

import java.awt.Color;

import org.ginsim.core.graph.view.NodeBorder;
import org.ginsim.core.graph.view.NodeShape;

public class NodeStyleOverride<V> implements NodeStyle<V> {

	private final NodeStyle<V> defaultStyle;
	private NodeStyle<V> baseStyle;
	
	public NodeStyleOverride(NodeStyle<V> style) {
		this.defaultStyle = style;
		this.baseStyle = defaultStyle;
	}
	
	public void setBaseStyle(NodeStyle<V> style) {
		if (style == null) {
			this.baseStyle = defaultStyle;
		} else {
			this.baseStyle = style;
		}
	}
	
	@Override
	public StyleProperty[] getProperties() {
		return null;
	}

	@Override
	public Object getProperty(StyleProperty prop) {
		return null;
	}

	@Override
	public void setProperty(StyleProperty prop, Object value) {
	}

	@Override
	public int getKey() {
		return 0;
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
