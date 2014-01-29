package org.ginsim.core.graph.tree;

import org.ginsim.core.graph.Edge;
import org.ginsim.core.graph.Graph;

public interface Tree extends Graph<TreeNode, TreeEdge>{
	
	
	public TreeBuilder getParser();
	
	
	public void setMode(int treeMode);
	
	public int getMode();
	
	/**
	 * Define the root (a TreeNode) of the tree
	 * @param root define the root 
	 */
	public void setRoot( TreeNode root);
	
	/**
	 * add an edge between source and target
	 * @param source
	 * @param target
	 * @return the new edge
	 */
	public Edge<TreeNode> addEdge(TreeNode source, TreeNode target, int value);
	
	
	/**
     * Indicates if the tree contains a node
     * @param node
     * @return true if the tree contains the node
     */
    public boolean containsNode(TreeNode node);
}
