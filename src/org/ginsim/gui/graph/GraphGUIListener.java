package org.ginsim.gui.graph;

import org.ginsim.core.graph.GraphEventCascade;
import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.common.GraphChangeType;
import org.ginsim.core.graph.common.GraphListener;
import org.ginsim.core.graph.common.GraphModel;

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
	void graphSelectionChanged(GraphGUI<G, V, E> gui);
	
	/**
	 * The GUI was closed.
	 * 
	 * @param gui
	 */
	void graphGUIClosed(GraphGUI<G, V, E> gui);
	
	/**
	 * The graph data was changed.
	 * 
	 * This method is used to forward change events (see GraphListener) to the GUI.
	 * Listeners should react by refreshing the GUI when needed but should not change the graph.
	 * 
	 * @param g
	 * @param type
	 * @param data
	 */
	void graphChanged(G g, GraphChangeType type, Object data);

}
