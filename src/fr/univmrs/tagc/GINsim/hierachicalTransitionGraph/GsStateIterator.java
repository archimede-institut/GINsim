package fr.univmrs.tagc.GINsim.hierachicalTransitionGraph;

import org.ginsim.gui.service.tools.reg2dyn.GsReg2dynPriorityClass;
import org.ginsim.gui.service.tools.reg2dyn.GsSimulationParameters;
import org.ginsim.gui.service.tools.reg2dyn.PriorityClassDefinition;

import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.OmddNode;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.GsRegulatoryMutantDef;

public abstract class GsStateIterator {
	protected OmddNode[] t_tree;

	public GsStateIterator(GsRegulatoryGraph regGraph, GsSimulationParameters params) {
        t_tree = regGraph.getParametersForSimulation(true);
        GsRegulatoryMutantDef mutant = (GsRegulatoryMutantDef)params.store.getObject(GsSimulationParameters.MUTANT);
        if (mutant != null) {
            mutant.apply(t_tree, regGraph);
        }
	}

	public GsStateIterator(GsRegulatoryGraph regGraph, GsRegulatoryMutantDef mutant) {
        t_tree = regGraph.getParametersForSimulation(true);
        if (mutant != null) {
            mutant.apply(t_tree, regGraph);
        }
	}	
	
	public abstract  byte[] next(byte[] initState, int call);
	

	public static GsStateIterator getInstance(GsRegulatoryGraph regGraph, GsSimulationParameters params) {
		PriorityClassDefinition pcdef = params.getPriorityClassDefinition();
		if (pcdef.getNbElements(null) < 2) {
			GsReg2dynPriorityClass pc = (GsReg2dynPriorityClass)pcdef.getElement(null,0);
			if (pc.getMode() == GsReg2dynPriorityClass.SYNCHRONOUS) {
				return null;//new SynchronousSimulationUpdater(regGraph, params);
			}
			return new AsynchronousStateIterator(regGraph, params);
		}
		return null;//new PrioritySimulationUpdater(regGraph, params);
	}

	public abstract boolean hasNext(byte[] state, int call);
	
}

class AsynchronousStateIterator extends GsStateIterator {
	
	public AsynchronousStateIterator(GsRegulatoryGraph regGraph, GsRegulatoryMutantDef mutant) {
		super(regGraph, mutant);
	}
	public AsynchronousStateIterator(GsRegulatoryGraph regGraph, GsSimulationParameters params) {
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