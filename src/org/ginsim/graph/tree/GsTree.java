package org.ginsim.graph.tree;

import org.ginsim.graph.common.Edge;
import org.ginsim.graph.common.Graph;

public interface GsTree extends Graph<GsTreeNode, Edge<GsTreeNode>>{
	
	
	public GsTreeParser getParser();
	
	
	public void setMode(int treeMode);
	
	public int getMode();
	
	
	public void setRoot( GsTreeNode root);
	
	/**
	 * add an edge between source and target
	 * @param source
	 * @param target
	 * @return the new edge
	 */
	public Edge<GsTreeNode> addEdge(GsTreeNode source, GsTreeNode target);
	
	
	/**
     * Indicates if the tree contains a node
     * @param node
     * @return true if the tree contains the node
     */
    public boolean containsNode(GsTreeNode node);
}
