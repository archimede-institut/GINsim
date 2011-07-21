package fr.univmrs.tagc.GINsim.reg2dyn;

import java.awt.Color;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Set;

import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.tagc.GINsim.hierachicalTransitionGraph.GsHierarchicalNode;
import fr.univmrs.tagc.GINsim.hierachicalTransitionGraph.GsHierarchicalNodeSet;
import fr.univmrs.tagc.GINsim.hierachicalTransitionGraph.GsHierarchicalSigmaSet;
import fr.univmrs.tagc.GINsim.hierachicalTransitionGraph.GsHierarchicalSigmaSetFactory;
import fr.univmrs.tagc.GINsim.hierachicalTransitionGraph.GsHierarchicalTransitionGraph;
import fr.univmrs.tagc.GINsim.reg2dyn.helpers.HTGSimulationHelper;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.common.Debugger;
import fr.univmrs.tagc.common.GsException;
import fr.univmrs.tagc.common.Tools;
import fr.univmrs.tagc.common.manageressources.Translator;


/**
 * 
 * Run a simulation to construct a HierarchicalTransitionGraph
 * 
 *
 */
public class HTGSimulation extends Simulation {
	

	
	/*  ****DEBUG RELATED STUFF*/
	private static final int DBG_DEPLOYMENT = 0;
	private static final int DBG_MAINLOOPS = 1;
	private static final int DBG_POSTTREATMENT = 2;
	private static final int DBG_APPARTENANCETESTS = 4;
	private static final int DBG_QUEUE = 8;
	private static final int DBG_SIGMA = 16; 
	private static final int DBG_MERGE = 32; 
	private static final int DBG_ALL = DBG_MAINLOOPS | DBG_POSTTREATMENT | DBG_APPARTENANCETESTS | DBG_QUEUE | DBG_SIGMA | DBG_MERGE; 
	
	private static final byte debug = DBG_ALL; //Work as a mask, use val | val | val .... //FIXME : set to DBG_DEPLOYMENT before deploying
	private PrintStream debug_o = System.err;
	private StringBuffer log_tabdepth = new StringBuffer();
	
	/**
	 * Indicates if the transient SCC with the same sigma should be compacted into a single HierarchicalNode.
	 */
	private boolean shouldCompactSCC;
	
	/**
	 * The HierarchicalTransitionGraph in construction
	 */
	private GsHierarchicalTransitionGraph htg;
	/**
	 * The regulatory graph
	 */
	private GsRegulatoryGraph regGraph;
	
	/**
	 * An array indicating for each node in the nodeOrder their count of childs. (ie. their max value)
	 */
	private byte[] childsCount;
	/**
	 * A HashSet&lt;GsHierarchicalNode&gt; containg all the masters nodes.
	 */
	private GsHierarchicalNodeSet nodeSet;
	/**
	 * The index of the next state found during the dfs.
	 */
	private int index;
	/**
	 * The current depth in the dfs. Used mainly for the simulation limit's.
	 */
	private int depth;
	/**
	 * The current maximal depth of the dfs of the current initial state.
	 */
	private int max_depth_reached;
	private long lastDraw = 0;
	private int nbinitialstates = 1;

	/**
	 * The simulation parameters
	 */
	private GsSimulationParameters params;
	
	private GsHierarchicalSigmaSetFactory sigmaFactory;
	
	
	public HTGSimulation(GsRegulatoryGraph regGraph, SimulationManager frame, GsSimulationParameters params) {
		this(regGraph, frame, params, true, true);
	}

	public HTGSimulation(GsRegulatoryGraph regGraph, SimulationManager frame, GsSimulationParameters params, boolean runNow) {
		this(regGraph, frame, params, runNow, true);
	}

    public HTGSimulation(GsRegulatoryGraph regGraph, SimulationManager frame, GsSimulationParameters params, boolean runNow, boolean useInit) {
		super(regGraph, frame, params, false, useInit);
		
		helper = new HTGSimulationHelper(regGraph, params);
		this.params = params;
   		if (runNow) {
   		    start();
   		}
	}
  
    
    
	/**
	 * Run the simulation, handle exception and build the graph.
	 */
	public GsGraph do_simulation() {
		long time = System.currentTimeMillis();
		this.htg = (GsHierarchicalTransitionGraph) helper.getDynamicGraph();
		this.shouldCompactSCC = htg.areTransientCompacted();
		this.regGraph = (GsRegulatoryGraph) helper.getRegulatoryGraph();
		this.sigmaFactory = new GsHierarchicalSigmaSetFactory();
		Debugger.setDebug(debug);
		ready = true;
		try {
			runSimulationOnInitialStates();									// run the simulation for each initial states
		} catch (GsException e) {
			debug_o.println("Error : "+e.getMessage());
            debug_o.println("simulation was interrupted");
		} catch (OutOfMemoryError e) {
		    Tools.error("Out Of Memory", null);
		    return null;
		} catch (Exception e) {
			debug_o.println("Error : "+e.getMessage());
			e.printStackTrace();
		}
		Debugger.log(-100, "Simulation done in : "+(System.currentTimeMillis()-time)+"ms");
		addAllNodeTo();									// add all nodes to the graph
		addAllEdgesTo();								// add all the edges to the graph
		Debugger.log(-100, "Graph created in : "+(System.currentTimeMillis()-time)+"ms");
		return helper.endSimulation();
	}


	/* ****************** DEBUG AND Debugger.log STUFF**********/

	/**
	 * The main entrance for the algorithm. Basically, initialize the main variables, and run the recursive explore function on every initial states
	 *
	 * <pre> index = 0
	 * For each initial state __state__
	 *     If __state__ is not in __nodeSet__, that is is not already processed
	 *         If __state__ has no successors
	 *             It is a stable state; continue
	 *         Else
	 *             Explore({__state__, index, index})
	 * </pre>
	 */
	private void runSimulationOnInitialStates() throws Exception {
		nodeSet = new GsHierarchicalNodeSet();
		childsCount = htg.getChildsCount();
		index = 0;
		                                                                                            						
		while(initStatesIterator.hasNext()) { 																				//For each initial states
			byte[] state = (byte[])initStatesIterator.next();																//  __state__ is the current initial state.
			Debugger.log(DBG_MAINLOOPS,log_tabdepth+"New initial state :"+print_state(state));               						
			                                                                                        						
			GsHierarchicalNode processed_hnode = nodeSet.getHNodeForState(state);											//  Search __state__ in the nodeSet
			if (processed_hnode  == null) { 																				//  If the new state was not in the nodeSet, that is has not been processed
				SimulationUpdater updater = getUpdaterForState(state);														//    Get the updater of the state
				if (!updater.hasNext()) {                                                                                   //    If it has no successor
					processStableState(state);                                                                           	//      Process it as a stable state
					continue;                                                                                                     
				}                                                                                                                 
				HTGSimulationQueuedState e = new HTGSimulationQueuedState(state, index, index);					//    Create __e__ a queue item with the state, index and updater
				depth = -1;                                                                                                 //    Set the depth to -1, (as it will be incremented immediatly to 0)
				max_depth_reached = -1;
				explore(e, updater);                                                                                                 //    Call the recursive dunction explore() on __e__.
			} else {
				Debugger.log(DBG_MAINLOOPS,log_tabdepth+"\tAlready processed :"+processed_hnode.getUniqueId());
			}
			nbinitialstates++;
		}

	}

	/**
	 * The recursive function of the algorithm.
	 *
	 * <pre> For a given queue item __e__
	 * increment __index__
	 * increment __depth__
	 * initialize __e_sigma__ as a new Set<GsHierarchicalNode>
	 * queue __e__
	 * for each successor __n_state__ of __e__
	 *   If __n__ has no successor
	 *      process it as a stable state ¬ __stable_state__
	 *      append __stable_state__ to the set of __outgoingEdges__ of __e__
	 *      append __stable_state__.__sigma__ to __e_sigma__
	 *      continue;
	 *   __n__ = Search __n_state__ in the queue
	 *   If __n__ is found   
	 *      __e__.low_index = min(__e__.low_index, __n__.index)
	 *   Else
	 *      __hnode__ = Search __n_state__ in nodeSet, that is, has it already been processed.
	 *      If __hnode__ is found
	 *          append __hnode__ to the set of __outgoingEdges__ of __e__
	 *          append __hnode__.__sigma__ to __e_sigma__
	 *          continue;
	 *      Else
	 *          __n__ = {__n_state__, index, index}
	 *          __hnode__ = explore(__n__)
	 *          __e__.low_index = min(__e__.low_index, __n__.low_index)
	 *          If __hnode__ is not null
	 *          append __hnode__ to the set of __outgoingEdges__ of __e__
	 *          append __hnode__.__sigma__ to __e_sigma__
	 *              
	 *   
	 *              
	 *   
	 *              
	 *   
	 *              
	 *   
	 * </pre>
	 */
	private GsHierarchicalNode explore(HTGSimulationQueuedState e, SimulationUpdater e_updater) throws Exception {
		checkStopConditions();
		
		HTGSimulationQueueItem n = null;
		log_tabdepth.append('\t');
		Debugger.log(DBG_MAINLOOPS,log_tabdepth+"Exploring :"+e);
		index++;
		depth++;
		
		queue.add(e);																										//Queueing the current state
		Debugger.log(DBG_QUEUE,log_tabdepth+"queue :"+queue);                                            							
		while (e_updater.hasNext()) {																						//For each successors
			byte[] n_state= ((SimulationQueuedState)e_updater.next()).state;												// n_state is the state of the successor
			SimulationUpdater n_updater = getUpdaterForState(n_state);                          							
			if (!n_updater.hasNext()) {																						// n_state has no child No child => stable state
				GsHierarchicalNode stableState = processStableState(n_state);
				e.addOutgoingHNode(stableState);
			} else {
				Debugger.log(DBG_MAINLOOPS,log_tabdepth+"nextState :"+print_state(n_state));
				n = getTripletInQueueForState(n_state);															   				//Search the state in the queue
				if (n != null) {																				   				//If found
					Debugger.log(DBG_APPARTENANCETESTS,log_tabdepth+"in P :"+n);
					HTGSimulationQueueSCC newCycleItem = new HTGSimulationQueueSCC(null, index, n.getLow_index());
					GsHierarchicalNode n_hnode = cycleFound(n, newCycleItem);
					//newCycleItem.setSCC(n_hnode);
					e.setLow_index(Math.min(e.getLow_index(), newCycleItem.getLow_index()));												   				//  update the index
					queue.add(newCycleItem);
					Debugger.log(DBG_QUEUE,log_tabdepth+"\tqueue:"+queue);
				} else {																						   				//Else the state is not in the queue
					Debugger.log(DBG_APPARTENANCETESTS,log_tabdepth+"not in P");                         				   				 
					GsHierarchicalNode n_hnode = nodeSet.getHNodeForState(n_state);								   				//  If it already processed (in the nodeSet)	
					if (n_hnode != null) {                                                                         				
						Debugger.log(DBG_APPARTENANCETESTS,log_tabdepth+"in N :"+n_hnode.getUniqueId());                    				
						e.addOutgoingHNode(n_hnode);                                                               				
					} else {																					   				//  Else
						Debugger.log(DBG_APPARTENANCETESTS,log_tabdepth+"not in N");                     				   				 
						n = new HTGSimulationQueuedState(n_state, index, index);						   				//     explore it
						n_hnode = explore((HTGSimulationQueuedState) n, n_updater);																					//     update the index
						e.setLow_index(Math.min(e.getLow_index(), n.getLow_index()));
						if (n_hnode != null) {
							e.addOutgoingHNode(n_hnode);
						}
					}
				}
			}

		}
		Debugger.log(DBG_MAINLOOPS,log_tabdepth+"Comparing indexes "+e);
		if (e.isCycle() || e.getIndex() == e.getLow_index()) {
			GsHierarchicalNode hnode = buildSCC(e);
			log_tabdepth.deleteCharAt(log_tabdepth.length()-1);
			return hnode;
		} else if (!e.isCycle()) {
			GsHierarchicalNode scc = ((HTGSimulationQueuedState)e).getInCycle().getSCC();
			Set out = ((HTGSimulationQueuedState)e).getOutgoindHNodes();
			for (Iterator it = out.iterator(); it.hasNext();) {
				GsHierarchicalNode hnode = (GsHierarchicalNode) it.next();
				scc.addOutgoingEdge(hnode);
			}
		}
		log_tabdepth.deleteCharAt(log_tabdepth.length()-1);
		return null;
	}
	
	
	private GsHierarchicalNode cycleFound(HTGSimulationQueueItem stopItemInQueue, HTGSimulationQueueSCC newCycleItem) {
		Debugger.log(DBG_QUEUE,log_tabdepth+"Cycle Found up to  "+stopItemInQueue);
		GsHierarchicalNode cycle = new GsHierarchicalNode(childsCount);
		newCycleItem.setSCC(cycle);
		HTGSimulationQueueItem n;
		int low_index = stopItemInQueue.getLow_index();
		do {
			n = (HTGSimulationQueueItem) queue.removeLast();
			if (n.getLow_index() < low_index) low_index = n.getLow_index();
			Debugger.log(DBG_QUEUE,log_tabdepth+"\tunqueuing:"+queue);
			if (n.isCycle()) {
				Debugger.log(DBG_MERGE,log_tabdepth+"\t\tmerge cycle "+cycle+" and "+n);
				cycle.merge(((HTGSimulationQueueSCC)n).getSCC(), nodeSet, sigmaFactory);		//Merge all the states of the SCC in the cycle
			} else {
				cycle.addState(((HTGSimulationQueuedState)n).getState(), 1);
				((HTGSimulationQueuedState)n).setInCycle(newCycleItem);
			}
		} while (!n.equals(stopItemInQueue));
		cycle.compactMaster();
		newCycleItem.setLow_index(low_index);
		return cycle;
	}
	
	private GsHierarchicalNode buildSCC(HTGSimulationQueueItem e) {
		GsHierarchicalNode scc;
		HTGSimulationQueueSCC inCycle = ((HTGSimulationQueuedState) e).getInCycle();
		if (inCycle != null) {
			Debugger.log(DBG_QUEUE,log_tabdepth+"\tthe state is in a cycle:"+queue);
			scc = inCycle.getSCC();
			GsHierarchicalSigmaSet sigma = computeSigma(scc, true);
			if (sigma.isSingleton()) {
				scc.setType(GsHierarchicalNode.TYPE_TERMINAL_CYCLE);
			} else {
				scc.setType(GsHierarchicalNode.TYPE_TRANSIENT_CYCLE);
			}
			nodeSet.add(scc);
		} else {
			scc = new GsHierarchicalNode(childsCount);
			scc.addState(((HTGSimulationQueuedState) e).getState(), 1);
			scc.setType(GsHierarchicalNode.TYPE_TRANSIENT_COMPONENT);
			Set out = ((HTGSimulationQueuedState)e).getOutgoindHNodes();
			for (Iterator it = out.iterator(); it.hasNext();) {
				GsHierarchicalNode hnode = (GsHierarchicalNode) it.next();
				hnode.compactMaster();
				scc.addOutgoingEdge(hnode);
			}
			GsHierarchicalSigmaSet sigma = computeSigma(scc, false);
			nodeSet.add(scc);
			sigma.setUnrecoverable(scc, shouldCompactSCC, nodeSet, sigmaFactory);
		}
		queue.removeLast();
		Debugger.log(DBG_MAINLOOPS,log_tabdepth+"NEW SCC sigma "+scc.getSigma());
		Debugger.log(DBG_MAINLOOPS,log_tabdepth+"NEW SCC = "+scc.statesToString(true));
		Debugger.log(DBG_MAINLOOPS,log_tabdepth+"ALL SCC = "+nodeSet);
		return scc;
	}

	private GsHierarchicalSigmaSet computeSigma(GsHierarchicalNode scc, boolean isCycle) {
		if (!shouldCompactSCC) return null;
		sigmaFactory.beginNewSigma();
		if (isCycle) sigmaFactory.addToNewSigma(scc);
		Set out = scc.getOutgoingEdges();
		for (Iterator it = out.iterator(); it.hasNext();) {
			GsHierarchicalNode node = (GsHierarchicalNode) it.next();
			node.compactMaster();
			System.out.println("On scc ("+isCycle+")"+scc.toString()+"::"+scc.toLongString()+" for node "+node.toString()+" -- "+node.toLongString()+" -- "+node.getSigma());
			System.out.println("---"+scc.getMaster()+"::"+scc.getMaster().getMaster()+" for node "+node.getMaster()+"::"+node.getMaster().getMaster());
			System.out.println("---"+node.getMaster().toLongString()+""+node.getMaster().getSigma());
			if (node.getOutgoingEdges() == null) {
				sigmaFactory.addToNewSigma(node);
			} else {
				sigmaFactory.addAllToNewSigma(node.getSigma().getSigmaImage());
			}
		}
		GsHierarchicalSigmaSet sigma = sigmaFactory.endNewSigma();
		scc.setSigma(sigma);
		System.out.println("Sigma computed for "+scc+" "+sigma.pathToString()+" --- "+scc.getSigma());
		return sigma;
	}

	/**
	 * 
	 * @param state
	 * @return
	 */
	private GsHierarchicalNode processStableState(byte[] state) {
		GsHierarchicalNode hnode = nodeSet.getHNodeForState(state);									//  If it already processed (in the nodeSet)	
		if (hnode != null) {
			Debugger.log(DBG_MAINLOOPS,log_tabdepth+"found stable state :"+print_state(state));
			return hnode;
		}
		index++;
		Debugger.log(DBG_MAINLOOPS,log_tabdepth+"found NEW stable state :"+print_state(state));
		hnode = new GsHierarchicalNode(childsCount);
		hnode.addState(state, 1);
		hnode.setType(GsHierarchicalNode.TYPE_STABLE_STATE);
		if (shouldCompactSCC) {
			sigmaFactory.beginNewSigma();
			sigmaFactory.addToNewSigma(hnode);
			hnode.setSigma(sigmaFactory.endNewSigma());			
		}
		nodeSet.add(hnode);
		return hnode;
	}

	/**
	 * Search __state__ in the queue
	 * @param state
	 * @return
	 */
	private HTGSimulationQueueItem getTripletInQueueForState(byte[] state) {
		for (Iterator it = queue.iterator(); it.hasNext();) {
			HTGSimulationQueueItem triplet = (HTGSimulationQueueItem) it.next();
			if (triplet.containsState(state)) {
				return triplet;
			}
		}
		return null;
	}

	private SimulationUpdater getUpdaterForState(byte[] state) {
   		SimulationUpdater updater = SimulationUpdater.getInstance(regGraph, params);
   		updater.setState(state, depth, null);
   		return updater;
	}

	private void addAllNodeTo() {
		int n = 0;
		Debugger.log(DBG_POSTTREATMENT,"Adding all nodes to the graph... ("+nodeSet.size()+")");
		GsVertexAttributesReader vreader = htg.getGraphManager().getVertexAttributesReader();
		for (Iterator it = nodeSet.iterator(); it.hasNext();) {
			n++;
			GsHierarchicalNode hnode = (GsHierarchicalNode) it.next();
			hnode.addAllTheStatesInQueue();
			hnode.releaseSigma();
			hnode.updateSize();
			htg.addVertex(hnode);
			vreader.setVertex(hnode);
			switch (hnode.getType()) {
			case GsHierarchicalNode.TYPE_STABLE_STATE:
				vreader.setShape(GsVertexAttributesReader.SHAPE_ELLIPSE);
				vreader.setBackgroundColor(GsHierarchicalNode.TYPE_STABLE_STATE_COLOR);
				break;
			case GsHierarchicalNode.TYPE_TRANSIENT_CYCLE:
				vreader.setBackgroundColor(GsHierarchicalNode.TYPE_TRANSIENT_CYCLE_COLOR);
				break;
			case GsHierarchicalNode.TYPE_TERMINAL_CYCLE:
				vreader.setShape(GsVertexAttributesReader.SHAPE_ELLIPSE);
				vreader.setBackgroundColor(GsHierarchicalNode.TYPE_TERMINAL_CYCLE_COLOR);
				break;
			case GsHierarchicalNode.TYPE_TRANSIENT_COMPONENT:
				Color color = null;
				if (hnode.getOutgoingEdges().size() == 0) color = GsHierarchicalNode.TYPE_EDEN_TRANSIENT_COMPONENT_COLOR; ///EDEN GARDEN TEST
				else if (hnode.getOutgoingEdges().size() == 1) {
					if (((GsHierarchicalNode)hnode.getOutgoingEdges().iterator().next()).equals(hnode)) {
						color = GsHierarchicalNode.TYPE_EDEN_TRANSIENT_COMPONENT_COLOR; ///EDEN GARDEN TEST
					}
				}
				if (color == null) {
					if (hnode.statesSet.getSizeOrOverApproximation() > 1) color = GsHierarchicalNode.TYPE_TRANSIENT_COMPONENT_COLOR;
					else color = GsHierarchicalNode.TYPE_TRANSIENT_COMPONENT_ALONE_COLOR;					
				}
				vreader.setBackgroundColor(color);
				break;
			default:
				break;
			}
			vreader.refresh();			
			Debugger.log(DBG_POSTTREATMENT,hnode+" added.");
		}
		Debugger.log(DBG_POSTTREATMENT," total of node added : "+n);
		Debugger.log(DBG_POSTTREATMENT," done");
	}
	
	private void addAllEdgesTo() {
		Debugger.log(DBG_POSTTREATMENT,"Adding all arcs to the graph...");
		int nbarc = 0;
		for (Iterator it = nodeSet.iterator(); it.hasNext();) {
			GsHierarchicalNode from = (GsHierarchicalNode) it.next();
			Debugger.log(DBG_POSTTREATMENT,"\tto "+from);
			Set tos = from.getOutgoingEdges();
			for (Iterator it2 = tos.iterator(); it2.hasNext();) {
				GsHierarchicalNode to = (GsHierarchicalNode) it2.next();
				boolean b = GsHierarchicalNode.addEdge(from, to, htg);
				if (b) nbarc++;
				Debugger.log(DBG_POSTTREATMENT,"\tfrom "+to+" --- "+b);
				
			}
			from.releaseEdges();
		}
		Debugger.log(DBG_POSTTREATMENT," ("+nbarc+") done");
	}


/* ****************** DEBUG AND Debugger.log STUFF**********/
	
	
	private static String print_state(byte[] t) {
		StringBuffer s = new StringBuffer();
		for (int i = 0 ; i < t.length ; i++){
			s.append(" "+t[i]);
		}
		return s.toString();
	}
	
	/**
	 * Throws an exception if condition to stop the algorithm are reached.
	 * 
	 * if max node reached
	 * if max depth reached
	 * if the Interrupt button has been pushed.
	 * 
	 * @throws Exception
	 */
	private void checkStopConditions() throws Exception { 
		if (maxnodes != 0 && nbnode >= maxnodes){
		    debug_o.println("maxnodes reached: " + maxnodes);
		    throw new GsException(GsException.GRAVITY_NORMAL, "Reached the maximum count of node");
		}
		if (maxdepth > 0 && depth >= maxdepth) {
		    throw new GsException(GsException.GRAVITY_NORMAL, "Reached the maximum depth");
		}
		if (!ready) {
		    throw new GsException(GsException.GRAVITY_NORMAL, Translator.getString("STR_interrupted"));
		}
		if (depth > max_depth_reached) max_depth_reached = depth;
	    if (frame != null) {
	    	if (System.currentTimeMillis() - lastDraw > 250) {
	    		frame.setProgress(nbinitialstates+", "+depth+"/"+max_depth_reached);
	    		lastDraw = System.currentTimeMillis();
	    	}
		}
	}
	
}
