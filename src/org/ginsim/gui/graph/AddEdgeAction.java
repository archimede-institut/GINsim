package org.ginsim.gui.graph;

import org.ginsim.graph.common.Edge;

public abstract class AddEdgeAction<V, E extends Edge<V>> extends EditAction {

	public AddEdgeAction(String name) {
		super(EditMode.EDGE, name);
	}
	
	public void addEdge( EditActionManager manager, V from, V to) {
		getNewEdge(from, to);
		manager.actionPerformed(this);
	}
	
	abstract protected E getNewEdge(V from, V to);

}
