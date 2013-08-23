package org.ginsim.core.graph.reducedgraph;


import java.util.Collection;
import java.util.Set;

import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.common.GraphAssociation;


/**
 * reduced Graph.
 */
public interface ReducedGraph<AG extends Graph<AV,AE>, AV, AE extends Edge<AV>> extends Graph<NodeReducedData, Edge<NodeReducedData>>, GraphAssociation<AG,AV,AE>{
	
	
	/**
	 * add an edge to this graph.
	 * @param source source node of this edge.
	 * @param target target node of this edge.
	 */
	public Edge<NodeReducedData> addEdge(NodeReducedData source, NodeReducedData target);
	
	
    /**
     * @return a map referencing all real nodes in the selected CC
     */
    public Set<String> getSelectedSet(Collection<NodeReducedData> selection);
}
