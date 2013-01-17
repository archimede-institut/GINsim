package org.ginsim.service.tool.composition;

import java.util.List;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;

/**
 * The integration function and proper components associated to an input
 * components
 * 
 * @author Nuno D. Mendes
 */
public class RegulatoryIntegration {

	private IntegrationFunction integrationFunction = null;
	private List<RegulatoryNode> properList = null;

	public RegulatoryIntegration(IntegrationFunction integrationFunction,
			List<RegulatoryNode> properList) {
		this.integrationFunction = integrationFunction;
		this.properList = properList;
	}

	public IntegrationFunction getIntegrationFunction() {
		return this.integrationFunction;
	}

	public List<RegulatoryNode> getProperComponents() {
		return this.properList;
	}

}
