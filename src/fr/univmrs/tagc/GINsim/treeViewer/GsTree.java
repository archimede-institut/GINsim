package fr.univmrs.tagc.GINsim.treeViewer;

import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import org.ginsim.graph.AbstractGraphFrontend;
import org.ginsim.graph.Edge;
import org.ginsim.graph.Graph;

import fr.univmrs.tagc.GINsim.data.GsDirectedEdge;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.gui.GsFileFilter;
import fr.univmrs.tagc.GINsim.gui.GsParameterPanel;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraphOptionPanel;
import fr.univmrs.tagc.GINsim.regulatoryGraph.OmddNode;
import fr.univmrs.tagc.common.GsException;
import fr.univmrs.tagc.common.managerresources.Translator;

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
	
	
	private JPanel optionPanel = null;
	
	private GsRegulatoryGraph regGraph = null;
	
	private int mode;
	public GsTreeNode root = null;
	private JPanel graphEditor = null;
	private GsTreeParser parser;
	
	public GsTree(GsTreeParser parser) {
		super( new GsTreeDescriptor());
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


	
	/* FILE handling */
	protected JPanel doGetFileChooserPanel() {
		return getOptionPanel();
	}
	private JPanel getOptionPanel() {
		if (optionPanel == null) {
            Object[] t_mode = { Translator.getString("STR_saveNone"),
                    Translator.getString("STR_savePosition"),
                    Translator.getString("STR_saveComplet") };
            optionPanel = new GsRegulatoryGraphOptionPanel(t_mode, mainFrame != null ? 2 : 0);
		}
		return optionPanel ;
	}
	
	protected FileFilter doGetFileFilter() {
		GsFileFilter ffilter = new GsFileFilter();
		ffilter.setExtensionList(new String[] {"ginml", "zginml"}, "(z)ginml files");
		return ffilter;
	}
	
	
	/* GsTreeDescriptor mapping */

	public List getSpecificLayout() {
		return GsTreeDescriptor.getLayout();
	}
	public List getSpecificExport() {
		return GsTreeDescriptor.getExport();
	}
    public List getSpecificAction() {
        return GsTreeDescriptor.getAction();
    }
    public List getSpecificObjectManager() {
        return GsTreeDescriptor.getObjectManager();
    }
    
    /* Save */
    
	protected void doSave(OutputStreamWriter os, int mode, boolean selectedOnly) throws GsException {

	}
	

    
    /* edge and vertex panels */
    
	public GsParameterPanel getEdgeAttributePanel() {
		return null;
	}

	public GsParameterPanel getVertexAttributePanel() {
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
	public GsDirectedEdge<GsTreeNode> addEdge(GsTreeNode source, GsTreeNode target) {
		
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

	public int getMode() { return mode; }
	public void setMode(int treeMode) { this.mode = treeMode; }
	public GsTreeParser getParser() {return parser; }
	public void setParser(GsTreeParser parser) {
		this.parser = parser; 
		parser.setTree(this);
	}
	
	public List getNodeOrder() {
		
		if (regGraph != null)
			return regGraph.getNodeOrder();
		return null;
	}

	/* Not used methods */
	public 	  void 		removeEdge(GsDirectedEdge<GsTreeNode> obj) {}
	public    void 		changeVertexId(Object vertex, String newId) throws GsException {}
	protected void 		setCopiedGraph( Graph graph) {}
	protected Graph 	getCopiedGraph() {return null;}
	protected Graph 	getSubGraph(Collection vertex, Collection edges) {return null;}
	protected GsDirectedEdge<GsTreeNode> 	doInteractiveAddEdge(GsTreeNode source, GsTreeNode target, int param) {return null;}
	protected GsTreeNode 	doInteractiveAddVertex(int param) {return null;}
	protected List		doMerge(Graph otherGraph) {return null;}
	public	  Vector 	searchNodes(String regexp) {return null;}
}
