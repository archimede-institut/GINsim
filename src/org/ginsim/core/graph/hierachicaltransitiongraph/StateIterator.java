package org.ginsim.core.graph.hierachicaltransitiongraph;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.mutant.RegulatoryMutantDef;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;
import org.ginsim.servicegui.tool.reg2dyn.PriorityClassDefinition;
import org.ginsim.servicegui.tool.reg2dyn.Reg2dynPriorityClass;
import org.ginsim.servicegui.tool.reg2dyn.SimulationParameters;


public abstract class StateIterator {
	protected OMDDNode[] t_tree;

	public StateIterator(RegulatoryGraph regGraph, SimulationParameters params) {
        t_tree = regGraph.getParametersForSimulation(true);
        RegulatoryMutantDef mutant = (RegulatoryMutantDef)params.store.getObject(SimulationParameters.MUTANT);
        if (mutant != null) {
            mutant.apply(t_tree, regGraph);
        }
	}

	public StateIterator(RegulatoryGraph regGraph, RegulatoryMutantDef mutant) {
        t_tree = regGraph.getParametersForSimulation(true);
        if (mutant != null) {
            mutant.apply(t_tree, regGraph);
        }
	}	
	
	public abstract  byte[] next(byte[] initState, int call);
	

	public static StateIterator getInstance(RegulatoryGraph regGraph, SimulationParameters params) {
		PriorityClassDefinition pcdef = params.getPriorityClassDefinition();
		if (pcdef.getNbElements(null) < 2) {
			Reg2dynPriorityClass pc = (Reg2dynPriorityClass)pcdef.getElement(null,0);
			if (pc.getMode() == Reg2dynPriorityClass.SYNCHRONOUS) {
				return null;//new SynchronousSimulationUpdater(regGraph, params);
			}
			return new AsynchronousStateIterator(regGraph, params);
		}
		return null;//new PrioritySimulationUpdater(regGraph, params);
	}

	public abstract boolean hasNext(byte[] state, int call);
	
}

class AsynchronousStateIterator extends StateIterator {
	
	public AsynchronousStateIterator(RegulatoryGraph regGraph, RegulatoryMutantDef mutant) {
		super(regGraph, mutant);
	}
	public AsynchronousStateIterator(RegulatoryGraph regGraph, SimulationParameters params) {
		super(regGraph, params);
	}
	
	public byte[] next(byte[] initState, int call) {
		byte[] nextState = (byte[]) initState.clone();
		nextState[call] = nodeChange(initState, call);
		return initState;
	}

	protected byte nodeChange(byte[] initState, int call) {
		byte curState = initState[call];
		byte nextState = t_tree[call].testStatus(initState);

		// now see if the node is willing to change it's state
		if (nextState > curState){
		    return 1;
		} else if (nextState < curState){
		    return -1;
		}
		return 0;
	}
	
	public boolean hasNext(byte[] state, int call) {
		return false;
	}
}