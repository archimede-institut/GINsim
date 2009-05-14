package fr.univmrs.tagc.GINsim.reg2dyn;

import java.awt.Color;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
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
import fr.univmrs.tagc.common.datastore.HashSetForSet;
import fr.univmrs.tagc.common.manageressources.Translator;

public class DynamicalHierarchicalSimulation extends Simulation {
	
	private final boolean reduce = true;
	
	protected DynamicalHierarchicalSimulationHelper helper;
	private int depth = 0;
	private DynamicalHierarchicalSimulationQueuedState e, next;
	private GsDynamicalHierarchicalNode dhnode;
	private GsDynamicalHierarchicalNodeSet nodeSet;
	
	private final byte debug = -127; //FIXME : set debug to -1
	private PrintStream debug_o = System.err;
	private int totalNode = 0;
	private GsDynamicalHierarchicalGraph dynGraph;
	private GsGraphManager rgm;
	
	private final Set voidSet = new HashSet(1); //A set to bypass the null value problem in mergeTransientChilds

	private byte[] childsCount;

	private int vertexCount;

	public DynamicalHierarchicalSimulation(GsRegulatoryGraph regGraph, SimulationManager frame, GsSimulationParameters params) {
		this(regGraph, frame, params, true, true);
	}

	public DynamicalHierarchicalSimulation(GsRegulatoryGraph regGraph, SimulationManager frame, GsSimulationParameters params, boolean runNow) {
		this(regGraph, frame, params, runNow, true);
	}

    public DynamicalHierarchicalSimulation(GsRegulatoryGraph regGraph, SimulationManager frame, GsSimulationParameters params, boolean runNow, boolean useInit) {
		super(regGraph, frame, params, false, useInit);
		
		helper = new DynamicalHierarchicalSimulationHelper(regGraph, params);
   		updater = SimulationUpdater.getInstance(regGraph, params);
   		if (runNow) {
   			long time = System.currentTimeMillis();
   		    start();
   		    log(-100, "Total time : "+(System.currentTimeMillis()-time)+"ms");
   		}
	}
    
    
	/**
	 * Run the simulation, handle exception and build the graph.
	 */
	public GsGraph do_simulation() {
		this.dynGraph = helper.dynHieGraph;
		this.rgm = helper.regGraph.getGraphManager();
		GsDynamicalHierarchicalNode.rgm = rgm; //FIXME : remove me
		ready = true;
		try {
			nodeSet = new GsDynamicalHierarchicalNodeSet();
			childsCount = dynGraph.getChildsCount();
			vertexCount = rgm.getVertexCount();
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
			dhnode = createNodefrom(e, 2);													// - create a new node
			dhnode.setType(GsDynamicalHierarchicalNode.TYPE_STABLE_STATE);				// - its type is STABLE_STATE
			nodeSet.add(dhnode);														// - add the dhnode to the set of all dhnode.
		} else {																	//Else
			log(5,"      => Stable state, no next (already processed)");				//It has already been treated do nothing
		}
		e.setParentNextChild(dhnode);												//add this dhnode to the childs list of the parent of e
		e.tellParentOneChildIsProcess(1);
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
		while (updater.hasNext()) {														//For each children of qs
			e.totalChild++;																//  increase the count of child of qs
																						//  create a new QueuedState
			GsDynamicalHierarchicalNode master = nodeSet.getDHNodeForState(e.state);
			DynamicalHierarchicalSimulationQueuedState newqs = new DynamicalHierarchicalSimulationQueuedState(((SimulationQueuedState)updater.next()).state, depth, e);
			dhnode = nodeSet.getDHNodeForState(newqs.state);										//  get the associated dhnode for the state
			if (dhnode != null) {														//  if the state is associated with a dhnode
				if (!dhnode.isProcessed()) {											//      if the state has not already been processed
					log(10,"        ?next is in a cycle building => add current path to this cycle");
					if (!dhnode.equals(master)) {										// 			if the parent is not already in the cycle
						addToTheBuildingCycle(master);									//          	add it in the cycle
					} else {
						log(10, "            the parent is already in the cycle.");
					}
				} else {																//      else if the state is associated with a building cycle
					log(10,"        $already procedded : not queueing "+newqs+" : "+dhnode);
				}
				newqs.setParentNextChild(dhnode);										//     add this dhnode to the children list of the parent of e
				newqs.tellParentOneChildIsProcess(1);
			} else {																	//  else it is not associated
				queue.add(newqs);														//     add the child to the queue 
				shouldIncreaseDepth = true;
				log(10,"        $queueing "+newqs);
			}
		}
		if (shouldIncreaseDepth) {
            depth++;												//  increase the depth of the queue if there is at least on children added to the queue
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
	private void addToTheBuildingCycle(GsDynamicalHierarchicalNode master) throws Exception {
		DynamicalHierarchicalSimulationQueuedState cur = e;
		while (cur != null && !dhnode.equals(master)) {									// for state from e from parent to parent, while the state is not in the cycle
			if (master != null) {														//     If the current state is associated with a node
				log(10,"            cur "+cur+" is in another cycle merging : "+master);
				encounterANodeInACycle(cur, master);
			} else {
				if (!dhnode.contains(cur.state)) {										//     if the cycle does not contain the current state	
					dhnode.addStateToThePile(cur.state);		//         add the state to the cycle
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
		dhnode.addPileToOmdd(childsCount);
		dhnode.reduce();
	}

	/**
	 * IN : The queue is just pushed with all the new children of 'e', and there is at least one child in the queue (next != e)
	 * OUT : if the last child create a cycle, then creates it and remove the last child.
	 * @throws Exception
	 */
	private void runOnNext() throws Exception {
		if (next.previous != e) {
            throw new Exception("FAIL next.previous != e");
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
					dhnode.addStateToThePile(next.state);	//				add 'next' in the cycle
					log(10,"            adding state"+print_t(next.state));
				}
			}
			next.setParentNextChild(dhnode);											//		add this dhnode to the children list of the parent of e
			next.tellParentOneChildIsProcess(1);
			

			DynamicalHierarchicalSimulationQueuedState cur = e;
			while (cur != null && !cur.equals(next)) {									//		For each queued state from 'e' to 'next' in the queue (going up)
				if (master != null) {													//		If cur is already in a cycle
					encounterANodeInACycle(cur, master);
				} else if (master == null || master.equals(dhnode)){					//		Else if cur is not already in a cycle or if both cycle are the same
					nodeSet.add(dhnode);												//			add them to the list of dhnode
				}
				if (dhnode.contains(cur.state)) {
					log(10,"            state already in"+print_t(cur.state));
				} else {																//			If the cycle does not already contain 'cur'
					dhnode.addStateToThePile(cur.state);		//				add 'cur' in the cycle
					log(10,"            adding state"+print_t(cur.state));
				}
				cur = cur.previous;
				if (cur == null) {
                    break;
                }
				master = nodeSet.getDHNodeForState(cur.state);
			}//while
			dhnode.addPileToOmdd(childsCount);
			dhnode.reduce();
			dhnode.updateSize(childsCount);
			removeLastInQueue();														//	Remove the last item in the queue
		}//alreadyInTheQueue(next)
	}
	
	/**
	 * IN : the current state is already associated with a node.
	 * DURING : depending on the kind of node, they will be a merge, or a split...
	 * OUT : dhnode is updated and contains the current state
	 * 
	 * @param cur
	 * @param master
	 * @throws Exception
	 */
	private void encounterANodeInACycle(DynamicalHierarchicalSimulationQueuedState cur, GsDynamicalHierarchicalNode master) throws Exception {
		if (master.isCycle()) {													//			If the node is a cycle
			dhnode.merge(master, nodeSet, vertexCount, helper);		//				merge the current building cycle (dhnode) with the cycle of cur
		} else if (master.isTransient()) {										//			else if the node is transient
			throw new Exception("Error : transient "+master+" "+dhnode+" "+cur);
//			master.remove(cur.state, nodeSet);											//				remove the current state from the transient node
//			if (!dhnode.contains(cur.state)) {									//    			if the cycle does not contain the current state	
//				dhnode.addStateToThePile(cur.state, childsCount);	//        	 		add the state to the cycle
//				log(10,"            adding state"+print_t(cur.state));
//			} else {															//     			else
//				log(10,"            state already in"+print_t(cur.state));		//        	 		do nothing
//			}
		} else if (master.isStable()) {
			throw new Exception("Error : cannot merge with a stable state. master: "+master+" dhnode: "+dhnode);
		} else {
			throw new Exception("Error : unexpected type. master: "+master+" dhnode: "+dhnode);
		}
	}

	
	private void simplifyQueue() throws Exception {
		while (e != null && e.isProcessed()) { 																		//While all the successors of the state have been visited.
			dhnode = nodeSet.getDHNodeForState(e.state);
			if (dhnode == null) { 																					//    	If the state is not processed
				log(10,"      }all  childs of e have been processed"+e);
				//TODO : merge if ....
				dhnode = createNodefrom(e, 2);																			//			Create a new node from the current state
				nodeSet.add(dhnode);																				//			Add this node to the nodeSet
				log(10,"      }e "+e+" has a lot of transient childs "+dhnode.toLongString(vertexCount));
			} else {
				dhnode.processState(e.state, childsCount);												//		Process the current state in the current node
				log(10,"      }e "+e+" is already processed in "+dhnode.toLongString(vertexCount));
			}
			if (reduce) reduce();
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
			if (!e.isProcessed()) {
				throw new Exception("Error : a state is not fully processed "+e+" (("+dhnode.toLongString(vertexCount)+"))");
			}
			if (dhnode.root.testStatus(e.state) != 2) {
				throw new Exception("Error : a node is not fully processed for state "+e+" "+dhnode.toLongString(vertexCount)+"\n"+dhnode.statesToString(vertexCount, true));
			}
			removeLastInQueue();																					//		Remove the last item in the queue
			e = getLastInQueue();																					//		e is set to the next last item in the queue
			if (e == null && !dhnode.isProcessed()) {
				throw new Exception("Error : a node is not fully processed "+dhnode.toLongString(vertexCount)+"\n"+dhnode.statesToString(vertexCount, true));
			}
		}
		log(2,"      }set e @["+queue.size()+"]:"+e);
	}	
	
	private void reduce() throws Exception {
		int size = e.childs.size();
		boolean merge = size == 1;
		if (size > 1) {
			mergeTransientChilds();
		}		
		if (merge) {//There is only one child or there are all the same (does work ?)
			GsDynamicalHierarchicalNode child = (GsDynamicalHierarchicalNode) e.childs.get(0);
			if (child.isTransient() && dhnode.isTransient()) {
				log(-127,"         }merging e with its only child "+e+" :: "+child.toLongString(vertexCount));
				child.merge(dhnode, nodeSet, vertexCount, helper);
				child.reduce();
				child.updateSize(childsCount);
			}
		}
		
//		
//		Set transientComponants = new HashSet();	
//		GsDynamicalHierarchicalNode child = null;
//		for (Iterator it = e.childs.iterator(); it.hasNext();) {
//			child = (GsDynamicalHierarchicalNode) it.next();
//			if (child.isTransient()) {
//				transientComponants.add(child);
//			}
//		}
//		if (transientComponants.size() > 1) {															//IF there is more than one transient child
//																											//::Merge transient childs going one to another with same other childs
//			Set mergedChilds = null;
//			for (Iterator it_comp = transientComponants.iterator(); it_comp.hasNext();) {				//For each transient child 'from'
//				GsDynamicalHierarchicalNode from = (GsDynamicalHierarchicalNode) it_comp.next();
//				
//				Set fromEdgesSet = from.getOutgoingEdges();
//				if (fromEdgesSet != null) {
//					for (Iterator it = e.childs.iterator(); it.hasNext();) { 							//For each outgoing edges 'to' of 'from'
//						GsDynamicalHierarchicalNode to = (GsDynamicalHierarchicalNode) it.next();
//						if (to.equals(from) || !to.isTransient()) {//If to equals from or 'to' is not transient
//                            continue;																			//Skip 'to'
//                        }
//						Set toEdgesSet = to.getOutgoingEdges();
//						if (toEdgesSet != null) {
//							toEdgesSet.add(to);
//							if (fromEdgesSet.containsAll(toEdgesSet)) {
//								toEdgesSet.remove(to);
//								log(10,"      } merging (inclusion) "+from.toLongString(vertexCount)+" and "+to.toLongString(vertexCount));
//								if (mergedChilds == null) {
//									mergedChilds = new HashSet();
//								}
//								mergedChilds.add(to);
//								from.merge(to, nodeSet, vertexCount, helper);
//								from.reduce();
//							} else {
//								toEdgesSet.remove(to);
//							}
//						}
//					}
//				}
//			}
//			if (mergedChilds != null) {											//If we have merged some transient childs, remove them from the transient list.
//				for (Iterator it = mergedChilds.iterator(); it.hasNext();) {
//					child = (GsDynamicalHierarchicalNode) it.next();
//					transientComponants.remove(child);
//				}
//			}
//		} 
//		if (transientComponants.size() == 1) { //There is only one TRANSIENT child, then merge the current node in the child
//			dhnode = (GsDynamicalHierarchicalNode) transientComponants.iterator().next();
//			dhnode.addState(e.state, childsCount, 1); //FIXME here not 1
//			dhnode.reduce();
//			log(15,"      }e "+e+" has only one transient child "+dhnode.toLongString(vertexCount));
//		} else {
//		}
	}
	
	/**
	 * 
	 * 
	 * 
	 * @return true if there is only one remaining child
	 * @throws Exception 
	 */
	private boolean mergeTransientChilds() throws Exception {
		Map terminals;
		HashSetForSet results, s, sprime, s1,s2;
		GsDynamicalHierarchicalNode c1, c2;
		
		terminals = new HashMap(2);
		log(-127, "  e : "+e);
		for (Iterator it1 = e.childs.iterator(); it1.hasNext();) {
			c1 = (GsDynamicalHierarchicalNode) it1.next();
			if (c1.isTransient()) {
				for (Iterator it2 = c1.getOutgoingEdges().iterator(); it2.hasNext();) {
					c2 = (GsDynamicalHierarchicalNode) it2.next();
					s = (HashSetForSet) terminals.get(c2);
					if (s == null) {
						s = new HashSetForSet(2);
						s.add(c1);
						terminals.put(c2, s);
					}
					s.add(c1);						
				}
			} //else c1 is terminal, there is a direct path from e to c1, fusion will take place later if needed.
		}
		
		for (Iterator it = terminals.keySet().iterator(); it.hasNext();) {
			GsDynamicalHierarchicalNode key = (GsDynamicalHierarchicalNode)it.next();
			Set terminal = (Set) terminals.get(key);
			log(-127, "  "+key.getShortId()+" <- ");
			for (Iterator it3 = terminal.iterator(); it3.hasNext();) {
				GsDynamicalHierarchicalNode node = (GsDynamicalHierarchicalNode) it3.next();
				log(-127, "     "+node.getShortId());
			}
		}
				
		results = new HashSetForSet(2);
		s = null;
		for (Iterator it = terminals.values().iterator(); it.hasNext();) {
			HashSetForSet terminal = (HashSetForSet) it.next();
			for (Iterator ita = terminal.iterator(); ita.hasNext();) { log(-127, "   on terminal : "+((GsDynamicalHierarchicalNode)ita.next()).getShortId());}
			for (Iterator it2 = results.iterator(); it2.hasNext();) {
				HashSetForSet result = (HashSetForSet) it2.next();
				for (Iterator ita = result.iterator(); ita.hasNext();) { log(-127, "      on result : "+((GsDynamicalHierarchicalNode)ita.next()).getShortId());}
				if (terminal.size() == 1 && result.size() == 1) {
					for (Iterator it3 = terminal.iterator(); it3.hasNext();) {
						GsDynamicalHierarchicalNode node = (GsDynamicalHierarchicalNode) it3.next();
						if (result.remove(node)) { //Try to remove, and if succeed
							it3.remove();
						}
					}
					continue;
				} else if (terminal.size() > result.size()) {
					s1 = result;
					s2 = terminal;
				} else {
					s2 = result;
					s1 = terminal;
				}
				for (Iterator it3 = s1.iterator(); it3.hasNext();) {
					GsDynamicalHierarchicalNode node = (GsDynamicalHierarchicalNode) it3.next();
					log(-127, "          on node "+node.getShortId());
					if (s2.remove(node)) { //Try to remove, and if succeed
						it3.remove();
						if (s == null) {
							s = new HashSetForSet(1);
						}
						s.add(node);
						log(-127, "          remove s1 et s2 "+node.getShortId());
					}
				}
				while (s != null && s.size() > 0) {
					sprime = new HashSetForSet(1);
				}
				if (result.size() == 0) {
					log(-127, "      remove result"+results.size());
					it2.remove();
					log(-127, "      aremove result"+results.size());
				}
				if (terminal.size() == 0) {
					it.remove();
					break;
				}
			}
			if (s != null && s.size() > 0) {
				results.add(s);
				for (Iterator ita = s.iterator(); ita.hasNext();) { log(-127, "   adding s : "+((GsDynamicalHierarchicalNode)ita.next()).getShortId());}
			}
			s = null;
			if (terminal.size() > 0) {
				results.add(terminal);
				for (Iterator ita = terminal.iterator(); ita.hasNext();) { log(-127, "   adding terminal : "+((GsDynamicalHierarchicalNode)ita.next()).getShortId());}
			}
		}
		
		for (Iterator it = results.iterator(); it.hasNext();) {
			Set set = (Set) it.next();
			log(-127, "    ["+set.size());

			for (Iterator it2 = set.iterator(); it2.hasNext();) {
				GsDynamicalHierarchicalNode node = (GsDynamicalHierarchicalNode) it2.next();
				log(-127, "       "+node.getShortId());
			}
		}
		for (Iterator it = results.iterator(); it.hasNext();) {
			Set set = (Set) it.next();
			if (set.size() > 1) {
				Iterator it2 = set.iterator(); 
				GsDynamicalHierarchicalNode n0 = (GsDynamicalHierarchicalNode) it2.next();
				while (it2.hasNext()) {
					GsDynamicalHierarchicalNode n1 = (GsDynamicalHierarchicalNode) it2.next();
					log(-127, "   merging "+n0.getShortId()+" et "+n1.getShortId());
					n0.merge(n1, nodeSet, vertexCount, helper);
				}
				n0.reduce();
				n0.updateSize(childsCount);
			} else {
				it.remove();
			}
		}
		if (results.size() == 1) {
			return true;
		}
		return false;
	}
	
	

	/**
	 * remove the last item in the queue and decrease the depth
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
		return createNodefrom(qs, 1);
	}
	
	/**
	 * Create a node from the QueuedState qs, and add it to the list.
	 * @param qs
	 * @return
	 */
	private GsDynamicalHierarchicalNode createNodefrom(DynamicalHierarchicalSimulationQueuedState qs, int childValue) {
		GsDynamicalHierarchicalNode dhnode = new GsDynamicalHierarchicalNode(qs.state, childsCount, childValue); //TODO: more complex merge
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
//                debug_o.println("        processNextAlreadyProcessed::from : ["+from.state_l+"]"+print_t(from.state)+" tmp_dhnode::"+tmp_dhnode.toLongString(vertexCount));
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
		if (queue.size() > 0) {
			return (DynamicalHierarchicalSimulationQueuedState)queue.getLast();
		} else {
			return null;
		}
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
	 * @throws Exception
	 */
	private void checkStopConditions() throws Exception {
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
		if (debug == -127) {
			int ptotalNode = totalNode;
			totalNode = nodeSet.size();
			if (totalNode > ptotalNode &&  totalNode%100 == 0) debug_o.println("totalNode = "+totalNode);
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
