package fr.univmrs.ibdm.GINsim.gui;


/**
 * Here are the callback for entry in the "edit" menu
 * 
 * 
 */
public class GsEditCallBack {
	
	private GsMainFrame main;
	
	/**
	 * simple constructor
	 * @param m
	 */
	public GsEditCallBack(GsMainFrame m) {
		main = m;
	}
	
	/**
	 * undo last action
	 */
	public void undo() {
	    main.getGraph().getGraphManager().undo();
	}
	
	/**
	 * redo last "undoed" action
	 */
	public void redo() {
	    main.getGraph().getGraphManager().redo();
	}
	
	/**
	 * select all nodes
	 */
	public void selectAll() {
	    main.getGraph().getGraphManager().selectAll();
	}
	
	/**
	 * select all previously unselected nodes and vice-versa
	 */
	public void invertSelection() {
	    main.getGraph().getGraphManager().invertSelection();
	}
	
	/**
	 * copy the selection
	 * @see fr.univmrs.ibdm.GINsim.graph.GsGraph#copy()
	 */
	public void copy() {
	    main.getGraph().copy();
	}
	
	/**
	 * paste the previously copied selection
	 * @see fr.univmrs.ibdm.GINsim.graph.GsGraph#paste()
	 */
	public void paste() {
	    main.getGraph().paste();
	}
	
	/**
	 * delete selection
	 */
	public void delete() {
	    main.getGraph().getGraphManager().delete();
	}
	
}
