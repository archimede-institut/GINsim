package org.ginsim.graph.tree;

import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;

import org.ginsim.exception.GsException;
import org.ginsim.graph.common.AbstractGraph;
import org.ginsim.graph.common.Edge;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.graph.regulatorygraph.RegulatoryVertex;
import org.ginsim.graph.regulatorygraph.omdd.OMDDNode;
import org.ginsim.gui.service.tool.regulatorytreefunction.TreeActionPanel;
import org.ginsim.gui.shell.editpanel.AbstractParameterPanel;


public class TreeImpl  extends AbstractGraph<TreeNode, Edge<TreeNode>> 
	implements Tree{
	
	public final static int MODE_DIAGRAM_WITH_MULTIPLE_LEAFS = 0;
	public final static int MODE_DIAGRAM = 1;
	public final static int MODE_TREE = 2;

	/**
	 * The tree pendant to OMDDNode.TERMINALS
	 */
	public static TreeNode[] leafs;

	static {
		leafs = new TreeNode[OMDDNode.TERMINALS.length];
		
		for (byte i = 0; i < OMDDNode.TERMINALS.length; i++) {
			leafs[i] = new TreeNode(""+i,-1, i, TreeNode.TYPE_LEAF, i);
		}
	}
	
	
	private RegulatoryGraph regGraph = null;
	
	private int mode;
	public TreeNode root = null;
	private JPanel graphEditor = null;
	private TreeParser parser;
	
	public TreeImpl(TreeParser parser) {
		super();
		this.parser = parser;
		parser.setTree(this);
	}

	/**
     * Indicates if the tree contains a node
     * @param node
     * @return true if the tree contains the node
     */
	@Override
    public boolean containsNode(TreeNode node) {
    	
    	for (Iterator it = getVertices().iterator(); it.hasNext();) {
    		TreeNode treeNode = (TreeNode) it.next();
    		if (treeNode == node) return true;
		}
		return false;
	}
    

    // TODO : REFACTORING ACTION
	// TODO : Does this method has to be moved to GUI side?
	public JPanel getGraphParameterPanel() {
        if (graphEditor == null) {
            graphEditor = new TreeActionPanel(this, parser);
		}
		return graphEditor;	
	}
	
	/* adding edge and vertex */
//	/**
//	 * add a vertex to this graph.
//	 * @param vertex
//	 */
	// TODO REMOVE since it duplicates a method existing in AbstractGraphFrontend
//	public boolean addVertex(TreeNode vertex) {
//		
//		return graphManager.addVertex(vertex);
//	}
	/**
	 * add an edge between source and target
	 * @param source
	 * @param target
	 * @return the new edge
	 */
	@Override
	public Edge<TreeNode> addEdge(TreeNode source, TreeNode target) {
		
		Edge<TreeNode> edge = getEdge(source, target);
		if (edge == null) {
			edge = new Edge<TreeNode>(source, target);
			if (!addEdge(edge)) {
				return null;
			}
		}
		return edge;
	}
	
		
	/* Getters/ Setters */


	@Override
	public void setRoot( TreeNode root) {
		
		this.root = root;
	}

	@Override
	public int getMode() { 
		return mode; 
	}


	@Override
	public void setMode(int treeMode) { 
		this.mode = treeMode; 
	}
	
	@Override
	public TreeParser getParser() {
		
		return parser; 
	}
	
	private void setParser(TreeParser parser) {
		
		this.parser = parser; 
		parser.setTree(this);
	}
	
	
    /**
     * Return the size of the node order
     * 
     * @return the size of the node order
     */
    @Override
	public int getNodeOrderSize(){
		
		return regGraph.getNodeOrderSize();
	}

	@Override
	public void doSave(OutputStreamWriter osw, Collection<TreeNode> vertices,
			Collection<Edge<TreeNode>> edges, int saveMode) {
		// TODO Auto-generated method stub
	}

	@Override
	protected List<?> doMerge(Graph<TreeNode, Edge<TreeNode>> graph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Graph<TreeNode, Edge<TreeNode>> getSubgraph(
			Collection<TreeNode> vertex, Collection<Edge<TreeNode>> edges) {
		// TODO Auto-generated method stub
		return null;
	}

}
