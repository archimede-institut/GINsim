package org.ginsim.gui.graph;

import org.ginsim.core.graph.view.NodeAttributesReader;

public abstract class AddNodeAction<V> extends EditAction {

	private final NodeAttributesReader reader;
	
	public AddNodeAction(String name, NodeAttributesReader reader) {
		this(name, reader, null);
	}
	public AddNodeAction(String name, NodeAttributesReader reader, String icon) {
		super(EditMode.NODE, name, icon);
		this.reader = reader;
	}
	
	public void addNode( EditActionManager manager, int x, int y) {
		V vertex = getNewNode();
		reader.setNode(vertex);
		reader.setPos(x, y);
		manager.select(vertex);
		manager.actionPerformed(this);
	}
	
	abstract protected V getNewNode();
}
