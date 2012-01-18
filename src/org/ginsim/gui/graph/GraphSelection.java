package org.ginsim.gui.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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


	/**
	 * Select all the edges and 
	 */
	public void selectAll() {
		nodes.addAll(gui.getGraph().getNodes());
		edges.addAll(gui.getGraph().getEdges());
		updateType();
	}
	
	/**
	 * Select all the nodes
	 */
	public void selectAllNodes() {
		nodes.addAll(gui.getGraph().getNodes());
		updateType();
	}

	/**
	 * Select all the edges
	 */
	public void selectAllEdges() {
		edges.addAll(gui.getGraph().getEdges());
		updateType();
	}
	
	/**
	 * Unselect all the edges and nodes
	 */
	public void unselectAll() {
		this.nodes.clear();
		this.edges.clear();
		updateType();
	}

	/**
	 * Unselect all the nodes
	 */
	public void unselectAllNodes() {
		this.nodes.clear();
		updateType();
	}

	/**
	 * Unselect all the edges
	 */
	public void unselectAllEdges() {
		this.edges.clear();
		updateType();
	}

	/**
	 * Add the nodes in l to the list of selected nodes
	 * @param l the list of nodes to select
	 */
	public void addNodesToSelection(Collection<V> l) {
		nodes.addAll(l);
		updateType();
	}

	/**
	 * Add the edges in l to the list of selected edges
	 * @param l the list of edges to select
	 */
	public void addEdgesToSelection(Collection<E> l) {
		edges.addAll(l);
		updateType();
	}

	
	/**
	 * Set the list of selected nodes to the nodes in the list l
	 * @param l the list of nodes to select
	 */
	public void setSelectedNodes(Collection<V> l) {
		unselectAllNodes();
		nodes.addAll(l);
		updateType();
	}
	
	/**
	 * Set the list of selected edges to the edges in the list l
	 * @param l the list of edges to select
	 */
	public void setSelectedEdges(Collection<E> l) {
		unselectAllEdges();
		edges.addAll(l);
		updateType();
	}

	/**
	 * Add all the incoming nodes of the selected nodes to the list of selected nodes
	 */
	public void extendSelectionToIncomingNodes() {
		for (V node : nodes) {
			for (E edge : gui.getGraph().getIncomingEdges(node)) {
				nodes.add(edge.getSource());
			}
		}
		updateType();
	}

	/**
	 * Add all the incoming edges of the selected nodes to the list of selected edges
	 */
	public void extendSelectionToIncomingEdges() {
		for (V node : nodes) {
			addEdgesToSelection(gui.getGraph().getIncomingEdges(node));
		}
		updateType();
	}

	/**
	 * Add all the outgoing nodes of the selected nodes to the list of selected nodes
	 */
	public void extendSelectionToOutgoingNodes() {
		for (V node : nodes) {
			for (E edge : gui.getGraph().getOutgoingEdges(node)) {
				nodes.add(edge.getTarget());
			}
		}
		updateType();
	}

	/**
	 * Add all the outgoing edges of the selected nodes to the list of selected edges
	 */
	public void extendSelectionToOutgoingEdges() {
		for (V node : nodes) {
			addEdgesToSelection(gui.getGraph().getOutgoingEdges(node));
		}
	}

	/**
	 * Invert the selection of nodes and edges
	 */
	public void invertSelection() {
		invertNodesSelection();
		invertEdgesSelection();
		updateType();
	}
		
	/**
	 * Invert the selection of nodes
	 */
	public void invertNodesSelection() {
		nodes = new ArrayList<V>(gui.getGraph().getNodes());
		nodes.removeAll(nodes);
		updateType();
	}

	/**
	 * Invert the selection of edges
	 */
	public void invertEdgesSelection() {
		edges = new ArrayList<E>(gui.getGraph().getEdges());
		edges.removeAll(edges);
		updateType();
	}


}
