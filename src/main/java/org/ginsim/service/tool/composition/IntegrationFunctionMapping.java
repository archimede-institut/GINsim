package org.ginsim.service.tool.composition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.ginsim.common.application.GsException;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;

/**
 * The mapping of the input components
 * 
 * @author Nuno D. Mendes
 */
public class IntegrationFunctionMapping {
	private HashMap<RegulatoryNode, RegulatoryIntegration> mapping = new HashMap<RegulatoryNode, RegulatoryIntegration>();

	public IntegrationFunctionMapping() {
	}

	/**
	 * @param input An input component being mapped
	 * 
	 * @param properList A list of proper components the input is being mapped
	 * to
	 * 
	 * @param integrationFunction A representation of the logical function that
	 * determines the value of the input based on the value of the mapped proper
	 * components
	 */
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

	/**
	 * @param input An input components
	 * 
	 * @return The integration function that is used to compute the value of the
	 * input based on the value of the mapped proper components
	 */
	public IntegrationFunction getIntegrationFunctionForInput(
			RegulatoryNode input) {
		RegulatoryIntegration value = mapping.get(input);
		if (value == null) {
			return null;
		}
		return value.getIntegrationFunction();
	}

	/**
	 * @param input An input components
	 * 
	 * @return The list of proper components the input is mapped to
	 */
	public List<RegulatoryNode> getProperComponentsForInput(RegulatoryNode input) {
		RegulatoryIntegration value = mapping.get(input);
		if (value == null) {
			return null;
		}
		return value.getProperComponents();
	}

	/**
	 * @param input An input components
	 * 
	 * @return True if the input is mapped, false otherwise
	 */
	public boolean isMapped(RegulatoryNode input) {
		return mapping.containsKey(input);
	}

	/**
	 *  Indicates whether a proper components influence an integration functions
	 *  
	 * @param proper the proper component
	 * @return TRUE if the proper components does influence an integration function, FALSE otherwise
	 */
	public boolean isMappedTo(RegulatoryNode proper){
		if (proper.isInput())
			return false;
	
		for (RegulatoryIntegration regulation : mapping.values())
			if (regulation.getProperComponents().contains(proper))
				return true;
		
		return false;
		
	
	}
	
	/**
	 * Returns the list of input components whose value is influenced by the given proper component
	 * 
	 * @param proper the proper components
	 * @return a collection of input components 
	 */
			
	public Collection<RegulatoryNode> getInfluencedInputs(RegulatoryNode proper){
		ArrayList<RegulatoryNode> listInputs = new ArrayList<RegulatoryNode>();
		
		if (proper.isInput())
			return listInputs;
		
		for (RegulatoryNode input : mapping.keySet()){
			RegulatoryIntegration regulation = mapping.get(input);
			if (regulation.getProperComponents().contains(proper))
				listInputs.add(input);
		}
		
		return listInputs;
	}
	
	
	
	/**
	 * @return A collection of all input components that are mapped
	 */
	public Collection<RegulatoryNode> getMappedInputs() {
		return mapping.keySet();
	}

}
