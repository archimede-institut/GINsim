package org.ginsim.core.graph.regulatorygraph;

import java.awt.Color;

import org.ginsim.common.utils.ColorPalette;
import org.ginsim.core.graph.view.EdgeEnd;
import org.ginsim.core.graph.view.style.EdgeStyleImpl;
import org.ginsim.core.graph.view.style.StyleProperty;

/**
 * Default style for edge of regulatory graphs.
 *
 * Edge color and arrows denote the sign of the interactions.
 * 
 * @author Aurelien Naldi
 */
public class DefaultRegulatoryEdgeStyle extends EdgeStyleImpl<RegulatoryNode, RegulatoryMultiEdge> {

	private static final StyleProperty PROP_POSITIVE = StyleProperty.createColorProperty("positive");
	private static final StyleProperty PROP_NEGATIVE = StyleProperty.createColorProperty("negative");
	private static final StyleProperty PROP_DUAL = StyleProperty.createColorProperty("dual");
	
	private static final Color COLOR_POSITIVE = new Color(0,200,0);
	private static final Color COLOR_NEGATIVE = new Color(200,0,0);
	private static final Color COLOR_DUAL = new Color(0,0,200);
	private static final Color COLOR_NONE = new Color(20,20,20);
	
	private static final StyleProperty[] EXTRA_PROPERTIES = {
		PROP_POSITIVE,
		PROP_NEGATIVE,
		PROP_DUAL,
	};
	private static final StyleProperty[] PROPERTIES = StyleProperty.merge(EdgeStyleImpl.DEFAULT_PROPERTIES, EXTRA_PROPERTIES);

	public Color getDefaultColor(RegulatoryEdgeSign sign) {
		switch (sign) {
		case POSITIVE:
			return (Color)getProperty(PROP_POSITIVE);
		case NEGATIVE:
			return (Color)getProperty(PROP_NEGATIVE);
		case DUAL:
			return (Color)getProperty(PROP_DUAL);
		}
		return COLOR_NONE;
	}
	
	private Color c_positive = COLOR_POSITIVE;
	private Color c_negative = COLOR_NEGATIVE;
	private Color c_dual = COLOR_DUAL;
	
	public DefaultRegulatoryEdgeStyle() {
		// setWidth(2);
	}
	
	@Override
	public Color getColor(RegulatoryMultiEdge edge) {
        if (edge == null) {
            return super.getColor(null);
        }

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
        if (edge == null) {
            return EdgeEnd.UNKNOWN;
        }

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
 
    @Override
    public String getCSS() {
        String parent = super.getCSS();

        // add styles for signed edges
        StringBuffer sb = new StringBuffer();
        sb.append(".edge_"+ RegulatoryEdgeSign.POSITIVE+" {\n");
        sb.append("stroke: "+ColorPalette.getColorCode(c_positive)+";\n");
        sb.append("}\n");

        sb.append(".edge_"+ RegulatoryEdgeSign.NEGATIVE+" {\n");
        sb.append("stroke: "+ColorPalette.getColorCode(c_negative)+";\n");
        sb.append("}\n");

        sb.append(".edge_"+ RegulatoryEdgeSign.DUAL+" {\n");
        sb.append("stroke: "+ColorPalette.getColorCode(c_dual)+";\n");
        sb.append("}\n");

        return parent+sb.toString();
    }

    @Override
    public String getCSSClass(RegulatoryMultiEdge edge) {
        return super.getCSSClass(edge) + " edge_"+edge.getSign();
    }

}
