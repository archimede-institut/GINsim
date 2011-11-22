package org.ginsim.graph.backend;

import org.ginsim.graph.common.EdgeAttributesReader;
import org.ginsim.graph.common.VertexAttributesReader;

public interface GraphViewBackend{

	public EdgeAttributesReader getEdgeAttributeReader();
	
	public VertexAttributesReader getVertexAttributeReader();
	
	public void addViewListener(GraphViewListener listener);

	public void refresh(Object vertex);

}
