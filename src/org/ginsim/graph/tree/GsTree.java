package org.ginsim.graph.tree;

import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;

import org.ginsim.exception.GsException;
import org.ginsim.graph.common.AbstractGraphFrontend;
import org.ginsim.graph.common.Edge;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.regulatorygraph.GsRegulatoryGraph;
import org.ginsim.graph.regulatorygraph.GsRegulatoryVertex;
import org.ginsim.gui.service.tools.regulatorytreefunction.GsTreeActionPanel;
import org.ginsim.gui.shell.editpanel.AbstractParameterPanel;

import fr.univmrs.tagc.GINsim.regulatoryGraph.OmddNode;

public class GsTree extends AbstractGraphFrontend<GsTreeNode, Edge<GsTreeNode>> {
	
	public final static int MODE_DIAGRAM_WITH_MULTIPLE_LEAFS = 0;
	public final static int MODE_DIAGRAM = 1;
	public final static int MODE_TREE = 2;

	/**
	 * The tree pendant to OmddNode.TERMINALS
	 */
	protected static GsTreeNode[] leafs;

	static {
		leafs = new GsTreeNode[OmddNode.TERMINALS.length];
		
		for (byte i = 0; i < OmddNode.TERMINALS.length; i++) {
			leafs[i] = new GsTreeNode(""+i,-1, i, GsTreeNode.TYPE_LEAF, i);
		}
	}
	
	
	private GsRegulatoryGraph regGraph = null;
	
	private int mode;
	public GsTreeNode root = null;
	private JPanel graphEditor = null;
	private GsTreeParser parser;
	
	public GsTree(GsTreeParser parser) {
		super();
		this.parser = parser;
		parser.setTree(this);
	}

	/**
     * Indicates if the tree contains a node
     * @param node
     * @return true if the tree contains the node
     */
    public boolean containsNode(GsTreeNode node) {
    	
    	for (Iterator it = getVertices().iterator(); it.hasNext();) {
    		GsTreeNode treeNode = (GsTreeNode) it.next();
    		if (treeNode == node) return true;
		}
		return false;
	}
    
    /* Save */
    
	protected void doSave(OutputStreamWriter os, int mode, boolean selectedOnly) throws GsException {

	}

    /* edge and vertex panels */
    
	public AbstractParameterPanel getEdgeAttributePanel() {
		return null;
	}

	public AbstractParameterPanel getVertexAttributePanel() {
		return null;
	}
	public JPanel getGraphParameterPanel() {
        if (graphEditor == null) {
            graphEditor = new GsTreeActionPanel(this, parser);
		}
		return graphEditor;	
	}
	
	/* adding edge and vertex */
//	/**
//	 * add a vertex to this graph.
//	 * @param vertex
//	 */
	// TODO REMOVE since it duplicates a method existing in AbstractGraphFrontend
//	public boolean addVertex(GsTreeNode vertex) {
//		
//		return graphManager.addVertex(vertex);
//	}
	/**
	 * add an edge between source and target
	 * @param source
	 * @param target
	 * @return the new edge
	 */
	public Edge<GsTreeNode> addEdge(GsTreeNode source, GsTreeNode target) {
		
		Edge<GsTreeNode> edge = getEdge(source, target);
		if (edge == null) {
			edge = new Edge<GsTreeNode>(source, target);
			if (!addEdge(edge)) {
				return null;
			}
		}
		return edge;
	}
	
		
	/* Getters/ Setters */

	public int getMode() { 
		return mode; 
	}
	
	public void setMode(int treeMode) { 
		this.mode = treeMode; 
	}
	public GsTreeParser getParser() {
		return parser; 
	}
	
	public void setParser(GsTreeParser parser) {
		
		this.parser = parser; 
		parser.setTree(this);
	}
	
	public List<GsRegulatoryVertex> getNodeOrder() {
		
		if (regGraph != null)
			return regGraph.getNodeOrder();
		return null;
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
	public void doSave(OutputStreamWriter osw, Collection<GsTreeNode> vertices,
			Collection<Edge<GsTreeNode>> edges, int saveMode) {
		// TODO Auto-generated method stub
	}

	@Override
	protected List<?> doMerge(Graph<GsTreeNode, Edge<GsTreeNode>> graph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Graph<GsTreeNode, Edge<GsTreeNode>> getSubgraph(
			Collection<GsTreeNode> vertex, Collection<Edge<GsTreeNode>> edges) {
		// TODO Auto-generated method stub
		return null;
	}
}
