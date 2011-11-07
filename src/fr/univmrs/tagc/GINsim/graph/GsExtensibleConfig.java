package fr.univmrs.tagc.GINsim.graph;

import org.ginsim.graph.Graph;

public class GsExtensibleConfig {

	Graph<?,?> graph;
	Object specificConfig = null;

	public GsExtensibleConfig( Graph<?,?> graph) {
		this.graph = graph;
	}
	public Object getSpecificConfig() {
		return specificConfig;
	}
	
	public void setSpecificConfig(Object specificConfig) {
		this.specificConfig = specificConfig;
	}
	
	public Graph<?,?> getGraph() {
		
		return graph;
	}
}
