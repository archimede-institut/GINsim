package org.ginsim.service.tool.connectivity;

import java.util.List;

import org.ginsim.core.graph.reducedgraph.NodeReducedData;

/**
 * Store the result of the connectivity Algorithm :
 * 	 - The list of components : List<NodeReducedData> components
 */
public class ConnectivityResult {
	private List<NodeReducedData> components;
	private ConnectivityAlgo algo;

	public ConnectivityResult() {
		this.components = null;
	}

	/**
	 * define the set o components
	 * @param components
	 */
	protected void setComponents(List<NodeReducedData> components) {
		this.components = components;
	}

	/**
	 * Return the set of components (SCC) computed by the algorithm
	 * @return the set of components (SCC) computed by the algorithm
	 */
	public List<NodeReducedData> getComponents() {
		return components;
	}

	/**
	 * Define the algo that generates this result
	 * @param algo
	 */
	protected void setAlgo(ConnectivityAlgo algo) {
		this.algo = algo;
	}

	/**
	 * Cancel the algo if its running
	 */
	public void cancel() {
		if (algo!= null) {
			algo.cancel();
		}
	}

	/**
	 * Call at the end of the run() method, it remove the reference to the algo.
	 */
	public void algoIsComputed() {
		this.algo = null;
	}
}
