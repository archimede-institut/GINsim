package org.ginsim.core.graph.regulatorygraph;

import org.ginsim.core.graph.view.NodeShape;
import org.ginsim.core.graph.view.style.NodeStyleImpl;

public class DefaultRegulatoryNodeStyle extends NodeStyleImpl<RegulatoryNode> {

	@Override
	public NodeShape getNodeShape(RegulatoryNode vertex) {
		if (vertex != null && vertex.getMaxValue() > 1) {
			return NodeShape.RECTANGLE;
		}
		return NodeShape.ELLIPSE;
	}
	
	
}
