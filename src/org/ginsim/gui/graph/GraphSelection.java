package org.ginsim.gui.graph;

import java.util.List;

import org.ginsim.common.utils.log.LogManager;
import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.gui.shell.editpanel.SelectionType;

/**
 * Manage the selection for a GraphGUI
 * 
 * @author Aurelien Naldi
 *
 * @param <V>
 * @param <E>
 */
public class GraphSelection<V, E extends Edge<V>> {

	private SelectionType type = SelectionType.SEL_NONE;
	private int nodeCount;
	private int edgeCount;

	private final GraphGUI<Graph<V,E>, V, E> gui;
	private List<V> nodes = null;
	private List<E> edges = null;
	
	public GraphSelection(GraphGUI gui) {
		this.gui = gui;
		updateType();
	}

	public SelectionType getSelectionType() {
		return type;
	}
	
	public List<E> getSelectedEdges() {
		return edges;
	}
	public List<V> getSelectedNodes() {
		return nodes;
	}
	

	/**
	 * Set the selection.
	 * This method should only be called by the GraphGUI to propagate interactive selection changes
	 *  
	 * @param nodes
	 * @param edges
	 */
	public void setSelection(List<V> nodes, List<E> edges) {
		this.nodes = nodes;
		this.edges = edges;
		
		updateType();
	}
	
	private void updateType() {
		if (edges == null || edges.size() == 0) {
			edges = null;
			edgeCount = 0;
		} else {
			edgeCount = edges.size();
		}
		
		if (nodes == null || nodes.size() == 0) {
			nodes = null;
			nodeCount = 0;
		} else {
			nodeCount = nodes.size();
		}
		
		type = findType();
	}

	/**
	 * @return the current type of selection
	 */
	private SelectionType findType() {
		if (edgeCount == 0) {
			if (nodeCount == 0) {
				return SelectionType.SEL_NONE;
			}
			if (nodeCount == 1) {
				return SelectionType.SEL_NODE;
			} 
			return SelectionType.SEL_MULTIPLE;
		} 
		
		if (edgeCount == 1) {
			if (nodeCount == 0) {
				return SelectionType.SEL_EDGE;
			}
			return SelectionType.SEL_MULTIPLE;
		}
		return SelectionType.SEL_MULTIPLE;
	}

	
	
	// TODO : REFACTORING ACTION
	// TODO: add methods to change the selection
	public void selectNodes(List l) {
		LogManager.error("Select vertices not implemented");
	}
	
	
//	/**
//	 * Select all the graph objects (vertices and edges)
//	 * 
//	 */
//   public void selectAll();
//	
//	
//   /**
//    * Select the graph objects in the List.
//    * 
//    * @param l the list of objects to select
//    */
//   public void select(Collection l);
//   
//   
//   /**
//    * Add the graph objects of the List to the selected objects
//    * 
//    * @param l the list of objects to select
//    */
//   public void addSelection(List l);

}
