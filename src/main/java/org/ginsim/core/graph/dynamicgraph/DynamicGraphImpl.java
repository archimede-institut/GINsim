package org.ginsim.core.graph.dynamicgraph;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.NodeInfo;
import org.colomoto.mddlib.MDDManager;
import org.ginsim.common.application.GsException;
import org.ginsim.common.xml.XMLWriter;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.AbstractDerivedGraph;
import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.common.GraphChangeType;
import org.ginsim.core.graph.common.GraphEventCascade;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraphImpl;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.EdgePattern;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.graph.view.style.NodeStyle;
import org.ginsim.core.io.parser.GINMLWriter;
import org.ginsim.core.io.parser.GinmlHelper;

/**
 * Implementation of dynamical graphs.
 * This class should not be used directly.
 * 
 * @author Aurelien Naldi
 * @author Lionel Spinelli
 */
public final class DynamicGraphImpl extends AbstractDerivedGraph<DynamicNode, Edge<DynamicNode>, RegulatoryGraph, RegulatoryNode, RegulatoryMultiEdge> implements DynamicGraph{


	public static final String GRAPH_ZIP_NAME = "stateTransitionGraph.ginml";
	
	protected List v_stables = null;

    private List<NodeInfo> nodeOrder;
    
    private String[] extraNames = null;
    private MDDManager ddmanager = null;
    private int[] extraFunctions = null;
    
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
	    NodeInfo node_info;
	    for (Object node: nodeOrder) {
	    	if (node instanceof NodeInfo) {
	    		node_info = (NodeInfo)node;
	    	} else if (node instanceof RegulatoryNode) {
	    		RegulatoryNode regNode = (RegulatoryNode) node;
	    		node_info = regNode.getNodeInfo();
	    	} else {
	    		node_info = new NodeInfo( node.toString());
	    	}
	    	this.nodeOrder.add( node_info);
	    }
	}
	
	/**
	 * @param filename
	 */
	public DynamicGraphImpl( boolean parsing) {
        super( DynamicGraphFactory.getInstance(), parsing);
	}

	@Override
	public GraphEventCascade graphChanged(RegulatoryGraph g,
			GraphChangeType type, Object data) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param map
	 * @param file
	 */
	public DynamicGraphImpl(Set<String> set, File file)  throws GsException{
		
	    this( true);
        DynamicParser parser = new DynamicParser();
        parser.parse(file, set, this);
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
	 * Set a list of String representing the order of node as defined by the model
	 * 
	 * @param list the list of String representing the order of node as defined by the model
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
	protected GINMLWriter getGINMLWriter() {
    	return new DynamicGINMLWriter(this);
    }

	@Override
	public boolean removeEdge(Edge<DynamicNode> obj) {
		return false;
	}

	@Override
	public Edge<DynamicNode> addEdge(DynamicNode source, DynamicNode target, boolean multiple) {
		
		Edge<DynamicNode> edge = new Edge<DynamicNode>(this, source, target);
		if (!addEdge(edge)) {
			return null;
		}
		if (multiple) {
			EdgeAttributesReader eReader = getEdgeAttributeReader();
			eReader.setEdge(edge);
			eReader.setDash(EdgePattern.DASH);
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
        Iterator it = otherGraph.getNodes().iterator();
        NodeAttributesReader vReader = getNodeAttributeReader();
        NodeAttributesReader cvreader = otherGraph.getNodeAttributeReader();
        while (it.hasNext()) {
            DynamicNode vertex = (DynamicNode)it.next();
            addNode(vertex);
            cvreader.setNode(vertex);
            vReader.setNode(vertex);
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
        if (containsNode(n) && containsNode(n2)) {
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

	@Override
	public String[] getExtraNames() {
		return extraNames;
	}

	@Override
	public byte[] fillExtraValues(byte[] state, byte[] extraValues) {
		if (extraFunctions == null) {
			return null;
		}
		
		byte[] extra = extraValues;
		
		if (extra == null || extra.length != extraFunctions.length) {
			extra = new byte[extraFunctions.length];
		}
		
		for (int i=0 ; i<extra.length ; i++) {
			extra[i] = ddmanager.reach(extraFunctions[i], state);
		}
		return extra;
	}

	@Override
	public void setLogicalModel(LogicalModel model) {
		List<NodeInfo> extraNodes = null;
		if (model != null) {
			extraNodes = model.getExtraComponents();
			if (extraNodes == null || extraNodes.size() < 1) {
				model = null;
			}
		}
		if (model == null) {
			// reset extra information
			ddmanager = null;
			extraNames = null;
			extraFunctions = null;
			return;
		}
		
		ddmanager = model.getMDDManager();
		extraFunctions = model.getExtraLogicalFunctions();
		extraNames= new String[extraFunctions.length];

		for (int i=0 ; i<extraNames.length ; i++) {
			extraNames[i] = extraNodes.get(i).getNodeID();
		}
	}

}
