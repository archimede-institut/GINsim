package fr.univmrs.tagc.GINsim.reg2dyn;

import java.util.HashMap;
import java.util.Map;

import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.InitialStatesIterator;
import fr.univmrs.tagc.common.GsException;
import fr.univmrs.tagc.common.Tools;
import fr.univmrs.tagc.common.manageressources.Translator;

public class DynamicalHierarchicalSimulation extends Simulation {
	
	protected DynamicalHierarchicalSimulationHelper helper;

	public DynamicalHierarchicalSimulation(GsRegulatoryGraph regGraph, SimulationManager frame, GsSimulationParameters params) {
		this(regGraph, frame, params, true, true);
	}

	public DynamicalHierarchicalSimulation(GsRegulatoryGraph regGraph, SimulationManager frame, GsSimulationParameters params, boolean runNow) {
		this(regGraph, frame, params, runNow, true);
	}

    public DynamicalHierarchicalSimulation(GsRegulatoryGraph regGraph, SimulationManager frame, GsSimulationParameters params, boolean runNow, boolean useInit) {
		super(regGraph, frame, params, false, false);
		if (params.buildSTG == GsSimulationParameters.BUILD_DHG) {
			helper = new DynamicalHierarchicalSimulationHelper(regGraph, params);
		}
   		updater = SimulationUpdater.getInstance(regGraph, params);
   		if (useInit) {
   		    initStatesIterator = new InitialStatesIterator(params.nodeOrder, params.m_initState);
   		}
   		if (runNow) {
   		    start();
   		}
	}
	
	public GsGraph do_simulation() {
		ready = true;
		boolean maxDepthReached = false;
		try {
			Map map = new HashMap();
			int current_symbol = 1;
			// iterate through initial states and run the simulation from each of them
			while(initStatesIterator.hasNext()) {
				// add the next proposed state
				int[] state = (int[])initStatesIterator.next();
				if (!map.containsKey(state)) {
					queue.add(new DynamicalHierarchicalSimulationQueuedState(state, 0, null, false, null));
				}
				
				// do the simulation itself
				while (!queue.isEmpty()) {
					DynamicalHierarchicalSimulationQueuedState item = (DynamicalHierarchicalSimulationQueuedState)(
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

//							// stop if it has been asked or if memory becomes unsufficient
//							if (ready && Runtime.getRuntime().freeMemory() < 5000) {
//								Runtime.getRuntime().gc();
//								if (Runtime.getRuntime().freeMemory() > 40000 ) {
//									System.out.println("out of memory: saved by garbage collector: "+nbgc);
//								} else {
//									GsEnv.error("out of memory, I'll stop to prevent loosing everything", null);
//									System.out.println("not ready anymore!!");
//									ready = false;
//								}
//							}
						if (!ready) {
						    throw new GsException(GsException.GRAVITY_NORMAL, Translator.getString("STR_interrupted"));
						}

						// run the simulation on the new node
						updater.setState(item.state, item.depth, helper.node);
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
			// return the result
			return helper.endSimulation();
		}
	}		
}