package org.ginsim.core.graph.view;

import java.awt.Color;

import org.ginsim.core.graph.common.Edge;

/**
 * Simple implementation for EdgeStyle.
 * It can be used to override a parent style (defining some properties)
 * or as default style (defining all properties).
 * 
 * @author Aurelien Naldi
 *
 * @param <V>
 * @param <E>
 */
public class EdgeStyleImpl<V, E extends Edge<V>> implements EdgeStyle<V, E> {

	private static int DEFAULT_WIDTH = 1;
	private static Color DEFAULT_COLOR = Color.BLACK;
	private static EdgeEnd DEFAULT_ENDING = EdgeEnd.POSITIVE;
	private static EdgePattern DEFAULT_PATTERN = EdgePattern.SIMPLE;
	
	private final EdgeStyle<V, E> parent;
	
	private Color color = null;
	private EdgePattern pattern = null;
	private EdgeEnd ending = null;
	private int width = -1;
	
	public EdgeStyleImpl() {
		this(null);
	}
	
	public EdgeStyleImpl(EdgeStyle<V, E> defaultStyle) {
		this.parent = defaultStyle;
		
		if (parent == null) {
			color = DEFAULT_COLOR;
			pattern = DEFAULT_PATTERN;
			ending = DEFAULT_ENDING;
			width = DEFAULT_WIDTH;
		}
	}
	
	@Override
	public Color getColor(E edge) {
		if (color == null) {
			if (parent == null) {
				return DEFAULT_COLOR;
			}
			return parent.getColor(edge);
		}
		
		if (parent != null && parent.enforceColor()) {
			return parent.getColor(edge);
		}
		return color;
	}

	@Override
	public int getWidth(E edge) {
		if (width <0) {
			if (parent == null) {
				return DEFAULT_WIDTH;
			}
			return parent.getWidth(edge);
		}
		
		if (parent != null && parent.enforceWidth()) {
			return parent.getWidth(edge);
		}
		return width;
	}
	@Override
	public EdgePattern getPattern(E edge) {
		if (pattern == null) {
			if (parent == null) {
				return DEFAULT_PATTERN;
			}
			return parent.getPattern(edge);
		}
		
		if (parent != null && parent.enforcePattern()) {
			return parent.getPattern(edge);
		}
		return pattern;
	}

	@Override
	public EdgeEnd getEnding(E edge) {
		if (ending == null) {
			if (parent == null) {
				return DEFAULT_ENDING;
			}
			
			if (parent != null && parent.enforceEnding()) {
				return parent.getEnding(edge);
			}
			return parent.getEnding(edge);
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

	@Override
	public boolean enforceColor() {
		if (parent != null) {
			return parent.enforceColor();
		}
		return false;
	}

	@Override
	public boolean enforceEnding() {
		if (parent != null) {
			return parent.enforceEnding();
		}
		return false;
	}

	@Override
	public boolean enforcePattern() {
		if (parent != null) {
			return parent.enforcePattern();
		}
		return false;
	}

	@Override
	public boolean enforceWidth() {
		if (parent != null) {
			return parent.enforceWidth();
		}
		return false;
	}
}
