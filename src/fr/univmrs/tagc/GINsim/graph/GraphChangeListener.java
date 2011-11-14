package fr.univmrs.tagc.GINsim.graph;

import org.ginsim.graph.common.Graph;


/**
 * Object willing to be informed of changes in the graph should implement this interface
 */
public interface GraphChangeListener {

	/**
	 * action to take when selected object(s) changed.
	 * @param event presents the new selection.
	 */
	public void graphSelectionChanged(GsGraphSelectionChangeEvent event);
	/**
	 * alert the listeners that the graph is being closed
	 * @param graph
	 */
	public void graphClosed( Graph graph);
	
	/**
	 * @param graph
	 */
	public void updateGraphNotificationMessage( Graph graph);
}
