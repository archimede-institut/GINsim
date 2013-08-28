package org.ginsim.core.graph.tree;

import java.awt.Color;
import java.util.Collection;

import org.ginsim.common.utils.ColorPalette;
import org.ginsim.core.graph.view.NodeBorder;
import org.ginsim.core.graph.view.NodeShape;
import org.ginsim.core.graph.view.style.NodeStyleImpl;


public class DefaultTreeNodeStyle extends NodeStyleImpl<TreeNode> {

	@Override
	public Color getBackground(TreeNode vertex) {
		if (vertex.getType() == TreeNode.TYPE_LEAF) {
			return ColorPalette.defaultPalette[vertex.getValue()+1];
		}
		if (vertex.getValue() == TreeNode.SKIPPED) {
			return Color.WHITE;
		}
		return ColorPalette.defaultPalette[0];
	}

	@Override
	public Color getTextColor(TreeNode vertex) {
		if (vertex.getType() == TreeNode.TYPE_LEAF) {
			return Color.WHITE;
		}
		if (vertex.getValue() == TreeNode.SKIPPED) {
			return Color.BLACK;
		}
		return Color.WHITE;
	}

	@Override
	public NodeShape getNodeShape(TreeNode vertex) {
		if (vertex.getType() == TreeNode.TYPE_LEAF) {
			return NodeShape.ELLIPSE;
		}
		return NodeShape.RECTANGLE;
	}
	
}
