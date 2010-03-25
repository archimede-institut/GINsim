package fr.univmrs.tagc.GINsim.treeViewer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import fr.univmrs.tagc.GINsim.graph.GsGraphManager;
import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.tagc.common.Tools;

public abstract class GsTreeParser {
	public static final String PARAM_NODEORDER = "p_nodeOrder";
	protected GsTree tree;
	protected List nodeOrder;
	protected GsGraphManager graphManager;
	private Map parameters;

	/**
	 * Create a new parser, knowing a nodeOrder and the tree to fill.
	 * @param nodeOrder
	 * @param tree
	 */
	public GsTreeParser() {
	}
	
	/**
	 * Run the parser on the OmddNode root, and fill the tree.
	 * 
	 * @param root the Omdd's root to parse
	 * @param treeMode the kind of tree to parse (tree, diagram or diagramWithAllleafs)
	 * @param parameters the parameters to use in the init() method.
	 */
	public void run(int treeMode) {
		tree.setMode(treeMode);
		this.graphManager = tree.getGraphManager();
		this.nodeOrder = (List) getParameter(PARAM_NODEORDER);
		
		clearTree();			//Remove all the treeNode from the current tree
		init();					//Init the variables needed for the parsing
		parseOmdd();			//Launch the correct parser depending on the mode.
		updateVertexLayout();	//Refresh the position and style of the GsTreeNodes
	}

	public void setTree(GsTree tree) {
		this.tree = tree;
		this.graphManager = tree.getGraphManager();
	}
	
	/**
	 * This method is called before parsing the tree. Should initialize all the side variables.
	 * 
	 * @param parameters a map of optional parameters to use during the initialization.
	 */
	public abstract void init();

	public abstract void parseOmdd();
	public abstract void updateLayout(GsVertexAttributesReader vreader, GsTreeNode vertex);

	
	
	/**
	 * Compute the layout for each node in this tree
	 */
	public void updateVertexLayout() {
		GsVertexAttributesReader vreader = tree.getGraphManager().getVertexAttributesReader();
		for (Iterator it = graphManager.getVertexIterator(); it.hasNext();) {
			GsTreeNode vertex = (GsTreeNode) it.next();
			updateLayout(vreader, vertex);
		}		
	}
	
	/**
	 * Remove all the vertices from the tree
	 * @param tree
	 */
	public void clearTree() {
		Vector tmp = new Vector(graphManager.getVertexCount());
		for (Iterator it = graphManager.getVertexIterator(); it.hasNext();) {
			Object vertex = (Object) it.next();
			tmp.add(vertex);
		}
		for (Iterator it = tmp.iterator(); it.hasNext();) {
			Object vertex = (Object) it.next();
			graphManager.removeVertex(vertex);
		}
	}	
	
	protected Object getParameter(String key) {
		Object value = this.parameters.get(key);
		if (value == null) {
			Tools.error("expected parameter '"+key+"' not found or null", null);
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
	 * @param tabs
	 * @return
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