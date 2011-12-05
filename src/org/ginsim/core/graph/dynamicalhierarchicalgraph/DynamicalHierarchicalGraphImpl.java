package org.ginsim.core.graph.dynamicalhierarchicalgraph;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ginsim.common.xml.XMLWriter;
import org.ginsim.core.exception.GsException;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.AbstractDerivedGraph;
import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.common.NodeInfo;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.io.parser.GinmlHelper;
import org.ginsim.gui.resource.Translator;


public final class DynamicalHierarchicalGraphImpl  extends AbstractDerivedGraph<DynamicalHierarchicalNode, Edge<DynamicalHierarchicalNode>, RegulatoryGraph, RegulatoryNode, RegulatoryMultiEdge>
				implements DynamicalHierarchicalGraph{

	public static final String GRAPH_ZIP_NAME = "dynamicalHierarchicalGraph.ginml";
	
	private String dtdFile = GinmlHelper.DEFAULT_URL_DTD_FILE;
	
	private byte[] childsCount = null;
	private List<NodeInfo> nodeOrder = new ArrayList<NodeInfo>();

	/**
	 * create a new DynamicalHierarchicalGraph with a nodeOrder.
	 * @param nodeOrder the node order
	 */
	public DynamicalHierarchicalGraphImpl(List<RegulatoryNode> nodeOrder) {
		
	    this( false);
	    for (RegulatoryNode vertex: nodeOrder) {
	    	this.nodeOrder.add(new NodeInfo(vertex.getId(), vertex.getMaxValue()));
	    }
	}
	
	/**
	 * create a new empty DynamicalHierarchicalGraph.
	 */
	public DynamicalHierarchicalGraphImpl() {
		
		this( false);
	}
	
	public DynamicalHierarchicalGraphImpl( boolean parsing) {
		
        super( parsing);
	}


	public DynamicalHierarchicalGraphImpl(Map map, File file) {
		
	    this( true);
        DynamicalHierarchicalParser parser = new DynamicalHierarchicalParser();
        parser.parse(file, map, this);
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

    
    /* Save */
    
	/**
	 * Return the zip extension for the graph type
	 * 
	 * @return the zip extension for the graph type
	 */
	protected String getGraphZipName(){
		
		return GRAPH_ZIP_NAME; 
		
	}
    
	protected void doSave(OutputStreamWriter os, Collection<DynamicalHierarchicalNode> vertices, Collection<Edge<DynamicalHierarchicalNode>> edges, int mode) throws GsException {
	       try {
	            XMLWriter out = new XMLWriter(os, dtdFile);
		  		out.write("<gxl xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n");
				out.write("\t<graph id=\"" + graphName + "\"");
				out.write(" class=\"dynamicalHierarchical\"");
				out.write(" nodeorder=\"" + stringNodeOrder() +"\">\n");
				saveNode(out, mode, vertices);
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
	
    private void saveEdge(XMLWriter out, int mode, Collection<Edge<DynamicalHierarchicalNode>> edges) throws IOException {
    	
        for (Edge edge: edges) {
            String source = ""+((DynamicalHierarchicalNode)edge.getSource()).getUniqueId();
            String target =""+((DynamicalHierarchicalNode) edge.getTarget()).getUniqueId();
            out.write("\t\t<edge id=\"e"+ source +"_"+target+"\" from=\"s"+source+"\" to=\"s"+target+"\"/>\n");
        }
    }
    
    /**
     * @param out
     * @param mode
     * @param selectedOnly
     * @throws IOException
     */
    private void saveNode(XMLWriter out, int mode, Collection<DynamicalHierarchicalNode> vertices) throws IOException {
    	
    	Iterator<DynamicalHierarchicalNode> it = vertices.iterator();

    	NodeAttributesReader vReader = getNodeAttributeReader();
        while (it.hasNext()) {
        	DynamicalHierarchicalNode vertex = (DynamicalHierarchicalNode)it.next();
            vReader.setNode(vertex);
            out.write("\t\t<node id=\"s"+vertex.getUniqueId()+"\">\n");
            out.write("<attr name=\"type\"><string>"+vertex.typeToString()+"</string></attr>");
            out.write("<attr name=\"states\"><string>"+vertex.write().toString()+"</string></attr>");
            out.write(GinmlHelper.getFullNodeVS(vReader));
            out.write("\t\t</node>\n");
        }
    }
    
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
	 * add an edge between source and target
	 * @param source
	 * @param target
	 * @return the new edge
	 */
	@Override
	public Edge<DynamicalHierarchicalNode> addEdge(DynamicalHierarchicalNode source, DynamicalHierarchicalNode target) {
		
		Edge<DynamicalHierarchicalNode> edge = new Edge<DynamicalHierarchicalNode>(source, target);
		if (addEdge(edge)) {
			return edge;
		}
		return null;
	}


	
	/* something else */
	
	/**
	 * return an array indicating for each node in the nodeOrder their count of childs. (ie. their max value)
	 */
	@Override
	public byte[] getChildsCount() {
		if (childsCount == null) {
			childsCount = new byte[nodeOrder.size()];
			int i = 0;
			for (Iterator it = nodeOrder.iterator(); it.hasNext();) {
				RegulatoryNode v = (RegulatoryNode) it.next();
				childsCount[i++] = (byte) (v.getMaxValue()+1);
			}			
		}
		return childsCount;
	}
	

	@Override
	public void setChildsCount(byte[] cc) {
		childsCount = cc;
	}
	
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
//			DynamicalHierarchicalNode vertex = (DynamicalHierarchicalNode) it.next();
//			matcher.reset(vertex.statesToString(this.getNodeOrderSize()));
//			if (matcher.find()) {
//				v.add(vertex);
//			}
//		}
//		return v;
//	}
	
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
	 * @param list the list of nodeInfo representing the order of node as defined by the model
	 */
	@Override
	public void setNodeOrder( List<NodeInfo> node_order){
		
		nodeOrder = node_order;
	}


	private DynamicalHierarchicalNode getNodeForState(byte[] state) {
		for (Iterator it = this.getNodes().iterator(); it.hasNext();) {
			DynamicalHierarchicalNode v = (DynamicalHierarchicalNode) it.next();
			if (v.contains(state)) return v;
		}
		return null;
	}
	
	/**
	 * 
	 * @param sid a string representation of the id with a letter in first position eg. "s102"
	 * @return the node with the corresponding id. eg. 102.
	 */
	@Override
	public DynamicalHierarchicalNode getNodeById(String sid) {
		int id = Integer.parseInt(sid.substring(1));
		for (Iterator it = this.getNodes().iterator(); it.hasNext();) {
			DynamicalHierarchicalNode v = (DynamicalHierarchicalNode) it.next();
			if (v.getUniqueId() == id) return v;
		}
		return null;
	}

	@Override
	protected List<?> doMerge(
			Graph<DynamicalHierarchicalNode, Edge<DynamicalHierarchicalNode>> graph) {
		// not implemented for this type of graph
		return null;
	}

	@Override
	public Graph<DynamicalHierarchicalNode, Edge<DynamicalHierarchicalNode>> getSubgraph(
			Collection<DynamicalHierarchicalNode> vertex,
			Collection<Edge<DynamicalHierarchicalNode>> edges) {
		// not implemented for this type of graph
		return null;
	}
}