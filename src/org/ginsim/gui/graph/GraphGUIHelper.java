package org.ginsim.gui.graph;

import org.ginsim.graph.Edge;
import org.ginsim.graph.Graph;

/**
 * GUI-side helper for a graph type. Implementors will have to be declared in a configuration file
 * 
 * @author Aurelien Naldi
 *
 * @param <V>
 * @param <E>
 */
public interface GraphGUIHelper<V, E extends Edge<V>> {

	public GUIEditor<Graph<V,E>> getMainEditionPanel();
	
	public String getEditingTabLabel();
	
	public GUIEditor<V> getNodeEditionPanel();
	
	public GUIEditor<E> getEdgeEditionPanel();
}
