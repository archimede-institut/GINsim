package org.ginsim.service.tool.reg2dyn.limitedsimulation;

/**
 * Indicates if the states in the immediates neighborhood should be added the the resulting dynamic graph of the simualtion.
 * @author Duncan Berenguier
 *
 */
public enum OutgoingNodesHandlingStrategy {
	/**
	 * Compute only the given hierarchical nodes
	 */
    CONTAIN_TO_SELECTION, 
    /**
     * Compute the given hierarchical nodes and the immediate neighborhood
     */
    ADD_FIRST_OUTGOING_STATE; 
}
