package org.ginsim.service.tool.composition;

public class CompositionConfig {
	private Topology topology = null;
	private IntegrationFunctionMapping mapping = null;
	private boolean reduce = true;
	
	public Topology getTopology() {
		return topology;
	}
	public void setTopology(Topology topology) {
		this.topology = topology;
	}
	public IntegrationFunctionMapping getMapping() {
		return mapping;
	}
	public void setMapping(IntegrationFunctionMapping mapping) {
		this.mapping = mapping;
	}
	public boolean isReduce() {
		return reduce;
	}
	public void setReduce(boolean reduce) {
		this.reduce = reduce;
	}
	
	

}
