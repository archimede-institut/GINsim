package org.ginsim.graph.tree;

import org.ginsim.graph.common.Edge;
import org.ginsim.graph.common.Graph;

public interface Tree extends Graph<TreeNode, Edge<TreeNode>>{
	
	
	public TreeParser getParser();
	
	
	public void setMode(int treeMode);
	
	public int getMode();
	
	
	public void setRoot( TreeNode root);
	
	/**
	 * add an edge between source and target
	 * @param source
	 * @param target
	 * @return the new edge
	 */
	public Edge<TreeNode> addEdge(TreeNode source, TreeNode target);
	
	
	/**
     * Indicates if the tree contains a node
     * @param node
     * @return true if the tree contains the node
     */
    public boolean containsNode(TreeNode node);
}
