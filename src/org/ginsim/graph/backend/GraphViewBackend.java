package org.ginsim.graph.backend;

import org.ginsim.graph.common.EdgeAttributesReader;
import org.ginsim.graph.common.NodeAttributesReader;

public interface GraphViewBackend{

	public EdgeAttributesReader getEdgeAttributeReader();
	
	public NodeAttributesReader getVertexAttributeReader();
	
	public void addViewListener(GraphViewListener listener);

	public void refresh(Object vertex);

}
