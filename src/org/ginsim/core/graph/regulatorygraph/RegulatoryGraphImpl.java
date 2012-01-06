package org.ginsim.core.graph.regulatorygraph;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ginsim.common.exception.GsException;
import org.ginsim.common.xml.XMLWriter;
import org.ginsim.core.annotation.BiblioManager;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.AbstractGraph;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.dynamicgraph.DynamicGraph;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.mutant.MutantListManager;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.io.parser.GinmlHelper;
import org.ginsim.core.notification.NotificationManager;
import org.ginsim.core.notification.resolvable.resolution.NotificationResolution;


public final class RegulatoryGraphImpl  extends AbstractGraph<RegulatoryNode, RegulatoryMultiEdge> 
	implements RegulatoryGraph{

	public static final String GRAPH_ZIP_NAME = "regulatoryGraph.ginml";
	
	private int nextid=0;

	private List<RegulatoryNode> nodeOrder = new ArrayList<RegulatoryNode>();

    private static Graph copiedGraph = null;

    static {
    	ObjectAssociationManager.getInstance().registerObjectManager( RegulatoryGraph.class, new MutantListManager());
    	ObjectAssociationManager.getInstance().registerObjectManager( RegulatoryGraph.class,  new BiblioManager());
    }

    /**
     */
    public RegulatoryGraphImpl() {
    	
        this( false);
    }

    
    /**
     * Return the node order
     * 
     * @return the node order as a list of RegulatoryNode
     */
    @Override
    public List<RegulatoryNode> getNodeOrder() {
    	
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
	 * Set a list of class dependent objects representing the order of node as defined by the model
	 * 
	 * @param list the list of objects representing the order of node as defined by the model
	 */
    @Override
    public void setNodeOrder( List<RegulatoryNode> nodeOrder) {
    	
		this.nodeOrder = nodeOrder;
	}
    
    
	
    
    /**
     * @param parsing
     */
    public RegulatoryGraphImpl( boolean parsing) {
    	
        super( parsing);
    	// getNodeAttributeReader().setDefaultNodeSize(55, 25);
    	// getEdgeAttributeReader().setDefaultEdgeSize(2);
    }
    
    /**
     * @param map
     * @param file
     */
    public RegulatoryGraphImpl(Map map, File file) {
    	
        this( true);
        RegulatoryParser parser = new RegulatoryParser();
        parser.parse(file, map, this);
    }

    public RegulatoryNode addNode() {

        while ( getNodeByName("G" + nextid) != null) {
        		nextid++;
        }
        RegulatoryNode obj = new RegulatoryNode(nextid++, this);
        if (super.addNode( obj)) {
    		nodeOrder.add( obj);
    		return obj;
        }
        return null;
    }
    
    /**
     * Add the node to the graph, updating the NodeOrder
     * 
     * @param node the node to add
     * @return true if the node has been correctly added, false if not
     */
    @Override
	public boolean addNode( RegulatoryNode node){
		
        if (node != null && super.addNode( node)) {
    		nodeOrder.add( node);
    		return true;
        }
        
        return false;
	}
    
    /**
     * Add a signed edge
     * 
     * @param source
     * @param target
     * @param sign
     * @return
     */
    @Override
    public RegulatoryMultiEdge addEdge(RegulatoryNode source, RegulatoryNode target, RegulatoryEdgeSign sign) {
    	RegulatoryMultiEdge obj = getEdge(source, target);
    	if (obj != null) {

    		// TODO: restore this action without requiring to know the GUIManager?
    		NotificationResolution resolution = null;
//    		NotificationResolution resolution = new NotificationResolution(){
//    			
//    			public boolean perform( Graph graph, Object[] data, int index) {
//    				
//    				GUIManager.getInstance().getGraphGUI(graph).selectEdge((Edge<?>)data[0]);
//    				return true;
//    			}
//    			
//    			public String[] getOptionsName(){
//    				
//    				String[] t_option = {"Go"};
//    				return t_option;
//    			}
//    		};
    		
    		NotificationManager.publishResolvableWarning( this, "STR_usePanelToAddMoreEdges", this, new Object[] {obj}, resolution);
    		
    		return obj;
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
    protected void doSave( OutputStreamWriter os, Collection<RegulatoryNode> vertices, Collection<RegulatoryMultiEdge> edges, int mode) throws GsException {
    	try {
            XMLWriter out = new XMLWriter(os, GinmlHelper.DEFAULT_URL_DTD_FILE);
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
            throw new GsException( "STR_unableToSave", e);
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
		            edge.toXML(out, GinmlHelper.getEdgeVS(ereader), mode);
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
    private void saveNode(XMLWriter out, int mode, Collection<RegulatoryNode> vertices) throws IOException {
    	
    	Iterator<RegulatoryNode> it = vertices.iterator();
    	
		NodeAttributesReader vReader = getNodeAttributeReader(); 
    	
    	switch (mode) {
    		case 1:
    	        while (it.hasNext()) {
    	            Object vertex = it.next();
    	            String svs = "";
	                vReader.setNode(vertex);
	                svs = GinmlHelper.getShortNodeVS( vReader);
    	            ((RegulatoryNode)vertex).toXML(out, svs, mode);
    	        }
    			break;
    		case 2:
    	        while (it.hasNext()) {
    	            Object vertex = it.next();
    	            String svs = "";
	                vReader.setNode(vertex);
	                svs = GinmlHelper.getFullNodeVS(vReader);
    	            ((RegulatoryNode)vertex).toXML(out, svs, mode);
    	        }
    			break;
    		default:
    	        while (it.hasNext()) {
    	            Object vertex = it.next();
    	            ((RegulatoryNode)vertex).toXML(out, "", mode);
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

    /**
     * 
     * @param newId
     * @return True if a node of the graph has the given ID
     */
    @Override
    public boolean idExists(String newId) {
        Iterator it = getNodes().iterator();
        while (it.hasNext()) {
            if (newId.equals(((RegulatoryNode)it.next()).getId())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 
     * @param node
     * @param newId
     * @throws GsException
     */
    @Override
    public void changeNodeId(Object node, String newId) throws GsException {
        RegulatoryNode rvertex = (RegulatoryNode)node;
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

    public boolean removeNode(RegulatoryNode obj) {
        for (RegulatoryMultiEdge me: getOutgoingEdges(obj)) {
            removeEdge(me);
        }
        super.removeNode( obj);
        nodeOrder.remove(obj);
        fireGraphChange(CHANGE_VERTEXREMOVED, obj);
        return true;
    }

    /**
     * add a node from textual parameters (for the parser).
     *
     * @param id
     * @param name
     * @param max
     * @return the new node.
     */
    @Override
    public RegulatoryNode addNewNode(String id, String name, byte max) {
        RegulatoryNode vertex = new RegulatoryNode(id, this);
        if (name != null) {
            vertex.setName(name);
        }
        vertex.setMaxValue(max, this);
        if (addNode(vertex)) {
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
    @Override
    public RegulatoryEdge addNewEdge(String from, String to, byte minvalue, String sign)  throws GsException{
    	RegulatoryEdgeSign vsign = RegulatoryEdgeSign.UNKNOWN;
    	for (RegulatoryEdgeSign s : RegulatoryEdgeSign.values()) {
    		if (s.getLongDesc().equals(sign)) {
    			vsign = s;
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
    @Override
    public RegulatoryEdge addNewEdge(String from, String to, byte minvalue, RegulatoryEdgeSign sign) throws GsException {
        RegulatoryNode source = null;
        RegulatoryNode target = null;

        source = (RegulatoryNode) getNodeByName(from);
        if (from.equals(to)) {
            target = source;
        } else {
            target = (RegulatoryNode) getNodeByName(to);
        }

        if (source == null || target == null) {
            throw new GsException( GsException.GRAVITY_ERROR, "STR_noSuchNode");
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
     * 
     * @param node
     * @param newMax
     * @param l_fixable
     * @param l_conflict
     */
    @Override
	public void canApplyNewMaxValue(RegulatoryNode node, byte newMax, List l_fixable, List l_conflict) {
		for (RegulatoryMultiEdge me: getOutgoingEdges(node)) {
			me.canApplyNewMaxValue(newMax, l_fixable, l_conflict);
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

    protected Graph getCopiedGraph() {
    	
        return copiedGraph;
    }

    protected List doMerge( Graph<RegulatoryNode, RegulatoryMultiEdge> otherGraph) {
        if (!(otherGraph instanceof RegulatoryGraph)) {
            return null;
        }
        List ret = new ArrayList();
        HashMap copyMap = new HashMap();
        Iterator<RegulatoryNode> it = otherGraph.getNodes().iterator();
        NodeAttributesReader vReader = getNodeAttributeReader();
        NodeAttributesReader cvreader = otherGraph.getNodeAttributeReader();
        while (it.hasNext()) {
            RegulatoryNode vertexOri = (RegulatoryNode)it.next();
            RegulatoryNode vertex = (RegulatoryNode)vertexOri.clone();
            addNodeWithNewId(vertex);
            cvreader.setNode(vertexOri);
            vReader.setNode(vertex);
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
        	RegulatoryMultiEdge edge = addEdge((RegulatoryNode)copyMap.get(deOri.getSource()), (RegulatoryNode)copyMap.get(deOri.getTarget()), RegulatoryEdgeSign.POSITIVE);
            edge.copyFrom(deOri);
            cereader.setEdge(deOri);
            eReader.setEdge(edge);
            eReader.copyFrom(cereader);
            eReader.refresh();
            copyMap.put(deOri, edge);
            ret.add(edge);
        }

        it = otherGraph.getNodes().iterator();
        while (it.hasNext()) {
            it.next().cleanupInteractionForNewGraph(copyMap);
        }
        return ret;
    }
    
    
    /**
     * @param node
     */
    private void addNodeWithNewId(RegulatoryNode node) {
        String id = node.getId();
        if (getNodeByName(id) == null) {
            addNode(node);
            nodeOrder.add(node);
            return;
        }
        int addon = 1;
        while ( getNodeByName(id+"_"+addon) != null) {
            addon++;
        }
        node.setId(id+"_"+addon);
        addNode(node);
        nodeOrder.add(node);
    }

    @Override
    public Graph getSubgraph(Collection<RegulatoryNode> v_vertex, Collection<RegulatoryMultiEdge> v_edges) {
    	
        RegulatoryGraph copiedGraph = GraphManager.getInstance().getNewGraph();
        NodeAttributesReader vReader = getNodeAttributeReader();
        NodeAttributesReader cvreader = copiedGraph.getNodeAttributeReader();
        HashMap copyMap = new HashMap();
        if (v_vertex != null) {
            for (RegulatoryNode vertexOri: v_vertex) {
                RegulatoryNode vertex = (RegulatoryNode)vertexOri.clone();
                ((RegulatoryGraphImpl)copiedGraph).addNodeWithNewId(vertex);
                vReader.setNode(vertexOri);
                cvreader.setNode(vertex);
                cvreader.copyFrom(vReader);
                copyMap.put( vertexOri, vertex);
            }
        }

        if (v_edges != null) {
        	EdgeAttributesReader eReader = getEdgeAttributeReader();
            EdgeAttributesReader cereader = copiedGraph.getEdgeAttributeReader();
	        for (RegulatoryMultiEdge edgeOri: v_edges) {
	        	RegulatoryMultiEdge edge = copiedGraph.addEdge((RegulatoryNode)copyMap.get(edgeOri.getSource()), (RegulatoryNode)copyMap.get(edgeOri.getTarget()), RegulatoryEdgeSign.POSITIVE);
	            edge.copyFrom(edgeOri);
	            copyMap.put(edgeOri, edge);
                eReader.setEdge(edgeOri);
                cereader.setEdge(edge);
                cereader.copyFrom(eReader);
	        }
        }

        if (v_vertex != null) {
            for (RegulatoryNode v: v_vertex) {
                v.cleanupInteractionForNewGraph(copyMap);
            }
        }

        RegulatoryGraphImpl.copiedGraph = copiedGraph;
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

        List regOrder = regGraph.getNodeOrder();
        List dynOrder = dynGraph.getNodeOrder();
        if (regOrder == null || dynOrder == null || regOrder.size() != dynOrder.size()) {
            return false;
        }

        for (int i=0 ; i<regOrder.size() ; i++) {
            if (!dynOrder.get(i).equals( regOrder.get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param focal if true, leaves are focal points. Otherwise their are directions (-1, 0, +1)
     * @return a tree representation of logical parameters
     */
    @Override
    public OMDDNode[] getAllTrees(boolean focal) {
    	return getAllTrees(null, focal);
    }
    
    /**
     * Computes the tree representing the logical parameters, receiving an optional node ordering
     * (otherwise uses the one already defined in the regulatory graph)
     * 
     * @param focal if true, leaves are focal points. Otherwise their are directions (-1, 0, +1)
     * @return a tree representation of logical parameters
     */
    @Override
    public OMDDNode[] getAllTrees(List<RegulatoryNode> tmpNodeOrder, boolean focal) {
    	if (tmpNodeOrder == null)
    		tmpNodeOrder = nodeOrder;
        OMDDNode[] t_tree = new OMDDNode[tmpNodeOrder.size()];
        for (int i = 0; i < tmpNodeOrder.size(); i++) {
        	RegulatoryNode vertex = (RegulatoryNode)tmpNodeOrder.get(i);
            t_tree[i] = vertex.getTreeParameters(tmpNodeOrder);
            if (!focal) {
            	// FIXME: does non-focal tree works correctly ??????
            	t_tree[i] = t_tree[i].buildNonFocalTree(i, vertex.getMaxValue()+1).reduce();
            } else {
            	t_tree[i] = t_tree[i].reduce();
            }
        }
        return t_tree;
    }

    /**
     * 
     * @return
     */
    @Override
	public List getNodeOrderForSimulation() {
		
		return getNodeOrder();
	}
	
    /**
     * 
     * @param focal
     * @return
     */
	public OMDDNode[] getParametersForSimulation(boolean focal) {
		
		return getAllTrees(focal);
	}



}
