package org.ginsim.service.tool.composition;

/**
 * Represents the interface of any Topology
 * 
 * @author Nuno D. Mendes
 */

public interface Topology {


	/**
	 * 
	 * @return number of module instances
	 */
	public int getNumberInstances();

	/**
	 * 
	 * Specifies that module neighindex2 is a neighbour of module neighindex1,
	 * but not the other way around.
	 * 
	 * @param neighindex1
	 *            indicates the index of the first module
	 * 
	 * @param neighindex2
	 *            indicates the index of the second module
	 */
	public void addNeighbour(int neighindex1, int neighindex2);


	/**
	 * @param neighindex1
	 *            indicates the index of the first module
	 * 
	 * @param neighindex2
	 *            indicates the index of the second module
	 */
	public void removeNeighbour(int neighindex1, int neighindex2);
	
	/**
	 * 
	 * Method determining whether one module in neighbour of another in the
	 * defined Topology
	 * 
	 * @param neighindex1
	 *            Index of first module (starting in 0)
	 * @param neighindex2
	 *            Index of second module (starting in 0)
	 * @return TRUE if the first module is a neighbour of the second module,
	 *         FALSE otherwise
	 */
	public boolean areNeighbours(int neighindex1, int neighindex2);

	/**
	 * @param index
	 *            A module index
	 * @return TRUE if the module has neighbours, FALSE otherwise
	 */
	public boolean hasNeighbours(int index);

	/**
	 * @param index
	 *            A module index
	 * 
	 * @return the number of neighbours of the given module
	 */

	public int getNumberNeighbours(int index);
	
	public int[] getNeighbourIndices(int index);
	

	

}
