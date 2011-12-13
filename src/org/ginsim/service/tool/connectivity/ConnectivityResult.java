package org.ginsim.service.tool.connectivity;

import java.util.List;

import org.ginsim.core.graph.reducedgraph.NodeReducedData;

/**
 * Store the result of the connectivity Algorithm :
 * 	 - The list of components : List<NodeReducedData> components
 */
public class ConnectivityResult {
	private List<NodeReducedData> components;

	public ConnectivityResult() {
		this.components = null;
	}
	
	public void setComponents(List<NodeReducedData> components) {
		this.components = components;
	}

	public List<NodeReducedData> getComponents() {
		return components;
	}
}
