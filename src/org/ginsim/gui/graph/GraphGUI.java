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
	 * @return a widget showing the graph
	 */
	public Component getGraphComponent();
	
	
	// the rest is commented out for quick testing...
	
//    /**
//     * select all objects (vertices and edges).
//     */
//    public void selectAll();
//    /**
//     * select some objects (vertices and edges).
//     * @param l the list of objects to select
//     */
//    public void select(List l);
//    /**
//     * select some objects (vertices and edges).
//     * @param s the Set of objects to select
//     */
//    public void select(Set s);
//    /**
//     * select some objects (vertices and edges) and keep the previous selection.
//     * @param l the list of objects to select
//     */
//    public void addSelection(List l);
//    /**
//     * select some objects (vertices and edges) and keep the previous selection.
//     * @param s the Set of objects to select
//     */
//    public void addSelection(Set s);
//    /** 
//     * select a single item.
//     * 
//     * @param obj the item to select 
//     */
//    public void select(Object obj);
//
//    /**
//     * invert the selection.
//     * ie unselect currently selected objects and select all others.
//     *
//     */
//    public void invertSelection();
//    
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
//    
//    
//    /**
//     * show/hide the grid.
//     * @param b
//     */
//    public void showGrid(boolean b);
//
//    /**
//     * make the grid (in)active.
//     * @param b
//     */
//    public void setGridActive(boolean b);
//
//    /**
//     * @return true if the grid is visible
//     */
//    public abstract boolean isGridDisplayed();
//    /**
//     * @return true if the grid is visible
//     */
//    public abstract boolean isGridActive();
//
//    
//    /**
//     * move all vertex to front
//     * @param b
//     */
//    public void vertexToFront(boolean b);
//
//    /**
//     * zoom out the display.
//     */
//    public void zoomOut();
//
//    /**
//     * zoom in the display.
//     */
//    public void zoomIn();
//
//    /**
//     * restore the display to the default zoom level.
//     */
//    public void zoomNormal();
//
//    /**
//     * show/hide edge name on the display.
//     * @param b
//     */
//    public void displayEdgeName(boolean b);
//
//    /**
//     * show/hide vertex name on the display.
//     * @param b
//     */
//    public void displayVertexName(boolean b);
}
