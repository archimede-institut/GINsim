package fr.univmrs.tagc.GINsim.graph;

import org.ginsim.graph.Graph;

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
