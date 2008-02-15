package fr.univmrs.ibdm.GINsim.reg2dyn;

import java.util.Iterator;
import java.util.LinkedList;

import fr.univmrs.ibdm.GINsim.dynamicGraph.GsDynamicGraph;
import fr.univmrs.ibdm.GINsim.dynamicGraph.GsDynamicNode;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsGenericRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.OmddNode;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.initialState.InitialStatesIterator;
import fr.univmrs.tagc.common.GsException;
import fr.univmrs.tagc.common.Tools;
import fr.univmrs.tagc.common.manageressources.Translator;

/**
 * This is the main part of the simulation. It supports pluggable backends
 * to generate the following states and to switch from building a full
 * state transition graph to a "simple" reachability set.
 */
public final class Simulation extends Thread implements Runnable {

	protected static final int SEARCH_SYNCHRONE = 0;
	protected static final int SEARCH_ASYNCHRONE_DF = 1;
	protected static final int SEARCH_ASYNCHRONE_BF = 2;
    protected static final int SEARCH_BYPRIORITYCLASS = 3;
    protected static final String[] MODE_NAMES = new String[] { "synchrone", "asynchrone_df", "asynchrone_bf", "priorityClass" };

	private LinkedList queue = new LinkedList(); // exploration queue

	private GsReg2dynFrame frame;
	private GsSimulationParameters	params;
	private SimulationHelper helper;
	SimulationUpdater updater;

	private boolean breathFirst = false;
	int nbnode = 0;
	private boolean ready = false;
	
	/**
	 * Constructs an empty dynamic graph
	 *
	 * @param regGraph the regulatory graph on which we are working
	 * @param frame
	 * @param params
	 */
	protected Simulation(GsRegulatoryGraph regGraph, GsReg2dynFrame frame, GsSimulationParameters params) {
		this.frame = frame;
		this.params = params;
		
		if (params.buildSTG) {
			helper = new DynGraphHelper(regGraph, params);
		}
		breathFirst = params.mode == SEARCH_ASYNCHRONE_BF;
   		updater = SimulationUpdater.getInstance(regGraph, params);
        start();
	}

	public void interrupt() {
		ready = false;
	}

	/**
	 * run the simulation in a new thread.
	 */
	public void run() {
        ready = true;
		boolean maxDepthReached = false;
		try {
			// iterate through initial states and run the simulation from each of them
	        Iterator iterator = new InitialStatesIterator(params.nodeOrder, params.m_initState);
			while(iterator.hasNext()) {
				// add the next proposed state
				queue.add(new SimulationQueuedState((int[])iterator.next(), 0, null, false));
				
				// do the simulation itself
				while (!queue.isEmpty()) {
					SimulationQueuedState item = (SimulationQueuedState)(
							breathFirst ? queue.removeFirst() 
										: queue.removeLast());

					if (helper.addNode(item)) {
						// this is a new node, increase node count, do some checks and so on
						nbnode++;
						if (nbnode % 100 == 0) {
						    if (frame != null) {
				                frame.setProgress(nbnode);
				            }
						}
						if (params.maxnodes != 0 && nbnode >= params.maxnodes){
						    System.out.println("maxnodes reached: "+params.maxnodes);
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
						updater.setState(item.state, item.depth, helper.node);
						if (!updater.hasNext()) {
							helper.setStable();
							System.out.print("stable (depth "+item.depth+"): ");
							for (int i=0 ; i<item.state.length ; i++ ) {
								System.out.print(item.state[i]+" ");
							}
							System.out.println();
						} else {
							if (params.maxdepth == 0 || item.depth < params.maxdepth) {
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
		    return;
		} finally {
			if (maxDepthReached) {
				Tools.error("Reached the max depth", null);
				//TODO: explain what happened and give some hints
			}
			// return the result
			frame.endSimu(helper.endSimulation());
		}
	}
}

abstract class SimulationHelper {
	GsDynamicNode node;
	abstract boolean addNode(SimulationQueuedState item);
	abstract GsGraph endSimulation();
	abstract void setStable();
}

class DynGraphHelper extends SimulationHelper {

	GsDynamicGraph dynGraph;
	GsVertexAttributesReader vreader;
	
	DynGraphHelper(GsGenericRegulatoryGraph regGraph, GsSimulationParameters params) {
		dynGraph = new GsDynamicGraph(params.nodeOrder);
		if (regGraph instanceof GsGraph) {
			dynGraph.setAssociatedGraph((GsGraph)regGraph);
		}
        vreader = dynGraph.getGraphManager().getVertexAttributesReader();
	    vreader.setDefaultVertexSize(5+10*params.nodeOrder.size(), 25);
        // add some default comments to the state transition graph
        dynGraph.getAnnotation().setComment(params.getDescr());
	}

	public boolean addNode(SimulationQueuedState item) {
		node = new GsDynamicNode(item.state);
		boolean isnew = dynGraph.addVertex(node);
		if (item.previous != null) {
			dynGraph.addEdge(item.previous, node, item.multiple);
		}
		return isnew;
	}

	public GsGraph endSimulation() {
		return dynGraph;
	}

	public void setStable() {
		node.setStable(true, vreader);
	}
}

class ReachabilitySetHelper extends SimulationHelper {

	private int[] t_max;
	private int length;
	private OmddNode dd_reachable = OmddNode.TERMINALS[0];
	
	ReachabilitySetHelper(GsSimulationParameters params) {
		length = params.nodeOrder.size();
		t_max = new int[length];
		for (int i=0 ; i<length ; i++) {
			t_max[i] = ((GsRegulatoryVertex)params.nodeOrder.get(i)).getMaxValue()+1;
		}
	}
	
	public boolean addNode(SimulationQueuedState item) {
		
		OmddNode newReachable = addReachable(dd_reachable, item.state, 0);
		if (newReachable != null) {
			dd_reachable = newReachable.reduce();
			return true;
		}
		return false;
	}

	public GsGraph endSimulation() {
//		System.out.println("results ("+nbnode+" nodes):");
//		dd_stable = dd_stable.reduce();
//		dd_reachable = dd_reachable.reduce();
//		System.out.println("-------- STABLES -----------");
//		System.out.println(dd_stable.getString(0, params.nodeOrder));
//		System.out.println("-------- REACHABLE ----------");
//		System.out.println(dd_reachable.getString(0, params.nodeOrder));
//		System.out.println("----------------------------");
		return null;
	}

	public void setStable() {
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
