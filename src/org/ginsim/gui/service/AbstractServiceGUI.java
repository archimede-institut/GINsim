/**
 * 
 */
package org.ginsim.gui.service;

/**
 * This class contains the weight used to order the services' actions in the menus
 * The services must inherit from this class
 *
 */
public abstract class AbstractServiceGUI implements ServiceGUI {

	private int weight;

	/**
	 * @return the weight
	 */
	public int getWeight() {
		return weight + getInitialWeight();
	}

	/**
	 * @param weight the weight to set
	 */
	public void setWeight(int weight) {
		this.weight = weight;
	}
	
}
