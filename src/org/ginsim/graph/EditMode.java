package org.ginsim.graph;

/**
 * Minimalistic description of the available edit types for a Graph.
 * 
 * @author Aurelien Naldi
 */
public class EditMode {

	private final EditGroup mode;
	private final String name;
	private final int value;
	
	public EditMode(EditGroup mode, String name, int value) {
		this.mode = mode;
		this.name = name;
		this.value = value;
	}
	
	/**
	 * @return the type of edit performed (to group nodes and edges related actions)
	 */
	public EditGroup getMode() {
		return mode;
	}
	
	/**
	 * @return the name of this edit 
	 */
	public String getName() {
		return name;
	}
	
	public int getValue() {
		return value;
	}
}
