package org.ginsim.core.graph.reducedgraph;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ginsim.common.application.GsException;
import org.ginsim.core.graph.common.AbstractDerivedGraph;
import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;


public class ReducedGraphImpl<G extends Graph<V,E>, V, E extends Edge<V>>  extends AbstractDerivedGraph<NodeReducedData, Edge<NodeReducedData>, G,V,E>
	implements ReducedGraph<G,V,E>{

	public static final String GRAPH_ZIP_NAME = "connectedComponent.ginml";
	
	/**
	 * @param parent
	 */
	public ReducedGraphImpl( G parent) {
		
	    this( false);
    	setAssociatedGraph( parent);
	}

	/**
	 * @param map
	 * @param file
	 */
	public ReducedGraphImpl(Set set, File file)  throws GsException{
		
	    this( true);
        ReducedGraphParser parser = new ReducedGraphParser();
        parser.parse(file, set, this);
	}

	/**
	 * @param filename
	 */
	public ReducedGraphImpl( boolean parsing) {
		
        super(ReducedGraphFactory.getInstance().getGraphType(), parsing);
	}
	/**
     * 
     */
    public ReducedGraphImpl() {
    	
        this( false);
    }

    
	/**
	 * Return the zip extension for the graph type
	 * 
	 * @return the zip extension for the graph type
	 */
    @Override
	public String getGraphZipName(){
		
		return GRAPH_ZIP_NAME;
		
	}
    
    /**
     * Return 0 since no node order is defined on this king of graph
     * 
     * @return 0 since no node order is defined on this king of graph
     */
    @Override
	public int getNodeOrderSize(){
		
		return 0;
	}
    
    /**
     * Indicates if the given graph can be associated to the current one
     * 
     * @param graph the graph to associate to the current one
     * @return true is association is possible, false if not
     */
    @Override
    protected boolean isAssociationValid( Graph<?, ?> graph) {
    	
		return true;
    }

    @Override
	protected void doSave(OutputStreamWriter os, Collection<NodeReducedData> vertices, Collection<Edge<NodeReducedData>> edges, int mode) throws GsException {
    	ReducedGINMLWriter writer = new ReducedGINMLWriter(this);
        try {
        	writer.write(os, vertices, edges, mode);
        } catch (IOException e) {
            throw new GsException( "STR_unableToSave", e);
        }
	}
	
	/**
	 * add an edge to this graph.
	 * @param source source node of this edge.
	 * @param target target node of this edge.
	 */
    @Override
	public Edge<NodeReducedData> addEdge(NodeReducedData source, NodeReducedData target) {
		Edge<NodeReducedData> edge = new Edge<NodeReducedData>(this, source, target);
		addEdge( edge);
		return edge;
	}
	
    @Override
    protected List doMerge(Graph otherGraph) {
        return null;
    }
    
    @Override
    public Graph getSubgraph(Collection vertex, Collection edges) {
        // no copy for reduced graphs
        return null;
    }

    /**
     * @return a map referencing all real nodes in the selected CC
     */
    @Override
    public Set<String> getSelectedSet(Collection<NodeReducedData> selection) {
        Set set = new HashSet();
        for (NodeReducedData node: selection) {
            List content = node.getContent();
            for (Object o: content) {
                set.add(o.toString());
            }
        }
        return set;
    }
}
