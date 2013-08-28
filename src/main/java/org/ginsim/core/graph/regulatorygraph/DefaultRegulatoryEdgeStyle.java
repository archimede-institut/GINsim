package org.ginsim.core.graph.regulatorygraph;

import java.awt.Color;

import org.ginsim.core.graph.view.EdgeEnd;
import org.ginsim.core.graph.view.style.EdgeStyleImpl;
import org.ginsim.core.graph.view.style.StyleProperty;

/**
 * Special default edge properties for regulatory graphs.
 * 
 * @author Aurelien Naldi
 */
public class DefaultRegulatoryEdgeStyle extends EdgeStyleImpl<RegulatoryNode, RegulatoryMultiEdge> {

	private static final StyleProperty PROP_POSITIVE = StyleProperty.createColorProperty("positive");
	private static final StyleProperty PROP_NEGATIVE = StyleProperty.createColorProperty("negative");
	private static final StyleProperty PROP_DUAL = StyleProperty.createColorProperty("dual");
	
	private static final StyleProperty[] EXTRA_PROPERTIES = {
		PROP_POSITIVE,
		PROP_NEGATIVE,
		PROP_DUAL,
	};
	private static final StyleProperty[] PROPERTIES = StyleProperty.merge(EdgeStyleImpl.DEFAULT_PROPERTIES, EXTRA_PROPERTIES);
	
	private Color c_positive = Color.cyan;
	private Color c_negative = Color.red.darker();
	private Color c_dual = Color.blue.darker();
	
	@Override
	public Color getColor(RegulatoryMultiEdge edge) {
		switch (edge.getSign()) {
		case POSITIVE:
			return c_positive;
		case NEGATIVE:
			return c_negative;
		case DUAL:
			return c_dual;
		}
		return super.getColor(edge);
	}

	@Override
	public EdgeEnd getEnding(RegulatoryMultiEdge edge) {
		switch (edge.getSign()) {
		case POSITIVE:
			return EdgeEnd.POSITIVE;
		case NEGATIVE:
			return EdgeEnd.NEGATIVE;
		case DUAL:
			return EdgeEnd.DUAL;
		}
		return EdgeEnd.UNKNOWN;
	}

	@Override
	public boolean setColor(Color color) {
		return false;
	}

	@Override
	public boolean setEnding(EdgeEnd ending) {
		return false;
	}
	
	@Override
	public boolean enforceEnding() {
		return true;
	}

	
	@Override
	public StyleProperty[] getProperties() {
		return PROPERTIES;
	}

	@Override
	protected Object getCustomProperty(StyleProperty prop) {
		if (prop == PROP_POSITIVE) {
			return c_positive;
		}
		if (prop == PROP_NEGATIVE) {
			return c_negative;
		}
		if (prop == PROP_DUAL) {
			return c_dual;
		}
		return null;
	}

	@Override
	protected void setCustomProperty(StyleProperty prop, Object value) {
		if (prop == PROP_POSITIVE) {
			c_positive = (Color)value;
		}
		if (prop == PROP_NEGATIVE) {
			c_negative = (Color)value;
		}
		if (prop == PROP_DUAL) {
			c_dual = (Color)value;
		}
	}
}
