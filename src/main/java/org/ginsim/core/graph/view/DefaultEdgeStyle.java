package org.ginsim.core.graph.view;

import java.awt.Color;

import org.ginsim.core.graph.common.Edge;

public class DefaultEdgeStyle<V, E extends Edge<V>> implements EdgeStyle<V, E> {

	private static EdgePattern DEFAULT_PATTERN = EdgePattern.SIMPLE;
	private static EdgeEnd DEFAULT_ENDING = EdgeEnd.POSITIVE;
	
	private Color color = Color.BLACK;
	private int width = 1;
	private EdgePattern pattern = DEFAULT_PATTERN;
	private EdgeEnd ending = DEFAULT_ENDING;
	
	@Override
	public Color getColor(E edge) {
		return color;
	}

	@Override
	public int getWidth(E edge) {
		return width;
	}

	@Override
	public EdgePattern getPattern(E edge) {
		return pattern ;
	}

	@Override
	public EdgeEnd getEnding(E edge) {
		return ending ;
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public void setWidth(int w) {
		this.width = w;
	}

	@Override
	public void setPattern(EdgePattern pattern) {
		if (pattern == null) {
			this.pattern = DEFAULT_PATTERN;
		} else {
			this.pattern = pattern;
		}
	}

	@Override
	public void setEnding(EdgeEnd ending) {
		if (ending == null) {
			this.ending = DEFAULT_ENDING;
		} else {
			this.ending = ending;
		}
	}
	
}
