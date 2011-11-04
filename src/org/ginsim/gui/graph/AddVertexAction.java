package org.ginsim.gui.graph;

import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;

public abstract class AddVertexAction<V> extends EditAction {

	private final GsVertexAttributesReader reader;
	
	public AddVertexAction(String name, GsVertexAttributesReader reader) {
		super(EditMode.NODE, name);
		this.reader = reader;
	}
	
	public void addVertex(int x, int y) {
		V vertex = getNewVertex();
		reader.setVertex(vertex);
		reader.setPos(x, y);
	}
	
	abstract protected V getNewVertex();
}
