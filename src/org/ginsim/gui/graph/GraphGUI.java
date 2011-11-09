package org.ginsim.gui.graph;

import java.awt.Component;
import java.util.Collection;

import javax.swing.JMenu;
import javax.swing.JPanel;

import org.ginsim.graph.Edge;
import org.ginsim.graph.Graph;

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
	public Graph<V, E> getGraph();
	
	/**
	 * @return the widget showing the graph
	 */
	public Component getGraphComponent();

	/**
	 * Fill the view menu with actions available on this graph GUI
	 */
	public JMenu getViewMenu(JMenu layoutMenu);

	/**
	 * @return the list of selected vertices
	 */
	public Collection<V> getSelectedVertices();
	/**
	 * @return the list of selected edges
	 */
	public Collection<E> getSelectedEdges();

	public GUIEditor<G> getMainEditionPanel();

	public String getEditingTabLabel();

	public GUIEditor<V> getNodeEditionPanel();

	public GUIEditor<E> getEdgeEditionPanel();
	
	public JPanel getInfoPanel();

	public EditActionManager getEditActionManager();
	
// TODO Commented out for quick testing: remove comments	

//	/**
//	 * Select all the graph objects (vertices and edges)
//	 * 
//	 */
//    public void selectAll();
//	
//	
//    /**
//     * Select the graph objects in the List.
//     * 
//     * @param l the list of objects to select
//     */
//    public void select(List l);
//    
//    
//    /**
//     * Select the graph objects in the Set.
//     * 
//     * @param s the Set of objects to select
//     */
//    public void select(Set s);
//    
//    
//    /**
//     * Add the graph objects of the List to the selected objects
//     * 
//     * @param l the list of objects to select
//     */
//    public void addSelection(List l);
//    
//    
//    /**
//     * Add the graph objects of the Set to the selected objects
//     * 
//     * @param s the Set of objects to select
//     */
//    public void addSelection(Set s);
//    
//    
//    /** 
//     * Select the provided graph object
//     * 
//     * @param obj the item to select 
//     */
//    public void select(Object obj);
//
//    
//    /**
//     * Invert the selection ie unselect currently selected objects and select all others.
//     *
//     */
//    public void invertSelection();
//
//    
//    /**
//     * @return an iterator on the selected edges if their source and target vertices are also selected.
//     */
//	abstract public Iterator<E> getFullySelectedEdgeIterator();
//    /**
//     * @return an iterator on the selected edges, even if their source and target vertices are not selected.
//     */
//	abstract public Iterator<E> getSelectedEdgeIterator();
//	
//    /**
//     * @return an iterator to selected vertices.
//     */
//	abstract public Iterator<V> getSelectedVertexIterator();
//    
//    /**
//     * Move all vertex to front if boolean is true.
//     * Remove vertices from front if boolean is false. 
//     * 
//     * @param b
//     */
//    public void vertexToFront(boolean b);
//    
//
//    /**
//     * zoom out the display.
//     */
//    public void zoomOut();
//
//    
//    /**
//     * zoom in the display.
//     */
//    public void zoomIn();
//
//    
//    /**
//     * restore the display to the default zoom level.
//     */
//    public void zoomNormal();
//
//    
//    /**
//     * Show the edge name on the display if boolean is true.
//     * Hide the edge name on the display if boolean is false.
//     * 
//     * @param b
//     */
//    public void displayEdgeName(boolean b);
//
//    /**
//     * Show the vertex name on the display if boolean is true.
//     * Hide the vertex name on the display if boolean is false.
//     * 
//     * @param b
//     */
//    public void displayVertexName(boolean b);

}
