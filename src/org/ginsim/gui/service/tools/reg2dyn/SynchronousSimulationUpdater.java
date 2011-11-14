package org.ginsim.gui.service.tools.reg2dyn;

import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.GsRegulatoryMutantDef;

public class SynchronousSimulationUpdater extends SimulationUpdater {

	public SynchronousSimulationUpdater(GsRegulatoryGraph regGraph, GsSimulationParameters params) {
		super(regGraph, params);
	}

	public SynchronousSimulationUpdater(GsRegulatoryGraph regGraph, GsRegulatoryMutantDef mutant) {
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

