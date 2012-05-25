package org.ginsim.service.tool.reg2dyn.updater;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.mutant.Perturbation;
import org.ginsim.core.logicalmodel.LogicalModel;
import org.ginsim.service.tool.reg2dyn.SimulationParameters;


public class SynchronousSimulationUpdater extends SimulationUpdater {

	public SynchronousSimulationUpdater(ModelHelper helper) {
		super(helper);
	}
	public SynchronousSimulationUpdater(LogicalModel model) {
		super(model);
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

	@Override
	public SimulationUpdater doClone() {
		return new SynchronousSimulationUpdater(this.modelHelper);
	}
}

