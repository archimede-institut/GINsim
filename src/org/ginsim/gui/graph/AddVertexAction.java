package org.ginsim.gui.graph;

import org.ginsim.graph.common.VertexAttributesReader;

public abstract class AddVertexAction<V> extends EditAction {

	private final VertexAttributesReader reader;
	
	public AddVertexAction(String name, VertexAttributesReader reader) {
		super(EditMode.NODE, name);
		this.reader = reader;
	}
	
	public void addVertex( EditActionManager manager, int x, int y) {
		V vertex = getNewVertex();
		reader.setVertex(vertex);
		reader.setPos(x, y);
		reader.refresh();
		manager.actionPerformed(this);
	}
	
	abstract protected V getNewVertex();
}
