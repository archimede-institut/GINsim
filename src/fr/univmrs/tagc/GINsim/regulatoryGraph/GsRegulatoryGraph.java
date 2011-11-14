package fr.univmrs.tagc.GINsim.regulatoryGraph;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import org.ginsim.exception.GsException;
import org.ginsim.exception.NotificationMessage;
import org.ginsim.exception.NotificationMessageAction;
import org.ginsim.exception.NotificationMessageHolder;
import org.ginsim.graph.AbstractGraphFrontend;
import org.ginsim.graph.common.Edge;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.dynamicgraph.GsDynamicGraph;
import org.ginsim.graph.regulatoryGraph.RegulatoryGraphFactory;
import org.ginsim.gui.GUIManager;

import fr.univmrs.tagc.GINsim.graph.GsEdgeAttributesReader;
import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.tagc.GINsim.gui.GsFileFilter;
import fr.univmrs.tagc.GINsim.xml.GsGinmlHelper;
import fr.univmrs.tagc.common.datastore.ObjectEditor;
import fr.univmrs.tagc.common.managerresources.ImageLoader;
import fr.univmrs.tagc.common.managerresources.Translator;
import fr.univmrs.tagc.common.xml.XMLWriter;

/**
 * The regulatory graph
 */
public final class GsRegulatoryGraph extends AbstractGraphFrontend<GsRegulatoryVertex, GsRegulatoryMultiEdge> {

	private int nextid=0;

	private List<GsRegulatoryVertex> nodeOrder = new ArrayList<GsRegulatoryVertex>();

    private static Graph copiedGraph = null;
	public final static String zip_mainEntry = "regulatoryGraph.ginml";

    static {
        RegulatoryGraphFactory.registerObjectManager(new GsMutantListManager());
    }

    /**
     */
    public GsRegulatoryGraph() {
    	
        this( false);
    }

    protected String getGraphZipName() {
    	return zip_mainEntry;
    }

    
    /**
     * Return the node order
     * 
     * @return the node order as a list of RegulatoryVertex
     */
    public List<GsRegulatoryVertex> getNodeOrder() {
    	
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
	public void setNodeOrder( List<GsRegulatoryVertex> list){
		
		nodeOrder = list;
	}
	
    
    /**
     * @param savefilename
     */
    public GsRegulatoryGraph( boolean parsing) {
    	
        super( RegulatoryGraphFactory.getInstance(), parsing);
    	// getVertexAttributeReader().setDefaultVertexSize(55, 25);
    	// getEdgeAttributeReader().setDefaultEdgeSize(2);
    }
    /**
     * @param map
     * @param file
     */
    public GsRegulatoryGraph(Map map, File file) {
    	
        this( true);
        GsRegulatoryParser parser = new GsRegulatoryParser();
        parser.parse(file, map, this);
    }

    public GsRegulatoryVertex addVertex() {

        while ( getVertexByName("G" + nextid) != null) {
        		nextid++;
        }
        GsRegulatoryVertex obj = new GsRegulatoryVertex(nextid++, this);
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
		public MEdgeNotificationAction(GsRegulatoryGraph graph) {
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
    public GsRegulatoryMultiEdge addEdge(GsRegulatoryVertex source, GsRegulatoryVertex target, int sign) {
    	GsRegulatoryMultiEdge obj = getEdge(source, target);
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
    		sign = GsRegulatoryMultiEdge.SIGN_NEGATIVE;
    	} else if (sign > 0) {
    		sign = GsRegulatoryMultiEdge.SIGN_POSITIVE;
    	} else {
    		sign = GsRegulatoryMultiEdge.SIGN_UNKNOWN;
    	}
    	obj = new GsRegulatoryMultiEdge(source, target, sign);
    	addEdge(obj);
    	obj.rescanSign( this);
    	target.incomingEdgeAdded(obj);
    	return obj;
    }

    protected void doSave( OutputStreamWriter os, int mode, String dtd_file, List<GsRegulatoryVertex> vertices, List<GsRegulatoryMultiEdge> edges) throws GsException {
    	try {
            XMLWriter out = new XMLWriter(os, dtd_file);
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
    private void saveEdge(XMLWriter out, int mode, List<GsRegulatoryMultiEdge> edges) throws IOException {
    	
        Iterator<GsRegulatoryMultiEdge> it = edges.iterator();

        switch (mode) {
	    	case 2:
	    	    GsEdgeAttributesReader ereader = getEdgeAttributeReader();
		        while (it.hasNext()) {
		        	GsRegulatoryMultiEdge edge = it.next();
		            ereader.setEdge(edge);
		            edge.toXML(out, GsGinmlHelper.getEdgeVS(ereader), mode);
		        }
		        break;
        	default:
		        while (it.hasNext()) {
		        	GsRegulatoryMultiEdge edge = it.next();
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
    private void saveNode(XMLWriter out, int mode, List<GsRegulatoryVertex> vertices) throws IOException {
    	
    	Iterator<GsRegulatoryVertex> it = vertices.iterator();
    	
    	if ( mode >=0) {
    	}

		GsVertexAttributesReader vReader = getVertexAttributeReader(); 
    	
    	switch (mode) {
    		case 1:
    	        while (it.hasNext()) {
    	            Object vertex = it.next();
    	            String svs = "";
	                vReader.setVertex(vertex);
	                svs = GsGinmlHelper.getShortNodeVS( vReader);
    	            ((GsRegulatoryVertex)vertex).toXML(out, svs, mode);
    	        }
    			break;
    		case 2:
    	        while (it.hasNext()) {
    	            Object vertex = it.next();
    	            String svs = "";
	                vReader.setVertex(vertex);
	                svs = GsGinmlHelper.getFullNodeVS(vReader);
    	            ((GsRegulatoryVertex)vertex).toXML(out, svs, mode);
    	        }
    			break;
    		default:
    	        while (it.hasNext()) {
    	            Object vertex = it.next();
    	            ((GsRegulatoryVertex)vertex).toXML(out, "", mode);
    	        }
        }
    }

//    /**
//     * @param edge
//     */
//    public void addToExistingEdge( GsRegulatoryMultiEdge edge) {
//        edge.addEdge(this);
//    }
//
//    /**
//     * @param edge
//     * @param param
//     */
//    public void addToExistingEdge(GsRegulatoryMultiEdge edge, int param) {
//        edge.addEdge(this);
//    }

    public boolean idExists(String newId) {
        Iterator it = getVertices().iterator();
        while (it.hasNext()) {
            if (newId.equals(((GsRegulatoryVertex)it.next()).getId())) {
                return true;
            }
        }
        return false;
    }
    public void changeVertexId(Object vertex, String newId) throws GsException {
        GsRegulatoryVertex rvertex = (GsRegulatoryVertex)vertex;
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
    public void removeEdgeFromMultiEdge(GsRegulatoryMultiEdge multiEdge, int index) throws GsException {
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
    public boolean removeEdge(GsRegulatoryMultiEdge edge) {
       edge.markRemoved();
       super.removeEdge(edge);
       edge.getTarget().removeEdgeFromInteraction(edge);
       fireGraphChange(CHANGE_EDGEREMOVED, edge);
       return true;
    }

    public boolean removeVertex(GsRegulatoryVertex obj) {
        for (GsRegulatoryMultiEdge me: getOutgoingEdges(obj)) {
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
    public GsRegulatoryVertex addNewVertex(String id, String name, byte max) {
        GsRegulatoryVertex vertex = new GsRegulatoryVertex(id, this);
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
    public GsRegulatoryEdge addNewEdge(String from, String to, byte minvalue, String sign)  throws GsException{
    	byte vsign = GsRegulatoryMultiEdge.SIGN_UNKNOWN;
    	for (byte i=0 ; i<GsRegulatoryMultiEdge.SIGN.length ; i++) {
    		if (GsRegulatoryMultiEdge.SIGN[i].equals(sign)) {
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
    public GsRegulatoryEdge addNewEdge(String from, String to, byte minvalue, byte sign) throws GsException{
        GsRegulatoryVertex source = null;
        GsRegulatoryVertex target = null;

        source = (GsRegulatoryVertex) getVertexByName(from);
        if (from.equals(to)) {
            target = source;
        } else {
            target = (GsRegulatoryVertex) getVertexByName(to);
        }

        if (source == null || target == null) {
            throw new GsException( GsException.GRAVITY_ERROR, "STR_noSuchVertex");
        }
        GsRegulatoryMultiEdge me = getEdge(source, target);
        int index = 0;
        if (me == null) {
            me = new GsRegulatoryMultiEdge(source, target, sign, minvalue);
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
	public void canApplyNewMaxValue(GsRegulatoryVertex vertex, byte newMax, List l_fixable, List l_conflict) {
		for (GsRegulatoryMultiEdge me: getOutgoingEdges(vertex)) {
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

    /**
     * Return the Object Managers specialized for this class
     * 
     * @return a List of Object Managers
     */
    @Override
    public List getSpecificObjectManager() {
    	
        return RegulatoryGraphFactory.getObjectManager();
    }

    protected Graph getCopiedGraph() {
    	
        return copiedGraph;
    }

    protected List doMerge( Graph<GsRegulatoryVertex, GsRegulatoryMultiEdge> otherGraph) {
        if (!(otherGraph instanceof GsRegulatoryGraph)) {
            return null;
        }
        List ret = new ArrayList();
        HashMap copyMap = new HashMap();
        Iterator<GsRegulatoryVertex> it = otherGraph.getVertices().iterator();
        GsVertexAttributesReader vReader = getVertexAttributeReader();
        GsVertexAttributesReader cvreader = otherGraph.getVertexAttributeReader();
        while (it.hasNext()) {
            GsRegulatoryVertex vertexOri = (GsRegulatoryVertex)it.next();
            GsRegulatoryVertex vertex = (GsRegulatoryVertex)vertexOri.clone();
            addVertexWithNewId(vertex);
            cvreader.setVertex(vertexOri);
            vReader.setVertex(vertex);
            vReader.copyFrom(cvreader);
            vReader.refresh();
            copyMap.put(vertexOri, vertex);
            ret.add(vertex);
        }

        Iterator<GsRegulatoryMultiEdge> it2 = otherGraph.getEdges().iterator();
        GsEdgeAttributesReader eReader = getEdgeAttributeReader();
        GsEdgeAttributesReader cereader = otherGraph.getEdgeAttributeReader();
        while (it2.hasNext()) {
        	GsRegulatoryMultiEdge deOri = it2.next();
        	GsRegulatoryMultiEdge edge = addEdge((GsRegulatoryVertex)copyMap.get(deOri.getSource()), (GsRegulatoryVertex)copyMap.get(deOri.getTarget()), 0);
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
    private void addVertexWithNewId(GsRegulatoryVertex vertex) {
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
    public Graph getSubgraph(Collection<GsRegulatoryVertex> v_vertex, Collection<GsRegulatoryMultiEdge> v_edges) {
    	
        GsRegulatoryGraph copiedGraph = new GsRegulatoryGraph();
        GsVertexAttributesReader vReader = getVertexAttributeReader();
        GsVertexAttributesReader cvreader = copiedGraph.getVertexAttributeReader();
        HashMap copyMap = new HashMap();
        if (v_vertex != null) {
            for (GsRegulatoryVertex vertexOri: v_vertex) {
                GsRegulatoryVertex vertex = (GsRegulatoryVertex)vertexOri.clone();
                copiedGraph.addVertexWithNewId(vertex);
                vReader.setVertex(vertexOri);
                cvreader.setVertex(vertex);
                cvreader.copyFrom(vReader);
                copyMap.put( vertexOri, vertex);
            }
        }

        if (v_edges != null) {
        	GsEdgeAttributesReader eReader = getEdgeAttributeReader();
            GsEdgeAttributesReader cereader = copiedGraph.getEdgeAttributeReader();
	        for (GsRegulatoryMultiEdge edgeOri: v_edges) {
	        	GsRegulatoryMultiEdge edge = copiedGraph.addEdge((GsRegulatoryVertex)copyMap.get(edgeOri.getSource()), (GsRegulatoryVertex)copyMap.get(edgeOri.getTarget()), 0);
	            edge.copyFrom(edgeOri);
	            copyMap.put(edgeOri, edge);
                eReader.setEdge(edgeOri);
                cereader.setEdge(edge);
                cereader.copyFrom(eReader);
	        }
        }

        if (v_vertex != null) {
            for (GsRegulatoryVertex v: v_vertex) {
                v.cleanupInteractionForNewGraph(copyMap);
            }
        }

        GsRegulatoryGraph.copiedGraph = copiedGraph;
        return copiedGraph;
    }

    protected void setCopiedGraph( Graph graph) {
        if (graph != null && graph instanceof GsRegulatoryGraph) {
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
    public static boolean associationValid(GsRegulatoryGraph regGraph, GsDynamicGraph dynGraph) {
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
    public OmddNode[] getAllTrees(boolean focal) {
        OmddNode[] t_tree = new OmddNode[nodeOrder.size()];
        for (int i=0 ; i<nodeOrder.size() ; i++) {
        	GsRegulatoryVertex vertex = (GsRegulatoryVertex)nodeOrder.get(i);
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
	public OmddNode[] getParametersForSimulation(boolean focal) {
		
		return getAllTrees(focal);
	}
}
