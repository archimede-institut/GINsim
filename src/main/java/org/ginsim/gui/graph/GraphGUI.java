package org.ginsim.gui.graph;

import java.awt.Component;

import javax.swing.JMenu;
import javax.swing.JPanel;

import org.ginsim.core.graph.Edge;
import org.ginsim.core.graph.Graph;

/**
 * Deal with the GUI view of a graph: get a component, provide access to selected items and view options.
 * 
 * @author Aurelien Naldi
 *
 * @param <V>
 * @param <E>
 */
public interface GraphGUI<G extends Graph<V,E>, V, E extends Edge<V>> {

	/**
	 * @return the underlying graph
	 */
	Graph<V, E> getGraph();
	
	/**
	 * @return the widget showing the graph
	 */
	Component getGraphComponent();

	/**
	 * Fill the view menu with actions available on this graph GUI
	 */
	JMenu getViewMenu(JMenu layoutMenu);

	GUIEditor<G> getMainEditionPanel();

	String getEditingTabLabel();

	GUIEditor<V> getNodeEditionPanel();

	GUIEditor<E> getEdgeEditionPanel();

	JPanel getInfoPanel();

	EditActionManager getEditActionManager();

	/**
	 * Does this graph type support copy/paste actions?
	 * 
	 * @return true if copy/paste is supported, false otherwise.
	 */
	boolean canCopyPaste();

	/**
	 * Returns true if this graph has not been modified since the last change
	 * 
	 * @return true if this graph has not been modified since the last change
	 */
	boolean isSaved();

	/**
	 * Set the graph has been saved or has not been modified since opened
	 * 
	 * @param isSaved
	 */
	void setSaved( boolean isSaved);
	
	/**
	 * Indicates if the type of graph associated to the GraphGUI can be saved or not
	 * 
	 * @return true if the type of graph associated to the GraphGUI can be saved, false if not
	 */
	boolean canBeSaved();
	
	/**
	 * Save the graph.
	 * 
	 * @return true, unless save failed
	 */
	boolean save();
	
	/**
	 * Pick a destination and save the graph.
	 */
	boolean saveAs();

	/**
	 * Warn the listeners that the graph will be closed
	 */
	void fireGraphClose();

	/**
	 * Register a GraphGUIListener
	 * 
	 * @param listener
	 */
	void addGraphGUIListener(GraphGUIListener<G, V, E> listener);

	/**
	 * Remove a listener.
	 * 
	 * @param listener
	 */
	void removeGraphGUIListener(GraphGUIListener<G, V, E> listener);
	
	/**
	 * Get an object to interact with the selection
	 * 
	 * @return
	 */
	GraphSelection<V, E> getSelection();
	
	boolean isEditAllowed();
	
	/**
	 * Update selection to reflect the internal selection object
	 */
	void selectionChanged();
	
	/**
	 * Force a repaint (should be used only by layout actions)
	 */
	void repaint();

}
