package fr.univmrs.tagc.GINsim.reg2dyn;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.reg2dyn.helpers.GsSTGSimulationHelper;
import fr.univmrs.tagc.GINsim.reg2dyn.helpers.SimulationHelper;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.InitialStatesIterator;
import fr.univmrs.tagc.common.GsException;
import fr.univmrs.tagc.common.Tools;
import fr.univmrs.tagc.common.managerresources.Translator;

/**
 * This is the main part of the simulation. It supports pluggable backends
 * to generate the following states and to switch from building a full
 * state transition graph to a "simple" reachability set.
 */
public class Simulation extends Thread implements Runnable {

	protected LinkedList queue = new LinkedList(); // exploration queue

	protected SimulationManager frame;
	protected int maxnodes, maxdepth;
	protected Iterator initStatesIterator;
	protected SimulationHelper helper;
	protected SimulationUpdater updater;

	protected boolean breadthFirst = false;
	public int nbnode = 0;
	protected boolean ready = false;
	
	/**
	 * Constructs an empty dynamic graph
	 *
	 * @param regGraph the regulatory graph on which we are working
	 * @param frame
	 * @param params
	 */
    public Simulation(GsRegulatoryGraph regGraph, SimulationManager frame, GsSimulationParameters params) {
        this(regGraph, frame, params, true, true);
    }
    public Simulation(GsRegulatoryGraph regGraph, SimulationManager frame, GsSimulationParameters params, boolean runNow) {
        this(regGraph, frame, params, runNow, true);
    }
    public Simulation(GsRegulatoryGraph regGraph, SimulationManager frame, GsSimulationParameters params, boolean runNow, boolean useInit) {
		this.frame = frame;
		this.maxdepth = params.maxdepth;
		this.maxnodes = params.maxnodes;

		if (params.simulationStrategy == GsSimulationParameters.STRATEGY_STG) {
			helper = new GsSTGSimulationHelper(regGraph, params);
		}
		breadthFirst = params.breadthFirst;
   		updater = SimulationUpdater.getInstance(regGraph, params);
   		if (useInit) {
   		    initStatesIterator = new InitialStatesIterator(params.nodeOrder, params);
   		}
   		if (runNow) {
   		    start();
   		}
	}

    public void startSimulation(List nodeOrder, Map inputs, Map m_initState) {
        set_initialStates(nodeOrder, inputs, m_initState);
        start();
    }
    public void set_initialStates(List nodeOrder, Map inputs, Map m_initState) {
        initStatesIterator = new InitialStatesIterator(nodeOrder, inputs, m_initState);
    }
	public void interrupt() {
		ready = false;
	}

    /**
     * run the simulation in a new thread.
     */
    public void run() {
        frame.endSimu(do_simulation());
    }
	public GsGraph do_simulation() {
        ready = true;
		boolean maxDepthReached = false;
		try {
			// iterate through initial states and run the simulation from each of them
			while(initStatesIterator.hasNext()) {
				// add the next proposed state
				queue.add(new SimulationQueuedState((byte[])initStatesIterator.next(), 0, null, false));
				
				// do the simulation itself
				while (!queue.isEmpty()) {
					SimulationQueuedState item = (SimulationQueuedState)(
							breadthFirst ? queue.removeFirst() 
										: queue.removeLast());

					if (helper.addNode(item)) {
						// this is a new node, increase node count, do some checks and so on
						nbnode++;
						if (nbnode % 100 == 0) {
						    if (frame != null) {
				                frame.setProgress(nbnode);
				            }
						}
						if (maxnodes != 0 && nbnode >= maxnodes){
						    System.out.println("maxnodes reached: " + maxnodes);
						    throw new GsException(GsException.GRAVITY_NORMAL, (String)null);
						}

//						// stop if it has been asked or if memory becomes unsufficient
//						if (ready && Runtime.getRuntime().freeMemory() < 5000) {
//							Runtime.getRuntime().gc();
//							if (Runtime.getRuntime().freeMemory() > 40000 ) {
//								System.out.println("out of memory: saved by garbage collector: "+nbgc);
//							} else {
//								GsEnv.error("out of memory, I'll stop to prevent loosing everything", null);
//								System.out.println("not ready anymore!!");
//								ready = false;
//							}
//						}
						if (!ready) {
						    throw new GsException(GsException.GRAVITY_NORMAL, Translator.getString("STR_interrupted"));
						}

						// run the simulation on the new node
						updater.setState(item.state, item.depth, helper.getNode());
						if (!updater.hasNext()) {
							helper.setStable();
							frame.addStableState(item);
							for (int i=0 ; i<item.state.length ; i++ ) {
								System.out.print(item.state[i]+" ");
							}
							System.out.println();
						} else {
							if (maxdepth == 0 || item.depth < maxdepth) {
								while (updater.hasNext()) {
									queue.addLast(updater.next());
								}
							} else {
								maxDepthReached = true;
							}
						}
					}
				}
			}
		} catch (GsException e) {
			System.out.println("simulation was interrupted");
		} catch (OutOfMemoryError e) {
		    Tools.error("Out Of Memory", null);
		    return null;
		} finally {
			if (maxDepthReached) {
				Tools.error("Reached the max depth", null);
				//TODO: explain what happened and give some hints
			}
		}
		return helper.endSimulation();
	}
}
