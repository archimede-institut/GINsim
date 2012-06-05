package org.ginsim.core.logicalmodel;

import java.util.List;

import org.ginsim.core.graph.common.NodeInfo;

import fr.univmrs.tagc.javaMDD.MDDFactory;

/**
 * A LogicalModel is a ready to use object containing all necessary information to perform simulations and analysis,
 * without the editing facilities provided by a full RegulatoryGraph.
 * 
 * @author Aurelien Naldi
 */
public interface LogicalModel {

	List<NodeInfo> getNodeOrder();
	
	MDDFactory getMDDFactory();
	
	int[] getLogicalFunctions();

	LogicalModel clone();
	
}
