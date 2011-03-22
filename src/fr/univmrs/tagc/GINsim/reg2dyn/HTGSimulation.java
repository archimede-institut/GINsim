package fr.univmrs.tagc.GINsim.reg2dyn;

import java.awt.Color;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.graph.GsGraphManager;
import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.tagc.GINsim.hierachicalTransitionGraph.GsHierarchicalNode;
import fr.univmrs.tagc.GINsim.hierachicalTransitionGraph.GsHierarchicalNodeSet;
import fr.univmrs.tagc.GINsim.hierachicalTransitionGraph.GsHierarchicalTransitionGraph;
import fr.univmrs.tagc.GINsim.reg2dyn.helpers.HTGSimulationHelper;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.common.GsException;
import fr.univmrs.tagc.common.Tools;
import fr.univmrs.tagc.common.manageressources.Translator;


/**
 * 
 * 
 * Two variables for the algorithm : e and n, the currEnt state and the Next state.
 * 
 * @author duncan
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
	
	private static final byte debug = DBG_DEPLOYMENT; //Work as a mask, use val | val | val .... //FIXME : set to DBG_DEPLOYMENT before deploying
	private PrintStream debug_o = System.err;
	private StringBuffer log_tabdepth = new StringBuffer();
	
	/**
	 * Indicates if the transient SCC should be compacted into a hierarchical transition graph.
	 */
	private boolean shouldCompactSCC;
	
	
	private GsHierarchicalTransitionGraph htg;
	/**
	 * The GsGraphManager of the regulatory graph
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
	 * The count of genes in the RegulatoryGraph
	 */
	private int index;
	private int depth;

	private GsSimulationParameters params;
	
	
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
		addAllNodeTo();									// add all nodes to the graph
		addAllEdgesTo();								// add all the edges to the graph
		updateTerminalCycles();							// indicates if a cycle is terminal
		log(-100, "Simulation done in : "+(System.currentTimeMillis()-time)+"ms");
		return helper.endSimulation();
	}


	/* ****************** DEBUG AND LOG STUFF**********/

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
			log(DBG_MAINLOOPS,log_tabdepth+"New initial state :"+print_state(state));               						
			                                                                                        						
			GsHierarchicalNode processed_hnode = nodeSet.getHNodeForState(state);											//  Search __state__ in the nodeSet
			if (processed_hnode  == null) { 																				//  If the new state was not in the nodeSet, that is has not been processed
				SimulationUpdater updater = getUpdaterForState(state);														//    Get the updater of the state
				if (!updater.hasNext()) {                                                                                   //    If it has no successor
					processStableState(state);                                                                           	//      Process it as a stable state
					continue;                                                                                                     
				}                                                                                                                 
				HTGSimulationQueuedState e = new HTGSimulationQueuedState(state, index, index, updater);					//    Create __e__ a queue item with the state, index and updater
				updateProgess();																							//    Update the GUI to indicates the progress (ie. one node processed)
				depth = -1;                                                                                                 //    Set the depth to -1, (as it will be incremented immediatly to 0)
				explore(e);                                                                                                 //    Call the recursive dunction explore() on __e__.
			} else {
				log(DBG_MAINLOOPS,log_tabdepth+"\tAlready processed :"+processed_hnode.getUniqueId());
			}
		}

	}

	/**
	 * The reccursive function of the algorithm.
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
	private GsHierarchicalNode explore(HTGSimulationQueuedState e) throws Exception {
		checkStopConditions();
		
		HTGSimulationQueuedState n = null;
		log_tabdepth.append('\t');
		log(DBG_MAINLOOPS,log_tabdepth+"Exploring :"+e);
		index++;
		depth++;
		Set e_sigma = new HashSet(); 
		
		queue.add(e);																										//Queueing the current state
		log(DBG_QUEUE,log_tabdepth+"queue :"+queue);                                            							
		while (e.updater.hasNext()) {																						//For each successors
			byte[] n_state= ((SimulationQueuedState)e.updater.next()).state;												// n_state is the state of the successor
			SimulationUpdater n_updater = getUpdaterForState(n_state);                          							
			if (!n_updater.hasNext()) {																						// n_state has no child No child => stable state
				GsHierarchicalNode stableState = processStableState(n_state);
				e.addOutgoingHNode(stableState);
				e_sigma.addAll(stableState.getSigma());
				continue;
			}

			log(DBG_MAINLOOPS,log_tabdepth+"nextState :"+print_state(n_state));
			n = getTripletInQueueForState(n_state);															   				//Search the state in the queue
			if (n != null) {																				   				//If found
				log(DBG_APPARTENANCETESTS,log_tabdepth+"in P :"+n);                         				   				
				e.low_index = Math.min(e.low_index, n.index);												   				//  update the index
			} else {																						   				//Else the state is not in the queue
				log(DBG_APPARTENANCETESTS,log_tabdepth+"not in P");                         				   				 
				GsHierarchicalNode n_hnode = nodeSet.getHNodeForState(n_state);								   				//  If it already processed (in the nodeSet)	
				if (n_hnode != null) {                                                                         				
					log(DBG_APPARTENANCETESTS,log_tabdepth+"in N :"+n_hnode.getUniqueId());                    				
					e.addOutgoingHNode(n_hnode);                                                               				
					e_sigma.addAll(n_hnode.getSigma());                                                      				
					continue;																				   				//     then continue to the next successor	
				} else {																					   				//  Else
					log(DBG_APPARTENANCETESTS,log_tabdepth+"not in N");                     				   				 
					n = new HTGSimulationQueuedState(n_state, index, index, n_updater);						   				//     explore it
					n_hnode = explore(n);																					//     update the index
					e.low_index = Math.min(e.low_index, n.low_index);
					if (n_hnode != null) {
						e.addOutgoingHNode(n_hnode);
						e_sigma.addAll(n_hnode.getSigma());
					}
				}
			}
		}
		log(DBG_MAINLOOPS,log_tabdepth+"Comparing indexes "+e);
		if (e.index == e.low_index) {
			Set e_dhnode_successors = new HashSet();
			GsHierarchicalNode new_hnode = new GsHierarchicalNode(childsCount);
			HTGSimulationQueuedState tmp;
			log(DBG_QUEUE,log_tabdepth+"\tunqueuing:"+queue);
			do {
				tmp = (HTGSimulationQueuedState) queue.removeLast();
				log(DBG_QUEUE,log_tabdepth+"\tunqueuing:"+queue);
				new_hnode.addStateToThePile(tmp.state);
				for (Iterator it = tmp.getOutgoindHNodes().iterator(); it.hasNext();) {
					GsHierarchicalNode node = (GsHierarchicalNode) it.next();
					node.addIncomingEdge(new_hnode);
					e_dhnode_successors.add(node);
					e_sigma.addAll(node.getSigma()); //merge the sigmas
				}
			} while (!tmp.equals(e));
			new_hnode.addAllTheStatesInPile();
			if (new_hnode.getSize() == 1) {
				new_hnode.setType(GsHierarchicalNode.TYPE_TRANSIENT_COMPONENT);
			} else {
				new_hnode.setType(GsHierarchicalNode.TYPE_TRANSIENT_CYCLE);
				e_sigma.add(new_hnode);
			}
			log(DBG_SIGMA,log_tabdepth+"\tsigma:"+new_hnode.getSigma());
			new_hnode.setSigma(e_sigma);
			if (shouldCompactSCC && new_hnode.isTransient()) {
				log(DBG_MERGE,log_tabdepth+"\t\tsigma_new"+e_sigma);
				for (Iterator it = e_dhnode_successors.iterator(); it.hasNext();) {
					GsHierarchicalNode node = (GsHierarchicalNode) it.next();
					log(DBG_MERGE,log_tabdepth+"\t\tsigma_suc"+node.getSigma());
					if (node.isTransient() && e_sigma.equals(node.getSigma())) {
						log(DBG_MERGE,log_tabdepth+"\t\t\tshould merge "+new_hnode+" with "+node);
						new_hnode.merge(node, nodeSet);
					}
				}
			}

			
			nodeSet.add(new_hnode);
			
			log(DBG_QUEUE,log_tabdepth+"\tunqueuing:"+queue+"\n"+log_tabdepth+"done");
			log(DBG_MAINLOOPS,log_tabdepth+"NEW SCC = "+new_hnode.statesToString(true));
			log_tabdepth.deleteCharAt(log_tabdepth.length()-1);
			return new_hnode;
		}
		log_tabdepth.deleteCharAt(log_tabdepth.length()-1);
		return null;
	}

	
	private GsHierarchicalNode processStableState(byte[] state) {
		GsHierarchicalNode hnode = nodeSet.getHNodeForState(state);									//  If it already processed (in the nodeSet)	
		if (hnode != null) {
			log(DBG_MAINLOOPS,log_tabdepth+"found stable state :"+print_state(state));
			return hnode;
		}
		index++;
		log(DBG_MAINLOOPS,log_tabdepth+"found NEW stable state :"+print_state(state));
		hnode = new GsHierarchicalNode(childsCount);
		hnode.addState(state, GsHierarchicalNode.STATUS_PROCESSED);
		hnode.setType(GsHierarchicalNode.TYPE_STABLE_STATE);
		hnode.getSigma().add(hnode);
		nodeSet.add(hnode);
		return hnode;
	}

	private HTGSimulationQueuedState getTripletInQueueForState(byte[] state) {
		for (Iterator it = queue.iterator(); it.hasNext();) {
			HTGSimulationQueuedState triplet = (HTGSimulationQueuedState) it.next();
			if (Arrays.equals(triplet.state, state)) {
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
		log(DBG_POSTTREATMENT,"Adding all nodes to the graph... ("+nodeSet.size()+")");
		GsVertexAttributesReader vreader = htg.getGraphManager().getVertexAttributesReader();
		for (Iterator it = nodeSet.iterator(); it.hasNext();) {
			n++;
			GsHierarchicalNode hnode = (GsHierarchicalNode) it.next();
			hnode.addAllTheStatesInPile();
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
				vreader.setBackgroundColor(GsHierarchicalNode.TYPE_CYCLE_COLOR);
				break;
//			case GsHierarchicalNode.TYPE_TERMINAL_CYCLE:
//				vreader.setShape(GsVertexAttributesReader.SHAPE_ELLIPSE);
//				vreader.setBackgroundColor(GsHierarchicalNode.TYPE_TERMINAL_CYCLE_COLOR);
//				break;
			case GsHierarchicalNode.TYPE_TRANSIENT_COMPONENT:
				Color color = null;
				if (hnode.getIncomingEdges().size() == 0) color = GsHierarchicalNode.TYPE_EDEN_TRANSIENT_COMPONENT_COLOR; ///EDEN GARDEN TEST
				else if (hnode.getIncomingEdges().size() == 1) {
					if (((GsHierarchicalNode)hnode.getIncomingEdges().iterator().next()).equals(hnode)) {
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
			log(DBG_POSTTREATMENT,hnode+" added.");
		}
		log(DBG_POSTTREATMENT," total of node added : "+n);
		log(DBG_POSTTREATMENT," done");
	}
	
	private void addAllEdgesTo() {
		log(DBG_POSTTREATMENT,"Adding all arcs to the graph...");
		int nbarc = 0;
		for (Iterator it = nodeSet.iterator(); it.hasNext();) {
			GsHierarchicalNode to = (GsHierarchicalNode) it.next();
			log(DBG_POSTTREATMENT,"\tto "+to);
			Set froms = to.getIncomingEdges();
			for (Iterator it2 = froms.iterator(); it2.hasNext();) {
				GsHierarchicalNode from = (GsHierarchicalNode) it2.next();
				if (from.addEdge(to, htg)) nbarc++;
			}
			to.releaseEdges();
		}
		log(DBG_POSTTREATMENT," ("+nbarc+") done");
	}

	private void updateTerminalCycles() {
		log(DBG_POSTTREATMENT,"Updating terminal cycles...");
		GsGraphManager gm = htg.getGraphManager();
		GsVertexAttributesReader vreader = gm.getVertexAttributesReader();
		for (Iterator it = nodeSet.iterator(); it.hasNext();) {
			GsHierarchicalNode hnode = (GsHierarchicalNode) it.next();
			if (hnode.isCycle() && gm.getOutgoingEdges(hnode).size() == 0) {
				hnode.setType(GsHierarchicalNode.TYPE_TERMINAL_CYCLE);
				vreader.setVertex(hnode);
				vreader.setShape(GsVertexAttributesReader.SHAPE_ELLIPSE);
				vreader.setBackgroundColor(GsHierarchicalNode.TYPE_TERMINAL_CYCLE_COLOR);
				vreader.refresh();
			}
		}
		log(DBG_POSTTREATMENT,"done");
	}



/* ****************** DEBUG AND LOG STUFF**********/
	
	
	public static String print_state(byte[] t) {
		StringBuffer s = new StringBuffer();
		for (int i = 0 ; i < t.length ; i++){
			s.append(" "+t[i]);
		}
		return s.toString();
	}
	
	public void log(int mask, String msg) {
		if ((debug & mask) != 0) {
            debug_o.println(msg);
        }
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

	}
	
	private void updateProgess() {
		nbnode++;
	    if (frame != null) {
            frame.setProgress(nbnode); 
            if (debug == -127) debug_o.println(nbnode);
		}
	}
	
}
