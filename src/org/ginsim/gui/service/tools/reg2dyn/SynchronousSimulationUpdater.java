package org.ginsim.gui.service.tools.reg2dyn;

import org.ginsim.graph.regulatorygraph.RegulatoryGraph;

import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.GsRegulatoryMutantDef;

public class SynchronousSimulationUpdater extends SimulationUpdater {

	public SynchronousSimulationUpdater(RegulatoryGraph regGraph, SimulationParameters params) {
		super(regGraph, params);
	}

	public SynchronousSimulationUpdater(RegulatoryGraph regGraph, GsRegulatoryMutantDef mutant) {
		super(regGraph, mutant);
	}

	protected void doBuildNext() {
		next = null;
	}

	protected void doSetState() {
		next = new byte[length];
		boolean hasChange = false;
		// for each node
		for (int i=0 ; i<length ; i++){
		    byte change = (byte) nodeChange(cur_state, i);
		    if (change != 0) {
		    	if (hasChange) {
		    		multiple = true;
		    	}
		    	hasChange = true;
		    	next[i] = (byte) (cur_state[i] + change);
		    } else {
		    	next[i] = cur_state[i];
		    }
		}
		if (!hasChange) {
			next = null;
		}
	}
}

