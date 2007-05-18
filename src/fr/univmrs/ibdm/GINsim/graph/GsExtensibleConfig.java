package fr.univmrs.ibdm.GINsim.graph;

public class GsExtensibleConfig {

	GsGraph graph;
	Object specificConfig = null;

	public GsExtensibleConfig(GsGraph graph) {
		this.graph = graph;
	}
	public Object getSpecificConfig() {
		return specificConfig;
	}
	
	public void setSpecificConfig(Object specificConfig) {
		this.specificConfig = specificConfig;
	}
	
	public GsGraph getGraph() {
		return graph;
	}
}
