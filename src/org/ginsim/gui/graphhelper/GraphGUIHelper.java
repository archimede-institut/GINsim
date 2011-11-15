package org.ginsim.gui.graphhelper;

import java.util.List;

import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import org.ginsim.graph.common.Edge;
import org.ginsim.graph.common.Graph;
import org.ginsim.gui.graph.EditAction;
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
 * Implementations must be annotated "@ProviderFor( GraphGUIHelper.class)" to be discovered at runtime
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
	 * Get the graph class the helper helps to manage
	 * 
	 * @return the graph class the helper helps to manage
	 */
	public Class<G> getGraphClass();
	
	/**
	 * @param graph the edited graph
	 * @return the list of actions or null if none are available
	 */
	public List<EditAction> getEditActions(G graph);
	
	/**
	 * Provide the file filter to apply to a file chooser
	 * 
	 * @return the file filter to apply to a file chooser
	 */
	public FileFilter getFileFilter();
	
	/**
	 * Create a panel containing the option for graph saving 
	 * 
	 * @param graph the edited graph
	 */
	public JPanel getSaveOptionPanel( G graph);
	
}
