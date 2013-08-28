package org.ginsim.core.graph.tree;

import java.awt.Color;

import org.ginsim.common.utils.ColorPalette;
import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.view.style.EdgeStyleImpl;

public class DefaultTreeEdgeStyle extends EdgeStyleImpl<TreeNode, TreeEdge> {

	@Override
	public Color getColor(TreeEdge edge) {
		return ColorPalette.defaultPalette[edge.getValue()+1];
	}
	
}
