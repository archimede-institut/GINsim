package fr.univmrs.ibdm.GINsim.reg2dyn;

import java.util.Iterator;
import java.util.Vector;

import fr.univmrs.ibdm.GINsim.dynamicGraph.GsDynamicGraph;
import fr.univmrs.ibdm.GINsim.dynamicGraph.GsDynamicNode;
import fr.univmrs.ibdm.GINsim.global.GsEnv;
import fr.univmrs.ibdm.GINsim.global.GsException;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.ibdm.GINsim.manageressources.Translator;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsGenericRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.OmddNode;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.initialState.GsInitialState;

/**
 * here are the methods for the construction of the state transition graph
 * for a given regulatory graph.
 *
 * supported methods are:
 * <ul>
 * 	<li>synchronous search: all changes at once, each state has at most one successor</li>
 * 	<li>asynchronous search: changes one by one (depth first or breadth first), each state can have many successors</li>
 * 	<li>by priority class: genes are classed by priority groups.
 *  <ul>
 *      <li>only changes on the higher priority group(s) containing changing genes will be applied.</li>
 *      <li>if this concerns several groups, changes corresponding to each group will be applied asynchronously</li>
 *      <li>each group can be either synchronous or asynchronous, changes will be applied all at once or one by one, depending on the group!</li>
 *      <li>+1 and -1 transitions can be separated to create really fine-tuned groups</li>
 *  </ul></li>
 * </ul>
 * additionally, genes can be blocked at given state (or range of state): once they reached those
 * states, they can't leave them anymore (but can still evolve inside the range).
 */
public final class Simulation extends Thread implements Runnable {

	private GsReg2dynFrame frame;
	private boolean goon = true;
    GsVertexAttributesReader vreader;

	private int maxdepth; 		// limitation of the depth for exploration (all types but BFS)
	private int maxnodes; 		// limitation of the number of nodes for exploration (all types)
	private Vector initStates;
	private Vector listGenes;
	private int[] t_max;
	private int length;
	private boolean ready = false;
	private boolean buildSTG;
	private int nbgc = 0;

	protected static final int SEARCH_SYNCHRONE = 0;
	protected static final int SEARCH_ASYNCHRONE_DF = 1;
	protected static final int SEARCH_ASYNCHRONE_BF = 2;
    protected static final int SEARCH_BYPRIORITYCLASS = 3;

    protected static final String[] MODE_NAMES = new String[] { "synchrone", "asynchrone_df", "asynchrone_bf", "priorityClass" };

	private Vector toExplore = new Vector(); // queue for the BFS exploration

	private int searchMode;

	private int curdepth = 0;
    private int[][] pclass;
	private OmddNode[] t_tree;

	private GsDynamicGraph dynGraph;
	private GsDynamicNode node;
	private OmddNode dd_reachable = OmddNode.TERMINALS[0];
	private OmddNode dd_stable = OmddNode.TERMINALS[0];
    int[] t_state;
    int nbnode = 0;

	/**
	 * Constructs an empty dynamic graph
	 *
	 * @param regGraph the regulatory graph on which we are working
	 * @param frame
	 * @param params
	 */
	protected Simulation(GsGenericRegulatoryGraph regGraph, GsReg2dynFrame frame, GsSimulationParameters params) {
		this.frame = frame;
		listGenes = regGraph.getNodeOrderForSimulation();
		length = listGenes.size();
        buildSTG = params.buildSTG;

        t_max = new int[length];
        for (int i=0 ; i<length ; i++) {
        	t_max[i] = ((GsRegulatoryVertex)listGenes.get(i)).getMaxValue()+1;
        }
        if (buildSTG) {
    		dynGraph = new GsDynamicGraph(listGenes);
    		if (regGraph instanceof GsGraph) {
    			dynGraph.setAssociatedGraph((GsGraph)regGraph);
    		}
            vreader = dynGraph.getGraphManager().getVertexAttributesReader();
    	    vreader.setDefaultVertexSize(5+10*listGenes.size(), 25);
            // add some default comments to the state transition graph
            dynGraph.getAnnotation().setComment(params.getDescr());
        } else {
        	// TODO: build reachability set here
        }

        this.searchMode = params.mode;
        switch (searchMode) {
            case SEARCH_ASYNCHRONE_BF:
            case SEARCH_ASYNCHRONE_DF:
            case SEARCH_BYPRIORITYCLASS:
            case SEARCH_SYNCHRONE:
                break;
            default:
                this.searchMode = SEARCH_ASYNCHRONE_DF;
        }
        switch (searchMode) {
            case SEARCH_BYPRIORITYCLASS:
                pclass = params.getPclass();
                break;
        }

        t_tree = regGraph.getParametersForSimulation(true);
        if (params.mutant != null) {
            params.mutant.apply(t_tree, listGenes, false);
        }
        maxdepth = params.maxdepth;
        maxnodes = params.maxnodes;
        if (maxdepth == 0)  {
            this.maxdepth = Integer.MAX_VALUE;
        } else {
            this.maxdepth = params.maxdepth;
        }
        if (maxnodes == 0)  {
            this.maxnodes = Integer.MAX_VALUE;
        } else {
            this.maxnodes = params.maxnodes;
        }

        // deploy initial states
        if (params.m_initState != null && params.m_initState.size() > 0) {
            this.initStates = new Vector();
            Iterator it = params.m_initState.keySet().iterator();
            while (it.hasNext()) {
                Reg2DynStatesIterator iterator = new Reg2DynStatesIterator(listGenes, ((GsInitialState)it.next()).getMap());
                while (iterator.hasNext()) {
                    initStates.add(iterator.next());
                }
            }
        }
        ready = true;
        start();
	}

	/**
	 * search the full dynamic graph
	 */
	private void runFullGraph() throws GsException {
        // the iterator to construct all initial states
        reg2DynFullIterator iterator = new reg2DynFullIterator(listGenes);
		try {
			//generate all initial states and construct partial graph from all these initial states;
			while(iterator.hasNext()) {
				t_state = iterator.next();

				// verify if nextNode is already in the dynamic graph and construct partial graph
				if(addState(t_state, false, false)){
					switch (searchMode) {
						case SEARCH_SYNCHRONE:
							calcDynGraphSynchro();
							break;
						case SEARCH_ASYNCHRONE_BF:
							if (addState(t_state, true, false)) {
								toExplore.addElement(node);
							}
							break;
						case SEARCH_ASYNCHRONE_DF:
							calcDynGraphAsynchroDepthFirst();
							break;
                        case SEARCH_BYPRIORITYCLASS:
                            calcDynGraphByPriorityClass();
                            break;
					}
				}
				if (searchMode == SEARCH_ASYNCHRONE_BF) {
					calcDynGraphAsynchroBreadthFirst();
				}
				if (!goon) {
					return;
				}
			}
		} catch (OutOfMemoryError e) {
		    GsEnv.error("Out Of Memory", null);
		    return;
		}
	}

	/**
	 * search the dynamic graph for the given initial states
	 * @throws GsException
	 *
	 */
	private void runPartialGraph() throws GsException {
		int[] vstate;

        if (initStates.size() == 0) {
            return;
        }
        if (buildSTG) {
            vstate = (int[])initStates.elementAt(0);
        	dynGraph.addObject("reg2dyn_firstState", vstate);
        }

		for (int i=0 ; i<initStates.size() ; i++) {
			// create the first node and check if already present
			vstate = (int[])initStates.elementAt(i);
			if (vstate.length != length) {
                return;
			}

			if(addState(vstate, false, false)){
				switch (searchMode) {
					case SEARCH_SYNCHRONE:
						calcDynGraphSynchro();
						break;
					case SEARCH_ASYNCHRONE_BF:
						if (addState(vstate, true, false)) {
							toExplore.addElement(node);
						}
						break;
					case SEARCH_ASYNCHRONE_DF:
						calcDynGraphAsynchroDepthFirst();
						break;
                    case SEARCH_BYPRIORITYCLASS:
                        calcDynGraphByPriorityClass();
                        break;
				}
			}
			if (searchMode == SEARCH_ASYNCHRONE_BF) {
				calcDynGraphAsynchroBreadthFirst();
			}

			if (!goon) {
				return;
			}
		}
	}

	/**
	 * Returns all changing genes and their new value of activation
	 *
	 * @param initState
	 * @return an int[] showing the way genes are willing to move to
	 */
	private int[] getChangingGene(int[] initState){
		int[] t_nextState = new int[length];

		// for each node
		for (int i=0 ; i<length ; i++){
		    t_nextState[i] = nodeChange(initState, i);
		}
		return t_nextState;
	}

    /**
     * get changing genes and change mode for the first priority class.
     * used by the search by priority class.
     *
     * @param initState
     * @return int int[] of the gene number and it's change direction or null if the state is stable
     */
    private Vector getPriorityClassChangingGene(int[] initState){
        int change = 0;
        boolean changed = false;
        int i = 0;
        int j = 0;
        int priority = 0;
        int[] tchanges = null;
        int[] tclass = null;
        Vector v = null;

        // look for the first changing priority class
        for ( ; i<pclass.length ; i++) {
            tclass = pclass[i];

            for (j=2 ; j<tclass.length ; j+=2) {
                change = nodeChange(initState, tclass[j]);
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
                change = nodeChange(initState, tclass[j]);
                if (change != 0 && change+tclass[j+1] != 0) {
                    tchanges[p++] = tclass[j];
                    tchanges[p++] = change;
                }
            }

            // copy the usefull part of tchanges in the returned array
            int[] ret = new int[p];
            for (int a=0 ; a<p ; a++) {
                ret[a] = tchanges[a];
            }
            v = new Vector();
            v.add(ret);

            while(++i < pclass.length && pclass[i][0] == priority) {
                tclass = pclass[i];
                p = 0;
                tchanges = new int[tclass.length*2-1];
                tchanges[p++] = tclass[1];
                for (j=2 ; j<tclass.length ; j+=2) {
                    change = nodeChange(initState, tclass[j]);
                    if (change != 0 && change+tclass[j+1] != 0) {
                        tchanges[p++] = tclass[j];
                        tchanges[p++] = change;
                    }
                }
                if (p>1) {
                    // copy the usefull part of tchanges in the returned array
                    ret = new int[p];
                    for (int a=0 ; a<p ; a++) {
                        ret[a] = tchanges[a];
                    }
                    v.add(ret);
                }
            }
        }
        return v;
    }

	/**
	 * get change step for a gene
	 *
	 * @param initState
	 * @param i index of the gene to test
	 * @return the direction in which the gene want to change: 0 for no change, 1 for increase and -1 for decrease
	 */
	private int nodeChange(int[] initState, int i) {
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

	/**
	 * this is the main recursive function for the construction of the graph under a synchronous assumption
	 * it  creates the dynamic graph by searching next states recursively
	 * it has to be called for every initial state (ie all possible state if we want the full dynamic graph!).
	 * @throws GsException
	 */
	private void calcDynGraphSynchro () throws GsException {
		int[] changes = getChangingGene(t_state);
		// test if we are on a stable state (no changes)
		boolean stable = true;
		for (int i=0 ; i< length ; i++) {
			if (changes[i] != 0) {
				stable = false;
				break;
			}
		}
		if (stable) {
			if (buildSTG) {
				node.setStable(true, vreader);
			} else {
				OmddNode dd_tmp = addReachable(this.dd_stable, t_state, 0);
				if (dd_tmp != null) {
					dd_stable = dd_tmp;
				}
			}
			return;
		}
		if (curdepth >= maxdepth) {
			return;
		}
		curdepth++;
		// the real algo!
		// we are in the synchronous case: apply all changes at once
		// create the next state
		int c = 0;
		for (int i=0 ; i<t_state.length ; i++) {
			if (changes[i] != 0) {
				c++;
			}
			changes[i] += t_state[i];
		}

		// add the next node to the graph and recursively call if not already present
		if (addState(changes, true, c>1)) {
			calcDynGraphSynchro();
		}
		curdepth--;
	}

    /**
     * this is the main recursive function for the construction of the graph with changes classed in priority classes
     * it  creates the dynamic graph by searching next states recursively
     * it has to be called for every initial state (ie all possible state if we want the full dynamic graph!).
     */
    private void calcDynGraphByPriorityClass () throws GsException {
        int[]   nextState;
        Vector  v_changes = getPriorityClassChangingGene(t_state);

        if (v_changes == null) {
        	if (buildSTG) {
	            node.setStable(true, vreader);
			} else {
				OmddNode dd_tmp = addReachable(this.dd_stable, t_state, 0);
				if (dd_tmp != null) {
					dd_stable = dd_tmp;
				}
        	}
            return;
        }
		if (curdepth >= maxdepth) {
			return;
		}
		curdepth++;
        int[] thisState = t_state;
        GsDynamicNode thisNode = node;
        for (int index=0 ; index<v_changes.size() ; index++) {
            int[] changes = (int[])v_changes.get(index);
            switch (changes[0]) {
                case GsReg2dynPriorityClass.SYNCHRONOUS: // synchronous case
                    nextState = (int[])thisState.clone();
                    int i;
                    for (i=1 ; i<changes.length ; i++) {
                        nextState[changes[i++]] += changes[i];
                    }
                    // add the next node to the graph and recursively call if not already present
                    if (addState(nextState, true, changes.length>3)) {
                        calcDynGraphByPriorityClass();
                    }
                    node = thisNode;
                    t_state = thisState;
                    break;
                case GsReg2dynPriorityClass.ASYNCHRONOUS: // asynchronous case
                    // we need to keep a trace of the current node (to add correct edges)
                    for (i=1 ; i<changes.length ; i++) {
                        nextState = (int[])thisState.clone();
                        nextState[changes[i++]] += changes[i];
                        if (addState(nextState, true, false)) {
                            calcDynGraphByPriorityClass();
                        }
                        node = thisNode;
                        t_state = thisState;
                    }
            }
        }
        curdepth--;
    }

	/**
	 * this is the main recursive function for the construction of the graph with
	 * an asynchronous assumption and a depth first search strategy
	 * it  creates the dynamic graph by searching next states recursivly
	 * it has to be called for every initial state (ie all possible state if we want the full dynamic graph!).
	 * The exploration can be limited to a maximal depth (maxdepth, 0 for unlimited)
	 * and also to a maximal number of nodes (maxnodes)
	 */
	private void calcDynGraphAsynchroDepthFirst () throws GsException {
		int[] 	changes	= getChangingGene(t_state);
		int[] 	nextState;
		if (curdepth >= maxdepth) {
			return;
		}
		curdepth++;

		// test if we are on a stable state (no changes)
		boolean stable = true;
		for (int i=0 ; i< length ; i++) {
			if (changes[i] != 0) {
				stable = false;
				break;
			}
		}
		if (stable) {
			if (buildSTG) {
				node.setStable(true, vreader);
			} else {
				OmddNode dd_tmp = addReachable(this.dd_stable, t_state, 0);
				if (dd_tmp != null) {
					dd_stable = dd_tmp;
				}
			}
		    curdepth--;
			return;
		}

		// we are in the asynchrone case: apply changes one by one and then
		// add nextNode to the dynamical graph if it does not already exist
		// and if the maximum number of nodes is not reached
		// then search this new node

		// we need to keep a trace of the current node (to add correct edges)
        int[] thisState = t_state;
        GsDynamicNode thisNode = node;
		for (int i=0 ; ; i++) {
			while (i<length && changes[i] == 0) {
				i++;
			}
			if (i == length) {
				break;
			}
			nextState = (int[])thisState.clone();
			nextState[i] += changes[i];
			if (addState(nextState, true, false)) {
				//recursively call calcDynGraph if it is a depth first search
				calcDynGraphAsynchroDepthFirst();
			}
			t_state = thisState;
			node = thisNode;
		}
		curdepth--;
	}

	/**
	 * this is the main function for the construction of the graph with
	 * an asynchronous assumption and a breadth first search strategy
	 * this exploration can be limited to a maximal number of nodes (maxnodes)
	 */
	private void calcDynGraphAsynchroBreadthFirst() throws GsException {
		int[] 	nextState;
        while (toExplore.size() != 0 ) {
            // get the first node to explore
            // first determine calls for updating on this state
            GsDynamicNode exploringNode = (GsDynamicNode)toExplore.remove(0);
			int[]	changes    = getChangingGene(exploringNode.state);

			// test if we are on a stable state (no changes)
			boolean stable = true;
			for (int i=0 ; i< length ; i++) {
				if (changes[i] != 0) {
					stable = false;
					break;
				}
			}
			if (stable) {
				if (buildSTG) {
					exploringNode.setStable(true, vreader);
				} else {
					OmddNode dd_tmp = addReachable(this.dd_stable, t_state, 0);
					if (dd_tmp != null) {
						dd_stable = dd_tmp;
					}
				}
			} else {
				for ( int i=0 ;  ; i++ ) {
					while( i<changes.length && changes[i] == 0 ) {
						i++;
					}
					if (i == length) {
						break;
					}
					nextState = (int[])exploringNode.state.clone();
					nextState[i] += changes[i];
					if (addState(nextState, true, false)) {
						toExplore.addElement(node);
					}
				}
			}
		}
	}

	private void emergencySave () {
		goon = false;
	}

	public void interrupt() {
		ready = false;
	}

	/**
	 * run the simulation in a new thread.
	 */
	public void run() {
		if (!ready) {
			return;
		}

		try {
			if (initStates == null) {
				runFullGraph();
			}
			else {
				runPartialGraph();
			}
		} catch (GsException e) {
			System.out.println("simulation was interrupted");
		} finally {
			// return the result
			if (buildSTG) {
				frame.endSimu(dynGraph);
			} else {
				System.out.println("results ("+nbnode+" nodes):");
				dd_stable = dd_stable.reduce();
				dd_reachable = dd_reachable.reduce();
				System.out.println("-------- STABLES -----------");
				System.out.println(dd_stable.getString(0, listGenes));
				System.out.println("-------- REACHABLE ----------");
				System.out.println(dd_reachable.getString(0, listGenes));
				System.out.println("----------------------------");
				frame.endSimu(null);
			}
		}
	}

	private void testIfshouldGo() {
		// stop if it has been asked or if memory becomes unsufficient
		if (ready && Runtime.getRuntime().freeMemory() < 10000) {
			if (nbgc<8) {
				Runtime.getRuntime().gc();
				nbgc++;
			}
			if (Runtime.getRuntime().freeMemory() > 40000 ) {
				System.out.println("out of memory: saved by garbage collector");
			} else {
			    GsEnv.error("out of memory, I'll stop to prevent loosing everything", null);
				ready = false;
			}
		}
		if (!ready) {
			emergencySave();
		}
	}

	private boolean addState (int[] vstate, boolean addEdge, boolean multiple) throws GsException {
		//stop if the max depth is reached
		if (nbnode % 100 == 0) {
		    if (frame != null) {
                frame.setProgress(nbnode);
            }
		}
		if (nbnode >= maxnodes){
		    throw new GsException(GsException.GRAVITY_NORMAL, (String)null);
		}

		testIfshouldGo();
		if(!goon) {
		    throw new GsException(GsException.GRAVITY_NORMAL, Translator.getString("STR_interrupted"));
		}

		boolean isnew = false;
		if (buildSTG) { // build a full state transition graph
			GsDynamicNode newnode = new GsDynamicNode(vstate);
			isnew = dynGraph.addVertex(newnode);
			if (addEdge) {
				dynGraph.addEdge(node, newnode, multiple);
			}
			node = newnode;
			t_state = vstate;
		} else { // only build reachability set
			OmddNode newReachable = addReachable(dd_reachable, vstate, 0);

			t_state = vstate;
			if (newReachable != null) {
				isnew = true;
				dd_reachable = newReachable.reduce();
			}
		}

		if (isnew) {
			nbnode++;
		}
		return isnew;
	}

	private OmddNode addReachable(OmddNode reachable, int[] vstate, int depth) {
		if (depth == vstate.length) {
			if (reachable.equals(OmddNode.TERMINALS[1])) {
				return null;
			}
			return OmddNode.TERMINALS[1];
		}
		int curval = vstate[depth];
		if (reachable.next == null) {
			if (reachable.value == 1) {
				return null;
			}
			OmddNode ret = new OmddNode();
			ret.level = depth;
			ret.next = new OmddNode[t_max[depth]];
			for (int i=0 ; i<ret.next.length ; i++) {
				if (i==curval) {
					ret.next[i] = addReachable(reachable, vstate, depth+1);
				} else {
					ret.next[i] = OmddNode.TERMINALS[0];
				}
			}
			return ret;
		}
		// reachable is not a leaf: first explore it and then create a new node if needed
		OmddNode child;
		if (reachable.level > depth) {
			child = addReachable(reachable, vstate, depth+1);
		} else {
			child = addReachable(reachable.next[curval], vstate, depth+1);
		}
		if (child != null) {
			OmddNode ret = new OmddNode();
			ret.level = depth;
			ret.next = new OmddNode[t_max[depth]];
			for (int i=0 ; i<ret.next.length ; i++) {
				if (i==curval) {
					ret.next[i] = child;
				} else {
					ret.next[i] = reachable.level > depth ? reachable : reachable.next[i];
				}
			}
			return ret;
		}
		return null;
	}
}
