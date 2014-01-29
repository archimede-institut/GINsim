package org.ginsim.core.graph.dynamicgraph;

import org.ginsim.core.graph.view.NodeShape;
import org.ginsim.core.graph.view.style.NodeStyleImpl;
import org.ginsim.core.graph.view.style.StyleProperty;

/**
 * Default style for nodes in the State Transition Graph.
 * The default width depends on the number of nodes.
 * Elliptic nodes denote stable states.
 *
 * @author Aurelien Naldi
 */
public class DefaultDynamicNodeStyle extends NodeStyleImpl<DynamicNode> {

	public DefaultDynamicNodeStyle(DynamicGraph graph) {
		int w = 5+10*graph.getNodeOrderSize();
		if (w < 30) {
			w = 30;
		} else if (w > 500) {
			w = 500;
		}
    	setProperty(StyleProperty.WIDTH, w);
    	setProperty(StyleProperty.HEIGHT, 25);
	}

	@Override
	public NodeShape getNodeShape(DynamicNode obj) {
		if (obj.isStable()) {
			return NodeShape.ELLIPSE;
		}
		return NodeShape.RECTANGLE;
	}
}
