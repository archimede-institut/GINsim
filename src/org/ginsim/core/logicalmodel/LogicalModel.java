package org.ginsim.core.logicalmodel;

import java.util.List;

import org.colomoto.mddlib.MDDManager;
import org.ginsim.core.graph.common.NodeInfo;


/**
 * A LogicalModel is a ready to use object containing all necessary information to perform simulations and analysis,
 * without the editing facilities provided by a full RegulatoryGraph.
 * 
 * @author Aurelien Naldi
 */
public interface LogicalModel {

	/**
	 * Get the MDD factory holding logical functions for this model.
	 * @return the MDD factory in which logical function are stored.
	 */
	MDDManager getMDDFactory();

	/**
	 * Get the list of core nodes in this model.
	 * Logical functions for these nodes (in the same order) can be obtained with the
	 * <code>getLogicalFunctions()</code> method.
	 * 
	 * @return the list of nodeInfo objects for core components.
	 */
	List<NodeInfo> getNodeOrder();
	
	/**
	 * Get the logical function of core components in this model.
	 * The order used is the same as for <code>getNodeOrder()</code>.
	 * These functions are just identifiers, actual functions are stored in
	 * the MDD factory provided by <code>getMDDFactory</code>.
	 * 
	 * @return the list of logical function identifiers for core components
	 */
	int[] getLogicalFunctions();

	/**
	 * Compute the target value of a component for a given state.
	 * 
	 * @param nodeIdx index of the component in the node order
	 * @param state value of components
	 * @return the target value reached for this state
	 */
	byte getTargetValue(int componentIdx, byte[] state);
	
	byte getComponentValue(int componentIdx, byte[] path);
	
	/**
	 * Make a copy of this model.
	 * This will duplicate logical functions pointers, but not the actual MDD Factory.
	 * 
	 * @return a copy of this model.
	 */
	LogicalModel clone();
	
}
