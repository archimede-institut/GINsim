package org.ginsim.graph.tree;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.ginsim.graph.common.VertexAttributesReader;

import fr.univmrs.tagc.common.Tools;

public abstract class GsTreeParser<V,E> {
	public static final String PARAM_NODEORDER = "p_nodeOrder";
	protected GsTree tree;
	protected List nodeOrder;
	private Map parameters;

	/**
	 * Create a new parser, knowing a nodeOrder and the tree to fill.
	 */
	public GsTreeParser() {
	}
	
	/**
	 * Run the parser on the OmddNode root, and fill the tree.
	 * 
	 * @param treeMode the kind of tree to parse (tree, diagram or diagramWithAllleafs)
	 */
	public void run(int treeMode) {
		tree.setMode(treeMode);
		this.nodeOrder = (List) getParameter(PARAM_NODEORDER);
		
		clearTree();			//Remove all the treeNode from the current tree
		init();					//Init the variables needed for the parsing
		parseOmdd();			//Launch the correct parser depending on the mode.
		updateVertexLayout();	//Refresh the position and style of the GsTreeNodes
	}

	public void setTree(GsTree tree) {
		this.tree = tree;
	}
	
	/**
	 * This method is called before parsing the tree. Should initialize all the side variables.
	 */
	public abstract void init();

	public abstract void parseOmdd();
	public abstract void updateLayout(VertexAttributesReader vreader, GsTreeNode vertex);

	
	/**
	 * Compute the layout for each node in this tree
	 */
	public void updateVertexLayout() {
		VertexAttributesReader vreader = tree.getVertexAttributeReader();
		for (Iterator it = tree.getVertices().iterator(); it.hasNext();) {
			GsTreeNode vertex = (GsTreeNode) it.next();
			updateLayout(vreader, vertex);
		}		
	}
	
	/**
	 * Remove all the vertices from the tree
	 */
	public void clearTree() {
		Vector<GsTreeNode> tmp = new Vector( tree.getVertexCount());
		for (Iterator<GsTreeNode> it = tree.getVertices().iterator(); it.hasNext();) {
			GsTreeNode vertex = (GsTreeNode) it.next();
			tmp.add(vertex);
		}
		for (Iterator it = tmp.iterator(); it.hasNext();) {
			GsTreeNode vertex = (GsTreeNode) it.next();
			tree.removeVertex(vertex);
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