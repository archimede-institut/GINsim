package org.ginsim.graph.reducedgraph;


import java.util.Collection;
import java.util.Map;

import org.ginsim.graph.common.GraphAssociation;
import org.ginsim.graph.common.Edge;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.graph.regulatorygraph.RegulatoryVertex;


/**
 * reduced Graph.
 */
public interface ReducedGraph extends Graph<NodeReducedData, Edge<NodeReducedData>>, GraphAssociation<RegulatoryGraph, RegulatoryVertex, RegulatoryMultiEdge>{
	
	
	/**
	 * add an edge to this graph.
	 * @param source source vertex of this edge.
	 * @param target target vertex of this edge.
	 */
	public void addEdge(NodeReducedData source, NodeReducedData target);
	
	
    /**
     * @return a map referencing all real nodes in the selected CC
     */
    public Map getSelectedMap(Collection<NodeReducedData> selection);
}
