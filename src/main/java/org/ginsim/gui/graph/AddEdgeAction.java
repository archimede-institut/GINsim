package org.ginsim.gui.graph;

import org.ginsim.core.graph.Edge;

public abstract class AddEdgeAction<V, E extends Edge<V>> extends EditAction {

	public AddEdgeAction(String name, String icon) {
		super(EditMode.EDGE, name, icon);
	}
	public AddEdgeAction(String name) {
		this(name, null);
	}
	
	public void addEdge( EditActionManager manager, V from, V to) {
		E edge = getNewEdge(from, to);
		manager.select(edge);
		manager.actionPerformed(this);
	}
	
	abstract protected E getNewEdge(V from, V to);

}
