package org.ginsim.core.graph;

import org.ginsim.core.graph.common.Graph;

public class GsExtensibleConfig<G extends Graph> {

	G graph;
	Object specificConfig = null;

	public GsExtensibleConfig( G graph) {
		this.graph = graph;
	}
	public Object getSpecificConfig() {
		return specificConfig;
	}
	
	public void setSpecificConfig(Object specificConfig) {
		this.specificConfig = specificConfig;
	}
	
	public G getGraph() {
		
		return graph;
	}
}
