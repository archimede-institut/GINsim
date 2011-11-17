package org.ginsim.graph.dynamicalhierarchicalgraph;

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

import org.ginsim.exception.GsException;
import org.ginsim.graph.GraphManager;
import org.ginsim.graph.common.AbstractAssociatedGraphFrontend;
import org.ginsim.graph.common.Edge;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.common.VertexAttributesReader;
import org.ginsim.graph.regulatorygraph.GsRegulatoryGraph;
import org.ginsim.graph.regulatorygraph.GsRegulatoryMultiEdge;
import org.ginsim.graph.regulatorygraph.GsRegulatoryVertex;
import org.ginsim.gui.service.tools.dynamicalhierarchicalsimplifier.GsDynamicalHierarchicalParameterPanel;
import org.ginsim.gui.service.tools.dynamicalhierarchicalsimplifier.NodeInfo;
import org.ginsim.gui.shell.editpanel.AbstractParameterPanel;

import fr.univmrs.tagc.GINsim.xml.GsGinmlHelper;
import fr.univmrs.tagc.common.managerresources.Translator;
import fr.univmrs.tagc.common.xml.XMLWriter;

public class GsDynamicalHierarchicalGraph extends AbstractAssociatedGraphFrontend<GsDynamicalHierarchicalNode, Edge<GsDynamicalHierarchicalNode>, GsRegulatoryGraph, GsRegulatoryVertex, GsRegulatoryMultiEdge>{

	public static final String GRAPH_ZIP_NAME = "dynamicalHierarchicalGraph.ginml";
	
	private String dtdFile = GsGinmlHelper.DEFAULT_URL_DTD_FILE;
	
	private byte[] childsCount = null;
	private GsDynamicalHierarchicalParameterPanel vertexPanel = null;
	
	private List<NodeInfo> nodeOrder = new ArrayList<NodeInfo>();

	/**
	 * create a new GsDynamicalHierarchicalGraph with a nodeOrder.
	 * @param nodeOrder the node order
	 */
	public GsDynamicalHierarchicalGraph(List<GsRegulatoryVertex> nodeOrder) {
		
	    this( false);
	    for (GsRegulatoryVertex vertex: nodeOrder) {
	    	this.nodeOrder.add(new NodeInfo(vertex.getId(), vertex.getMaxValue()));
	    }
	}
	
	/**
	 * create a new empty GsDynamicalHierarchicalGraph.
	 */
	public GsDynamicalHierarchicalGraph() {
		
		this( false);
	}
	
	public GsDynamicalHierarchicalGraph( boolean parsing) {
		
        super( parsing);
	}


	public GsDynamicalHierarchicalGraph(Map map, File file) {
		
	    this( true);
        GsDynamicalHierarchicalParser parser = new GsDynamicalHierarchicalParser();
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
    	
    	if( graph instanceof GsRegulatoryGraph){
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
    
	protected void doSave(OutputStreamWriter os, Collection<GsDynamicalHierarchicalNode> vertices, Collection<Edge<GsDynamicalHierarchicalNode>> edges, int mode) throws GsException {
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
	
    private void saveEdge(XMLWriter out, int mode, Collection<Edge<GsDynamicalHierarchicalNode>> edges) throws IOException {
    	
        for (Edge edge: edges) {
            String source = ""+((GsDynamicalHierarchicalNode)edge.getSource()).getUniqueId();
            String target =""+((GsDynamicalHierarchicalNode) edge.getTarget()).getUniqueId();
            out.write("\t\t<edge id=\"e"+ source +"_"+target+"\" from=\"s"+source+"\" to=\"s"+target+"\"/>\n");
        }
    }
    
    /**
     * @param out
     * @param mode
     * @param selectedOnly
     * @throws IOException
     */
    private void saveNode(XMLWriter out, int mode, Collection<GsDynamicalHierarchicalNode> vertices) throws IOException {
    	
    	Iterator<GsDynamicalHierarchicalNode> it = vertices.iterator();

    	VertexAttributesReader vReader = getVertexAttributeReader();
        while (it.hasNext()) {
        	GsDynamicalHierarchicalNode vertex = (GsDynamicalHierarchicalNode)it.next();
            vReader.setVertex(vertex);
            out.write("\t\t<node id=\"s"+vertex.getUniqueId()+"\">\n");
            out.write("<attr name=\"type\"><string>"+vertex.typeToString()+"</string></attr>");
            out.write("<attr name=\"states\"><string>"+vertex.write().toString()+"</string></attr>");
            out.write(GsGinmlHelper.getFullNodeVS(vReader));
            out.write("\t\t</node>\n");
        }
    }
    
	private String stringNodeOrder() {
		String s = "";
		for (NodeInfo v: nodeOrder) {
			s += v.name+":"+v.max+" ";
		}
		if (s.length() > 0) {
			return s.substring(0, s.length()-1);
		}
		return s;
	}

    
    /* edge and vertex panels */
    
	public AbstractParameterPanel getEdgeAttributePanel() {
		return null;
	}

	public AbstractParameterPanel getVertexAttributePanel() {
	    if (vertexPanel == null) {
	        vertexPanel  = new GsDynamicalHierarchicalParameterPanel(this);
	    }
		return vertexPanel;
	}


	
	/* adding edge and vertex */
  
//	/**
//	 * add a vertex to this graph.
//	 * @param vertex
//	 */
	// TODO to remove since it override an existing method on AbstractGraphFrontend doing the same thing
//	public boolean addVertex(GsDynamicalHierarchicalNode vertex) {
//		return graphManager.addVertex(vertex);
//	}
	/**
	 * add an edge between source and target
	 * @param source
	 * @param target
	 * @return the new edge
	 */
	public Edge<GsDynamicalHierarchicalNode> addEdge(GsDynamicalHierarchicalNode source, GsDynamicalHierarchicalNode target) {
		
		Edge<GsDynamicalHierarchicalNode> edge = new Edge<GsDynamicalHierarchicalNode>(source, target);
		if (addEdge(edge)) {
			return edge;
		}
		return null;
	}


	
	/* something else */
	
	/**
	 * return an array indicating for each node in the nodeOrder their count of childs. (ie. their max value)
	 */
	public byte[] getChildsCount() {
		if (childsCount == null) {
			childsCount = new byte[nodeOrder.size()];
			int i = 0;
			for (Iterator it = nodeOrder.iterator(); it.hasNext();) {
				GsRegulatoryVertex v = (GsRegulatoryVertex) it.next();
				childsCount[i++] = (byte) (v.getMaxValue()+1);
			}			
		}
		return childsCount;
	}
	
	public void setChildsCount(byte[] cc) {
		childsCount = cc;
	}
	
	
	public Vector searchNodes(String regexp) {
		Vector v = new Vector();
		
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < regexp.length(); i++) {
			char c = regexp.charAt(i);
			if (c == '\\') {
				s.append(regexp.charAt(++i));
			} else if (c == '*') {
				s.append("[0-9\\*]");
			} else if (c == '0' || (c >= '1' && c <= '9')){
				s.append("["+c+"\\*]");
			} else if (c == ' ' || c == '\t') {
				//pass
			} else {
				s.append(c);
			}
		}
		Pattern pattern = Pattern.compile(s.toString(), Pattern.COMMENTS | Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher("");
		
		for (Iterator it = this.getVertices().iterator(); it.hasNext();) {
			GsDynamicalHierarchicalNode vertex = (GsDynamicalHierarchicalNode) it.next();
			matcher.reset(vertex.statesToString(this.getNodeOrderSize()));
			if (matcher.find()) {
				v.add(vertex);
			}
		}
		return v;
	}
	
	/**
	 * Return the node order as a List of NodeInfo
	 * 
	 * @return the node order as a List of NodeInfo
	 */
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
	 * Set a list of NodeInfo representing the order of vertex as defined by the model
	 * 
	 * @param list the list of nodeInfo representing the order of vertex as defined by the model
	 */
	public void setNodeOrder( List<NodeInfo> node_order){
		
		nodeOrder = node_order;
	}

	public GsDynamicalHierarchicalNode getNodeForState(byte[] state) {
		for (Iterator it = this.getVertices().iterator(); it.hasNext();) {
			GsDynamicalHierarchicalNode v = (GsDynamicalHierarchicalNode) it.next();
			if (v.contains(state)) return v;
		}
		return null;
	}
	
	/**
	 * 
	 * @param sid a string representation of the id with a letter in first position eg. "s102"
	 * @return the node with the corresponding id. eg. 102.
	 */
	public GsDynamicalHierarchicalNode getNodeById(String sid) {
		int id = Integer.parseInt(sid.substring(1));
		for (Iterator it = this.getVertices().iterator(); it.hasNext();) {
			GsDynamicalHierarchicalNode v = (GsDynamicalHierarchicalNode) it.next();
			if (v.getUniqueId() == id) return v;
		}
		return null;
	}

	@Override
	protected List<?> doMerge(
			Graph<GsDynamicalHierarchicalNode, Edge<GsDynamicalHierarchicalNode>> graph) {
		// not implemented for this type of graph
		return null;
	}

	@Override
	public Graph<GsDynamicalHierarchicalNode, Edge<GsDynamicalHierarchicalNode>> getSubgraph(
			Collection<GsDynamicalHierarchicalNode> vertex,
			Collection<Edge<GsDynamicalHierarchicalNode>> edges) {
		// not implemented for this type of graph
		return null;
	}
}
