package org.ginsim.graph.tree;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.ginsim.graph.common.NodeAttributesReader;

import fr.univmrs.tagc.common.Tools;

public abstract class TreeParser<V,E> {
	public static final String PARAM_NODEORDER = "p_nodeOrder";
	protected Tree tree;
	protected List nodeOrder;
	private Map parameters;

	/**
	 * Create a new parser, knowing a nodeOrder and the tree to fill.
	 */
	public TreeParser() {
	}
	
	/**
	 * Run the parser on the OMDDNode root, and fill the tree.
	 * 
	 * @param treeMode the kind of tree to parse (tree, diagram or diagramWithAllleafs)
	 */
	public void run(int treeMode) {
		tree.setMode(treeMode);
		this.nodeOrder = (List) getParameter(PARAM_NODEORDER);
		
		clearTree();			//Remove all the treeNode from the current tree
		init();					//Init the variables needed for the parsing
		parseOmdd();			//Launch the correct parser depending on the mode.
		updateNodeLayout();	//Refresh the position and style of the GsTreeNodes
	}

	public void setTree(Tree tree) {
		this.tree = tree;
	}
	
	/**
	 * This method is called before parsing the tree. Should initialize all the side variables.
	 */
	public abstract void init();

	public abstract void parseOmdd();
	public abstract void updateLayout(NodeAttributesReader vreader, TreeNode vertex);

	
	/**
	 * Compute the layout for each node in this tree
	 */
	public void updateNodeLayout() {
		NodeAttributesReader vreader = tree.getNodeAttributeReader();
		for (Iterator it = tree.getVertices().iterator(); it.hasNext();) {
			TreeNode vertex = (TreeNode) it.next();
			updateLayout(vreader, vertex);
		}		
	}
	
	/**
	 * Remove all the vertices from the tree
	 */
	public void clearTree() {
		Vector<TreeNode> tmp = new Vector( tree.getNodeCount());
		for (Iterator<TreeNode> it = tree.getVertices().iterator(); it.hasNext();) {
			TreeNode vertex = (TreeNode) it.next();
			tmp.add(vertex);
		}
		for (Iterator it = tmp.iterator(); it.hasNext();) {
			TreeNode vertex = (TreeNode) it.next();
			tree.removeNode(vertex);
		}
	}	
	
	public Object getParameter(String key) {
		Object value = this.parameters.get(key);
		if (value == null) {
			Tools.error("expected parameter '"+key+"' not found or null");
		}
		return value;
	}
	
	public void setParameter(String key, Object value) {
		if (parameters == null) {
			parameters = new HashMap();
		}
		parameters.put(key, value);
	}
	
	/**
	 * Little helper function to write a tree in stdout
	 * @param tabs the count of tab to output
	 * @return a string with <b>tab</b> tabs
	 */
	protected String tab(int tabs) {
    	if (tabs <= 0) return "";
    	StringBuffer s = new StringBuffer(tabs);
    	for (int i = 0; i <= tabs; i++) {
			s.append("   ");
		}
		return s.toString();
	}
	
}