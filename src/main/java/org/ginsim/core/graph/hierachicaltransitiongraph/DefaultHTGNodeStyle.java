package org.ginsim.core.graph.hierachicaltransitiongraph;

import java.awt.Dimension;

import org.ginsim.core.graph.view.DefaultNodeStyle;
import org.ginsim.core.graph.view.NodeShape;

public class DefaultHTGNodeStyle extends DefaultNodeStyle {

	public DefaultHTGNodeStyle(HierarchicalTransitionGraph graph) {
		setDimension(5+10*graph.getNodeOrderSize(), 25);
	}
	
	// TODO: default color based on node type
}
