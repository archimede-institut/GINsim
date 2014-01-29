package org.ginsim.core.graph.reducedgraph;


import java.util.Collection;
import java.util.Set;

import org.ginsim.core.graph.Edge;
import org.ginsim.core.graph.Graph;
import org.ginsim.core.graph.GraphAssociation;


/**
 * Graph of the strongly connected components.
 *
 * @author Cecile Menahem
 * @author Aurelien Naldi
 *
 * @param <AG>   type of the original graph
 * @param <AV>   type of the original vertices
 * @param <AE>   type of the original edges
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
