package org.ginsim.core.graph.view.style;

import java.awt.Color;

import org.ginsim.core.graph.Edge;
import org.ginsim.core.graph.view.EdgeEnd;
import org.ginsim.core.graph.view.EdgePattern;


public class EdgeStyleOverride<V,E extends Edge<V>> extends BaseStyleOverride<EdgeStyle<V, E>> implements EdgeStyle<V,E> {

	public EdgeStyleOverride(EdgeStyle<V,E> style) {
		super(style);
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

    @Override
    public String getCSS() {
        StringBuffer sb = new StringBuffer();
        // TODO: add CSS rules
        return sb.toString();
    }

    @Override
    public String getCSSClass(E edge) {
        return "edge";
    }

	@Override
	public void copy(Style source) {
		// No copy for style overrides
	}
}
