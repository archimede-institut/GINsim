package org.ginsim.gui.graph;

import org.ginsim.graph.common.VertexAttributesReader;

public abstract class AddVertexAction<V> extends EditAction {

	private final VertexAttributesReader reader;
	
	public AddVertexAction(String name, VertexAttributesReader reader) {
		this(name, reader, null);
	}
	public AddVertexAction(String name, VertexAttributesReader reader, String icon) {
		super(EditMode.NODE, name, icon);
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
