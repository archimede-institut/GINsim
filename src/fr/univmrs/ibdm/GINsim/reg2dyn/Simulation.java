package fr.univmrs.ibdm.GINsim.reg2dyn;

import java.util.Vector;

import fr.univmrs.ibdm.GINsim.dynamicGraph.GsDynamicGraph;
import fr.univmrs.ibdm.GINsim.dynamicGraph.GsDynamicNode;
import fr.univmrs.ibdm.GINsim.global.GsEnv;
import fr.univmrs.ibdm.GINsim.graph.GsEdgeAttributesReader;
import fr.univmrs.ibdm.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.OmddNode;

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
 * additionnaly, genes can be blocked at given state (or range of state): once they reached those
 * states, they can't leave them anymore (but can still evolve inside the range).
 */
public final class Simulation extends Thread implements Runnable{
	
	private GsDynamicGraph dynGraph;
	private Reg2dynFrame frame;
	private boolean goon = true;
    GsVertexAttributesReader vreader;
    GsEdgeAttributesReader ereader;

	private int maxdepth; 		// limitation of the depth for exploration (all types but BFS)
	private int maxnodes; 		// limitation of the number of nodes for exploration (all types)
	private Vector initStates; 
	private Vector listGenes;
	private int[] t_min;
	private int[] t_max;
	private int length;
	private boolean ready = false;
	private int nbgc = 0;	

	protected static final int SEARCH_SYNCHRONE = 0;
	protected static final int SEARCH_ASYNCHRONE_DF = 1;
	protected static final int SEARCH_ASYNCHRONE_BF = 2;
    protected static final int SEARCH_BYPRIORITYCLASS = 3;
	
	private Vector toExplore = new Vector(); // queue for the BFS exploration
	
	private int searchMode;
	
	private int curdepth = 0;
	private GsDynamicNode node;
    private int[][] pclass;
	private OmddNode[] t_tree;
    private GsSimulationParameters params;

	/**
	 * Constructs an empty dynamic graph
	 * 
	 * @param regGraph the regulatory graph on which we are working
	 * @param frame
	 * @param params 
	 */
	protected Simulation(GsRegulatoryGraph regGraph, Reg2dynFrame frame, GsSimulationParameters params) {
		this.frame = frame;
		dynGraph = new GsDynamicGraph(regGraph);
		dynGraph.setAssociatedGraph(regGraph);
        vreader = dynGraph.getGraphManager().getVertexAttributesReader();
        ereader = dynGraph.getGraphManager().getEdgeAttributesReader();
	    vreader.setDefaultVertexSize(5+10*regGraph.getNodeOrder().size(), 25);
		listGenes = regGraph.getNodeOrder();
		length = listGenes.size();
        this.params = params;
        
        t_min = params.block[0];
        t_max = params.block[1];
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
                pclass = params.pclass;
                break;
        }
        
        t_tree = regGraph.getAllTrees();
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
        if (params.initStates != null) {
            this.initStates = new Vector();
            for (int i=0 ; i<params.initStates.size() ; i++) {
                Reg2DynStatesIterator iterator = new Reg2DynStatesIterator(listGenes, (Vector[])params.initStates.get(i));
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
	private void runFullGraph() {
        // the iterator to construct all initial states
        reg2DynFullIterator iterator = new reg2DynFullIterator(listGenes, frame);
		try {
			//generate all initial states and construct partial graph from all these initial states;
			while(iterator.hasNext() && ((maxnodes==0) || dynGraph.getGraphManager().getVertexCount()<maxnodes)) {
				node = new GsDynamicNode(iterator.next());
	
				// verify if nextNode is already in the dynamic graph and construct partial graph
				if(dynGraph.addVertex(node)){
					switch (searchMode) {
						case SEARCH_SYNCHRONE:
							calcDynGraphSynchro();
							break;
						case SEARCH_ASYNCHRONE_BF:
							toExplore.addElement(node);
							break;
						case SEARCH_ASYNCHRONE_DF:
							calcDynGraphAsynchroDepthFirst();
							break;
                        case SEARCH_BYPRIORITYCLASS:
                            calcDynGraphByPriorityClass();
                            break;
					}
				}
				if (searchMode == SEARCH_ASYNCHRONE_BF) calcDynGraphAsynchroBreadthFirst();
				if (!goon) {
					return;
				}
			}
		} catch (OutOfMemoryError e) {
            GsEnv.error("out of memory error", null);
		    return;
		}
	}
	
	/**
	 * search the dynamic graph for the given initial states
	 *
	 */
	private void runPartialGraph() {
		int[] vstate;
		
        if (initStates.size() == 0) {
            return;
        }
        vstate = (int[])initStates.elementAt(0);
        dynGraph.addObject("reg2dyn_firstState", vstate);
        
		for (int i=0 ; i<initStates.size() ; i++) {
			// create the first node and check if already present
			vstate = (int[])initStates.elementAt(i);
			if (vstate.length != length) {
                return;
			}
			node = new GsDynamicNode(vstate);
			
			if(dynGraph.addVertex(node)){
				switch (searchMode) {
					case SEARCH_SYNCHRONE:
						calcDynGraphSynchro();
						break;
					case SEARCH_ASYNCHRONE_BF:
						toExplore.addElement(node);
						break;
					case SEARCH_ASYNCHRONE_DF:
						calcDynGraphAsynchroDepthFirst();
						break;
                    case SEARCH_BYPRIORITYCLASS:
                        calcDynGraphByPriorityClass();
                        break;
				}
			}
			if (searchMode == SEARCH_ASYNCHRONE_BF) calcDynGraphAsynchroBreadthFirst();

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
            tchanges = new int[(tclass.length*2)-1];
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
                tchanges = new int[(tclass.length*2)-1];
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
			//if the new value is greater than the initial one and authorized, it is incremented by one
			if (t_min[i] == -1 || curState != t_max[i]) {
				return 1;
			} 
		} else if (nextState < curState){
		    //if the new value is lesser than the initial one, it is decremented by one
			if (t_min[i] == -1 || curState != t_min[i]) {
				return -1;
			}
		}
		return 0;
	}
	
	/**
	 * this is the main recursive function for the construction of the graph under a synchronous assumption
	 * it  creates the dynamic graph by searching next states recursively
	 * it has to be called for every initial state (ie all possible state if we want the full dynamic graph!).
	 */
	private void calcDynGraphSynchro () {

		int[] changes = getChangingGene(node.state);
		GsDynamicNode nextNode;

		// test if we are on a stable state (no changes)
		boolean stable = true;
		for (int i=0 ; i< length ; i++) {
			if (changes[i] != 0) {
				stable = false;
				break;
			}
		}
		if (stable) {
            node.setStable(true, vreader);
		    curdepth--;
			return;
		}
		
		//stop if the max depth is reached 
		int nbNode = dynGraph.getGraphManager().getVertexCount();
		if (nbNode % 100 == 0) {
		    frame.setProgress(nbNode);
		}
		if (curdepth >= maxdepth || nbNode >= maxnodes){
		    curdepth--;
		    return;
		}
		
		testIfshouldGo();
		if(!goon) {
		    curdepth--;
			return;
		}

		// the real algo!
		// we are in the synchrone case: apply all changes at once
		// create the next state
		int c = 0;
		for (int i=0 ; i<node.state.length ; i++) {
			if (changes[i] != 0) {
				c++;
			}
			changes[i] += node.state[i];
		}

		// add the next node to the graph and recursively call if not already present
		nextNode = new GsDynamicNode(changes);
		changes = null;
		if (dynGraph.addVertex(nextNode)) {
			dynGraph.addEdge(node, nextNode, c>1);
			curdepth++;
			node = nextNode;
			calcDynGraphSynchro();
		} else {
			// if the node was already present add the edge anyway
			dynGraph.addEdge(node, nextNode, c>1);
		}
		curdepth--;
	} 

    /**
     * this is the main recursive function for the construction of the graph with changes classed in priority classes
     * it  creates the dynamic graph by searching next states recursively
     * it has to be called for every initial state (ie all possible state if we want the full dynamic graph!).
     */
    private void calcDynGraphByPriorityClass () {
        int[]   nextState;
        Vector  v_changes = getPriorityClassChangingGene(node.state);

        if (v_changes == null) {
            node.setStable(true, vreader);
            curdepth--;
            return;
        }
        
        // stop if the max depth is reached 
        int nbNode = dynGraph.getGraphManager().getVertexCount();
        if (nbNode % 100 == 0) {
            frame.setProgress(nbNode);
        }
        if (curdepth >= maxdepth || nbNode >= maxnodes){
            curdepth--;
            return;
        }

        testIfshouldGo();
        if(!goon) {
            curdepth--;
            return;
        }
        
        GsDynamicNode thisnode = node;
        for (int index=0 ; index<v_changes.size() ; index++) {
            int[] changes = (int[])v_changes.get(index);
            switch (changes[0]) {
                case GsReg2dynPriorityClass.SYNCHRONOUS: // synchronous case
                    nextState = (int[])thisnode.state.clone();
                    int i=1;
                    for ( ; i<changes.length ; i++) {
                        nextState[changes[i++]] += changes[i];
                    }
    
                    // add the next node to the graph and recursively call if not already present
                    node = new GsDynamicNode(nextState);
                    boolean multiple = (changes.length > 2);
                    changes = null;
                    nextState = null;
                    if (dynGraph.addVertex(node)) {
                        dynGraph.addEdge(thisnode, node, multiple);
                        curdepth++;
                        calcDynGraphByPriorityClass();
                    } else {
                        // if the node was already present add the edge anyway
                        Object edge = dynGraph.addEdge(thisnode, node, multiple);
                        if (i > 3) {
                            ereader.setEdge(edge);
                            ereader.setLineWidth(2);
                        }
                    }
                    break;
                case GsReg2dynPriorityClass.ASYNCHRONOUS: // asynchronous case
                    // we need to keep a trace of the current node (to add correct edges)
                    for (i=1 ; ((maxnodes==0)||(dynGraph.getGraphManager().getVertexCount()<maxnodes)); i++) {
                        if (i >= changes.length) {
                            break;
                        }
                        nextState = (int[])thisnode.state.clone();
                        nextState[changes[i++]] += changes[i];
                        node = new GsDynamicNode(nextState);
                        nextState = null;
                        if (dynGraph.addVertex(node)) {// if nextNode already exists then just add the edge 
                            dynGraph.addEdge(thisnode, node, false);
                            //recursively call calcDynGraph if it is a depth first search
                            curdepth++;
                            calcDynGraphByPriorityClass();
                        }
                        else {// if the node was already present add the edge anyway
                            dynGraph.addEdge(thisnode, node, false);
                        }
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
	private void calcDynGraphAsynchroDepthFirst () {
							  	
		int[] 	changes	= getChangingGene(node.state);
		int[] 	nextState;
		GsDynamicNode nextNode;

		// test if we are on a stable state (no changes)
		boolean stable = true;
		for (int i=0 ; i< length ; i++) {
			if (changes[i] != 0) {
				stable = false;
				break;
			}
		}
		if (stable) {
            node.setStable(true, vreader);
		    curdepth--;
			return;
		}

		//stop if the max depth is reached 
		int nbNode = dynGraph.getGraphManager().getVertexCount();
		if (nbNode % 100 == 0) {
		    frame.setProgress(nbNode);
		}
		if (curdepth >= maxdepth || nbNode >= maxnodes){
		    curdepth--;
			return;
		}

		testIfshouldGo();
		if(!goon) {
		    curdepth--;
			return;
		}

		// we are in the asynchrone case: apply changes one by one and then
		// add nextNode to the dynamical graph if it does not already exist
		// and if the maximum number of nodes is not reached
		// then search this new node 
	
		// we need to keep a trace of the current node (to add correct edges)
		GsDynamicNode thisnode = node;
		for (int i=0 ; ((maxnodes==0)||(dynGraph.getGraphManager().getVertexCount()<maxnodes)); i++) {
			while (i<length && changes[i] == 0) {
				i++;
			}
			if (i == length) {
				break;
			}
			nextState = (int[])thisnode.state.clone();
			nextState[i] += changes[i];
			nextNode = new GsDynamicNode(nextState);
			nextState = null;
			if (dynGraph.addVertex(nextNode)) {// if nextNode already exists then just add the edge 
				dynGraph.addEdge(thisnode, nextNode, false);
				//recursively call calcDynGraph if it is a depth first search
				node = nextNode;
				curdepth++;
				calcDynGraphAsynchroDepthFirst();
			}
			else {// if the node was already present add the edge anyway
				dynGraph.addEdge(thisnode, nextNode, false);
			}
		}
		curdepth--;
	}
		
	/**
	 * this is the main function for the construction of the graph with 
	 * an asynchronous assumption and a breadth first search strategy
	 * this exploration can be limited to a maximal number of nodes (maxnodes)
	 */
	private void calcDynGraphAsynchroBreadthFirst () {
		
		int[] 	nextState;
		GsDynamicNode nextNode;

        while (toExplore.size() != 0 && ((maxnodes == 0)||(dynGraph.getGraphManager().getVertexCount() < maxnodes))){
            // get the first node to explore			  	
            // first determine calls for updating on this state
            GsDynamicNode node = (GsDynamicNode)toExplore.remove(0); 
			int[]	changes    = getChangingGene(node.state);

			// test if we are on a stable state (no changes)
			boolean stable = true;
			for (int i=0 ; i< length ; i++) {
				if (changes[i] != 0) {
					stable = false;
					break;
				}
			}
			if (stable) {
	            node.setStable(true, vreader);
			} else {
				//	stop if the max number of nodes is reached 
				// note that maxnodes == 0 if unlimited
				int nbNode = dynGraph.getGraphManager().getVertexCount();
				if (nbNode % 100 == 0) {
				    frame.setProgress(nbNode);
				}
				if (curdepth >= maxdepth || nbNode >= maxnodes){
					return;
				}
	
				testIfshouldGo();
				if(!goon) {
					return;
				}
	
				// create next states
				nbNode = dynGraph.getGraphManager().getVertexCount();
				for ( int i=0 ; (maxnodes == 0 || nbNode < maxnodes) ; i++ ) {
					while( i<changes.length && changes[i] == 0 ) {
						i++;
					}
					if (i == length) {
						break;
					}
					nextState = (int[])node.state.clone();
					nextState[i] += changes[i];
					nextNode = new GsDynamicNode(nextState);
					if (dynGraph.addVertex(nextNode)) {
						toExplore.addElement(nextNode);
					}
					dynGraph.addEdge(node, nextNode, false);
					nbNode = dynGraph.getGraphManager().getVertexCount();
					if (nbNode % 100 == 0) {
					    frame.setProgress(nbNode);
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
			System.out.println("reg2dyn can't run: not configured");
			return;
		}
        
		if (initStates == null) {
			runFullGraph();		
		}
		else {
			runPartialGraph();
		}
        
        // add some default comments to the state transition graph
        dynGraph.getAnnotation().setComment(params.getDescr());
        
		// return the graph found.
		frame.endSimu(dynGraph);
		dynGraph = null;
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
}
