package org.ginsim.graph.regulatorygraph;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.filechooser.FileFilter;

import org.ginsim.annotation.BiblioManager;
import org.ginsim.exception.GsException;
import org.ginsim.exception.NotificationMessage;
import org.ginsim.exception.NotificationMessageAction;
import org.ginsim.exception.NotificationMessageHolder;
import org.ginsim.graph.common.AbstractGraph;
import org.ginsim.graph.common.Edge;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.common.EdgeAttributesReader;
import org.ginsim.graph.common.VertexAttributesReader;
import org.ginsim.graph.dynamicgraph.DynamicGraph;
import org.ginsim.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.graph.regulatorygraph.omdd.OMDDNode;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.regulatorygraph.GsMutantListManager;

import fr.univmrs.tagc.GINsim.gui.GsFileFilter;
import fr.univmrs.tagc.GINsim.xml.GsGinmlHelper;
import fr.univmrs.tagc.common.managerresources.Translator;
import fr.univmrs.tagc.common.xml.XMLWriter;

/**
 * The regulatory graph
 */
public final class RegulatoryGraph extends AbstractGraph<RegulatoryVertex, RegulatoryMultiEdge> {

	public static final String GRAPH_ZIP_NAME = "regulatoryGraph.ginml";
	
	private int nextid=0;

	private List<RegulatoryVertex> nodeOrder = new ArrayList<RegulatoryVertex>();

    private static Graph copiedGraph = null;

    static {
    	ObjectAssociationManager.getInstance().registerObjectManager( RegulatoryGraph.class, new GsMutantListManager());
    	ObjectAssociationManager.getInstance().registerObjectManager( RegulatoryGraph.class,  new BiblioManager());
    }

    /**
     */
    public RegulatoryGraph() {
    	
        this( false);
    }

    
    /**
     * Return the node order
     * 
     * @return the node order as a list of RegulatoryVertex
     */
    public List<RegulatoryVertex> getNodeOrder() {
    	
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
	 * Set a list of class dependent objects representing the order of vertex as defined by the model
	 * 
	 * @param list the list of objects representing the order of vertex as defined by the model
	 */
	public void setNodeOrder( List<RegulatoryVertex> list){
		
		nodeOrder = list;
	}
	
    
    /**
     * @param parsing
     */
    public RegulatoryGraph( boolean parsing) {
    	
        super( parsing);
    	// getVertexAttributeReader().setDefaultVertexSize(55, 25);
    	// getEdgeAttributeReader().setDefaultEdgeSize(2);
    }
    
    /**
     * @param map
     * @param file
     */
    public RegulatoryGraph(Map map, File file) {
    	
        this( true);
        RegulatoryParser parser = new RegulatoryParser();
        parser.parse(file, map, this);
    }

    public RegulatoryVertex addVertex() {

        while ( getVertexByName("G" + nextid) != null) {
        		nextid++;
        }
        RegulatoryVertex obj = new RegulatoryVertex(nextid++, this);
        if (addVertex( obj)) {
    		nodeOrder.add(obj);
    		return obj;
        }
        return null;
    }

    /**
     * Extracted from the method below, not sure it is really required..
     */
    class MEdgeNotificationAction implements NotificationMessageAction {
    	
		final String[] t_action = {"go"};
		final Graph<?, ?> graph;
		public MEdgeNotificationAction(RegulatoryGraph graph) {
			this.graph = graph;
		}
		public boolean timeout( NotificationMessageHolder holder, Object data) {
			return true;
		}
		public boolean perform( NotificationMessageHolder holder, Object data, int index) {
			GUIManager.getInstance().getGraphGUI(graph).selectEdge((Edge<?>)data);
			return true;
		}
		public String[] getActionName() {
			return t_action;
		}

    }
    
    /**
     * Add a signed edge
     * 
     * @param source
     * @param target
     * @param sign
     * @return
     */
    public RegulatoryMultiEdge addEdge(RegulatoryVertex source, RegulatoryVertex target, int sign) {
    	RegulatoryMultiEdge obj = getEdge(source, target);
    	if (obj != null) {
    		NotificationMessageAction action = new MEdgeNotificationAction(this);
	    	this.addNotificationMessage( new NotificationMessage(this,
	    			Translator.getString("STR_usePanelToAddMoreEdges"),
	    			action,
	    			obj,
	    			NotificationMessage.NOTIFICATION_WARNING));
    		return obj;
    	}
    	if (sign < 0) {
    		sign = RegulatoryMultiEdge.SIGN_NEGATIVE;
    	} else if (sign > 0) {
    		sign = RegulatoryMultiEdge.SIGN_POSITIVE;
    	} else {
    		sign = RegulatoryMultiEdge.SIGN_UNKNOWN;
    	}
    	obj = new RegulatoryMultiEdge(source, target, sign);
    	addEdge(obj);
    	obj.rescanSign( this);
    	target.incomingEdgeAdded(obj);
    	return obj;
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
    protected void doSave( OutputStreamWriter os, Collection<RegulatoryVertex> vertices, Collection<RegulatoryMultiEdge> edges, int mode) throws GsException {
    	try {
            XMLWriter out = new XMLWriter(os, GsGinmlHelper.DEFAULT_URL_DTD_FILE);
	  		out.write("<gxl xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n");
			out.write("\t<graph id=\"" + getGraphName() + "\"");
			out.write(" class=\"regulatory\"");
			out.write(" nodeorder=\"" + stringNodeOrder() +"\"");
			out.write(">\n");
			saveNode(out, mode, vertices);
			saveEdge(out, mode, edges);
            if (graphAnnotation != null) {
            	graphAnnotation.toXML(out, null, 0);
            }
	  		out.write("\t</graph>\n");
	  		out.write("</gxl>\n");
        } catch (IOException e) {
            throw new GsException(GsException.GRAVITY_ERROR, Translator.getString("STR_unableToSave")+": "+ e.getMessage());
        }
    }

    /**
     * @param out
     * @param mode
     * @param selectedOnly
     * @throws IOException
     */
    private void saveEdge(XMLWriter out, int mode, Collection<RegulatoryMultiEdge> edges) throws IOException {
    	
        Iterator<RegulatoryMultiEdge> it = edges.iterator();

        switch (mode) {
	    	case 2:
	    	    EdgeAttributesReader ereader = getEdgeAttributeReader();
		        while (it.hasNext()) {
		        	RegulatoryMultiEdge edge = it.next();
		            ereader.setEdge(edge);
		            edge.toXML(out, GsGinmlHelper.getEdgeVS(ereader), mode);
		        }
		        break;
        	default:
		        while (it.hasNext()) {
		        	RegulatoryMultiEdge edge = it.next();
		            edge.toXML(out, null, mode);
		        }
        }
    }

    /**
     * @param out
     * @param mode
     * @param selectedOnly
     * @throws IOException
     */
    private void saveNode(XMLWriter out, int mode, Collection<RegulatoryVertex> vertices) throws IOException {
    	
    	Iterator<RegulatoryVertex> it = vertices.iterator();
    	
    	if ( mode >=0) {
    	}

		VertexAttributesReader vReader = getVertexAttributeReader(); 
    	
    	switch (mode) {
    		case 1:
    	        while (it.hasNext()) {
    	            Object vertex = it.next();
    	            String svs = "";
	                vReader.setVertex(vertex);
	                svs = GsGinmlHelper.getShortNodeVS( vReader);
    	            ((RegulatoryVertex)vertex).toXML(out, svs, mode);
    	        }
    			break;
    		case 2:
    	        while (it.hasNext()) {
    	            Object vertex = it.next();
    	            String svs = "";
	                vReader.setVertex(vertex);
	                svs = GsGinmlHelper.getFullNodeVS(vReader);
    	            ((RegulatoryVertex)vertex).toXML(out, svs, mode);
    	        }
    			break;
    		default:
    	        while (it.hasNext()) {
    	            Object vertex = it.next();
    	            ((RegulatoryVertex)vertex).toXML(out, "", mode);
    	        }
        }
    }

//    /**
//     * @param edge
//     */
//    public void addToExistingEdge( RegulatoryMultiEdge edge) {
//        edge.addEdge(this);
//    }
//
//    /**
//     * @param edge
//     * @param param
//     */
//    public void addToExistingEdge(RegulatoryMultiEdge edge, int param) {
//        edge.addEdge(this);
//    }

    public boolean idExists(String newId) {
        Iterator it = getVertices().iterator();
        while (it.hasNext()) {
            if (newId.equals(((RegulatoryVertex)it.next()).getId())) {
                return true;
            }
        }
        return false;
    }
    public void changeVertexId(Object vertex, String newId) throws GsException {
        RegulatoryVertex rvertex = (RegulatoryVertex)vertex;
        if (newId.equals(rvertex.getId())) {
            return;
        }
        if (idExists(newId)) {
        	throw  new GsException(GsException.GRAVITY_ERROR, "id already exists");
        }
        if (!rvertex.setId(newId)) {
        	throw  new GsException(GsException.GRAVITY_ERROR, "invalid id");
        }
        fireMetaChange();
    }

    /**
     * @param multiEdge
     * @param index
     * @throws GsException
     */
    public void removeEdgeFromMultiEdge(RegulatoryMultiEdge multiEdge, int index) throws GsException {
        if (index >= multiEdge.getEdgeCount()) {
            throw new GsException(GsException.GRAVITY_ERROR, "STR_noSuchSubedge");
        }
        if (multiEdge.getEdgeCount() == 1) {
            removeEdge(multiEdge);
        } else {
			multiEdge.removeEdge(index, this);
		}
    }
    /**
     *
     * @param obj
     */
    public boolean removeEdge(RegulatoryMultiEdge edge) {
       edge.markRemoved();
       super.removeEdge(edge);
       edge.getTarget().removeEdgeFromInteraction(edge);
       fireGraphChange(CHANGE_EDGEREMOVED, edge);
       return true;
    }

    public boolean removeVertex(RegulatoryVertex obj) {
        for (RegulatoryMultiEdge me: getOutgoingEdges(obj)) {
            removeEdge(me);
        }
        super.removeVertex( obj);
        nodeOrder.remove(obj);
        fireGraphChange(CHANGE_VERTEXREMOVED, obj);
        return true;
    }

    /**
     * add a vertex from textual parameters (for the parser).
     *
     * @param id
     * @param name
     * @param max
     * @return the new vertex.
     */
    public RegulatoryVertex addNewVertex(String id, String name, byte max) {
        RegulatoryVertex vertex = new RegulatoryVertex(id, this);
        if (name != null) {
            vertex.setName(name);
        }
        vertex.setMaxValue(max, this);
        if (addVertex(vertex)) {
        	nodeOrder.add(vertex);
        }
        return vertex;
    }

    /**
     * add an edge from textual parameters (for the parser).
     * @param from
     * @param to
     * @param minvalue
     * @param maxvalue
     * @param sign
     * @return the new edge
     */
    public RegulatoryEdge addNewEdge(String from, String to, byte minvalue, String sign)  throws GsException{
    	byte vsign = RegulatoryMultiEdge.SIGN_UNKNOWN;
    	for (byte i=0 ; i<RegulatoryMultiEdge.SIGN.length ; i++) {
    		if (RegulatoryMultiEdge.SIGN[i].equals(sign)) {
    			vsign = i;
    			break;
    		}
    	}
    	return addNewEdge(from, to, minvalue, vsign);
    }
    
    /**
     * add an edge from textual parameters (for the parser).
     * @param from
     * @param to
     * @param minvalue
     * @param maxvalue
     * @param sign
     * @return the new edge.
     */
    public RegulatoryEdge addNewEdge(String from, String to, byte minvalue, byte sign) throws GsException {
        RegulatoryVertex source = null;
        RegulatoryVertex target = null;

        source = (RegulatoryVertex) getVertexByName(from);
        if (from.equals(to)) {
            target = source;
        } else {
            target = (RegulatoryVertex) getVertexByName(to);
        }

        if (source == null || target == null) {
            throw new GsException( GsException.GRAVITY_ERROR, "STR_noSuchVertex");
        }
        RegulatoryMultiEdge me = getEdge(source, target);
        int index = 0;
        if (me == null) {
            me = new RegulatoryMultiEdge(source, target, sign, minvalue);
            addEdge(me);
        } else {
            index = me.addEdge(sign, minvalue, this);
        }
        return me.getEdge(index);
    }

	/**
	 * @param vertex
     * @return a warning string if necessary
	 */
	public void canApplyNewMaxValue(RegulatoryVertex vertex, byte newMax, List l_fixable, List l_conflict) {
		for (RegulatoryMultiEdge me: getOutgoingEdges(vertex)) {
			me.canApplyNewMaxValue(newMax, l_fixable, l_conflict);
		}
	}

	protected FileFilter doGetFileFilter() {
		GsFileFilter ffilter = new GsFileFilter();
		ffilter.setExtensionList(new String[] {"zginml", "ginml"}, "(z)ginml files");
		return ffilter;
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

    protected Graph getCopiedGraph() {
    	
        return copiedGraph;
    }

    protected List doMerge( Graph<RegulatoryVertex, RegulatoryMultiEdge> otherGraph) {
        if (!(otherGraph instanceof RegulatoryGraph)) {
            return null;
        }
        List ret = new ArrayList();
        HashMap copyMap = new HashMap();
        Iterator<RegulatoryVertex> it = otherGraph.getVertices().iterator();
        VertexAttributesReader vReader = getVertexAttributeReader();
        VertexAttributesReader cvreader = otherGraph.getVertexAttributeReader();
        while (it.hasNext()) {
            RegulatoryVertex vertexOri = (RegulatoryVertex)it.next();
            RegulatoryVertex vertex = (RegulatoryVertex)vertexOri.clone();
            addVertexWithNewId(vertex);
            cvreader.setVertex(vertexOri);
            vReader.setVertex(vertex);
            vReader.copyFrom(cvreader);
            vReader.refresh();
            copyMap.put(vertexOri, vertex);
            ret.add(vertex);
        }

        Iterator<RegulatoryMultiEdge> it2 = otherGraph.getEdges().iterator();
        EdgeAttributesReader eReader = getEdgeAttributeReader();
        EdgeAttributesReader cereader = otherGraph.getEdgeAttributeReader();
        while (it2.hasNext()) {
        	RegulatoryMultiEdge deOri = it2.next();
        	RegulatoryMultiEdge edge = addEdge((RegulatoryVertex)copyMap.get(deOri.getSource()), (RegulatoryVertex)copyMap.get(deOri.getTarget()), 0);
            edge.copyFrom(deOri);
            cereader.setEdge(deOri);
            eReader.setEdge(edge);
            eReader.copyFrom(cereader);
            eReader.refresh();
            copyMap.put(deOri, edge);
            ret.add(edge);
        }

        it = otherGraph.getVertices().iterator();
        while (it.hasNext()) {
            it.next().cleanupInteractionForNewGraph(copyMap);
        }
        return ret;
    }
    /**
     * @param vertex
     */
    private void addVertexWithNewId(RegulatoryVertex vertex) {
        String id = vertex.getId();
        if (getVertexByName(id) == null) {
            addVertex(vertex);
            nodeOrder.add(vertex);
            return;
        }
        int addon = 1;
        while ( getVertexByName(id+"_"+addon) != null) {
            addon++;
        }
        vertex.setId(id+"_"+addon);
        addVertex(vertex);
        nodeOrder.add(vertex);
    }

    @Override
    public Graph getSubgraph(Collection<RegulatoryVertex> v_vertex, Collection<RegulatoryMultiEdge> v_edges) {
    	
        RegulatoryGraph copiedGraph = new RegulatoryGraph();
        VertexAttributesReader vReader = getVertexAttributeReader();
        VertexAttributesReader cvreader = copiedGraph.getVertexAttributeReader();
        HashMap copyMap = new HashMap();
        if (v_vertex != null) {
            for (RegulatoryVertex vertexOri: v_vertex) {
                RegulatoryVertex vertex = (RegulatoryVertex)vertexOri.clone();
                copiedGraph.addVertexWithNewId(vertex);
                vReader.setVertex(vertexOri);
                cvreader.setVertex(vertex);
                cvreader.copyFrom(vReader);
                copyMap.put( vertexOri, vertex);
            }
        }

        if (v_edges != null) {
        	EdgeAttributesReader eReader = getEdgeAttributeReader();
            EdgeAttributesReader cereader = copiedGraph.getEdgeAttributeReader();
	        for (RegulatoryMultiEdge edgeOri: v_edges) {
	        	RegulatoryMultiEdge edge = copiedGraph.addEdge((RegulatoryVertex)copyMap.get(edgeOri.getSource()), (RegulatoryVertex)copyMap.get(edgeOri.getTarget()), 0);
	            edge.copyFrom(edgeOri);
	            copyMap.put(edgeOri, edge);
                eReader.setEdge(edgeOri);
                cereader.setEdge(edge);
                cereader.copyFrom(eReader);
	        }
        }

        if (v_vertex != null) {
            for (RegulatoryVertex v: v_vertex) {
                v.cleanupInteractionForNewGraph(copyMap);
            }
        }

        RegulatoryGraph.copiedGraph = copiedGraph;
        return copiedGraph;
    }

    protected void setCopiedGraph( Graph graph) {
        if (graph != null && graph instanceof RegulatoryGraph) {
            copiedGraph = graph;
        } else {
            copiedGraph = null;
        }
    }

    /**
     * Test if an association between a regulatory graph and a state transition graph is valid:
     * all what we can do is checking the node-order to see if they obviously differ (by size of node's name).
     *
     * if this function returns true, it's _NOT_ a garanty that both graphs are compatibles,
     * for exemple the state transition graph could have higher max value for one of the node.
     *
     * @param regGraph
     * @param dynHieGraph
     * @return true if the two graph can be associated
     */
    public static boolean associationValid(RegulatoryGraph regGraph, DynamicGraph dynGraph) {
        if (regGraph == null || dynGraph == null) {
            return false;
        }

        List regOrder = regGraph.nodeOrder;
        List dynOrder = dynGraph.getNodeOrder();
        if (regOrder == null || dynOrder == null || regOrder.size() != dynOrder.size()) {
            return false;
        }

        for (int i=0 ; i<regOrder.size() ; i++) {
            if (!regOrder.get(i).toString().equals(dynOrder.get(i).toString())) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param focal if true, leaves are focal points. Otherwise their are directions (-1, 0, +1)
     * @return a tree representation of logical parameters
     */
    public OMDDNode[] getAllTrees(boolean focal) {
        OMDDNode[] t_tree = new OMDDNode[nodeOrder.size()];
        for (int i=0 ; i<nodeOrder.size() ; i++) {
        	RegulatoryVertex vertex = (RegulatoryVertex)nodeOrder.get(i);
            t_tree[i] = vertex.getTreeParameters(nodeOrder);
            if (!focal) {
            	// FIXME: does non-focal tree works correctly ??????
            	t_tree[i] = t_tree[i].buildNonFocalTree(i, vertex.getMaxValue()+1).reduce();
            } else {
            	t_tree[i] = t_tree[i].reduce();
            }
        }
        return t_tree;
    }

	public List getNodeOrderForSimulation() {
		
		return getNodeOrder();
	}
	
	public OMDDNode[] getParametersForSimulation(boolean focal) {
		
		return getAllTrees(focal);
	}


}
