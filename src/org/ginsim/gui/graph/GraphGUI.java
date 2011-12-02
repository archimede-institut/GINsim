package org.ginsim.gui.graph;

import java.awt.Component;

import javax.swing.JMenu;
import javax.swing.JPanel;

import org.ginsim.graph.common.Edge;
import org.ginsim.graph.common.Graph;

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
	 * @return true if this graph has not been modified since the last change
	 */
	boolean isSaved();

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
	 * reset selection and select a single node.
	 * 
	 * @param node
	 */
	void selectNode(V node);
	/**
	 * reset selection and select a single edge.
	 * 
	 * @param edge
	 */
	void selectEdge(E edge);

	/**
	 * Update selection to reflect the internal selection object
	 */
	void selectionChanged();
	
//    /**
//     * Move all nodes to front if boolean is true.
//     * Remove vertices from front if boolean is false. 
//     * 
//     * @param b
//     */
//    public void nodeToFront(boolean b);
//    

}
