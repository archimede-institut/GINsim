package org.ginsim.graph.backend;

import fr.univmrs.tagc.GINsim.graph.GsEdgeAttributesReader;
import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;

public interface GraphViewBackend{

	public GsEdgeAttributesReader getEdgeReader();
	public GsVertexAttributesReader getVertexReader();

}
