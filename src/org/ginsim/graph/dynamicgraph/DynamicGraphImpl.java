package org.ginsim.graph.dynamicgraph;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.ginsim.exception.GsException;
import org.ginsim.graph.GraphManager;
import org.ginsim.graph.common.AbstractAssociatedGraphFrontend;
import org.ginsim.graph.common.Edge;
import org.ginsim.graph.common.EdgeAttributesReader;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.common.VertexAttributesReader;
import org.ginsim.graph.regulatorygraph.GsRegulatoryGraph;
import org.ginsim.graph.regulatorygraph.GsRegulatoryMultiEdge;
import org.ginsim.graph.regulatorygraph.GsRegulatoryVertex;
import org.ginsim.gui.service.tools.dynamicalhierarchicalsimplifier.NodeInfo;
import org.ginsim.gui.service.tools.stablestates.StableTableModel;

import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraphOptionPanel;
import fr.univmrs.tagc.GINsim.regulatoryGraph.RegulatoryGraphEditor;
import fr.univmrs.tagc.GINsim.xml.GsGinmlHelper;
import fr.univmrs.tagc.common.Debugger;
import fr.univmrs.tagc.common.datastore.ObjectEditor;
import fr.univmrs.tagc.common.managerresources.Translator;
import fr.univmrs.tagc.common.widgets.EnhancedJTable;
import fr.univmrs.tagc.common.xml.XMLWriter;

public final class DynamicGraphImpl extends AbstractAssociatedGraphFrontend<GsDynamicNode, Edge<GsDynamicNode>, GsRegulatoryGraph, GsRegulatoryVertex, GsRegulatoryMultiEdge> implements GsDynamicGraph{


	public static final String GRAPH_ZIP_NAME = "stateTransitionGraph.ginml";
	
	private String dtdFile = GsGinmlHelper.DEFAULT_URL_DTD_FILE;
	private GsRegulatoryGraphOptionPanel optionPanel;

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
	 * create a new GsDynamicGraph.
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
        GsDynamicParser parser = new GsDynamicParser();
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
	protected void doSave(OutputStreamWriter os, Collection<GsDynamicNode> nodes, Collection<Edge<GsDynamicNode>> edges, int mode) throws GsException {
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
    private void saveEdge(XMLWriter out, int mode, Collection<Edge<GsDynamicNode>> edges) throws IOException {
        if (edges == null) {
        	edges = getEdges();
        }

        EdgeAttributesReader eReader = getEdgeAttributeReader();
        
        switch (mode) {
        	case 2:
		        for (Edge<GsDynamicNode> edge: edges) {
	        	    eReader.setEdge(edge);
		            String source = edge.getSource().toString();
		            String target = edge.getTarget().toString();
		            out.write("\t\t<edge id=\"s"+ source +"_s"+target+"\" from=\"s"+source+"\" to=\"s"+target+"\">\n");
		            out.write(GsGinmlHelper.getEdgeVS(eReader));
		            out.write("</edge>");
		        }
        	    break;
	    	default:
		        for (Edge<GsDynamicNode> edge: edges) {
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
    private void saveNode(XMLWriter out, int mode, Collection<GsDynamicNode> nodes) throws IOException {
    	if (nodes == null) {
    		nodes = getVertices();
    	}
    	
    	VertexAttributesReader vReader = getVertexAttributeReader();
    	
        	switch (mode) {
	    		case 1:
	                for (GsDynamicNode node: nodes) {
	                    vReader.setVertex(node);
	                    String svs = GsGinmlHelper.getShortNodeVS(vReader);
	                    out.write("\t\t<node id=\""+node.getId()+"\">\n");
	                    out.write(svs);
	                    out.write("\t\t</node>\n");
	                }
	    			break;
				case 2:
	                for (GsDynamicNode node: nodes) {
	                    vReader.setVertex(node);
	                    String svs = GsGinmlHelper.getFullNodeVS(vReader);
	                    out.write("\t\t<node id=\""+node.getId()+"\">\n");
	                    out.write(svs);
	                    out.write("\t\t</node>\n");
	                }
	    			break;
        		default:
	                for (GsDynamicNode node: nodes) {
        	            out.write("\t\t<node id=\""+node.getId()+"\"/>\n");
        	        }
        }
    }

	@Override
	public boolean removeEdge(Edge<GsDynamicNode> obj) {
		return false;
	}

	/**
	 * add a vertex to this graph.
	 * @param state the state we want to add
	 * @return the new GsDynamicNode.
	 */
	private boolean addVertex( byte[] state) {
		return addVertex( new GsDynamicNode(state));
	}
	
	/**
	 * add an edge between source and target
	 * @param source
	 * @param target
	 * @param multiple
	 * @return the new edge
	 */
	@Override
	public Edge<GsDynamicNode> addEdge(GsDynamicNode source, GsDynamicNode target, boolean multiple) {
		
		Edge<GsDynamicNode> edge = new Edge<GsDynamicNode>(source, target);
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
        if (!(otherGraph instanceof GsDynamicGraph)) {
            return null;
        }
        List v_order = ((GsDynamicGraph)otherGraph).getNodeOrder();
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
        VertexAttributesReader vReader = getVertexAttributeReader();
        VertexAttributesReader cvreader = otherGraph.getVertexAttributeReader();
        while (it.hasNext()) {
            GsDynamicNode vertex = (GsDynamicNode)it.next();
            addVertex(vertex);
            cvreader.setVertex(vertex);
            vReader.setVertex(vertex);
            vReader.copyFrom(cvreader);
            vReader.refresh();
            ret.add(vertex);
        }

        for (Edge edge: (Collection<Edge>)otherGraph.getEdges()) {
            GsDynamicNode from = (GsDynamicNode)edge.getSource();
            GsDynamicNode to = (GsDynamicNode)edge.getTarget();
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


    /**
     * browse the graph, looking for stable states
     * @return the list of stable states found
     */
    private List getStableStates() {
        if (v_stables == null) {
            v_stables = new ArrayList();
            Iterator it = getVertices().iterator();
            while (it.hasNext()) {
                GsDynamicNode node = (GsDynamicNode)it.next();
                if (node.isStable()) {
                    v_stables.add(node.state);
                }
            }
        }
        return v_stables;
    }
    
    
    /**
     * override getInfoPanel to show stable states.
     * @return the info panel for the "whattodo" frame
     */
    public JPanel getInfoPanel() {
        JPanel pinfo = new JPanel();
        getStableStates();

        // just display the number of stable states here and a "show more" button
        if (v_stables.size() > 0) {
            pinfo.add(new JLabel("nb stable: "+v_stables.size()));
            JButton b_view = new JButton("view");
            // show all stables: quickly done but, it is "good enough" :)
            b_view.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	try{
                		viewStable();
                	}
                	catch( GsException ge){
                		// TODO : REFACTORING ACTION
                		// TODO : Launch a message box to the user
                		Debugger.log( "Unable to get the stable states" + ge);
                	}
                }
            });
            pinfo.add(b_view);
        } else if (v_stables.size() > 1) {
            pinfo.add(new JLabel("no stable state."));
        }

        return pinfo;
    }

    protected void viewStable() throws GsException{
        JFrame frame = new JFrame(Translator.getString("STR_stableStates"));
        frame.setSize(Math.min(30*(nodeOrder.size()+1), 800),
        		Math.min(25*(v_stables.size()+2), 600));
        JScrollPane scroll = new JScrollPane();
        StableTableModel model = new StableTableModel(nodeOrder);
        model.setResult(v_stables, this);
        scroll.setViewportView(new EnhancedJTable(model));
        frame.setContentPane(scroll);
        frame.setVisible(true);
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
    	
        GsDynamicNode n = new GsDynamicNode(source);
        GsDynamicNode n2 = new GsDynamicNode(target);
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
        
        if (!(graph instanceof GsRegulatoryGraph)) {
            return false;
        }
        return GsRegulatoryGraph.associationValid((GsRegulatoryGraph)graph, this);
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
//	        edgePanel = new GsDynamicItemAttributePanel(this);
//	    }
//		return edgePanel;
//	}
//
//	public GsParameterPanel getVertexAttributePanel() {
//	    if (vertexPanel == null) {
//	        vertexPanel = new GsDynamicItemAttributePanel(this);
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
//            optionPanel = new GsRegulatoryGraphOptionPanel(t_mode, mainFrame != null ? 2 : 0);
//		}
//		return optionPanel;
//	}
	
}
