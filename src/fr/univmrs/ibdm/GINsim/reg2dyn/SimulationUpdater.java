package fr.univmrs.ibdm.GINsim.reg2dyn;

import java.util.Iterator;

import fr.univmrs.ibdm.GINsim.dynamicGraph.GsDynamicNode;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.OmddNode;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.mutant.GsRegulatoryMutantDef;

/**
 * This is the part of the simulation in charge for generating the following states.
 *
 * supported methods are:
 * <ul>
 * 	<li>synchronous search: all changes at once, each state has at most one successor</li>
 * 	<li>asynchronous search: changes one by one, each state can have many successors</li>
 * 	<li>by priority class: genes are classed by priority groups.
 *  <ul>
 *      <li>only changes on the higher priority group(s) containing changing genes will be applied.</li>
 *      <li>if this concerns several groups, changes corresponding to each group will be applied asynchronously</li>
 *      <li>each group can be either synchronous or asynchronous, changes will be applied all at once or one by one, depending on the group!</li>
 *      <li>+1 and -1 transitions can be separated to create really fine-tuned groups</li>
 *  </ul></li>
 * </ul>
 */
abstract public class SimulationUpdater implements Iterator {
	private OmddNode[] t_tree;
	int length;
	int[] cur_state;
	int[] next = null;
	int depth;
	GsDynamicNode node;
	boolean multiple;
	
	public SimulationUpdater(GsRegulatoryGraph regGraph, GsSimulationParameters params) {
        t_tree = regGraph.getParametersForSimulation(true);
        GsRegulatoryMutantDef mutant = (GsRegulatoryMutantDef)params.store.getObject(GsSimulationParameters.MUTANT);
        if (mutant != null) {
            mutant.apply(t_tree, regGraph);
        }
		this.length = t_tree.length;
	}
	
	public boolean hasNext() {
		return next != null;
	}
	public Object next() {
		SimulationQueuedState ret = new SimulationQueuedState(next, depth+1, node, multiple);
		multiple = false;
		doBuildNext();
		return ret;
	}
	public void remove() {
	}

	/**
	 * set the state which should be used as basis.
	 * @param node 
	 * @param depth 
	 * 
	 * @param t_tree
	 */
	void setState(int[] state, int depth, GsDynamicNode node) {
		this.cur_state = state;
		this.depth = depth;
		this.node = node;
		multiple = false;
		doSetState();
	}
	
	abstract protected void doSetState();
	abstract protected void doBuildNext();
	
	/**
	 * get change step for a gene
	 *
	 * @param initState
	 * @param i index of the gene to test
	 * @return the direction in which the gene want to change: 0 for no change, 1 for increase and -1 for decrease
	 */
	protected int nodeChange(int[] initState, int i) {
        int curState = initState[i];
		int nextState = t_tree[i].testStatus(initState);

		// now see if the node is willing to change it's state
		if (nextState > curState){
		    return 1;
		} else if (nextState < curState){
		    return -1;
		}
		return 0;
	}
	
	static public SimulationUpdater getInstance(GsRegulatoryGraph regGraph, GsSimulationParameters params) {
		switch (params.mode) {
			case Simulation.SEARCH_SYNCHRONE:
				return new SynchronousSimulationUpdater(regGraph, params);
			case Simulation.SEARCH_BYPRIORITYCLASS:
				return new PrioritySimulationUpdater(regGraph, params);
			default:
				return new AsynchronousSimulationUpdater(regGraph, params);
		}
	}
}


/* **************************** Synchronous ***********************************/

class SynchronousSimulationUpdater extends SimulationUpdater {

	public SynchronousSimulationUpdater(GsRegulatoryGraph regGraph, GsSimulationParameters params) {
		super(regGraph, params);
	}

	protected void doBuildNext() {
		next = null;
	}

	protected void doSetState() {
		next = new int[length];
		boolean hasChange = false;
		// for each node
		for (int i=0 ; i<length ; i++){
		    int change = nodeChange(cur_state, i);
		    if (change != 0) {
		    	if (hasChange) {
		    		multiple = true;
		    	}
		    	hasChange = true;
		    	next[i] = cur_state[i] + change;
		    } else {
		    	next[i] = cur_state[i];
		    }
		}
		if (!hasChange) {
			next = null;
		}
	}
}


/* **************************** Asynchronous ***********************************/

class AsynchronousSimulationUpdater extends SimulationUpdater {

	int nextChange = -1;
	int nextUpdate;

	public AsynchronousSimulationUpdater(GsRegulatoryGraph regGraph, GsSimulationParameters params) {
		super(regGraph, params);
	}

	protected void doBuildNext() {
		if (nextChange == -1) {
			next = null;
			return;
		}
		next = (int[])cur_state.clone();
		next[nextChange] += nextUpdate;
		int i = nextChange + 1;
		nextChange = -1;
		for ( ; i<length ; i++){
			int change = nodeChange(cur_state, i);
			if (change != 0) {
				nextChange = i;
				nextUpdate = change;
				break;
			}
		}
	}

	protected void doSetState() {
		next = (int[])cur_state.clone();
		nextChange = -1;
		int i = 0;
		for ( ; i<length ; i++){
			int change = nodeChange(cur_state, i);
			if (change != 0) {
				nextChange = i;
				next[i] += change;
				break;
			}
		}
		if (nextChange == -1) {
			next = null;
			return;
		}
		i = nextChange + 1;
		nextChange = -1;
		for ( ; i<length ; i++){
			int change = nodeChange(cur_state, i);
			if (change != 0) {
				nextChange = i;
				nextUpdate = change;
				break;
			}
		}
	}
}


/* **************************** by priority ***********************************/

class PrioritySimulationUpdater extends SimulationUpdater {
	
	int[][] pclass;
    int[] classChangesList;
    int nextIndex;
    int curClass;
    int priority;

	
    public PrioritySimulationUpdater(GsRegulatoryGraph regGraph, GsSimulationParameters params) {
		super(regGraph, params);
		pclass = params.getPclass();
	}

	protected void doBuildNext() {
		
		if (classChangesList != null && nextIndex < classChangesList.length) {
			// we are processing an asynchronous class, continue this
	        next = (int[])cur_state.clone();
	        next[classChangesList[nextIndex++]] += classChangesList[nextIndex++];
	        return;
		}
		
		int change;
		int[] tclass;
        int[] tchanges = null;
		int p;
		classChangesList = null;
        while(++curClass < pclass.length && pclass[curClass][0] == priority) {
            tclass = pclass[curClass];
            p = 0;
            tchanges = new int[tclass.length*2-1];
            tchanges[p++] = tclass[1];
            for (int j=2 ; j<tclass.length ; j+=2) {
                change = nodeChange(cur_state, tclass[j]);
                if (change != 0 && change+tclass[j+1] != 0) {
                    tchanges[p++] = tclass[j];
                    tchanges[p++] = change;
                }
            }
            if (p>1) {
                // copy the useful part of tchanges in the returned array
                classChangesList = new int[p];
                for (int a=0 ; a<p ; a++) {
                    classChangesList[a] = tchanges[a];
                }
                break;
            }
        }
        
        /* *********************************************
         * build the next state from the list of changes
         */
        if (classChangesList == null) {
        	next = null;
        	return;
        }
        next = (int[])cur_state.clone();
        nextIndex = 1;
        if (classChangesList[0] == GsReg2dynPriorityClass.SYNCHRONOUS) {
        	for ( ; nextIndex<classChangesList.length ; nextIndex++) {
        		next[classChangesList[nextIndex++]] += classChangesList[nextIndex];
        	}
        	multiple = classChangesList.length > 1;
        } else {
        	next[classChangesList[1]] += classChangesList[2];
        	nextIndex = 3;
        }
	}

	protected void doSetState() {
		/* ****************************
		 *  build the list of changes
		 */
		classChangesList = null;
		priority = 0;
        int change = 0;
        boolean changed = false;
        curClass = 0;
        int j = 0;
        int[] tchanges = null;
        int[] tclass = null;

        // look for the first changing priority class
        for ( ; curClass<pclass.length ; curClass++) {
            tclass = pclass[curClass];

            for (j=2 ; j<tclass.length ; j+=2) {
                change = nodeChange(cur_state, tclass[j]);
                if (change != 0 && change+tclass[j+1] != 0) {
                    changed = true;
                    break;
                }
            }
            if (changed) {
                priority = tclass[0];
                break;
            }
        }
        // if something changes, analyse the rest of the corresponding priority class
        // AND of the next priority classes with the same priority
        if (changed && tclass != null) {
            int p = 0;
            tchanges = new int[tclass.length*2-1];
            tchanges[p++] = tclass[1];
            tchanges[p++] = tclass[j];
            tchanges[p++] = change;

            for (j+=2 ; j<tclass.length ; j+=2) {
                change = nodeChange(cur_state, tclass[j]);
                if (change != 0 && change+tclass[j+1] != 0) {
                    tchanges[p++] = tclass[j];
                    tchanges[p++] = change;
                }
            }

            // copy the usefull part of tchanges in the returned array
            classChangesList = new int[p];
            for (int a=0 ; a<p ; a++) {
                classChangesList[a] = tchanges[a];
            }
        }
        
        /* *********************************************
         * build the next state from the list of changes
         */
        if (classChangesList == null) {
        	next = null;
        	return;
        }
        next = (int[])cur_state.clone();
        nextIndex = 1;
        if (classChangesList[0] == GsReg2dynPriorityClass.SYNCHRONOUS) {
        	for ( ; nextIndex<classChangesList.length ; nextIndex++) {
        		next[classChangesList[nextIndex++]] += classChangesList[nextIndex];
        	}
        	multiple = classChangesList.length > 1;
        } else {
        	next[classChangesList[1]] += classChangesList[2];
        	nextIndex = 3;
        }
	}
}
