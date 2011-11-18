package org.ginsim.graph.reducedgraph;


import java.util.Collection;
import java.util.Map;

import org.ginsim.graph.common.GraphAssociation;
import org.ginsim.graph.common.Edge;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.regulatorygraph.GsRegulatoryGraph;
import org.ginsim.graph.regulatorygraph.GsRegulatoryMultiEdge;
import org.ginsim.graph.regulatorygraph.GsRegulatoryVertex;


/**
 * reduced Graph.
 */
public interface GsReducedGraph extends Graph<GsNodeReducedData, Edge<GsNodeReducedData>>, GraphAssociation<GsRegulatoryGraph, GsRegulatoryVertex, GsRegulatoryMultiEdge>{
	
	
	/**
	 * add an edge to this graph.
	 * @param source source vertex of this edge.
	 * @param target target vertex of this edge.
	 */
	public void addEdge(GsNodeReducedData source, GsNodeReducedData target);
	
	
    /**
     * @return a map referencing all real nodes in the selected CC
     */
    public Map getSelectedMap(Collection<GsNodeReducedData> selection);
}
