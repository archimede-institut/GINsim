package org.ginsim.core.graph.view;

import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;

@Deprecated
public class GraphicalAttributesStore {

	public EdgeAttributesReader ereader;
	public NodeAttributesReader vreader;

	public GraphicalAttributesStore( Graph graph) {
		
		this.ereader = graph.getEdgeAttributeReader();
		this.vreader = graph.getNodeAttributeReader();
	}
	
	public void storeAll() {
	}
	
	public void restoreAll() {
	}
	
	public void ensureStoreNode(Object o) {
	}
	
	public void ensureStoreEdge(Edge edge) {
	}
	
	public void restore(Object o) {
	}	
}
