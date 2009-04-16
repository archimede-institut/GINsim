package fr.univmrs.tagc.GINsim.reg2dyn;

import java.awt.Color;
import java.io.PrintStream;
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
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.InitialStatesIterator;
import fr.univmrs.tagc.common.GsException;
import fr.univmrs.tagc.common.Tools;
import fr.univmrs.tagc.common.manageressources.Translator;

public class DynamicalHierarchicalSimulation extends Simulation {
	
	protected DynamicalHierarchicalSimulationHelper helper;
	private int depth = 0;
	private DynamicalHierarchicalSimulationQueuedState e, next;
	private GsDynamicalHierarchicalNode dhnode;
	private GsDynamicalHierarchicalNodeSet nodeSet;
	
	private final short debug = 2;
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
		this.dynGraph = helper.dynHieGraph;
		this.rgm = helper.regGraph.getGraphManager();
		
		ready = true;
		try {
			nodeSet = new GsDynamicalHierarchicalNodeSet();
			
			while(initStatesIterator.hasNext()) { // iterate through initial states and run the simulation from each of them
				updateTimer();
				short[] state = (short[])initStatesIterator.next();
				
				if (debug > 5) debug_o.println("\nInit queue with state :"+print_t(state));
				
				depth = 0;
				e = new DynamicalHierarchicalSimulationQueuedState(state, depth, null);
				if (nodeSet.get(e.state) != null) { //If the new state has already been processed, pass
					if (debug > 5) debug_o.println("  Already processed, continue");
					continue;
				} else {
					queue.add(e);
				}
				while (!queue.isEmpty()) {
					if (debug > 5) debug_o.println("\n  processing:"+e);
					if (false) {
						for (Iterator it = queue.iterator(); it.hasNext();) {
							debug_o.println(it.next());
						}
					}
					updateGUI(); //GUI stuff, nothing to deal with the main algo

					updater.setState(e.state, depth, e); //To get the list of next states of 'e'
					if (!updater.hasNext()) { 	//If there is no next states for 'e' => 'e' is a stable state
						dhnode = (GsDynamicalHierarchicalNode) nodeSet.get(e.state);
						if (dhnode == null) {
							if (debug > 1) debug_o.println("      => Stable state, no next");
							dhnode = createNodefrom(e);
							dhnode.setType(GsDynamicalHierarchicalNode.TYPE_STABLE_STATE);
							nodeSet.add(dhnode);
						} else {
							if (debug > 5) debug_o.println("      => Stable state, no next (already processed)");
						}
						e.setParentNextChild(dhnode);
						queue.pollLast();
					} else {
						queueNextStatesOf(e);
						if (debug > 10) debug_o.println("      !queue size="+queue.size());
						processNextAlreadyProcessed(e, getLastInQueue());
						next = getLastInQueue();
						if (debug > 10) debug_o.println("      +set next :"+next + "eee"+e);
						if (next != null && !next.equals(e) && alreadyInTheQueue(next)) { // If next in Pile_p \ next with Pile_p the pile of previous only
							if (debug > 10) debug_o.println("        ?alreadyInTheQueue==true => creating a cycle");
							DynamicalHierarchicalSimulationQueuedState cur = next.previous;
							GsDynamicalHierarchicalNode master = (GsDynamicalHierarchicalNode) nodeSet.get(cur.state);
							if (master == null) {
								dhnode = createNodefrom(next);
								dhnode.setType(GsDynamicalHierarchicalNode.TYPE_CYCLE);
								if (debug > 5) debug_o.println("          creating a new Node");
								if (debug > 10) debug_o.println("            with state  "+print_t(next.state));
							} else {
								dhnode = master;
								dhnode.addStateToThePile(next.state, dynGraph.getChildsCount());
								if (debug > 5) debug_o.println("          get an existing Node");
								if (debug > 10) debug_o.println("            adding state"+print_t(next.state));
							}
							next.setParentNextChild(dhnode);
							while (cur != null && !cur.equals(next)) {
								if (master != null) {
									master.merge(dhnode, nodeSet, helper);
									dhnode = master;
									//propagateInQueue(master) //TODO : will implies a perf increase with less aliasing in the DHnodes ???
								} else if (master == null || master.equals(dhnode)){
									nodeSet.add(dhnode);
								}
								dhnode.addStateToThePile(cur.state, dynGraph.getChildsCount());
								if (debug > 10) debug_o.println("            adding state"+print_t(cur.state));
								cur.setParentNextChild(dhnode);
								cur = cur.previous;
								master = (GsDynamicalHierarchicalNode) nodeSet.get(cur.state);
							}
							dhnode.addPileToOmdd();
							dhnode.reduce();
							dhnode.updateSize(rgm.getVertexCount());
							if (cur != null) {
								cur.setParentNextChild(dhnode);
							}
							queue.pollLast();
						}
					}//if stable else not stable
					e = getLastInQueue();
					if (debug > 10) debug_o.println("      ++set e @["+queue.size()+"]:"+e);
					
					//FIXME : does  && e.childs.size() >= e.totalChild usefull ? because if we are on e then all its childs have been added before and removed as it is the last in the queue ?
					while (e != null && e.childs != null) { //All the successors of the state have been visited. //FIXME : node >= but ==
						dhnode = (GsDynamicalHierarchicalNode) nodeSet.get(e.state);
						if (debug > 10) debug_o.println("      }all  childs of e have been processed"+e+" ["+dhnode+"]");
						if (dhnode == null) { //If the state is not processed, we process him in regards to its childs, add the links.
							Set transientComponants = new HashSet();	
							GsDynamicalHierarchicalNode child = null;
							for (Iterator it = e.childs.iterator(); it.hasNext();) {
								child = ((GsDynamicalHierarchicalNode) it.next()).getMasterNode();
								if (child.getType() == GsDynamicalHierarchicalNode.TYPE_TRANSIENT_COMPONENT) {
									transientComponants.add(child);
								}
							}
							if (transientComponants.size() > 1) {
								//Merge transient childs going one to another with same other childs
								Set mergedChilds = null;
								for (Iterator it_comp = transientComponants.iterator(); it_comp.hasNext();) {
									GsDynamicalHierarchicalNode from = (GsDynamicalHierarchicalNode) it_comp.next();
									
									Set fromEdgesSet = helper.arcsFrom(from);
									if (fromEdgesSet != null) {
										for (Iterator it = e.childs.iterator(); it.hasNext();) {
											GsDynamicalHierarchicalNode to = ((GsDynamicalHierarchicalNode) it.next()).getMasterNode();
											if (to.equals(from)) continue;
											Set toEdgesSet = helper.arcsFrom(to);
											if (toEdgesSet != null) {
												toEdgesSet.add(to);
												if (fromEdgesSet.containsAll(toEdgesSet)) {
													toEdgesSet.remove(to);
													if (debug > 10) debug_o.println("      } merging (inclusion) "+from+" and "+to);
													if (mergedChilds == null) {
														mergedChilds = new HashSet();
													}
													mergedChilds.add(to);
													from.merge(to, nodeSet, helper);
													from.reduce();
												} else {
													toEdgesSet.remove(to);
												}
											}
										}
									}
								}
								if (mergedChilds != null) {
									for (Iterator it = mergedChilds.iterator(); it.hasNext();) {
										child = ((GsDynamicalHierarchicalNode) it.next()).getMasterNode();
										transientComponants.remove(child);
									}
								}
							} 
							if (transientComponants.size() == 1) { //There is only one TRANSIENT child, then merge the current node in the child
								dhnode = (GsDynamicalHierarchicalNode) transientComponants.iterator().next();
								dhnode.addState(e.state, dynGraph.getChildsCount());
								dhnode.reduce();
								if (debug > 15) debug_o.println("      }e has only one transient child "+dhnode);
							} else {
								//TODO : merge if ....
								dhnode = createNodefrom(e);
								nodeSet.add(dhnode);
								if (debug > 10) debug_o.println("      }e has a lot of transient childs "+dhnode);
							}
						} else {
							if (debug > 10) debug_o.println("      }e is already processed in "+dhnode);
						}
						for (Iterator it = e.childs.iterator(); it.hasNext();) {
							GsDynamicalHierarchicalNode child = (GsDynamicalHierarchicalNode) it.next();
							if (child != dhnode) {
								helper.addEdge(dhnode, child);				
							}
						}
						e.setParentNextChild(dhnode);
						queue.pollLast();
						e = getLastInQueue();
					}
					if (debug > 2) debug_o.println("      }set e @["+queue.size()+"]:"+e);
				}//while queue is empty
			} //foreach initial state
			
			if (debug > 100) {
				for (Iterator it = nodeSet.iterator(); it.hasNext();) {
					debug_o.println(it.next());
				}
				for (Iterator it = helper.arcs.keySet().iterator(); it.hasNext();) {
					debug_o.println(it.next());
				}
			}
			
		} catch (GsException e) {
			if (debug > 0)debug_o.println("simulation was interrupted");
		} catch (OutOfMemoryError e) {
		    Tools.error("Out Of Memory", null);
		    return null;
		} catch (Exception e) {
			debug_o.println("Error : "+e.getMessage());
			e.printStackTrace();
		}
		addAllNodeTo(helper.getGraph());
		addAllEdgesTo(helper.arcs, helper.getGraph());
		updateTerminalCycles(helper.getGraph());
		return helper.endSimulation();
	}

	/**
	 * Indicates if the cur is already in the queue (him excepted) by looking from previous to previous.
	 * @param cur the queuedState to begin from
	 * @return true if cur is already in the queue.
	 */
	private boolean alreadyInTheQueue(DynamicalHierarchicalSimulationQueuedState cur) {
		if (debug > 20) debug_o.println("      &Testing if next is already in queue...");
		if (next.state_l < 0) next.hash();
		if (debug > 20) debug_o.println("        $alreadyInTheQueue::next : "+print_t(next.state)+" -- "+next.state_l);
		cur = cur.previous;
		while (cur != null) {
			if (debug > 20) debug_o.println("        $alreadyInTheQueue::cur  : "+print_t(cur.state)+" -- "+cur.state_l);
			if (cur.equals(next)) return true;
			cur = cur.previous;
		}
		return false;
	}
	
	/**
	 * Remove the processed items in the queue of the 'queue' and mark their previous and add the right arcs.
	 */
	private void processNextAlreadyProcessed(DynamicalHierarchicalSimulationQueuedState e, DynamicalHierarchicalSimulationQueuedState from) {
		GsDynamicalHierarchicalNode tmp_dhnode = (GsDynamicalHierarchicalNode)nodeSet.get(from.state);
		while(tmp_dhnode != null) { //FIXME : !from.previous.equals(e) && 
			if (debug > 10) debug_o.println("        processNextAlreadyProcessed::from : ["+from.state_l+"]"+print_t(from.state)+" tmp_dhnode::"+tmp_dhnode);
			from.setParentNextChild(tmp_dhnode);
			queue.pollLast();
			from = getLastInQueue();
			if (from == null) return;
			tmp_dhnode = (GsDynamicalHierarchicalNode)nodeSet.get(from.state);
		}
	}


	/**
	 * Append to the queue all the states reachable from qs.
	 * @param qs
	 */
	private void queueNextStatesOf(DynamicalHierarchicalSimulationQueuedState qs) {
		if (debug > 10) debug_o.println("      &Queueing successors..."+queue.size());
		int totalChild = 0;
		while (updater.hasNext()) {
			queue.add(new DynamicalHierarchicalSimulationQueuedState(((SimulationQueuedState)updater.next()).state, depth++, qs));
			if (debug > 10) debug_o.println("        $queueing "+queue.getLast());
			totalChild++;
		}
		qs.totalChild = totalChild;
	}
	
	/**
	 * Create a node from the QueuedState qs, and add it to the list.
	 * @param qs
	 * @return
	 */
	private GsDynamicalHierarchicalNode createNodefrom(DynamicalHierarchicalSimulationQueuedState qs) {
		GsDynamicalHierarchicalNode dhnode = new GsDynamicalHierarchicalNode(qs.state, dynGraph.getChildsCount()); //TODO: more complex merge
		nodeSet.add(dhnode);
		if (debug > 10) debug_o.println("        %create a new node from state "+qs);
		return dhnode;
	}

	private void updateTerminalCycles(GsDynamicalHierarchicalGraph graph) {
		if (debug > 1) debug_o.print("Updating terminal cycles...");
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
		if (debug > 1) debug_o.println("done");
	}

	private void addAllEdgesTo(Map arcs, GsDynamicalHierarchicalGraph graph) {
		if (debug > 1) debug_o.print("Adding all arcs to the graph ("+arcs.size()+")...");
		for (Iterator it = arcs.keySet().iterator(); it.hasNext();) {
			GsDynamicalHierarchicalNode from = (GsDynamicalHierarchicalNode) it.next();
			GsDynamicalHierarchicalNode source = from.getMasterNode();
			if (debug > 15) debug_o.println("  from "+source);
			Set targets = (Set)arcs.get(from);
			for (Iterator it2 = targets.iterator(); it2.hasNext();) {
				GsDynamicalHierarchicalNode target = ((GsDynamicalHierarchicalNode) it2.next()).getMasterNode();
				if (!source.equals(target))	graph.addEdge(source, target);
			}
		}
		arcs = null;
		if (debug > 1) debug_o.println("done");
	}

	private void addAllNodeTo(GsDynamicalHierarchicalGraph graph) {
		if (debug > 1) debug_o.print("Adding all nodes to the graph ("+nodeSet.size()+")...");
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
			if (debug > 15)debug_o.println(dhnode+" added.");
		}
		if (debug > 1) debug_o.println("done");
	}
	
	/**
	 * @return the last item in 'queue'
	 */
	private DynamicalHierarchicalSimulationQueuedState getLastInQueue() {
		return (DynamicalHierarchicalSimulationQueuedState)queue.peekLast();
	}

	public static String print_t(short[] t) {
		StringBuffer s = new StringBuffer();
		for (int i = 0 ; i < t.length ; i++){
			s.append(" "+t[i]);
		}
		return s.toString();
	}
	
	private void updateGUI() throws GsException {
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
	
	private void updateTimer() {
		nbnode++;
	    if (frame != null) {
	    	if (nbnode % 10 == 0) {
                frame.setProgress(nbnode); 
	    	}
		}
	}
}