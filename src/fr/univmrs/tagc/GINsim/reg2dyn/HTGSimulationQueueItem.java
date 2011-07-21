package fr.univmrs.tagc.GINsim.reg2dyn;


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
	 * @return true if it is a cycle
	 */
	public abstract boolean isCycle();

}