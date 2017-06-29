package org.ginsim.core.graph.dynamicgraph;

import java.awt.Color;

import org.ginsim.common.utils.ColorPalette;
import org.ginsim.core.graph.regulatorygraph.RegulatoryEdgeSign;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.view.EdgePattern;
import org.ginsim.core.graph.view.style.EdgeStyleImpl;
import org.ginsim.core.graph.view.style.StyleProperty;

/**
 * Default style for edges in the State Transition Graph.
 * The color denote the type of changes in this transition.
 * Dashed edges denote multiple changes.
 *
 * @author Aurelien Naldi
 */
public class DefaultDynamicEdgeStyle extends EdgeStyleImpl<DynamicNode, DynamicEdge> {

	private static final StyleProperty PROP_INCREASE = StyleProperty.createColorProperty("inc");
	private static final StyleProperty PROP_DECREASE = StyleProperty.createColorProperty("dec");
	
	private static final StyleProperty[] EXTRA_PROPERTIES = {
		PROP_INCREASE,
		PROP_DECREASE,
	};
	private static final StyleProperty[] PROPERTIES = StyleProperty.merge(EdgeStyleImpl.DEFAULT_PROPERTIES, EXTRA_PROPERTIES);

	private Color c_inc = new Color(0,200,0);
	private Color c_dec = new Color(200,0,0);
	
	@Override
	public EdgePattern getPattern(DynamicEdge edge) {
        if (edge == null) {
            return EdgePattern.SIMPLE;
        }

		switch (edge.changeType) {
		case MULTIPLE_BOTH:
		case MULTIPLE_INCREASE:
		case MULTIPLE_DECREASE:
			return EdgePattern.DASH;
		}
		return EdgePattern.SIMPLE;
	}

	@Override
	public Color getColor(DynamicEdge edge) {
        if (edge == null) {
            return super.getColor(null);
        }

		switch (edge.changeType) {
		case INCREASE:
		case MULTIPLE_INCREASE:
			return c_inc;
		case DECREASE:
		case MULTIPLE_DECREASE:
			return c_dec;
		}
		return super.getColor(edge);
	}

	@Override
	public boolean enforcePattern() {
		return true;
	}

	@Override
	public StyleProperty[] getProperties() {
		return PROPERTIES;
	}

	@Override
	protected Object getCustomProperty(StyleProperty prop) {
		if (prop == PROP_INCREASE) {
			return c_inc;
		}
		if (prop == PROP_DECREASE) {
			return c_dec;
		}
		return null;
	}

	@Override
	protected void setCustomProperty(StyleProperty prop, Object value) {
		if (value == null) {
			return;
		}
		
		if (prop == PROP_INCREASE) {
			c_inc = (Color)value;
		}
		if (prop == PROP_DECREASE) {
			c_dec = (Color)value;
		}
	}

    @Override
    public String getCSS() {
        String parent = super.getCSS();

        // add styles for signed edges
        StringBuffer sb = new StringBuffer();
        sb.append(".edge_inc {\n");
        sb.append("stroke: "+ColorPalette.getColorCode(c_inc)+";\n");
        sb.append("}\n");

        sb.append(".edge_dec {\n");
        sb.append("stroke: "+ColorPalette.getColorCode(c_dec)+";\n");
        sb.append("}\n");

        return parent+sb.toString();
    }

    @Override
    public String getCSSClass(DynamicEdge edge) {
		String ret = super.getCSSClass(edge);
		switch (edge.changeType) {
		case INCREASE:
		case MULTIPLE_INCREASE:
			return ret + " edge_inc";
		case DECREASE:
		case MULTIPLE_DECREASE:
			return ret + " edge_dec";
		}

    	return ret;
    }

}
