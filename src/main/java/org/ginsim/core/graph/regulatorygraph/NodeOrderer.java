package org.ginsim.core.graph.regulatorygraph;

import java.util.List;

import org.colomoto.logicalmodel.NodeInfo;

/**
 * Order the components of a regulatory graph.
 * 
 * Implementors of this class can be used to select a good
 * variable order when turning a LRG into a LogicalModel object.
 * 
 * @author Aurelien Naldi
 */
public interface NodeOrderer {

	/**
	 * Pick an order for the components of a regulatory graph.
	 * 
	 * @param lrg
	 * @return
	 */
	List<NodeInfo> getOrder(RegulatoryGraph lrg);
	
}
