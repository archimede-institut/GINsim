package org.ginsim.gui.graph.helper;

import javax.swing.JPanel;

import org.ginsim.graph.Edge;
import org.ginsim.graph.Graph;
import org.ginsim.gui.graph.GUIEditor;

/**
 * GUI-side helper for a graph type.
 * 
 * A helper is a singleton dedicated to a specific graph type.
 * It provides GUI components required to visualize and edit a graph.
 * 
 * Implementors must be called with the same pattern: '<GraphClassName>GUIHelper.java' in order to have
 * the GraphGUIHelperFactory able to create their instances through introspective access
 * 
 * @author Aurelien Naldi
 *
 * @param <G>
 * @param <V>
 * @param <E>
 */
public interface GraphGUIHelper<G extends Graph<V,E>, V, E extends Edge<V>> {

	/**
	 * Create an edition panel for the graph itself.
	 * 
	 * @param graph the graph requiring the panel
	 * @return
	 */
	public GUIEditor<G> getMainEditionPanel(G graph);
	
	/**
	 * Retrieve the Title used for the selection edition tab
	 * 
	 * @param graph the graph requiring the panel
	 * @return
	 */
	public String getEditingTabLabel(G graph);
	
	/**
	 * Create an edition panel for nodes.
	 * 
	 * @param graph the graph requiring the panel
	 * @return
	 */
	public GUIEditor<V> getNodeEditionPanel(G graph);
	
	/**
	 * Create an edition panel for edges.
	 * 
	 * @param graph the graph requiring the panel
	 * @return 
	 */
	public GUIEditor<E> getEdgeEditionPanel(G graph);
	
	/**
	 * Create an Information Panel
	 * 
	 * @param graph the graph requiring the panel
	 * @return an Information Panel
	 */
	public JPanel getInfoPanel( G graph);
	
	/**
	 * Return the name of the graph class the helper help to manage
	 * 
	 * @return the name of the graph class the helper help to manage
	 */
	public String getGraphClassName();
}
