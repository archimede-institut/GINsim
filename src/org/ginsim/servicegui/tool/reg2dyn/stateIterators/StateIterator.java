package org.ginsim.servicegui.tool.reg2dyn.stateIterators;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.mutant.Perturbation;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;
import org.ginsim.servicegui.tool.reg2dyn.PriorityClassDefinition;
import org.ginsim.servicegui.tool.reg2dyn.Reg2dynPriorityClass;
import org.ginsim.servicegui.tool.reg2dyn.SimulationParameters;

/**
 * An iterator on the successors of a state, from a regulatory graph
 *
 */
public abstract class StateIterator {
	protected OMDDNode[] t_tree;

	protected StateIterator(RegulatoryGraph regGraph, SimulationParameters params) {
        t_tree = regGraph.getParametersForSimulation(true);
        Perturbation mutant = (Perturbation)params.store.getObject(SimulationParameters.MUTANT);
        if (mutant != null) {
            mutant.apply(t_tree, regGraph);
        }
	}

	protected StateIterator(RegulatoryGraph regGraph, Perturbation mutant) {
        t_tree = regGraph.getParametersForSimulation(true);
        if (mutant != null) {
            mutant.apply(t_tree, regGraph);
        }
	}	
	
	/**
	 * Return the call-th the successor of initState.
	 * In asynchronous, call is the index of the gene updated
	 * In synchronous, call should always be 0, as all genes update synchronously
	 * In priority classes its a bit more complex
	 * 
	 * @param initState the initial state
	 * @param call
	 * @return the call-th the successor of initState
	 */
	public abstract  byte[] next(byte[] initState, int call);
	

	/**
	 * Return a new instance of an iterator, it use the simulation parameters to select the proper stateIterator (synchronous, asynchronous...)
	 * It also apply any selected mutant in the params
	 * 
	 * @param regGraph the regulatory graph
	 * @param params the simulation parameters
	 * @return a new instance of an iterator
	 */
	public static StateIterator getNewInstance(RegulatoryGraph regGraph, SimulationParameters params) {
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
	
	public AsynchronousStateIterator(RegulatoryGraph regGraph, Perturbation mutant) {
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

//TODO : port the other states iterators