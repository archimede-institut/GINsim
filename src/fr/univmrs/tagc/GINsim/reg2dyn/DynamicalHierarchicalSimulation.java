package fr.univmrs.tagc.GINsim.reg2dyn;

import java.awt.Color;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import fr.univmrs.tagc.GINsim.dynamicalHierachicalGraph.GsDynamicalHierarchicalGraph;
import fr.univmrs.tagc.GINsim.dynamicalHierachicalGraph.GsDynamicalHierarchicalNode;
import fr.univmrs.tagc.GINsim.dynamicalHierachicalGraph.GsDynamicalHierarchicalNodeSet;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.graph.GsGraphManager;
import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.common.GsException;
import fr.univmrs.tagc.common.Tools;
import fr.univmrs.tagc.common.manageressources.Translator;

public class DynamicalHierarchicalSimulation extends Simulation {
	
	protected DynamicalHierarchicalSimulationHelper helper;
	private int depth = 0;
	private DynamicalHierarchicalSimulationQueuedState e, next;
	private GsDynamicalHierarchicalNode dhnode;
	private GsDynamicalHierarchicalNodeSet nodeSet;
	
	private final byte debug = 11; //FIXME : set debug to 0
	private PrintStream debug_o = System.err;
	private GsDynamicalHierarchicalGraph dynGraph;
	private GsGraphManager rgm;

	public DynamicalHierarchicalSimulation(GsRegulatoryGraph regGraph, SimulationManager frame, GsSimulationParameters params) {
		this(regGraph, frame, params, true, true);
	}

	public DynamicalHierarchicalSimulation(GsRegulatoryGraph regGraph, SimulationManager frame, GsSimulationParameters params, boolean runNow) {
		this(regGraph, frame, params, runNow, true);
	}

    public DynamicalHierarchicalSimulation(GsRegulatoryGraph regGraph, SimulationManager frame, GsSimulationParameters params, boolean runNow, boolean useInit) {
		super(regGraph, frame, params, false, useInit);
		
		if (params.buildSTG == GsSimulationParameters.BUILD_DHG) {
			helper = new DynamicalHierarchicalSimulationHelper(regGraph, params);
		}
   		updater = SimulationUpdater.getInstance(regGraph, params);
   		if (runNow) {
   		    start();
   		}
	}
    
    
	/**
	 * Run the simulation, handle exception and build the graph.
	 */
	public GsGraph do_simulation() {
		this.dynGraph = helper.dynHieGraph;
		this.rgm = helper.regGraph.getGraphManager();
		ready = true;
		try {
			nodeSet = new GsDynamicalHierarchicalNodeSet();
			
			runSimulationOnInitialStates();									// run the simulation for each initial states
						
		} catch (GsException e) {
            debug_o.println("simulation was interrupted");
		} catch (OutOfMemoryError e) {
		    Tools.error("Out Of Memory", null);
		    return null;
		} catch (Exception e) {
			debug_o.println("Error : "+e.getMessage());
			e.printStackTrace();
		}
		addAllNodeTo(helper.getGraph());									// add all nodes to the graph
		addAllEdgesTo(helper.getGraph());									// add all the edges to the graph
		updateTerminalCycles(helper.getGraph());							// indicates if a cycle is terminal
		return helper.endSimulation();
	}
	
	/**
	 * Run the simulation on all the initial states defined by initStatesIterator
	 * @throws Exception 
	 */
	private void runSimulationOnInitialStates() throws Exception {
		while(initStatesIterator.hasNext()) { 										//For each initial states
			byte[] state = (byte[])initStatesIterator.next();						//  fetch the current initial state.
			updateProgess();														//  Update the GUI to indicates the progress (ie. one node processed)
			log(5,"\nInit queue with state :"+print_t(state));
			
			depth = 0;
			e = new DynamicalHierarchicalSimulationQueuedState(state, depth, null);	// +set 'e' is a new queuedState for this initial state.
			if (nodeSet.getDHNodeForState(state) == null) { 										//  If the new state has not been processed
				queue.add(e);														//		queue this state
				runOnQueue();														//		run the simulation on the queue.
			} else {																//  else
				log(5,"  Already processed, continue");								//     continue to the next state
			}
		}
	}
	
	/**
	 * Run the simulation on the queue.
	 * 
	 * queue (List) contains only one element, the initial state e (QueuedState)
	 * @throws Exception 
	 */
	private void runOnQueue() throws Exception {
		while (!queue.isEmpty()) {													//While the queue is not empty
			log(5,"\n  processing:"+e);
			logQueue(50);
			
			checkStopConditions(); 													//  check if the simulation can continue

			updater.setState(e.state, depth, e);									//  retrieve the list of next states (children) of 'e'

			if (!updater.hasNext()) { 												//  if there is no children => 'e' is a stable state
				processStableState();												//     process e
			} else {																//  else
				queueNextStatesOf();												//     queue the next child (not already processed) of e
				next = getLastInQueue();											//  set the 'next' to the last state in the queue
				log(10,"      +set next :"+next + "eee"+e);

				if (next != null && !next.equals(e)) {								//  If there is a next (not equals to e)
					runOnNext();													//     process it
				}
			}
			
			e = getLastInQueue();													//  set the 'e' to the last state in the queue
			log(10,"      ++set e @["+queue.size()+"]:"+e);	
			
			simplifyQueue();														//  remove the states with all their children processed from the queue 
		}
	}
	
	/**
	 * IN : Called when a stable state is encounter
	 * OUT: The stable state has been processed and removed from the queue
	 */
	private void processStableState() {
		dhnode = nodeSet.getDHNodeForState(e.state);												//Get the dhnode associated to e
		if (dhnode == null) {														//If it has no dhnode associated
			log(1,"      => Stable state, no next");
			dhnode = createNodefrom(e);													// - create a new node
			dhnode.setType(GsDynamicalHierarchicalNode.TYPE_STABLE_STATE);				// - its type is STABLE_STATE
			nodeSet.add(dhnode);														// - add the dhnode to the set of all dhnode.
		} else {																	//Else
			log(5,"      => Stable state, no next (already processed)");				//It has already been treated do nothing
		}
		e.setParentNextChild(dhnode);												//add this dhnode to the childs list of the parent of e
		e.tellParentOneChildIsProcess(1);
		dhnode.processState(e.state, dynGraph.getChildsCount());					//indicate to the dhnode that one of its element has been processed
		removeLastInQueue();														//Remove it from the queue
	}
	

	/**
	 * Append to the queue all the states reachable from qs.
	 * 
	 * IN : a queuedState at the bottom of the queue
	 * DURING :	
	 *   - compute the count of processed / total children
	 *   - process the children to add in a building cycle
	 * OUT : All the children that are not already associated are added to the queue, the count of processed children/total children is up to date. 
	 * 
	 * @param e
	 * @throws Exception 
	 */
	private void queueNextStatesOf() throws Exception {
		log(10,"      &Queueing successors...");
		boolean shouldIncreaseDepth = false;
		if (e.totalChild == -1) {
            e.totalChild = 0;
        }
		while (updater.hasNext()) {													//For each children of qs
			e.totalChild++;															//  increase the count of child of qs
																					//  create a new QueuedState
			DynamicalHierarchicalSimulationQueuedState newqs = new DynamicalHierarchicalSimulationQueuedState(((SimulationQueuedState)updater.next()).state, depth, e);
			dhnode = nodeSet.getDHNodeForState(newqs.state);										//  get the associated dhnode for the state
			if (dhnode != null) {													//  if the state is associated with a dhnode
				if (!dhnode.isProcessed()) {										//      if the state has not already been processed
					log(10,"        ?next is in a cycle building => add current path to this cycle");
					addToTheBuildingCycle(newqs);								//          add it in the cycle
				} else {															//      else if the state is associated with a building cycle
					log(10,"        $already procedded : not queueing "+newqs+" : "+dhnode);
				}
				newqs.setParentNextChild(dhnode);									//     add this dhnode to the children list of the parent of e
				newqs.tellParentOneChildIsProcess(1);
			} else {																//  else it is not associated
				queue.add(newqs);													//     add the child to the queue 
				shouldIncreaseDepth = true;
				log(10,"        $queueing "+newqs);
			}
		}
		if (shouldIncreaseDepth) {
            depth++;											//  increase the depth of the queue if there is at least on children added to the queue
        }
	}
	
	/**
	 * IN : a queued state (parentqs) has a child (cycleqs) in a building cycle (dhnode)
	 * OUT : the cycle is increased with the path "x -> parentqs" where x is the first previous member of the cycle in the queue
	 * 
	 * @param e the current queuedState
	 * @param cycleqs one of its children
	 * @throws Exception 
	 */
	private void addToTheBuildingCycle(DynamicalHierarchicalSimulationQueuedState cycleqs) throws Exception {
		DynamicalHierarchicalSimulationQueuedState cur = e;
		GsDynamicalHierarchicalNode master = nodeSet.getDHNodeForState(cur.state);

		if (dhnode.equals(master)) {													// if the parent is  already in the cycle
			log(10, "            the parent is already in the cycle.");
		}
		
		while (cur != null && !cur.equals(next) && !dhnode.equals(master)) {			// for state from parentqs from parent to parent, while the state is not in the cycle
			if (master != null) {														//     If the current state is in a cycle
				log(10,"            cur "+cur+" is in another cycle merging : "+master);
				
				if (master.isStable()) {
					throw new GsException(1,"Error : cannot merge with a stable state. master: "+master+" dhnode: "+dhnode);
				}
				
				master.setType(GsDynamicalHierarchicalNode.TYPE_CYCLE);					//			set transient type to cycle. //FIXME remove line when removing the throw in merge
				dhnode.merge(master, nodeSet, rgm.getVertexCount(), helper);			//			merge the current building cycle (dhnode) with the cycle of cur
			} else {
				if (!dhnode.contains(cur.state)) {										//     if the cycle does not contain the current state	
					dhnode.addStateToThePile(cur.state, dynGraph.getChildsCount());		//         add the state to the cycle
					log(10,"            adding state"+print_t(cur.state));
				} else {																//     else
					log(10,"            state already in"+print_t(cur.state));			//         do nothing
				}
			}
			cur = cur.previous;
			if (cur != null) {
				master = nodeSet.getDHNodeForState(cur.state);
			}
		}
		dhnode.addPileToOmdd(dynGraph.getChildsCount());
		dhnode.reduce();
	}

	/**
	 * IN : The queue is just pushed with all the new children of 'e', and there is at least one child in the queue (next != e)
	 * OUT : if the last child create a cycle, then creates it and remove the last child.
	 * @throws Exception
	 */
	private void runOnNext() throws Exception {
		if (next.previous != e) {
            throw new GsException(1, "FAIL next.previous != e");
        }
		GsDynamicalHierarchicalNode master = null;
		if (alreadyInTheQueue(next)) { 													// If 'next' is already in the queue : there is a path from 'next' to ... to 'next' => its a cycle
			log(10,"        ?alreadyInTheQueue==true => creating a cycle");
			master = nodeSet.getDHNodeForState(e.state);
			if (master == null) {														//     If e is not already processed
				dhnode = createNodefrom(next);											//			create a new dhnode from next
				dhnode.setType(GsDynamicalHierarchicalNode.TYPE_CYCLE);					//			mark it as a cycle
				log(5,"          creating a new Node");
				log(10,"            with state  "+print_t(next.state));
			} else {																	//		Else cur is already in a CFC (!! prove its a cycle)
				dhnode = master;
				log(5,"          get an existing Node");
				if (dhnode.contains(next.state)) {										
					log(10,"            state already in"+print_t(next.state));
				} else {																//			If the cycle does not already contain 'next'
					dhnode.addStateToThePile(next.state, dynGraph.getChildsCount());	//				add 'next' in the cycle
					log(10,"            adding state"+print_t(next.state));
				}
			}
			next.setParentNextChild(dhnode);											//		add this dhnode to the children list of the parent of e
			next.tellParentOneChildIsProcess(1);
			

			DynamicalHierarchicalSimulationQueuedState cur = e;
			while (cur != null && !cur.equals(next)) {									//		For each queued state from 'e' to 'next' in the queue (going up)
				if (master != null) {													//		If cur is already in a cycle
					master.merge(dhnode, nodeSet, rgm.getVertexCount(), helper);		//			merge the current building cycle (dhnode) with the cycle of cur
					dhnode = master;													//			dhnode is the result of the merge
				} else if (master == null || master.equals(dhnode)){					//		Else if cur is not already in a cycle or if both cycle are the same
					nodeSet.add(dhnode);												//			add them to the list of dhnode
				}
				if (dhnode.contains(cur.state)) {
					log(10,"            state already in"+print_t(cur.state));
				} else {																//			If the cycle does not already contain 'cur'
					dhnode.addStateToThePile(cur.state, dynGraph.getChildsCount());		//				add 'cur' in the cycle
					log(10,"            adding state"+print_t(cur.state));
				}
				cur = cur.previous;
				if (cur == null) {
                    break;
                }
				master = nodeSet.getDHNodeForState(cur.state);
			}//while
			dhnode.addPileToOmdd(dynGraph.getChildsCount());
			dhnode.reduce();
			dhnode.updateSize(dynGraph.getChildsCount());
			removeLastInQueue();
		}//alreadyInTheQueue(next)
	}
	
	private void simplifyQueue() throws Exception {
		while (e != null && e.isProcessed()) { 																		//While all the successors of the state have been visited.
			dhnode = nodeSet.getDHNodeForState(e.state);
			if (dhnode == null) { 																					//    	If the state is not processed
				log(10,"      }all  childs of e have been processed"+e);
				//TODO : merge if ....
				dhnode = createNodefrom(e);																			//			Create a new node from the current state
				nodeSet.add(dhnode);																				//			Add this node to the nodeSet
				log(10,"      }e "+e+" has a lot of transient childs "+dhnode.toLongString(rgm.getVertexCount()));
			} else {
				log(10,"      }e "+e+" is already processed in "+dhnode.toLongString(rgm.getVertexCount()));
			}
			for (Iterator it = e.childs.iterator(); it.hasNext();) {												//		For each children of the current set
				GsDynamicalHierarchicalNode child = (GsDynamicalHierarchicalNode) it.next();						
				if (!child.equals(dhnode)) {																		//			If the child is not the current node
					helper.addEdge(dhnode, child);																	//				Add an edge from the current node to the child
				}
			}
			if (e.previous != null) {																				//		If e has a previous in the queue
				e.setParentNextChild(dhnode);																		//			Add the current node to the set of children of the previous of e
				e.tellParentOneChildIsProcess(1);
			}
			dhnode.processState(e.state, dynGraph.getChildsCount());												//		Process the current state in the current node
			removeLastInQueue();																					//		Remove the last item in the queue
			e = getLastInQueue();																					//		e is set to the next last item in the queue
		}
		log(2,"      }set e @["+queue.size()+"]:"+e);
	}	
	
	private void todo() throws Exception {
		Set transientComponants = new HashSet();	
		GsDynamicalHierarchicalNode child = null;
		for (Iterator it = e.childs.iterator(); it.hasNext();) {
			child = (GsDynamicalHierarchicalNode) it.next();
			if (child.isTransient()) {
				transientComponants.add(child);
			}
		}
		if (transientComponants.size() > 1) {															//IF there is more than one transient child
																											//::Merge transient childs going one to another with same other childs
			Set mergedChilds = null;
			for (Iterator it_comp = transientComponants.iterator(); it_comp.hasNext();) {				//For each transient child 'from'
				GsDynamicalHierarchicalNode from = (GsDynamicalHierarchicalNode) it_comp.next();
				
				Set fromEdgesSet = from.getOutgoingEdges();
				if (fromEdgesSet != null) {
					for (Iterator it = e.childs.iterator(); it.hasNext();) { 							//For each outgoing edges 'to' of 'from'
						GsDynamicalHierarchicalNode to = (GsDynamicalHierarchicalNode) it.next();
						if (to.equals(from) || !to.isTransient()) {//If to equals from or 'to' is not transient
                            continue;																			//Skip 'to'
                        }
						Set toEdgesSet = to.getOutgoingEdges();
						if (toEdgesSet != null) {
							toEdgesSet.add(to);
							if (fromEdgesSet.containsAll(toEdgesSet)) {
								toEdgesSet.remove(to);
								log(10,"      } merging (inclusion) "+from.toLongString(rgm.getVertexCount())+" and "+to.toLongString(rgm.getVertexCount()));
								if (mergedChilds == null) {
									mergedChilds = new HashSet();
								}
								mergedChilds.add(to);
								from.merge(to, nodeSet, rgm.getVertexCount(), helper);
								from.reduce();
							} else {
								toEdgesSet.remove(to);
							}
						}
					}
				}
			}
			if (mergedChilds != null) {											//If we have merged some transient childs, remove them from the transient list.
				for (Iterator it = mergedChilds.iterator(); it.hasNext();) {
					child = (GsDynamicalHierarchicalNode) it.next();
					transientComponants.remove(child);
				}
			}
		} 
		if (transientComponants.size() == 1) { //There is only one TRANSIENT child, then merge the current node in the child
			dhnode = (GsDynamicalHierarchicalNode) transientComponants.iterator().next();
			dhnode.addState(e.state, dynGraph.getChildsCount());
			dhnode.reduce();
			log(15,"      }e "+e+" has only one transient child "+dhnode.toLongString(rgm.getVertexCount()));
		} else {
		}
	}
	
	

	/**
	 * remove the last item in the queue and decrase the depth
	 */
	private void removeLastInQueue() {
		if (queue.size() > 1) { 
			DynamicalHierarchicalSimulationQueuedState qs = (DynamicalHierarchicalSimulationQueuedState) queue.get(queue.size()-2);
			DynamicalHierarchicalSimulationQueuedState qsl = (DynamicalHierarchicalSimulationQueuedState) queue.getLast();
			if (qs.depth == qsl.previous.depth) {
				depth--;				
			}
		} else if (queue.size() == 1) {
			depth--;
		}
		queue.removeLast();
	}
	
	/**
	 * Create a node from the QueuedState qs, and add it to the list.
	 * @param qs
	 * @return
	 */
	private GsDynamicalHierarchicalNode createNodefrom(DynamicalHierarchicalSimulationQueuedState qs) {
		GsDynamicalHierarchicalNode dhnode = new GsDynamicalHierarchicalNode(qs.state, dynGraph.getChildsCount()); //TODO: more complex merge
		nodeSet.add(dhnode);
		log(10,"        %create a new node from state "+qs+" dhnode: "+dhnode);
		return dhnode;
	}


	/**
	 * Indicates if the cur is already in the queue (him excepted) by looking from previous to previous.
	 * @param cur the queuedState to begin from
	 * @return true if cur is already in the queue.
	 */
	private boolean alreadyInTheQueue(DynamicalHierarchicalSimulationQueuedState cur) {
		log(20,"      &Testing if next is already in queue...");
		if (next.state_l < 0) {
            next.hash();
        }
		log(20,"        $alreadyInTheQueue::next : "+print_t(next.state)+" -- "+next.state_l);
		cur = cur.previous;
		while (cur != null) {
			log(20,"        $alreadyInTheQueue::cur  : "+print_t(cur.state)+" -- "+cur.state_l);
			if (cur.equals(next)) {
                return true;
            }
			cur = cur.previous;
		}
		return false;
	}
	
	/**
	 * Remove the processed items in the queue of the 'queue' and mark their previous and add the right arcs.
	 */
	private void processNextAlreadyProcessed(DynamicalHierarchicalSimulationQueuedState e, DynamicalHierarchicalSimulationQueuedState from) {
//		GsDynamicalHierarchicalNode tmp_dhnode = nodeSet.get(from.state);
//		while(tmp_dhnode != null) { //FIXME : !from.previous.equals(e) && 
//			if (debug > 10) {
//                debug_o.println("        processNextAlreadyProcessed::from : ["+from.state_l+"]"+print_t(from.state)+" tmp_dhnode::"+tmp_dhnode.toLongString(rgm.getVertexCount()));
//            }
//			from.setParentNextChild(tmp_dhnode);
//			queue.pollLast();
//			from = getLastInQueue();
//			if (from == null) {
//                return;
//            }
//			tmp_dhnode = nodeSet.get(from.state);
//		}
	}


	private void updateTerminalCycles(GsDynamicalHierarchicalGraph graph) {
		log(1,"Updating terminal cycles...");
		GsVertexAttributesReader vreader = graph.getGraphManager().getVertexAttributesReader();
		for (Iterator it = nodeSet.iterator(); it.hasNext();) {
			GsDynamicalHierarchicalNode dhnode = (GsDynamicalHierarchicalNode) it.next();
			if (dhnode.getType() == GsDynamicalHierarchicalNode.TYPE_CYCLE && graph.getGraphManager().getOutgoingEdges(dhnode).size() == 0) {
				dhnode.setType(GsDynamicalHierarchicalNode.TYPE_TERMINAL_CYCLE);
				vreader.setVertex(dhnode);
				vreader.setShape(GsVertexAttributesReader.SHAPE_ELLIPSE);
				vreader.setBackgroundColor(Color.BLUE);
				vreader.refresh();
			}
		}
		log(1,"done");
	}

	private void addAllEdgesTo(GsDynamicalHierarchicalGraph graph) {
		log(1,"Adding all arcs to the graph...");
		int nbarc = 0;
		for (Iterator it = nodeSet.iterator(); it.hasNext();) {
			GsDynamicalHierarchicalNode from = (GsDynamicalHierarchicalNode) it.next();
			log(15,"  from "+from);
			Set targets = from.getOutgoingEdges();
			for (Iterator it2 = targets.iterator(); it2.hasNext();) {
				GsDynamicalHierarchicalNode target = (GsDynamicalHierarchicalNode) it2.next();
				if (!from.equals(target)) {
                    graph.addEdge(from, target);
                    nbarc++;
                }
			}
			from.releaseEdges();
		}
		log(1," ("+nbarc+") done");
	}

	private void addAllNodeTo(GsDynamicalHierarchicalGraph graph) {
		log(1,"Adding all nodes to the graph... ("+nodeSet.size()+")");
		GsVertexAttributesReader vreader = graph.getGraphManager().getVertexAttributesReader();
		for (Iterator it = nodeSet.iterator(); it.hasNext();) {
			GsDynamicalHierarchicalNode dhnode = (GsDynamicalHierarchicalNode) it.next();
			graph.addVertex(dhnode);
			vreader.setVertex(dhnode);
			switch (dhnode.getType()) {
			case GsDynamicalHierarchicalNode.TYPE_STABLE_STATE:
				vreader.setShape(GsVertexAttributesReader.SHAPE_ELLIPSE);
				vreader.setBackgroundColor(Color.RED);
				break;
			case GsDynamicalHierarchicalNode.TYPE_TERMINAL_CYCLE:
				vreader.setShape(GsVertexAttributesReader.SHAPE_ELLIPSE);
				vreader.setBackgroundColor(Color.BLUE);
				break;
			case GsDynamicalHierarchicalNode.TYPE_CYCLE:
				vreader.setBackgroundColor(Color.CYAN);
				break;
			case GsDynamicalHierarchicalNode.TYPE_TRANSIENT_COMPONENT:
				vreader.setBackgroundColor(Color.GREEN);
				break;

			default:
				break;
			}
			vreader.refresh();			
			log(15,dhnode+" added.");
		}
		log(1," done");
	}
	
	/**
	 * @return the last item in 'queue'
	 */
	private DynamicalHierarchicalSimulationQueuedState getLastInQueue() {
		return (DynamicalHierarchicalSimulationQueuedState)queue.getLast();
	}

	public static String print_t(byte[] t) {
		StringBuffer s = new StringBuffer();
		for (int i = 0 ; i < t.length ; i++){
			s.append(" "+t[i]);
		}
		return s.toString();
	}
	
	public void log(int level, String msg) {
		if (debug >= level) {
            debug_o.println(msg);
        }
	}
	
	private void logQueue(int level) {
		if (debug > level) {
			for (Iterator it = queue.iterator(); it.hasNext();) {
				debug_o.println(it.next());
			}
		}
	}
	
	/**
	 * Throws an exception if condition to stop the algorithm are reached.
	 * 
	 * if max node reached
	 * if max depth reached
	 * if the Interrupt button has been pushed.
	 * 
	 * @throws GsException
	 */
	private void checkStopConditions() throws GsException {
		if (maxnodes != 0 && nbnode >= maxnodes){
		    debug_o.println("maxnodes reached: " + maxnodes);
		    throw new GsException(GsException.GRAVITY_NORMAL, "Reached the maximum count of node");
		}
		if (maxdepth > 0 && e.depth >= maxdepth) {
		    debug_o.println("maxdepth reached: " + getLastInQueue().depth);
		    throw new GsException(GsException.GRAVITY_NORMAL, "Reached the maximum depth");
		}
		if (!ready) {
		    throw new GsException(GsException.GRAVITY_NORMAL, Translator.getString("STR_interrupted"));
		}		
	}
	
	private void updateProgess() {
		nbnode++;
	    if (frame != null) {
	    	if (nbnode % 10 == 0) {
                frame.setProgress(nbnode); 
	    	}
		}
	}
}
