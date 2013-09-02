package org.ginsim.core.graph.view.style;

import java.awt.Color;

import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.view.EdgeEnd;
import org.ginsim.core.graph.view.EdgePattern;


public class EdgeStyleOverride<V,E extends Edge<V>> implements EdgeStyle<V,E> {

	private final EdgeStyle<V,E> defaultStyle;
	private EdgeStyle<V,E> baseStyle;
	
	public EdgeStyleOverride(EdgeStyle<V,E> style) {
		this.defaultStyle = style;
		this.baseStyle = defaultStyle;
	}
	
	public void setBaseStyle(EdgeStyle<V,E> style) {
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
	public Color getColor(E edge) {
		return baseStyle.getColor(edge);
	}

	@Override
	public int getWidth(E edge) {
		return baseStyle.getWidth(edge);
	}

	@Override
	public EdgePattern getPattern(E edge) {
		return baseStyle.getPattern(edge);
	}

	@Override
	public EdgeEnd getEnding(E edge) {
		return baseStyle.getEnding(edge);
	}

	@Override
	public boolean enforceColor() {
		return false;
	}

	@Override
	public boolean enforceEnding() {
		return false;
	}

	@Override
	public boolean enforcePattern() {
		return false;
	}

	@Override
	public boolean enforceWidth() {
		return false;
	}

	@Override
	public boolean matches(Color color, EdgePattern pattern, int width) {
		return false;
	}
	
}
