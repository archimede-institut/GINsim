package org.ginsim.service.tool.composition;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.ginsim.common.application.GsException;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;

/*
 * The mapping of the input components
 */
public class IntegrationFunctionMapping {
	private HashMap<RegulatoryNode, RegulatoryIntegration> mapping = new HashMap<RegulatoryNode, RegulatoryIntegration>();

	public IntegrationFunctionMapping() {
	}

	public void addMapping(RegulatoryNode input,
			List<RegulatoryNode> properList,
			IntegrationFunction integrationFunction) throws GsException {
		if (!input.isInput()) {
			throw new GsException(GsException.GRAVITY_ERROR,
					"Only input components can be mapped");
		}
		Iterator<RegulatoryNode> iterator = properList.iterator();
		while (iterator.hasNext()) {
			if (iterator.next().isInput()) {
				throw new GsException(GsException.GRAVITY_ERROR,
						"Only proper components can be arguments of integration functions");
			}
		}

		RegulatoryIntegration regulatoryIntegration = new RegulatoryIntegration(
				integrationFunction, properList);
		mapping.put(input, regulatoryIntegration);

	}

	public IntegrationFunction getIntegrationFunctionForInput(
			RegulatoryNode input) {
		RegulatoryIntegration value = mapping.get(input);
		if (value == null) {
			return null;
		}
		return value.getIntegrationFunction();
	}

	public List<RegulatoryNode> getProperComponentsForInput(RegulatoryNode input) {
		RegulatoryIntegration value = mapping.get(input);
		if (value == null) {
			return null;
		}
		return value.getProperComponents();
	}

	public boolean isMapped(RegulatoryNode input) {
		return mapping.containsKey(input);
	}
	
	public Collection<RegulatoryNode> getMappedInputs(){
		return mapping.keySet();
	}

}
