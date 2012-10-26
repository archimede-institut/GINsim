package org.ginsim.service.tool.composition;

/**
 * Class representing the specification of parameters for the Composition service
 * 
 * @author Nuno D. Mendes
 */

public class CompositionConfig {
	private Topology topology = null;
	private IntegrationFunctionMapping mapping = null;
	private boolean reduce = true;

	/** @return the topology */
	public Topology getTopology() {
		return topology;
	}

	/** @param topology the topology */
	public void setTopology(Topology topology) {
		this.topology = topology;
	}

	/** @return the mapping */
	public IntegrationFunctionMapping getMapping() {
		return mapping;
	}

	/** @param mapping the mapping */
	public void setMapping(IntegrationFunctionMapping mapping) {
		this.mapping = mapping;
	}

	/** @return whether reduction of the composed graph is to be made */
	public boolean isReduce() {
		return reduce;
	}

	/** @param reduce specifies whether to perform reduction of the composed graph */
	public void setReduce(boolean reduce) {
		this.reduce = reduce;
	}

}
