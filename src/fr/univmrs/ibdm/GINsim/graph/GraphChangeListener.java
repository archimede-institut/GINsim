package fr.univmrs.ibdm.GINsim.graph;


/**
 * Object willing to be informed of changes in the graph sould implement this interface
 */
public interface GraphChangeListener {

    /**
     * action to take when the graph in the current frame changed.
     * 
     * @param event presents the new graph.
     */
	public void graphChanged(GsNewGraphEvent event);
	/**
	 * action to take when selected object(s) changed.
	 * @param event presents the new selection.
	 */
	public void graphSelectionChanged(GsGraphSelectionChangeEvent event);
	/**
	 * alert the listenners that the graph is being closed
	 * @param graph
	 */
	public void graphClosed(GsGraph graph);
	
	/**
	 * @param graph
	 */
	public void updateGraphNotificationMessage(GsGraph graph);
}
