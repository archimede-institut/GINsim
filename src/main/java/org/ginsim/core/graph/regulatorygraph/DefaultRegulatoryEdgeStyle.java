package org.ginsim.core.graph.regulatorygraph;

import java.awt.Color;

import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.view.DefaultEdgeStyle;
import org.ginsim.core.graph.view.EdgeEnd;

/**
 * Special default edge properties for regulatory graphs.
 * 
 * @author Aurelien Naldi
 */
public class DefaultRegulatoryEdgeStyle extends DefaultEdgeStyle<RegulatoryNode, RegulatoryMultiEdge> {

	@Override
	public Color getColor(RegulatoryMultiEdge edge) {
		switch (edge.getSign()) {
		case POSITIVE:
			return Color.GREEN;
		case NEGATIVE:
			return Color.RED;
		case DUAL:
			return Color.BLUE;
		}
		return Color.BLACK;
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
	public void setColor(Color color) {
	}

	@Override
	public void setEnding(EdgeEnd ending) {
	}
}
