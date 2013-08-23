package org.ginsim.core.graph.view;

import java.awt.Color;

import org.ginsim.core.graph.common.Edge;

public class SimpleEdgeStyle<V, E extends Edge<V>> implements EdgeStyle<V, E> {

	private final DefaultEdgeStyle<V, E> d;
	
	private Color color = null;
	private EdgePattern pattern = null;
	private EdgeEnd ending = null;
	private int width = -1;
	
	public SimpleEdgeStyle(DefaultEdgeStyle<V, E> defaultStyle) {
		this.d = defaultStyle;
	}
	
	@Override
	public Color getColor(E edge) {
		if (color == null) {
			return d.getColor(edge);
		}
		return color;
	}

	@Override
	public int getWidth(E edge) {
		if (width <0) {
			return d.getWidth(edge);
		}
		return width;
	}
	@Override
	public EdgePattern getPattern(E edge) {
		if (pattern == null) {
			return d.getPattern(edge);
		}
		return pattern;
	}

	@Override
	public EdgeEnd getEnding(E edge) {
		if (ending == null) {
			return d.getEnding(edge);
		}
		return ending;
	}

	@Override
	public boolean setColor(Color color) {
		this.color = color;
		return true;
	}

	@Override
	public boolean setWidth(int w) {
		this.width = w;
		return true;
	}

	@Override
	public boolean setPattern(EdgePattern pattern) {
		this.pattern = pattern;
		return true;
	}

	@Override
	public boolean setEnding(EdgeEnd ending) {
		this.ending = ending;
		return true;
	}

}
