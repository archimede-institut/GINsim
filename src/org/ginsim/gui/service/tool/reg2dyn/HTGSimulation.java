package org.ginsim.gui.service.tool.reg2dyn;

import java.awt.Color;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;

import org.ginsim.exception.GsException;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.common.NodeAttributesReader;
import org.ginsim.graph.hierachicaltransitiongraph.HierarchicalNode;
import org.ginsim.graph.hierachicaltransitiongraph.HierarchicalNodeSet;
import org.ginsim.graph.hierachicaltransitiongraph.HierarchicalSigmaSet;
import org.ginsim.graph.hierachicaltransitiongraph.HierarchicalSigmaSetFactory;
import org.ginsim.graph.hierachicaltransitiongraph.HierarchicalTransitionGraph;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.service.tool.reg2dyn.helpers.HTGSimulationHelper;

import fr.univmrs.tagc.common.Debugger;
import fr.univmrs.tagc.common.Tools;
import fr.univmrs.tagc.common.managerresources.Translator;


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
	private static final int DBG_DOT = 64; 
	private static final int DBG_ALL = DBG_MAINLOOPS | DBG_POSTTREATMENT | DBG_APPARTENANCETESTS | DBG_QUEUE | DBG_SIGMA | DBG_MERGE; 
	
	private static final byte debug = DBG_DEPLOYMENT; //Work as a mask, use val | val | val ....
	private PrintStream debug_o = System.err;
	private StringBuffer log_tabdepth = new StringBuffer();
	
	/**
	 * Indicates if the transient SCC with the same sigma should be compacted into a single HierarchicalNode.
	 */
	private boolean shouldCompactSCC;
	
	/**
	 * The HierarchicalTransitionGraph in construction
	 */
	private HierarchicalTransitionGraph htg;
	/**
	 * The regulatory graph
	 */
	private RegulatoryGraph regGraph;
	
	/**
	 * An array indicating for each node in the nodeOrder their count of childs. (ie. their max value)
	 */
	private byte[] childsCount;
	/**
	 * A HashSet&lt;HierarchicalNode&gt; containg all the masters nodes.
	 */
	private HierarchicalNodeSet nodeSet;
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
	private int nbinitialstates = 0;
	private int step = 0;

	/**
	 * The simulation parameters
	 */
	private SimulationParameters params;
	
	private HierarchicalSigmaSetFactory sigmaFactory;
	
	private NodeAttributesReader vreader;

	
	public HTGSimulation(RegulatoryGraph regGraph, SimulationManager frame, SimulationParameters params) {
		this(regGraph, frame, params, true, true);
	}

	public HTGSimulation(RegulatoryGraph regGraph, SimulationManager frame, SimulationParameters params, boolean runNow) {
		this(regGraph, frame, params, runNow, true);
	}

    public HTGSimulation(RegulatoryGraph regGraph, SimulationManager frame, SimulationParameters params, boolean runNow, boolean useInit) {
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
	public Graph do_simulation() throws GsException{
		
		long time = System.currentTimeMillis();
		this.htg = (HierarchicalTransitionGraph) helper.getDynamicGraph();
		this.vreader = htg.getNodeAttributeReader();
		this.shouldCompactSCC = htg.areTransientCompacted();
		this.regGraph = (RegulatoryGraph) helper.getRegulatoryGraph();
		this.sigmaFactory = new HierarchicalSigmaSetFactory();
		Debugger.setDebug(debug);
		
//		try {
//			Debugger.setOut(new PrintStream("/Users/duncan/Desktop/a.out"));
//		} catch (FileNotFoundException e1) {
//			e1.printStackTrace();
//		}
		Debugger.log(DBG_MAINLOOPS,log_tabdepth+"Begin algorithm with param, shouldCompactSCC:"+shouldCompactSCC);               						
		ready = true;
		try {
			runSimulationOnInitialStates();									// run the simulation for each initial states
		} catch (GsException e) {
			debug_o.println("Error : "+e.getMessage());
            debug_o.println("simulation was interrupted");
		} catch (OutOfMemoryError e) {
		    Tools.error("Out Of Memory");
		    return null;
		} catch (Exception e) {
			debug_o.println("Error : "+e.getMessage());
			e.printStackTrace();
		}
		System.out.println("Simulation done in : "+(System.currentTimeMillis()-time)+"ms");
		addAllNodeTo();									// add all nodes to the graph
		addAllEdgesTo();								// add all the edges to the graph
		System.out.println("Graph created in : "+(System.currentTimeMillis()-time)+"ms");
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
		nodeSet = new HierarchicalNodeSet();
		childsCount = htg.getChildsCount();
		index = 0;

		while(initStatesIterator.hasNext()) { 																				//For each initial states
			byte[] state = (byte[])initStatesIterator.next();																//  __state__ is the current initial state.
			nbinitialstates++;
			Debugger.log(DBG_MAINLOOPS,log_tabdepth+"New initial state :"+print_state(state));               						
			                                                                                        						
			HierarchicalNode processed_hnode = nodeSet.getHNodeForState(state);											//  Search __state__ in the nodeSet
			if (processed_hnode  == null) { 																				//  If the new state was not in the nodeSet, that is has not been processed
				SimulationUpdater updater = getUpdaterForState(state);														//    Get the updater of the state
				if (!updater.hasNext()) {                                                                                   //    If it has no successor
					processStableState(state);                                                                           	//      Process it as a stable state
					continue;                                                                                                     
				}                                                                                                                 
				HTGSimulationQueueState e = new HTGSimulationQueueState(state, index, index);					//    Create __e__ a queue item with the state, index and updater
				depth = -1;                                                                                                 //    Set the depth to -1, (as it will be incremented immediatly to 0)
				max_depth_reached = -1;
				explore(e, updater);                                                                                                 //    Call the recursive dunction explore() on __e__.
			} else {
				Debugger.log(DBG_MAINLOOPS,log_tabdepth+"\tAlready processed :"+processed_hnode.getUniqueId());
			}
		}

	}

	/**
	 * The recursive function of the algorithm.
	 *
	 */
	private HierarchicalNode explore(HTGSimulationQueueState e, SimulationUpdater e_updater) throws Exception {
		checkStopConditions();
		
		HTGSimulationQueueItem n = null;
		log_tabdepth.append('\t');
		Debugger.log(DBG_MAINLOOPS,log_tabdepth+"Exploring :"+e);
		index++;
		depth++;
		
		queue.add(e);																										//Queueing the current state
		Debugger.log(DBG_QUEUE,log_tabdepth+"queue :"+queue);                                            							
		Debugger.log(DBG_DOT,"DOT::"+print_state(e.getState())+"[label=\""+print_state(e.getState())+"/"+index+"\", rank=\""+index+"\"]");
		Debugger.log(DBG_DOT,"NODES::"+print_state(e.getState())+"/"+index);
		int tmp_i_succ = 0;
		while (e_updater.hasNext()) {																						//For each successors
			byte[] n_state= ((SimulationQueuedState)e_updater.next()).state;												// n_state is the state of the successor
			Debugger.log(DBG_DOT,"EDGE::"+(++step)+" "+print_state(e.getState())+"/"+print_state(n_state));
			SimulationUpdater n_updater = getUpdaterForState(n_state);                          							
			if (!n_updater.hasNext()) {																						// n_state has no child No child => stable state
				processStableState(n_state);
				Debugger.log(DBG_DOT,"DOT::"+print_state(e.getState())+"->"+print_state(n_state)+"[label=\""+(tmp_i_succ++)+"\", color=red]");                          							
			} else {
				Debugger.log(DBG_MAINLOOPS,log_tabdepth+"nextState :"+print_state(n_state));
				n = getTripletInQueueForState(n_state);															   				//Search the state in the queue
				if (n != null) {																				   				//If found
					Debugger.log(DBG_APPARTENANCETESTS,log_tabdepth+"in P :"+n);
					HTGSimulationQueueSCC newCycleItem = cycleFound(n, index-1, n.getLow_index());
					e.setLow_index(Math.min(e.getLow_index(), newCycleItem.getLow_index()));												   				//  update the index
					Debugger.log(DBG_QUEUE,log_tabdepth+"\tqueue:"+queue);
					Debugger.log(DBG_DOT,"DOT::"+print_state(e.getState())+"->"+print_state(n_state)+"[label=\""+(tmp_i_succ++)+"\", color=black]");                          							
				} else {																						   				//Else the state is not in the queue
					Debugger.log(DBG_APPARTENANCETESTS,log_tabdepth+"not in P"+queue);                         				   				 
					HierarchicalNode n_hnode = nodeSet.getHNodeForState(n_state);								   				//  If it already processed (in the nodeSet)	
					if (n_hnode != null) {                                                                         				
						Debugger.log(DBG_APPARTENANCETESTS,log_tabdepth+"in N :"+n_hnode.getUniqueId());                    				
						Debugger.log(DBG_DOT,"DOT::"+print_state(e.getState())+"->"+print_state(n_state)+"[label=\""+(tmp_i_succ++)+"\", color=gray, style=dotted]");                          							
					} else {																					   				//  Else
						Debugger.log(DBG_APPARTENANCETESTS,log_tabdepth+"not in N "+nodeSet);                     				   				 
						n = new HTGSimulationQueueState(n_state, index, index);						   				//     explore it
						nbnode++;
						Debugger.log(DBG_DOT,"DOT::"+print_state(e.getState())+"->"+print_state(n_state)+"[label=\""+(tmp_i_succ++)+"\", color=red]");                          							
						n_hnode = explore((HTGSimulationQueueState) n, n_updater);																					//     update the index
						e.setLow_index(Math.min(e.getLow_index(), n.getLow_index()));
					}
				}
			}

		}
		Debugger.log(DBG_MAINLOOPS,log_tabdepth+"Comparing indexes "+e);
		if (e.isCycle() || e.getIndex() == e.getLow_index()) {
			HierarchicalNode hnode = buildSCC(e);
			log_tabdepth.deleteCharAt(log_tabdepth.length()-1);
			return hnode;
		}
		log_tabdepth.deleteCharAt(log_tabdepth.length()-1);
		return null;
	}
	
	/**
	 * A back edge in the queue has been found, and it points to __stopItemInQueue__.
	 * All the QueueItems in the queue up to __stopItemInQueue__ will be removed and merged into a single SCC, which is added to the queue.
	 * @param stopItemInQueue the target of the back edge
	 * @param index the current index
	 * @param low_index the current low index
	 * @return
	 */
	private HTGSimulationQueueSCC cycleFound(HTGSimulationQueueItem stopItemInQueue, int index, int low_index) {
		if (stopItemInQueue.isCycle() && queue.getLast() == stopItemInQueue) {
			Debugger.log(DBG_MERGE,log_tabdepth+"\tCycle found, no merge to do, its already in the last cycle in the queue");
			return (HTGSimulationQueueSCC) stopItemInQueue;
		}
		Debugger.log(DBG_QUEUE,log_tabdepth+"Cycle Found up to  "+stopItemInQueue);
		HierarchicalNode cycle = new HierarchicalNode(childsCount);
		cycle.setType(HierarchicalNode.TYPE_TRANSIENT_CYCLE);
		HTGSimulationQueueSCC newCycleItem = new HTGSimulationQueueSCC(cycle, index, low_index);
		newCycleItem .setSCC(cycle);
		HTGSimulationQueueItem n;
		do {
			n = (HTGSimulationQueueItem) queue.removeLast();
			if (n.getLow_index() < low_index) low_index = n.getLow_index();
			Debugger.log(DBG_QUEUE,log_tabdepth+"\tunqueuing:"+queue);
			if (n.isCycle()) {
				Debugger.log(DBG_MERGE,log_tabdepth+"\t\tmerge cycle "+cycle+" and "+n);
				cycle.merge(((HTGSimulationQueueSCC)n).getSCC(), nodeSet, sigmaFactory, htg);		//Merge all the states of the SCC in the cycle
			} else {
				cycle.addState(((HTGSimulationQueueState)n).getState(), 1);
				((HTGSimulationQueueState)n).setInCycle(newCycleItem);
			}
		} while (!n.equals(stopItemInQueue));
		newCycleItem.setLow_index(low_index);
		queue.add(newCycleItem);
		return newCycleItem;
	}
	
	/**
	 * Called when a head of a SCC is found on the stateItem __e__, to construct the new GSHierarchicalNode.
	 * If __e__ is not already in a HierarchicalNode (because it is in a cycle), it is added into a new HierarchicalNode.
	 * The GsHierarhicalNode __scc__, is added to the nodeSet, and its edges and its sigma is computed.
	 * @param e
	 * @return scc
	 */
	private HierarchicalNode buildSCC(HTGSimulationQueueItem e) {
		HierarchicalNode scc;
		HierarchicalNode inCycle = ((HTGSimulationQueueState) e).getInCycle(nodeSet, queue);
		boolean isCycle;
		//Init the scc
		if (inCycle != null) {
			Debugger.log(DBG_QUEUE,log_tabdepth+"\tthe state "+print_state(((HTGSimulationQueueState) e).getState())+" is in a cycle:"+inCycle+" queue:"+queue);
			scc = inCycle;
			isCycle = true;
		} else {
			scc = new HierarchicalNode(childsCount);
			scc.addState(((HTGSimulationQueueState) e).getState(), 1);
			scc.setType(HierarchicalNode.TYPE_TRANSIENT_COMPONENT);
			isCycle = false;
		}
		nodeSet.add(scc);
		Debugger.log(DBG_SIGMA,log_tabdepth+"\tnew scc:"+scc.toLongString());

		scc.addAllTheStatesInQueue();
		scc.statesSet.reduce();
		
		if (shouldCompactSCC) {
			sigmaFactory.beginNewSigma();
			if (isCycle) sigmaFactory.addToNewSigma(scc);
		}
		boolean isTerminal = true;
		for (Iterator it = scc.statesSet.statesToFullList().iterator(); it.hasNext();) { //compute the edges and the sigma
			byte[] state = (byte[]) it.next();
			SimulationUpdater updater = getUpdaterForState(state);						//    Get the updater of the state
			while (updater.hasNext()) {
				SimulationQueuedState successor = (SimulationQueuedState) updater.next();
				HierarchicalNode hnode = nodeSet.getHNodeForState(successor.state);
				if (!hnode.equals(scc)) {
					isTerminal = false;
					if (shouldCompactSCC) {
						if (hnode.isTerminal()) sigmaFactory.addToNewSigma(hnode);
						else sigmaFactory.addAllToNewSigma(hnode.getSigma().getSigmaImage());
					}
					scc.addEdgeTo(hnode);
				}
			}
		}
		HierarchicalSigmaSet sigma = null;
		if (shouldCompactSCC) {
			sigma = sigmaFactory.endNewSigma();
			scc.setSigma(sigma);
			Debugger.log(DBG_SIGMA,log_tabdepth+"\tsigma computed:"+sigma.pathToString());

		}
		if (isCycle) {
			if (isTerminal) {
				scc.setType(HierarchicalNode.TYPE_TERMINAL_CYCLE);
			} else {
				scc.setType(HierarchicalNode.TYPE_TRANSIENT_CYCLE);
			}
		} else {
			if (shouldCompactSCC) {
				if (sigma.getUnrecoverable() != null) Debugger.log(DBG_SIGMA,log_tabdepth+"set over unrecoverable "+sigma.getUnrecoverable());
				sigma.setUnrecoverable(scc, nodeSet, sigmaFactory, htg);
			}
		}
		
		queue.removeLast();
		Debugger.log(DBG_MAINLOOPS,log_tabdepth+"ALL SCC = "+nodeSet);
		return scc;
	}

	
	/**
	 * Called when __state__ is identified as a stable state, ie. has no successors.
	 * If the state is not already processed, ie. a HierarchicalNode exists, a new HierarchicalNode is created.
	 * 
	 * @param state
	 * @return the HierarchicalNode (newly created or already processed) of the stable state
	 */
	private HierarchicalNode processStableState(byte[] state) {
		HierarchicalNode hnode = nodeSet.getHNodeForState(state);									//  If it already processed (in the nodeSet)	
		if (hnode != null) {
			Debugger.log(DBG_MAINLOOPS,log_tabdepth+"found stable state :"+print_state(state));
			return hnode;
		}
		index++;
		nbnode++;
		Debugger.log(DBG_MAINLOOPS,log_tabdepth+"found NEW stable state :"+print_state(state));
		Debugger.log(DBG_DOT,"DOT::"+print_state(state)+"[label=\""+print_state(state)+"/"+index+"\",shape=\"rectangle\", rank=\""+index+"\"]");
		Debugger.log(DBG_DOT,"NODES::"+print_state(state)+"/"+index);
		hnode = new HierarchicalNode(childsCount);
		hnode.addState(state, 1);
		hnode.setType(HierarchicalNode.TYPE_STABLE_STATE);
		if (shouldCompactSCC) {
			sigmaFactory.beginNewSigma();
			sigmaFactory.addToNewSigma(hnode);
			hnode.setSigma(sigmaFactory.endNewSigma());			
			Debugger.log(DBG_SIGMA,log_tabdepth+"\tsigma computed:"+hnode.getSigma().pathToString());
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
		for (ListIterator it = queue.listIterator(queue.size()); it.hasPrevious();) {
			HTGSimulationQueueItem triplet = (HTGSimulationQueueItem) it.previous();
			if (triplet.containsState(state)) {
				return triplet;
			}
		}
		return null;
	}

	/**
	 * Create and initialize a SimulationUpdater for a given __state__.
	 * @param state
	 * @return
	 */
	private SimulationUpdater getUpdaterForState(byte[] state) {
   		SimulationUpdater updater = SimulationUpdater.getInstance(regGraph, params);
   		updater.setState(state, depth, null);
   		return updater;
	}

	/**
	 * Define the graphical properties (color, shape) of a hnode.
	 * @param hnode
	 */
	private void setHnodeGraphicalProperties(HierarchicalNode hnode) {
		vreader.setNode(hnode);
		switch (hnode.getType()) {
		case HierarchicalNode.TYPE_STABLE_STATE:
			vreader.setShape(NodeAttributesReader.SHAPE_ELLIPSE);
			vreader.setBackgroundColor(HierarchicalNode.TYPE_STABLE_STATE_COLOR);
			break;
		case HierarchicalNode.TYPE_TRANSIENT_CYCLE:
			vreader.setBackgroundColor(HierarchicalNode.TYPE_TRANSIENT_CYCLE_COLOR);
			break;
		case HierarchicalNode.TYPE_TERMINAL_CYCLE:
			vreader.setShape(NodeAttributesReader.SHAPE_ELLIPSE);
			vreader.setBackgroundColor(HierarchicalNode.TYPE_TERMINAL_CYCLE_COLOR);
			break;
		case HierarchicalNode.TYPE_TRANSIENT_COMPONENT:
			Color color = null;
			if (hnode.getIn().isEmpty()) {
				color = HierarchicalNode.TYPE_EDEN_TRANSIENT_COMPONENT_COLOR;
			} else if (hnode.statesSet.getSizeOrOverApproximation() > 1) {
				color = HierarchicalNode.TYPE_TRANSIENT_COMPONENT_COLOR;
			} else {
				color = HierarchicalNode.TYPE_TRANSIENT_COMPONENT_ALONE_COLOR;
			}
			vreader.setBackgroundColor(color);
			break;
		default:
			break;
		}
		vreader.refresh();			
	}
	
	/**
	 * Add all the nodes to the graph, update their size and set their graphical properties
	 */
	private void addAllNodeTo() {
		for (Iterator it = nodeSet.iterator(); it.hasNext();) {
			HierarchicalNode node = (HierarchicalNode) it.next();
			node.updateSize();
			htg.addNode(node);
			setHnodeGraphicalProperties(node);
		}
	}
	
	/**
	 * Add all the edges in the graph, and empty hnode.in and hnode.out
	 */
	private void addAllEdgesTo() {
		Debugger.log(DBG_POSTTREATMENT,"Adding all arcs to the graph...");
		int nbarc = 0;
		for (Iterator it = nodeSet.iterator(); it.hasNext();) {
			HierarchicalNode from = (HierarchicalNode) it.next();
			Debugger.log(DBG_POSTTREATMENT,"\tto "+from);
			Set tos = from.getOut();
			for (Iterator it2 = tos.iterator(); it2.hasNext();) {
				HierarchicalNode to = (HierarchicalNode) it2.next();
				Object b = htg.addEdge(from, to);
				if (b != null) nbarc++;
				Debugger.log(DBG_POSTTREATMENT,"\tfrom "+to+" --- "+b);
				
			}
			from.releaseEdges();
		}
		Debugger.log(DBG_POSTTREATMENT," ("+nbarc+") done");
		nodeSet.clear();
	}


	

/* ****************** DEBUG AND Debugger.log STUFF**********/
	
	
	private static String print_state(byte[] t) {
		StringBuffer s = new StringBuffer();
		for (int i = 0 ; i < t.length ; i++){
			s.append(""+t[i]);
		}
		return s.toString();
	}
	
	/**
	 * Throws an exception if any condition to stop the algorithm is reached (maxnode, maxdepth.
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
	    		frame.setProgress("init:"+nbinitialstates+", total:"+nbnode+", depth:"+depth+"/"+max_depth_reached);
	    		lastDraw = System.currentTimeMillis();
	    	}
		}
	}
	
}
