package org.ginsim.gui.graph;

/**
 * Minimalistic description of the available edit types for a Graph.
 * 
 * @author Aurelien Naldi
 */
public class EditAction {

	private final EditMode mode;
	private final String name;
	
	public EditAction(EditMode mode, String name) {
		this.mode = mode;
		this.name = name;
	}
	
	/**
	 * @return the type of edit performed (to group nodes and edges related actions)
	 */
	public EditMode getMode() {
		return mode;
	}
	
	/**
	 * @return the name of this edit 
	 */
	public String getName() {
		return name;
	}
}
