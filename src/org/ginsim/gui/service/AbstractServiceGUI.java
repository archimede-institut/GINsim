/**
 * 
 */
package org.ginsim.gui.service;

/**
 * 
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
