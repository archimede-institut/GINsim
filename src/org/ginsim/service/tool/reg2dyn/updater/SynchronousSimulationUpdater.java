package org.ginsim.service.tool.reg2dyn.updater;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.mutant.Perturbation;
import org.ginsim.service.tool.reg2dyn.SimulationParameters;


public class SynchronousSimulationUpdater extends SimulationUpdater {

	public SynchronousSimulationUpdater(RegulatoryGraph regGraph, SimulationParameters params) {
		super(regGraph, params);
	}

	public SynchronousSimulationUpdater(RegulatoryGraph regGraph, Perturbation mutant) {
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
