package org.ginsim.gui.graph;

import java.awt.Component;

import org.ginsim.graph.Edge;

/**
 * Deal with the GUI view of a graph: get a component, provide access to selected items and view options.
 * 
 * @author Aurelien Naldi
 *
 * @param <V>
 * @param <E>
 */
public interface GraphGUI<V, E extends Edge<V>> {

	/**
	 * 
	 * @return the widget showing the graph
	 */
	public Component getGraphComponent();
	
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
// 
//    /**
//     * Show the grid if boolean is true. 
//     * Hide the grid if boolean is false.
//     * 
//     * @param b
//     */
//    public void showGrid(boolean b);
//    
//
//    /**
//     * Activate the grid if boolean is true.
//     * Deactivate the grid if boolean is false.
//     * 
//     * @param b
//     */
//    public void setGridActive(boolean b);
//    
//
//    /**
//     * @return true if the grid is visible
//     */
//    public boolean isGridDisplayed();
//    
//    
//    /**
//     * @return true if the grid is visible
//     */
//    public abstract boolean isGridActive();
//
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
