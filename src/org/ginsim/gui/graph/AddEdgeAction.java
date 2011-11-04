package org.ginsim.gui.graph;

import org.ginsim.graph.Edge;

public abstract class AddEdgeAction<V, E extends Edge<V>> extends EditAction {

	public AddEdgeAction(String name) {
		super(EditMode.EDGE, name);
	}
	
	public void addEdge(V from, V to) {
		getNewEdge(from, to);
	}
	
	abstract protected E getNewEdge(V from, V to);

}
