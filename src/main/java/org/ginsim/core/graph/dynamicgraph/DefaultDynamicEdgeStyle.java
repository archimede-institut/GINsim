package org.ginsim.core.graph.dynamicgraph;

import java.awt.Color;

import org.ginsim.core.graph.view.EdgePattern;
import org.ginsim.core.graph.view.style.EdgeStyleImpl;

public class DefaultDynamicEdgeStyle extends EdgeStyleImpl<DynamicNode, DynamicEdge> {

	@Override
	public EdgePattern getPattern(DynamicEdge edge) {
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
		switch (edge.changeType) {
		case INCREASE:
		case MULTIPLE_INCREASE:
			return Color.GREEN;
		case DECREASE:
		case MULTIPLE_DECREASE:
			return Color.RED;
		}
		return super.getColor(edge);
	}

	@Override
	public boolean enforcePattern() {
		return true;
	}

	
}
