package fr.univmrs.tagc.GINsim.reg2dyn;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Iterator;

import fr.univmrs.tagc.GINsim.dynamicalHierachicalGraph.GsDynamicalHierarchicalNode;
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
	private static final int DBG_DEPLOYMENT = -1;
	private static final int DBG_MAINLOOPS = 1;
	private static final int DBG_POSTTREATMENT = 2;
	private static final int DBG_APPARTENANCETESTS = 4;
	private static final int DBG_QUEUE = 8;
	private static final int DBG_ALL = DBG_MAINLOOPS | DBG_POSTTREATMENT | DBG_APPARTENANCETESTS | DBG_QUEUE;
	
	private final byte debug = DBG_MAINLOOPS | DBG_POSTTREATMENT | DBG_ALL ; //FIXME : set to DBG_DEPLOYMENT before deploying
	private PrintStream debug_o = System.err;
	private StringBuffer tabdepth = new StringBuffer();
	
	
	private GsHierarchicalTransitionGraph htg;
	/**
	 * The GsGraphManager of the regulatory graph
	 */
	private GsRegulatoryGraph regGraph;
	/**
	 * The GsGraphManager of the regulatory graph
	 */
	private GsGraphManager rgm;
	
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
	private int genesCount;
	private int index;
	private int depth;

	private GsHierarchicalNode hnode;
	private GsSimulationParameters params;
	
	
	
//	
//	protected DynamicalHierarchicalSimulationHelper helper;
//	private int depth = 0;
//	private DynamicalHierarchicalSimulationQueuedState e, next;
//	private GsDynamicalHierarchicalNode dhnode;
//	private GsDynamicalHierarchicalNodeSet nodeSet;
//	
//
//	
//	private int totalNode = 0;
//	private GsDynamicalHierarchicalGraph dynGraph;
//	private GsGraphManager rgm;
//	
//	private byte[] childsCount;
//
//	private int vertexCount;
	
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
		this.regGraph = (GsRegulatoryGraph) helper.getRegulatoryGraph();
		this.rgm = regGraph.getGraphManager();
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
//		addAllEdgesTo(helper.getRegulatoryGraph());									// add all the edges to the graph
//		updateTerminalCycles(helper.getRegulatoryGraph());							// indicates if a cycle is terminal
		log(-100, "Simulation done in : "+(System.currentTimeMillis()-time)+"ms");
		return helper.endSimulation();
	}


	/* ****************** DEBUG AND LOG STUFF**********/

	/**
	 * The main entrance for the algorithm. Basically, initialize the main variables, and run the recursive explore function on every initial states
	 */
	private void runSimulationOnInitialStates() throws Exception {
		nodeSet = new GsHierarchicalNodeSet();
		childsCount = htg.getChildsCount();
		genesCount = rgm.getVertexCount();
		index = 0;
		
		while(initStatesIterator.hasNext()) { 										//For each initial states
			byte[] state = (byte[])initStatesIterator.next();						//  fetch the current initial state.
			log(DBG_MAINLOOPS,tabdepth+"New initial state :"+print_state(state));
			
			hnode = nodeSet.getHNodeForState(state);					//  If it already processed (in the nodeSet)	
			if (hnode == null) { 										//  If the new state has not been processed
				HTGSimulationQueuedState e = new HTGSimulationQueuedState(state, index, index, getUpdaterForState(state));
				if (!e.updater.hasNext()) {
					processStableState(e.state);
					continue;
				}
				updateProgess();														//  Update the GUI to indicates the progress (ie. one node processed)
				depth = -1;
				explore(e);
			} else {
				log(DBG_MAINLOOPS,tabdepth+"\tAlready processed :"+hnode.getUniqueId());
			}
		}

	}

	private void explore(HTGSimulationQueuedState e) {
		HTGSimulationQueuedState n = null;
		tabdepth.append('\t');
		log(DBG_MAINLOOPS,tabdepth+"Exploring :"+e);
		index++;
		depth++;
		
		queue.add(e);																		//Queueing the current state
		log(DBG_QUEUE,tabdepth+"queue :"+queue);
		while (e.updater.hasNext()) {															//For each successors
			byte[] n_state= ((SimulationQueuedState)e.updater.next()).state;					// n_state is the state of the successor
			SimulationUpdater n_updater = getUpdaterForState(n_state);
			if (!n_updater.hasNext()) {															// n_state has no child No child => stable state
				processStableState(n_state);
				continue;
			}

			log(DBG_MAINLOOPS,tabdepth+"nextState :"+print_state(n_state));
			n = getTripletInQueueForState(n_state);				//Search the state in the queue
			if (n != null) {																//If found
				log(DBG_APPARTENANCETESTS,tabdepth+"in P :"+n);
				e.low_index = Math.min(e.low_index, n.index);								//  update the index
			} else {																		//Else the state is not in the queue
				log(DBG_APPARTENANCETESTS,tabdepth+"not in P");
				hnode = nodeSet.getHNodeForState(n_state);									//  If it already processed (in the nodeSet)	
				if (hnode != null) {
					log(DBG_APPARTENANCETESTS,tabdepth+"in N :"+hnode.getUniqueId());
					continue;																//     then continue to the next successor	
				} else {																	//  Else
					log(DBG_APPARTENANCETESTS,tabdepth+"not in N");
					n = new HTGSimulationQueuedState(n_state, index, index, n_updater);		//     explore it
					explore(n);																//     update the index
					e.low_index = Math.min(e.low_index, n.low_index);
				}
			}
		}
		log(DBG_MAINLOOPS,tabdepth+"Comparing indexes "+e);
		if (e.index == e.low_index) {
//			if (n == null) { //If it was a stable state
//				processStableState(e.state);
//				return;
//			}
			hnode = new GsHierarchicalNode(childsCount);
			hnode.addStateToThePile(e.state);
			HTGSimulationQueuedState tmp;
			log(DBG_QUEUE,tabdepth+"\tunqueuing:"+queue);
			do {
				tmp = (HTGSimulationQueuedState) queue.removeLast();
				log(DBG_QUEUE,tabdepth+"\tunqueuing:"+queue);
				hnode.addStateToThePile(tmp.state);
			} while (!tmp.equals(e));
			hnode.addAllTheStatesInPile();
			nodeSet.add(hnode);
			log(DBG_QUEUE,tabdepth+"\tunqueuing:"+queue+"\n"+tabdepth+"done");
			log(DBG_MAINLOOPS,tabdepth+"NEW SCC = "+hnode.statesToString(true));
		}
		tabdepth.deleteCharAt(tabdepth.length()-1);
	}

	
	private void processStableState(byte[] state) {
		hnode = nodeSet.getHNodeForState(state);									//  If it already processed (in the nodeSet)	
		if (hnode != null) {
			log(DBG_MAINLOOPS,tabdepth+"found stable state :"+print_state(state));
			return;
		}
		index++;
		log(DBG_MAINLOOPS,tabdepth+"found NEW stable state :"+print_state(state));
		hnode = new GsHierarchicalNode(childsCount);
		hnode.addState(state, GsHierarchicalNode.STATUS_PROCESSED);
		hnode.setType(GsHierarchicalNode.TYPE_STABLE_STATE);
		nodeSet.add(hnode);
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
			hnode.updateSize();
			htg.addVertex(hnode);
			vreader.setVertex(hnode);
			switch (hnode.getType()) {
			case GsDynamicalHierarchicalNode.TYPE_STABLE_STATE:
				vreader.setShape(GsVertexAttributesReader.SHAPE_ELLIPSE);
				vreader.setBackgroundColor(GsDynamicalHierarchicalNode.TYPE_STABLE_STATE_COLOR);
				break;
			case GsDynamicalHierarchicalNode.TYPE_TERMINAL_CYCLE:
				vreader.setShape(GsVertexAttributesReader.SHAPE_ELLIPSE);
				vreader.setBackgroundColor(GsDynamicalHierarchicalNode.TYPE_TERMINAL_CYCLE_COLOR);
				break;
			case GsDynamicalHierarchicalNode.TYPE_CYCLE:
				vreader.setBackgroundColor(GsDynamicalHierarchicalNode.TYPE_CYCLE_COLOR);
				break;
			case GsDynamicalHierarchicalNode.TYPE_TRANSIENT_COMPONENT:
				if (hnode.statesSet.getSizeOrOverApproximation() > 1) vreader.setBackgroundColor(GsDynamicalHierarchicalNode.TYPE_TRANSIENT_COMPONENT_COLOR);
				else vreader.setBackgroundColor(GsDynamicalHierarchicalNode.TYPE_TRANSIENT_COMPONENT_ALONE_COLOR);
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
	
	private void logQueue(int mask) {
		if ((debug & mask) != 0) {
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
		//TODO
//		if (maxnodes != 0 && nbnode >= maxnodes){
//		    debug_o.println("maxnodes reached: " + maxnodes);
//		    throw new GsException(GsException.GRAVITY_NORMAL, "Reached the maximum count of node");
//		}
//		if (maxdepth > 0 && e.depth >= maxdepth) {
//		    debug_o.println("maxdepth reached: " + getLastInQueue().depth);
//		    throw new GsException(GsException.GRAVITY_NORMAL, "Reached the maximum depth");
//		}
//		if (!ready) {
//		    throw new GsException(GsException.GRAVITY_NORMAL, Translator.getString("STR_interrupted"));
//		}
//		if (debug == -127) {
//			int ptotalNode = totalNode;
//			totalNode = nodeSet.size();
//			if (totalNode > ptotalNode &&  totalNode%16 == 0) debug_o.println("totalNode = "+totalNode);
//		}
	}
	
	private void updateProgess() {
		nbnode++;
	    if (frame != null) {
            frame.setProgress(nbnode); 
            if (debug == -127) debug_o.println(nbnode);
		}
	}
	
}
