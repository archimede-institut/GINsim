package org.ginsim.servicegui.tool.localgraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ginsim.common.application.GsException;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.css.CascadingStyleSheetManager;
import org.ginsim.service.tool.reg2dyn.updater.SimulationUpdater;

/**
 * 
 * Search all the non functional interactions in the graph 'g' and do some
 * actions on them depending on the options (opt_*).
 * 
 */
public class LocalGraph {
	private RegulatoryGraph regGraph;
	private CascadingStyleSheetManager cs = null;
	private LocalGraphSelector selector = null;
	private List<byte[]> states;
	private HashMap<RegulatoryMultiEdge, String> functionalityMap;
	private SimulationUpdater updater;

	public LocalGraph(RegulatoryGraph regGraph) throws GsException {
		this.regGraph = regGraph;
	}

	public LocalGraph(RegulatoryGraph regGraph, List<byte[]> states)
			throws GsException {
		this(regGraph);
		this.states = states;
	}

	public void setUpdater(SimulationUpdater updater) {
		this.updater = updater;
	}

	public void setState(byte[] state) {
		this.states = new ArrayList<byte[]>(1);
		this.states.add(state);
	}

	public void setStates(List<byte[]> states) {
		this.states = states;
	}

	public void run() {
		functionalityMap = new HashMap<RegulatoryMultiEdge, String>();
		selector = new LocalGraphSelector();
		selector.setCache(functionalityMap);

		for (byte[] state : states) {
			byte[] fstate = f(state);
			for (RegulatoryMultiEdge edge : regGraph.getEdges()) {
				RegulatoryNode source = edge.getSource();
				RegulatoryNode target = edge.getTarget();
				int i = regGraph.getNodeOrder().indexOf(source);
				int j = regGraph.getNodeOrder().indexOf(target);
				// Independently of being Boolean of multi-valued
				if (state[i] < source.getMaxValue()) {
					updateEdge(edge, state, fstate, i, j, 1);
				}
				if (state[i] > 0) {
					updateEdge(edge, state, fstate, i, j, -1);
				}
			}
		}
	}
	
	public String print(byte[] x) {
		String s = "";
		for (int i = 0 ; i < x.length ; i++) s+= x[i];
		return s;
	}
	private void updateEdge(RegulatoryMultiEdge edge, byte[] state,
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

	/**
	 * Colorize the edges in the Set nonFunctionalInteractions.
	 */
	public void doColorize() {
		if (functionalityMap == null) {
			return;
		}
		if (cs == null) {
			cs = new CascadingStyleSheetManager(true);
		} else {
			cs.shouldStoreOldStyle = false;
		}

		EdgeAttributesReader ereader = regGraph.getEdgeAttributeReader();
		for (RegulatoryMultiEdge me : regGraph.getEdges()) {
			ereader.setEdge(me);
			cs.applyOnEdge(selector, me, ereader);
		}

	}

	public void undoColorize() {
		cs.restoreAllEdges(regGraph.getEdges(),
				regGraph.getEdgeAttributeReader());
	}

	protected void finalize() {
		if (functionalityMap != null) {
			if (selector != null) {
				selector.flush(); // remove nonFunctionalInteractions from the
									// cache.
			}
		}
	}

	public Map<RegulatoryMultiEdge, String> getFunctionality() {
		return functionalityMap;
	}

}
