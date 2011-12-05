package org.ginsim.core.graph.reducedgraph;


import java.util.Collection;
import java.util.Map;

import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.common.GraphAssociation;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;


/**
 * reduced Graph.
 */
public interface ReducedGraph extends Graph<NodeReducedData, Edge<NodeReducedData>>, GraphAssociation<RegulatoryGraph, RegulatoryNode, RegulatoryMultiEdge>{
	
	
	/**
	 * add an edge to this graph.
	 * @param source source node of this edge.
	 * @param target target node of this edge.
	 */
	public void addEdge(NodeReducedData source, NodeReducedData target);
	
	
    /**
     * @return a map referencing all real nodes in the selected CC
     */
    public Map getSelectedMap(Collection<NodeReducedData> selection);
}
