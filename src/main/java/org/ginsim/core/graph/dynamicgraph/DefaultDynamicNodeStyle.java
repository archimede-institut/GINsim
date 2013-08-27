package org.ginsim.core.graph.dynamicgraph;

import org.ginsim.core.graph.view.NodeShape;
import org.ginsim.core.graph.view.NodeStyleImpl;

public class DefaultDynamicNodeStyle extends NodeStyleImpl<DynamicNode> {

	public DefaultDynamicNodeStyle(DynamicGraph graph) {
		setDimension(5+10*graph.getNodeOrderSize(), 25);
	}

	@Override
	public NodeShape getNodeShape(DynamicNode obj) {
		if (obj.isStable()) {
			return NodeShape.ELLIPSE;
		}
		return NodeShape.RECTANGLE;
	}
}
