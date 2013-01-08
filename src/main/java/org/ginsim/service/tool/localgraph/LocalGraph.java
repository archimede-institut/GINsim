package org.ginsim.service.tool.localgraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.service.tool.reg2dyn.updater.SimulationUpdater;
import org.ginsim.servicegui.tool.localgraph.LocalGraphSelector;

/**
 * 
 * Search all the non functional interactions in the graph 'g' and do some
 * actions on them depending on the options (opt_*).
 * 
 */
public class LocalGraph {
	private RegulatoryGraph regGraph;
	private List<byte[]> states;
	private SimulationUpdater updater;

	public LocalGraph(RegulatoryGraph regGraph) {
		this(regGraph, new ArrayList<byte[]>());
	}

	public LocalGraph(RegulatoryGraph regGraph, List<byte[]> states) {
		this.regGraph = regGraph;
		this.states = states;
	}

	public void setUpdater(SimulationUpdater updater) {
		this.updater = updater;
	}

	public void setStates(List<byte[]> states) {
		this.states = states;
	}

	public Map<RegulatoryMultiEdge, String> run() {
		Map<RegulatoryMultiEdge, String> functionalityMap = new HashMap<RegulatoryMultiEdge, String>();

		for (byte[] state : states) {
			byte[] fstate = f(state);
			for (RegulatoryMultiEdge edge : regGraph.getEdges()) {
				RegulatoryNode source = edge.getSource();
				RegulatoryNode target = edge.getTarget();
				int i = regGraph.getNodeOrder().indexOf(source);
				int j = regGraph.getNodeOrder().indexOf(target);
				// Independently of being Boolean of multi-valued
				if (state[i] < source.getMaxValue()) {
					updateEdge(functionalityMap, edge, state, fstate, i, j, 1);
				}
				if (state[i] > 0) {
					updateEdge(functionalityMap, edge, state, fstate, i, j, -1);
				}
			}
		}
		return functionalityMap;
	}

	private void updateEdge(Map<RegulatoryMultiEdge, String> functionalityMap, 
			RegulatoryMultiEdge edge, byte[] state,
			byte[] fstate, int i, int j, int diff) {
		byte[] stateDiff = getStateDiff(state, i, diff);
		byte[] fstateDiff = f(stateDiff);
		if (fstateDiff[j] == fstate[j]) {
			return; // No effect
		}
		String func = functionalityMap.get(edge);

		if ((diff > 0 && fstateDiff[j] > fstate[j])
				|| (diff < 0 && fstateDiff[j] < fstate[j])) {
			if (func == null || func == LocalGraphSelector.CAT_POSITIVE)
				functionalityMap.put(edge, LocalGraphSelector.CAT_POSITIVE);
			else
				functionalityMap.put(edge, LocalGraphSelector.CAT_DUAL);
		} else {
			if (func == null || func == LocalGraphSelector.CAT_NEGATIVE)
				functionalityMap.put(edge, LocalGraphSelector.CAT_NEGATIVE);
			else
				functionalityMap.put(edge, LocalGraphSelector.CAT_DUAL);
		}
	}

	/**
	 * Compute the next state
	 * 
	 * @param x
	 * @return
	 */
	private byte[] f(byte[] x) {
		updater.setState(x, 0, null);
		byte[] fx = updater.nextState();
		if (fx != null)
			return fx;
		return x;
	}

	private byte[] stateClone(byte[] x) {
		byte[] clone = new byte[x.length];
		for (int j = 0; j < clone.length; j++) {
			clone[j] = x[j];
		}
		return clone;
	}

	private byte[] getStateDiff(byte[] x, int i, int diff) {
		byte[] x_b = stateClone(x);
		x_b[i] = (byte) (x_b[i] + diff);
		return x_b;
	}
}
