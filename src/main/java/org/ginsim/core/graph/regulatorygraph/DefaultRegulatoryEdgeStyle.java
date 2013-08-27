package org.ginsim.core.graph.regulatorygraph;

import java.awt.Color;

import org.ginsim.core.graph.view.EdgeEnd;
import org.ginsim.core.graph.view.EdgeStyleImpl;

/**
 * Special default edge properties for regulatory graphs.
 * 
 * @author Aurelien Naldi
 */
public class DefaultRegulatoryEdgeStyle extends EdgeStyleImpl<RegulatoryNode, RegulatoryMultiEdge> {

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
}
