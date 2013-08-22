package org.ginsim.core.graph.dynamicgraph;

import java.awt.Dimension;

import org.ginsim.core.graph.view.DefaultNodeStyle;
import org.ginsim.core.graph.view.NodeShape;

public class DefaultDynamicNodeStyle extends DefaultNodeStyle {

	public DefaultDynamicNodeStyle(DynamicGraph graph) {
		setDimension(5+10*graph.getNodeOrderSize(), 25);
	}
	
}
