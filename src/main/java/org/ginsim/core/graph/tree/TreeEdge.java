package org.ginsim.core.graph.tree;

import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;

public class TreeEdge extends Edge<TreeNode> {

	private final int value;
	
	public TreeEdge(Graph g, TreeNode source, TreeNode target, int value) {
		super(g, source, target);
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
