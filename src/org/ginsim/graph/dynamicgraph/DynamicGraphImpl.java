package org.ginsim.graph.dynamicgraph;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ginsim.exception.GsException;
import org.ginsim.graph.GraphManager;
import org.ginsim.graph.common.AbstractDerivedGraph;
import org.ginsim.graph.common.Edge;
import org.ginsim.graph.common.EdgeAttributesReader;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.common.NodeAttributesReader;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.graph.regulatorygraph.RegulatoryGraphImpl;
import org.ginsim.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.gui.graph.regulatorygraph.RegulatoryGraphOptionPanel;
import org.ginsim.gui.graph.regulatorygraph.RegulatoryGraphEditor;
import org.ginsim.gui.service.tool.dynamicalhierarchicalsimplifier.NodeInfo;
import org.ginsim.io.parser.GinmlHelper;

import fr.univmrs.tagc.common.datastore.ObjectEditor;
import fr.univmrs.tagc.common.managerresources.Translator;
import fr.univmrs.tagc.common.xml.XMLWriter;

public final class DynamicGraphImpl extends AbstractDerivedGraph<DynamicNode, Edge<DynamicNode>, RegulatoryGraph, RegulatoryNode, RegulatoryMultiEdge> implements DynamicGraph{


	public static final String GRAPH_ZIP_NAME = "stateTransitionGraph.ginml";
	
	private String dtdFile = GinmlHelper.DEFAULT_URL_DTD_FILE;
	private RegulatoryGraphOptionPanel optionPanel;

	protected List v_stables = null;
    private ObjectEditor graphEditor = null;
    private float[] dashpattern = null;

    private List<NodeInfo> nodeOrder;
    
	/**
	 */
	public DynamicGraphImpl() {
		
	    this( false);

	}
    
	/**
	 * create a new DynamicGraph.
	 * @param regGraph
	 */
	public DynamicGraphImpl(List<?> nodeOrder) {
		
	    this( false);
	    this.nodeOrder = new ArrayList<NodeInfo>();
	    for (Object node: nodeOrder) {
	    	NodeInfo node_info = new NodeInfo( node.toString());
	    	this.nodeOrder.add( node_info);
	    }
	}
	
	/**
	 * @param filename
	 */
	public DynamicGraphImpl( boolean parsing) {
		
        super( parsing);
        dashpattern = getEdgeAttributeReader().getPattern(1);
	}

	/**
	 * @param map
	 * @param file
	 */
	public DynamicGraphImpl(Map map, File file) {
		
	    this( true);
        DynamicParser parser = new DynamicParser();
        parser.parse(file, map, this);
	}
	
	/**
	 * Return the node order as a list of String
	 * 
	 * @return the node order as a list of String
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
	 * Set a list of String representing the order of vertex as defined by the model
	 * 
	 * @param list the list of String representing the order of vertex as defined by the model
	 */
	@Override
	public void setNodeOrder( List<NodeInfo> node_order){
		
		nodeOrder = node_order;
	}
    
	/**
	 * Return the zip extension for the graph type
	 * 
	 * @return the zip extension for the graph type
	 */
	@Override
	protected String getGraphZipName(){
		
		return GRAPH_ZIP_NAME;
		
	}

	@Override
	protected void doSave(OutputStreamWriter os, Collection<DynamicNode> nodes, Collection<Edge<DynamicNode>> edges, int mode) throws GsException {
        try {
            XMLWriter out = new XMLWriter(os, dtdFile);
	  		out.write("<gxl xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n");
			out.write("\t<graph id=\"" + graphName + "\"");
			out.write(" class=\"dynamical\"");
			out.write(" nodeorder=\"" + stringNodeOrder() +"\"");
			out.write(">\n");
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
            throw new GsException(GsException.GRAVITY_ERROR, Translator.getString("STR_unableToSave")+": "+ e.getMessage());
        }
	}

	
	
	private String stringNodeOrder() {
		String s = "";
		for (int i=0 ; i<nodeOrder.size() ; i++) {
			s += nodeOrder.get(i)+" ";
		}
		if (s.length() > 0) {
			return s.substring(0, s.length()-1);
		}
		return s;
	}

    /**
     * @param out
     * @param mode
     * @param selectedOnly
     * @throws IOException
     */
    private void saveEdge(XMLWriter out, int mode, Collection<Edge<DynamicNode>> edges) throws IOException {
        if (edges == null) {
        	edges = getEdges();
        }

        EdgeAttributesReader eReader = getEdgeAttributeReader();
        
        switch (mode) {
        	case 2:
		        for (Edge<DynamicNode> edge: edges) {
	        	    eReader.setEdge(edge);
		            String source = edge.getSource().toString();
		            String target = edge.getTarget().toString();
		            out.write("\t\t<edge id=\"s"+ source +"_s"+target+"\" from=\"s"+source+"\" to=\"s"+target+"\">\n");
		            out.write(GinmlHelper.getEdgeVS(eReader));
		            out.write("</edge>");
		        }
        	    break;
	    	default:
		        for (Edge<DynamicNode> edge: edges) {
	        	    eReader.setEdge(edge);
		            String source = edge.getSource().toString();
		            String target = edge.getTarget().toString();
		            out.write("\t\t<edge id=\"s"+ source +"_s"+target+"\" from=\"s"+source+"\" to=\"s"+target+"\"/>\n");
		        }
		        break;
        }
    }

    /**
     * @param out
     * @param mode
     * @param selectedOnly
     * @throws IOException
     */
    private void saveNode(XMLWriter out, int mode, Collection<DynamicNode> nodes) throws IOException {
    	if (nodes == null) {
    		nodes = getVertices();
    	}
    	
    	NodeAttributesReader vReader = getVertexAttributeReader();
    	
        	switch (mode) {
	    		case 1:
	                for (DynamicNode node: nodes) {
	                    vReader.setVertex(node);
	                    String svs = GinmlHelper.getShortNodeVS(vReader);
	                    out.write("\t\t<node id=\""+node.getId()+"\">\n");
	                    out.write(svs);
	                    out.write("\t\t</node>\n");
	                }
	    			break;
				case 2:
	                for (DynamicNode node: nodes) {
	                    vReader.setVertex(node);
	                    String svs = GinmlHelper.getFullNodeVS(vReader);
	                    out.write("\t\t<node id=\""+node.getId()+"\">\n");
	                    out.write(svs);
	                    out.write("\t\t</node>\n");
	                }
	    			break;
        		default:
	                for (DynamicNode node: nodes) {
        	            out.write("\t\t<node id=\""+node.getId()+"\"/>\n");
        	        }
        }
    }

	@Override
	public boolean removeEdge(Edge<DynamicNode> obj) {
		return false;
	}

	/**
	 * add a vertex to this graph.
	 * @param state the state we want to add
	 * @return the new DynamicNode.
	 */
	private boolean addVertex( byte[] state) {
		return addVertex( new DynamicNode(state));
	}
	
	/**
	 * add an edge between source and target
	 * @param source
	 * @param target
	 * @param multiple
	 * @return the new edge
	 */
	@Override
	public Edge<DynamicNode> addEdge(DynamicNode source, DynamicNode target, boolean multiple) {
		
		Edge<DynamicNode> edge = new Edge<DynamicNode>(source, target);
		if (!addEdge(edge)) {
			return null;
		}
		if (multiple) {
			EdgeAttributesReader eReader = getEdgeAttributeReader();
			eReader.setEdge(edge);
			eReader.setDash(dashpattern);
		}
		return edge;
	}

	
	@Override
    protected List doMerge( Graph otherGraph) {

        // first check if this merge is allowed!
        if (!(otherGraph instanceof DynamicGraph)) {
            return null;
        }
        List v_order = ((DynamicGraph)otherGraph).getNodeOrder();
        if (v_order.size() != nodeOrder.size()) {
            return null;
        }
        for (int i=0 ; i<nodeOrder.size() ; i++) {
            if (!nodeOrder.get(i).toString().equals(v_order.get(i).toString())) {
                return null;
            }
        }

        List ret = new ArrayList();
        Iterator it = otherGraph.getVertices().iterator();
        NodeAttributesReader vReader = getVertexAttributeReader();
        NodeAttributesReader cvreader = otherGraph.getVertexAttributeReader();
        while (it.hasNext()) {
            DynamicNode vertex = (DynamicNode)it.next();
            addVertex(vertex);
            cvreader.setVertex(vertex);
            vReader.setVertex(vertex);
            vReader.copyFrom(cvreader);
            vReader.refresh();
            ret.add(vertex);
        }

        for (Edge edge: (Collection<Edge>)otherGraph.getEdges()) {
            DynamicNode from = (DynamicNode)edge.getSource();
            DynamicNode to = (DynamicNode)edge.getTarget();
            int c = 0;
            for ( int i=0 ; i<from.state.length ; i++) {
            	if (from.state[i] != to.state[i]) {
            		c++;
            	}
            }
            ret.add(addEdge(from, to, c>1));
        }

        return ret;
    }
    
	@Override
    public Graph getSubgraph(Collection vertex, Collection edges) {
        // no copy for state transition graphs
        return null;
    }


    public ObjectEditor getGraphEditor() {
		if (graphEditor == null) {
			graphEditor = new RegulatoryGraphEditor();
			graphEditor.setEditedItem(this);
		}
		return graphEditor;
    }

    /**
     * look for the shortest path between two states.
     * @param source
     * @param target
     * @return the List describing the path or null if none is found
     */
	@Override
    public List shortestPath(byte[] source, byte[] target) {
    	
        DynamicNode n = new DynamicNode(source);
        DynamicNode n2 = new DynamicNode(target);
        if (containsVertex(n) && containsVertex(n2)) {
            return getShortestPath(n, n2);
        }
        return null;
    }
    /**
     * Indicates if the given graph can be associated to the current one
     * 
     * @param graph the graph to associate to the current one
     * @return true is association is possible, false if not
     */
    @Override
    protected boolean isAssociationValid( Graph<?,?> graph) {
    	
        if (graph == null) {
            return true;
        }
        
        if (!(graph instanceof RegulatoryGraph)) {
            return false;
        }
        return RegulatoryGraphImpl.associationValid((RegulatoryGraph)graph, this);
    }
  

    // FIXME: move all this to a new GraphGUIHelper

//    private GsParameterPanel vertexPanel = null;
//    private GsParameterPanel edgePanel;
//    
//    public Vector getEditingModes() {
//        Vector v_mode = new Vector();
//        v_mode.add(new GsEditModeDescriptor("STR_addEdgePoint", "STR_addEdgePoint_descr", ImageLoader.getImageIcon("custumizeedgerouting.gif"), GsActions.MODE_ADD_EDGE_POINT, 0));
//        return v_mode;
//    }
//	public GsParameterPanel getEdgeAttributePanel() {
//	    if (edgePanel == null) {
//	        edgePanel = new DynamicItemAttributePanel(this);
//	    }
//		return edgePanel;
//	}
//
//	public GsParameterPanel getVertexAttributePanel() {
//	    if (vertexPanel == null) {
//	        vertexPanel = new DynamicItemAttributePanel(this);
//	    }
//		return vertexPanel;
//	}
//
//	protected FileFilter doGetFileFilter() {
//		GsFileFilter ffilter = new GsFileFilter();
//		ffilter.setExtensionList(new String[] {"ginml"}, "ginml files");
//		return ffilter;
//	}
//
//	public String getAutoFileExtension() {
//		return ".ginml";
//	}
//
//	protected JPanel doGetFileChooserPanel() {
//		return getOptionPanel();
//	}
//
//	private JPanel getOptionPanel() {
//		if (optionPanel == null) {
//            Object[] t_mode = { Translator.getString("STR_saveNone"),
//                    Translator.getString("STR_savePosition"),
//                    Translator.getString("STR_saveComplet") };
//            optionPanel = new RegulatoryGraphOptionPanel(t_mode, mainFrame != null ? 2 : 0);
//		}
//		return optionPanel;
//	}
	
}
