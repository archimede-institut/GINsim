package org.ginsim.core.graph.dynamicgraph;

import org.ginsim.core.graph.view.DefaultNodeStyle;

public class DefaultDynamicNodeStyle extends DefaultNodeStyle {

	public DefaultDynamicNodeStyle(DynamicGraph graph) {
		setDimension(5+10*graph.getNodeOrderSize(), 25);
	}
	
}
