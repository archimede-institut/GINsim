package org.ginsim.core.graph.trapspacetree;

import org.ginsim.core.graph.Graph;
import org.ginsim.core.graph.GraphAssociation;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;

/**
 * Trap-space inclusion tree provide an over-approximation of the attractor reachability landscape.
 * 
 * @author Aurelien Naldi
 */
public interface TrapSpaceTree extends Graph<TrapSpaceNode,TrapSpaceInclusion>, GraphAssociation<RegulatoryGraph, RegulatoryNode, RegulatoryMultiEdge> {

	
	
}
