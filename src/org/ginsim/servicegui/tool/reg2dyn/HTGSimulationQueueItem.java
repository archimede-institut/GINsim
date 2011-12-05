package org.ginsim.servicegui.tool.reg2dyn;


/**
 * 
 * Interface of the items in the queue of the HTG simulation.
 * Can be either a simple state encompassed into an {@link HTGSimulationQueueState} or a SCC encompassed into a {@link HTGSimulationQueueSCC}
 * 
 * As in Tarjan's algorithm each item has an index and a low_index in order to find the head of the SCC.
 * 
 * The method containsState() indicates if a state contained in the queue item, ie. if it is the state of the HTGSimulationQueueState, or if it is in the GsStateSet of the HTGSimulationQueueSCC
 *
 */
public interface HTGSimulationQueueItem {

	/**
	 * @param index the index to set
	 */
	public abstract void setIndex(int index);

	/**
	 * @return the index
	 */
	public abstract int getIndex();

	/**
	 * @param low_index the low_index to set
	 */
	public abstract void setLow_index(int low_index);

	/**
	 * @return the low_index
	 */
	public abstract int getLow_index();

	/**
	 * @return true if the element contains the state state
	 */
	public abstract boolean containsState(byte[] state);
	
	/**
	 * @return true if it is a cycle, ie. a HTGSimulationQueueSCC
	 */
	public abstract boolean isCycle();

}