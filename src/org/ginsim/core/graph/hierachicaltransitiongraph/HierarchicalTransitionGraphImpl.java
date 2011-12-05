package org.ginsim.core.graph.hierachicaltransitiongraph;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ginsim.common.xml.XMLWriter;
import org.ginsim.core.exception.GsException;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.AbstractDerivedGraph;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.common.NodeInfo;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.io.parser.GinmlHelper;
import org.ginsim.gui.graph.hierarchicaltransitiongraph.HierarchicalEdgeParameterPanel;
import org.ginsim.gui.resource.Translator;
import org.ginsim.gui.shell.editpanel.AbstractParameterPanel;
import org.ginsim.servicegui.tool.decisionanalysis.DecisionOnEdge;
import org.ginsim.servicegui.tool.reg2dyn.SimulationParameters;


public class HierarchicalTransitionGraphImpl extends AbstractDerivedGraph<HierarchicalNode, DecisionOnEdge, RegulatoryGraph, RegulatoryNode, RegulatoryMultiEdge>
	implements HierarchicalTransitionGraph{

	public static final String GRAPH_ZIP_NAME = "hierarchicalTransitionGraph.ginml";
	
	public static final int MODE_SCC = 1;
	public static final int MODE_HTG = 2;

	private String dtdFile = GinmlHelper.DEFAULT_URL_DTD_FILE;
	
	private List<NodeInfo> nodeOrder = new ArrayList<NodeInfo>();
	
	/**
	 * Mode is either SCC or HTG depending if we group the transients component by their atteignability of attractors.
	 */
	private int transientCompactionMode;
	
	/**
	 * An array indicating for each node in the nodeOrder their count of childs. (ie. their max value)
	 */
	private byte[] childsCount = null;
	private HierarchicalNodeParameterPanel vertexPanel = null;
	private long saveEdgeId;
	private SimulationParameters simulationParameters;
	private HierarchicalEdgeParameterPanel edgePanel;

	
/* **************** CONSTRUCTORS ************/	
	
	
	/**
	 * create a new empty DynamicalHierarchicalGraph.
	 */
	public HierarchicalTransitionGraphImpl() {
		this( false);
	}
				
	/**
	 * create a new DynamicalHierarchicalGraph with a nodeOrder.
	 * @param nodeOrder the node order
	 * @param transientCompactionMode MODE_SCC or MODE_HTG
	 */
	public HierarchicalTransitionGraphImpl( List<RegulatoryNode> nodeOrder, int transientCompactionMode) {
		
	    this();
	    for (RegulatoryNode vertex: nodeOrder) {
	    	this.nodeOrder.add(new NodeInfo(vertex));
	    }
	    this.transientCompactionMode = transientCompactionMode;
	}

	public HierarchicalTransitionGraphImpl( boolean parsing) {
		
        super( parsing);
	}

	public HierarchicalTransitionGraphImpl(Map map, File file) {
		
	    this( true);
        HierarchicalTransitionGraphParser parser = new HierarchicalTransitionGraphParser();
        parser.parse(file, map, this);
	}

	/**
	 * Return the node order as a List of NodeInfo
	 * 
	 * @return the node order as a List of NodeInfo
	 */
	@Override
	public List<NodeInfo> getNodeOrder() {
		return nodeOrder;
	}
	
    /**
     * Return the size of the node order
     * 
     * @return the size of the node order
     */
    @Override
	public int getNodeOrderSize(){
		
		if( nodeOrder != null){
			return nodeOrder.size();
		}
		else{
			return 0;
		}
	}
	
    
	/**
	 * Set a list of NodeInfo representing the order of node as defined by the model
	 * 
	 * @param list the list of NodeInfo representing the order of node as defined by the model
	 */
    @Override
	public void setNodeOrder( List<NodeInfo> node_order){
		
		nodeOrder = node_order;
	}


/* **************** EDITION OF VERTEX AND EDGE ************/	

	/**
	 * add an edge between source and target
	 * @param source a HierarchicalNode
	 * @param target a HierarchicalNode
	 * @return the new edge
	 */
	@Override
	public Object addEdge(HierarchicalNode source, HierarchicalNode target) {
		
		Object e = getEdge(source, target);
		if (e != null) return e;
		// FIXME: creating an empty DecisionOnEdge object: is it even possible?
		DecisionOnEdge edge = new DecisionOnEdge( source, target, nodeOrder);
		return addEdge(edge);
	}

		
		
/* **************** PANELS ************/	
		
    
	public AbstractParameterPanel getEdgeAttributePanel() {
	    if (edgePanel == null) {
	    	edgePanel  = new HierarchicalEdgeParameterPanel(this);
	    }
		return edgePanel;
	}

	public AbstractParameterPanel getNodeAttributePanel() {
	    if (vertexPanel == null) {
	        vertexPanel  = new HierarchicalNodeParameterPanel(this);
	    }
		return vertexPanel;
	}
	
		
/* **************** SAVE ************/	
		
	/**
	 * Return the zip extension for the graph type
	 * 
	 * @return the zip extension for the graph type
	 */
	@Override
	public String getGraphZipName(){
		
		return GRAPH_ZIP_NAME;
		
	}

	
	protected void doSave(OutputStreamWriter os, Collection<HierarchicalNode> nodes, Collection<DecisionOnEdge> edges, int mode) throws GsException {
       try {
            XMLWriter out = new XMLWriter(os, dtdFile);
	  		out.write("<gxl xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n");
			out.write("\t<graph id=\"" + graphName + "\"");
			out.write(" class=\"hierarchicalTransitionGraph\"");
			out.write(" iscompact=\""+this.transientCompactionMode+"\"");
			out.write(" nodeorder=\"" + stringNodeOrder() +"\">\n");
			saveNode(out, mode, nodes);
			saveEdge(out, mode, edges);
            if (graphAnnotation != null) {
            	graphAnnotation.toXML(out, null, 0);
            }
            // save the ref of the associated regulatory graph!
            if (associatedGraph != null) {
                associatedID = GraphManager.getInstance().getGraphPath( associatedGraph);
            }
            if (associatedID != null) {
                out.write("<link xlink:href=\""+associatedID+"\"/>\n");
            }
	  		out.write("\t</graph>\n");
	  		out.write("</gxl>\n");
        } catch (IOException e) {
            throw new GsException(GsException.GRAVITY_ERROR, Translator.getString("STR_unableToSave")+": " +e.getMessage());
        }
	}
	
	private void saveEdge(XMLWriter out, int mode, Collection<DecisionOnEdge> edges) throws IOException {
	     for (DecisionOnEdge edge: edges) {
            String source = "" + edge.getSource().getUniqueId();
            String target = "" + edge.getTarget().getUniqueId();
            out.write("\t\t<edge id=\"e"+(++saveEdgeId)+"\" from=\"s"+source+"\" to=\"s"+target+"\"/>\n");
	     }
	 }
	 
	 /**
	  * @param out
	  * @param mode
	  * @param vertices
	  * @throws IOException
	  */
	 private void saveNode(XMLWriter out, int mode, Collection<HierarchicalNode> vertices) throws IOException {
	 	NodeAttributesReader vReader = getNodeAttributeReader();
	     for (HierarchicalNode vertex: vertices) {
	         vReader.setNode(vertex);
	         out.write("\t\t<node id=\"s"+vertex.getUniqueId()+"\">\n");
	         out.write("<attr name=\"type\"><string>"+vertex.typeToString()+"</string></attr>");
	         out.write("<attr name=\"states\"><string>"+vertex.write().toString()+"</string></attr>");
	         out.write(GinmlHelper.getFullNodeVS(vReader));
	         out.write("\t\t</node>\n");
	     }
	 }		
		
/* **************** NODE SEARCH ************/
	
	// TODO : REFACTORING ACTION
	// TODO : The renaming of "Vertices" to "Nodes" has generate a collision between this method (originally named searchNodes)
	// and the one in AbstractGraph, originaly named searchVertices. However, this method seems to be unused (private and no
	 // local call) : do we have to keep it?
//	private Vector searchNodes(String regexp) {
//		Vector v = new Vector();
//		
//		StringBuffer s = new StringBuffer();
//		for (int i = 0; i < regexp.length(); i++) {
//			char c = regexp.charAt(i);
//			if (c == '\\') {
//				s.append(regexp.charAt(++i));
//			} else if (c == '*') {
//				s.append("[0-9\\*]");
//			} else if (c == '0' || (c >= '1' && c <= '9')){
//				s.append("["+c+"\\*]");
//			} else if (c == ' ' || c == '\t') {
//				//pass
//			} else {
//				s.append(c);
//			}
//		}
//		Pattern pattern = Pattern.compile(s.toString(), Pattern.COMMENTS | Pattern.CASE_INSENSITIVE);
//		Matcher matcher = pattern.matcher("");
//		
//		for (Iterator it = this.getNodes().iterator(); it.hasNext();) {
//			HierarchicalNode vertex = (HierarchicalNode) it.next();
//			matcher.reset(vertex.statesToString());
//			if (matcher.find()) {
//				v.add(vertex);
//			}
//		}
//		return v;
//	}
	
	@Override
	public HierarchicalNode getNodeForState(byte[] state) {
		for (Iterator it = this.getNodes().iterator(); it.hasNext();) {
			HierarchicalNode v = (HierarchicalNode) it.next();
			if (v.contains(state)) return v;
		}
		return null;
	}
	
		
	
		
/* **************** GETTER AND SETTERS ************/
		
	/**
	 * return an array indicating for each node in the nodeOrder their count of childs. (ie. their max value)
	 */
	@Override
	public byte[] getChildsCount() {
		if (childsCount == null) {
			childsCount = new byte[nodeOrder.size()];
			int i = 0;
			for (NodeInfo v: nodeOrder) {
				childsCount[i++] = (byte) ( v.getMax()+1);
			}			
		}
		return childsCount;
	}
	
	@Override
	public void setChildsCount(byte[] cc) {
		childsCount = cc;
	}
	
	/**
	 * Return a string representation of the nodeOrder
	 *  
	 * ex : <tt>G0:1 G1:2 G2:1 G3:3</tt>
	 * 
	 * @return
	 */
	private String stringNodeOrder() {
		String s = "";
		for (NodeInfo v: nodeOrder) {
			s += v.getNodeID() + ":" + v.getMax() + " ";
		}
		if (s.length() > 0) {
			return s.substring(0, s.length()-1);
		}
		return s;
	}
	
	/**
	 * Return <b>true</b> if the transients are compacted into component by their atteignability of attractors.
	 * @return
	 */
	@Override
	public boolean areTransientCompacted() {
		return transientCompactionMode == MODE_HTG;
	}
	
	@Override
	public void setMode(int mode) {
		transientCompactionMode = mode;
	}

    /**
     * Indicates if the given graph can be associated to the current one
     * 
     * @param graph the graph to associate to the current one
     * @return true is association is possible, false if not
     */
    @Override
    protected boolean isAssociationValid( Graph<?, ?> graph) {
    	
    	if( graph instanceof RegulatoryGraph){
    		return true;
    	}
    	
    	return false;
    }

		
/* **************** UNIMPLEMENTED METHODS ************/


	/**
	 * @see fr.univmrs.tagc.GINsim.graph.GsGraph#getSubGraph(java.utils.Vector, java.utils.Vector)
	 * 
	 * not used for this kind of graph: it's not interactively editable
	 */
	@Override
	public Graph getSubgraph(Collection<HierarchicalNode> vertex, Collection<DecisionOnEdge> edges) {
		return null;
	}

    /**
	 * @see fr.univmrs.tagc.GINsim.graph.GsGraph#doMerge(fr.univmrs.tagc.GINsim.graph.GsGraph)
	 * 
	 * not used for this kind of graph: it has no meaning
     */
	@Override
	protected List doMerge( Graph otherGraph) {
        return null;
    }


}
