package org.ginsim.core.graph.regulatorygraph;

import java.awt.Color;

import org.ginsim.core.graph.view.NodeShape;
import org.ginsim.core.graph.view.style.NodeStyleImpl;
import org.ginsim.core.graph.view.style.StyleProperty;

public class DefaultRegulatoryNodeStyle extends NodeStyleImpl<RegulatoryNode> {

	private static final StyleProperty PROP_ACTIVE = StyleProperty.createColorProperty("active");
	private static final StyleProperty PROP_MIDLEVEL = StyleProperty.createColorProperty("intermediate");

	private static final Color COLOR_ACTIVE = Color.ORANGE;
	private static final Color COLOR_MIDLEVEL = Color.YELLOW;

	private static final StyleProperty[] REGUATORY_PROPERTIES = {PROP_MIDLEVEL, PROP_ACTIVE};
	private static final StyleProperty[] PROPERTIES = StyleProperty.merge(NodeStyleImpl.DEFAULT_PROPERTIES, REGUATORY_PROPERTIES);

	private Color c_midlevel = COLOR_MIDLEVEL, c_active = COLOR_ACTIVE;

	public Color getDefaultColor(ActivityLevel level) {
		switch (level) {
		case MIDLEVEL:
			return (Color)getProperty(PROP_MIDLEVEL);
		case ACTIVE:
			return (Color)getProperty(PROP_ACTIVE);
		}
		return Color.WHITE;
	}

	@Override
	public NodeShape getNodeShape(RegulatoryNode vertex) {
		if (vertex != null && vertex.getMaxValue() > 1) {
			return NodeShape.RECTANGLE;
		}
		return NodeShape.ELLIPSE;
	}
	
	@Override
	public StyleProperty[] getProperties() {
		return PROPERTIES;
	}

	@Override
	protected Object getCustomProperty(StyleProperty prop) {
		if (prop == PROP_MIDLEVEL) {
			return c_midlevel;
		}
		if (prop == PROP_ACTIVE) {
			return c_active;
		}
		return null;
	}

	@Override
	protected void setCustomProperty(StyleProperty prop, Object value) {
		if (prop == PROP_MIDLEVEL) {
			c_midlevel = (Color)value;
		}
		if (prop == PROP_ACTIVE) {
			c_active = (Color)value;
		}
	}

}
