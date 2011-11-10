package org.ginsim.gui.graph;

import org.ginsim.graph.Edge;
import org.ginsim.graph.Graph;

/**
 * Interface for objects which listen for Graph GUI events.
 * 
 * @author Aurelien Naldi
 *
 * @param <G>
 * @param <V>
 * @param <E>
 */
public interface GraphGUIListener<G extends Graph<V, E>, V, E extends Edge<V>> {

	/**
	 * The selection has changed.
	 * 
	 * @param gui
	 */
	public void graphSelectionChanged(GraphGUI<G, V, E> gui);
	/**
	 * The GUI was closed.
	 * 
	 * @param gui
	 */
	public void graphGUIClosed(GraphGUI<G, V, E> gui);
}
