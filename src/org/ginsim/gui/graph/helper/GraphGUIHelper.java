package org.ginsim.gui.graph.helper;

import org.ginsim.graph.Edge;
import org.ginsim.graph.Graph;
import org.ginsim.gui.graph.GUIEditor;

/**
 * GUI-side helper for a graph type. Implementors will have to be declared in a configuration file
 * 
 * Implementors must be called with the same pattern: '<GraphClassName>GUIHelper.java' in order to have
 * the GraphGUIHelperFactory able to create their instances through introspective access
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
