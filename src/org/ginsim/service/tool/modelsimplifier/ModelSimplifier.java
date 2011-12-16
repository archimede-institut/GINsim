package org.ginsim.service.tool.modelsimplifier;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.ginsim.core.annotation.Annotation;
import org.ginsim.core.exception.GsException;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.initialstate.GsInitialStateList;
import org.ginsim.core.graph.regulatorygraph.initialstate.InitialState;
import org.ginsim.core.graph.regulatorygraph.initialstate.InitialStateList;
import org.ginsim.core.graph.regulatorygraph.initialstate.InitialStateManager;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.LogicalFunctionBrowser;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.LogicalParameter;
import org.ginsim.core.graph.regulatorygraph.mutant.MutantListManager;
import org.ginsim.core.graph.regulatorygraph.mutant.RegulatoryMutantDef;
import org.ginsim.core.graph.regulatorygraph.mutant.RegulatoryMutants;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.utils.log.LogManager;
import org.ginsim.servicegui.tool.modelsimplifier.ModelSimplifierConfigDialog;
import org.ginsim.servicegui.tool.modelsimplifier.RemovedInfo;
import org.ginsim.servicegui.tool.reg2dyn.PriorityClassDefinition;
import org.ginsim.servicegui.tool.reg2dyn.PriorityClassManager;
import org.ginsim.servicegui.tool.reg2dyn.Reg2dynPriorityClass;
import org.ginsim.servicegui.tool.reg2dyn.SimulationParameterList;
import org.ginsim.servicegui.tool.reg2dyn.SimulationParameters;
import org.ginsim.servicegui.tool.reg2dyn.SimulationParametersManager;

/**
 * Build a simplified model, based on a complete one, by removing some nodes.
 * 
 * The first step is to build new MDD for the targets of the removed nodes.
 * If this succeeded (no circuit was removed...), a new regulatory graph is created
 * and all non-removed nodes are copied into it, as well as all remaining interactions.
 * Then the logical parameters of the unaffected nodes are restored.
 * For the affected nodes, some work is required, using the newly built MDD for their logical function:
 * <ul>
 *   <li>new edges are added if needed (coming from the regulators of their deleted regulators)</li> 
 *   <li>new logical parameters are extracted from the MDD</li>
 * </ul>
 */
public class ModelSimplifier extends Thread implements Runnable {

	//GsGraphManager manager;
	ModelSimplifierConfigDialog dialog;
	int[] t_remove = null;

	RegulatoryGraph graph;
	List<RegulatoryNode> oldNodeOrder;
	RegulatoryGraph simplifiedGraph;

	Map<RegulatoryNode,boolean[]> m_edges = new HashMap<RegulatoryNode, boolean[]>();
	Map<Object, Object> copyMap = new HashMap<Object, Object>();
	Map<RegulatoryNode, List<RegulatoryNode>> m_removed;
	
	Map<RegulatoryNode, OMDDNode> m_affected = new HashMap<RegulatoryNode, OMDDNode>();
	String s_comment = "";
	List<RegulatoryNode> l_removed = new ArrayList<RegulatoryNode>();

	TargetEdgesIterator it_targets;
	
	boolean strict;
	ParameterGenerator pgen;

	public ModelSimplifier(RegulatoryGraph graph, ModelSimplifierConfig config, ModelSimplifierConfigDialog dialog, boolean start) {
		this.graph = graph;
		this.oldNodeOrder = graph.getNodeOrder();
		this.dialog = dialog;
		this.m_removed = new HashMap<RegulatoryNode, List<RegulatoryNode>>(config.m_removed);
		this.it_targets = new TargetEdgesIterator(m_removed);
		this.strict = config.strict;
		
		if (start) {
		    start();
		}
	}
	
	/**
	 * Run the reduction method.
	 */
	@Override
    public void run() {
		RegulatoryGraph simplifiedGraph = do_reduction();
        if (simplifiedGraph != null && dialog != null) {
            dialog.endSimu(simplifiedGraph, null);
        }
	}
	
    public RegulatoryGraph do_reduction() {
    	// prepare the list of removal requests
		List<RemovedInfo> l_todo = new ArrayList<RemovedInfo>();
		for (RegulatoryNode vertex: m_removed.keySet()) {
			int index = graph.getNodeOrder().indexOf(vertex);
			RemovedInfo ri = new RemovedInfo(vertex, index, graph.getOutgoingEdges(vertex));
			l_todo.add(ri);
		}
		
		
		// perform the actual reduction
		l_todo = remove_all(l_todo);

		// the "main" part is done, did it finish or fail ?
		if (l_todo.size() > 0) {
			if (dialog != null) {
				if (!dialog.showPartialReduction(l_todo)) {
					return null;
				}
				
				for (RemovedInfo ri: l_todo) {
					m_removed.remove(ri.vertex);
				}
				LogManager.trace( "Partial reduction result...");
			} else {
				// it failed, trigger an error message
				StringBuffer sb = new StringBuffer("Reduction failed.\n  Removed: ");
				for (RegulatoryNode v: l_removed) {
					sb.append(" "+v);
				}
				sb.append("\n  Failed: ");
				for (RemovedInfo ri: l_todo) {
					sb.append(" "+ri.vertex);
				}
				throw new RuntimeException(sb.toString());
			}
		}

		// go ahead and extract the result
        return extractReducedGraph();
    }
    
    private List<RemovedInfo> remove_all(List<RemovedInfo> l_todo) {
		// first do the "real" simplification work
		int todoSize = l_todo.size();
		int oldSize = todoSize + 1;
		while (todoSize > 0 && todoSize < oldSize) {
			oldSize = todoSize;
			l_todo = remove_batch(l_todo);
			todoSize = l_todo.size();
		}
		return l_todo;
    }
	
    /**
     * Go through a list of nodes to remove and try to remove all of them.
     * <p>
     * It may fail on some removals, in which case it will go on with the others and add them to the list of failed.
     * 
     * @param l_todo
     * @return the list of failed removals.
     */
    private List<RemovedInfo> remove_batch(List<RemovedInfo> l_todo) {
    	LogManager.trace( "batch of removal...");
    	List<RemovedInfo> l_failed = new ArrayList<RemovedInfo>();
    	
		for (RemovedInfo ri: l_todo) {
			RegulatoryNode vertex = ri.vertex;
			List<RegulatoryNode> targets = new ArrayList<RegulatoryNode>();
			OMDDNode deleted = m_affected.get(vertex);
			if (deleted == null) {
				deleted = vertex.getTreeParameters(graph);
			}
			try {
				if (strict) {
					// check that the node is not self-regulated
					checkNoSelfReg(deleted, ri.pos);
				}
				s_comment += ", "+vertex.getId();
			
				// mark all its targets as affected
				it_targets.setOutgoingList(ri.targets);
				while (it_targets.hasNext()) {
					RegulatoryNode target = (RegulatoryNode)it_targets.next();
					if (!target.equals(vertex)) {
						targets.add(target);
						OMDDNode targetNode = m_affected.get(target);
						if (targetNode == null) {
							targetNode = target.getTreeParameters(graph);
						}
						m_affected.put(target, remove(targetNode, deleted, ri.pos).reduce());
					}
				}
				m_removed.put(ri.vertex, new ArrayList<RegulatoryNode>(targets));
				l_removed.add(vertex);
			} catch (GsException e) {
				// this removal failed, remember that we may get a second chance
				l_failed.add(ri);
			}
		}
    	return l_failed;
    }

    /**
     * After the reduction, build a new regulatory graph with the result.
     * 
     * @return the reduced graph obtained after reduction
     */
    private RegulatoryGraph extractReducedGraph() {
		// create the new regulatory graph
		simplifiedGraph = GraphManager.getInstance().getNewGraph();
		Annotation note = simplifiedGraph.getAnnotation();
		note.copyFrom(graph.getAnnotation());
		if (s_comment.length() > 2) {
			note.setComment("Model Generated by GINsim on "+
					DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(new Date()) +
					", by removing the following nodes: "+s_comment.substring(2)+
					"\n\n"+note.getComment());
		}
		
		//GsGraphManager<RegulatoryNode, RegulatoryMultiEdge> simplifiedManager = simplifiedGraph.getGraphManager();
		List<RegulatoryNode> simplified_nodeOrder = simplifiedGraph.getNodeOrder();
		
		// Create all the nodes of the new model
		NodeAttributesReader vreader = graph.getNodeAttributeReader();
		NodeAttributesReader simplified_vreader = simplifiedGraph.getNodeAttributeReader();
		for (RegulatoryNode vertex: (List<RegulatoryNode>)graph.getNodeOrder()) {
			if (!m_removed.containsKey(vertex)) {
				RegulatoryNode clone = (RegulatoryNode)vertex.clone();
				simplifiedGraph.addNode(clone);
				vreader.setNode(vertex);
				simplified_vreader.setNode(clone);
				simplified_vreader.copyFrom(vreader);
				copyMap.put(vertex, clone);
				simplified_nodeOrder.add(clone);
			}
		}
		
		// copy all unaffected edges
		EdgeAttributesReader ereader = graph.getEdgeAttributeReader();
		EdgeAttributesReader simplified_ereader = simplifiedGraph.getEdgeAttributeReader();
		Iterator<RegulatoryMultiEdge>it = graph.getEdges().iterator();
		while (it.hasNext()) {
			RegulatoryMultiEdge me = it.next();
			RegulatoryNode src = (RegulatoryNode)copyMap.get(me.getSource());
			RegulatoryNode target = (RegulatoryNode)copyMap.get(me.getTarget());
			if (src != null && target != null) {
				RegulatoryMultiEdge me_clone = new RegulatoryMultiEdge(src, target);
				me_clone.copyFrom(me);
				Object new_me = simplifiedGraph.addEdge(me_clone);
				copyMap.put(me, me_clone);
				ereader.setEdge(me);
				simplified_ereader.setEdge(new_me);
				simplified_ereader.copyFrom(ereader);
			}
		}

		// build a mapping between new nodes and old position
		Map<RegulatoryNode, Integer> m_orderPos = new HashMap<RegulatoryNode, Integer>();
		Iterator<RegulatoryNode> it_oldOrder = oldNodeOrder.iterator();
		int pos = -1;
		for (RegulatoryNode vertex: simplified_nodeOrder) {;
			String id = vertex.getId();
			while (it_oldOrder.hasNext()) {
				pos++;
				RegulatoryNode oldNode = it_oldOrder.next();
				if (id.equals(oldNode.getId())) {
					m_orderPos.put(vertex, new Integer(pos));
					break;
				}
			}
		}
		// create the parameter generator with it
		pgen = new ParameterGenerator(oldNodeOrder, m_orderPos);

		// copy parameters/logical functions on the unaffected nodes
		for (RegulatoryNode vertex: oldNodeOrder) {
			RegulatoryNode clone = (RegulatoryNode)copyMap.get(vertex);
			if (m_removed.containsKey(vertex)) {
				continue;
			}
			if (!m_affected.containsKey(vertex)) {
				vertex.cleanupInteractionForNewGraph(copyMap);
				continue;
			}
			
			// this node needs new parameters
			OMDDNode newNode = m_affected.get(vertex);

			// make sure that the needed edges target the affected node
			m_edges.clear();
			extractEdgesFromNode(m_edges, newNode);
			RegulatoryNode target = (RegulatoryNode)copyMap.get(vertex);
			for (Entry<RegulatoryNode,boolean[]> e: m_edges.entrySet()) {
				RegulatoryNode src = (RegulatoryNode)copyMap.get(e.getKey());
				RegulatoryMultiEdge de = simplifiedGraph.getEdge(src, target);
				if (de == null) {
					de = new RegulatoryMultiEdge(src, target);
					simplifiedGraph.addEdge(de);
				}
				boolean[] t_required = e.getValue();
				de.copyFrom(t_required);
			}
			// rebuild the parameters
			m_edges.clear();
			Collection<RegulatoryMultiEdge> edges = simplifiedGraph.getIncomingEdges(clone);
			for (RegulatoryMultiEdge e: edges) {
				RegulatoryNode src = e.getSource();
				
				// FIXME: not sure what this should be! (used to be a integer[])
				boolean[] t_val = {false, true};
				m_edges.put(src, t_val);
			}
			pgen.browse(edges, clone, newNode);
		}
		
		// get as much of the associated data as possible
		Map m_alldata = new HashMap();
		// mutants: only copy mutants that don't affect removed nodes
		RegulatoryMutants mutants = (RegulatoryMutants) ObjectAssociationManager.getInstance().getObject( graph, MutantListManager.key, false);
		if (mutants != null && mutants.getNbElements(null) > 0) {
			RegulatoryMutants newMutants = (RegulatoryMutants) ObjectAssociationManager.getInstance().getObject( simplifiedGraph, MutantListManager.key, true);
			RegulatoryMutantDef mutant, newMutant;
			int mutantPos=0;
			for (int i=0 ; i<mutants.getNbElements(null) ; i++) {
				mutant = (RegulatoryMutantDef)mutants.getElement(null, i);
				mutantPos = newMutants.add();
				newMutant = (RegulatoryMutantDef)newMutants.getElement(null, mutantPos);
				newMutant.setName(mutant.getName());
				boolean ok = true;
				for (int j=0 ; j<mutant.getNbChanges() ; j++ ) {
					String id = mutant.getName(j);
					RegulatoryNode vertex = null;
					for (RegulatoryNode v: simplified_nodeOrder) {
						if (id.equals(v.getId())) {
							vertex = v;
							break;
						}
					}
					if (vertex == null) {
						ok = false;
						break;
					}
					newMutant.addChange(vertex, mutant.getMin(j), mutant.getMax(j));
					// TODO: transfer condition only if it does not involve removed nodes
					newMutant.setCondition(j, simplifiedGraph, mutant.getCondition(j));
				}
				if (!ok) {
					newMutants.remove(null, new int[] {mutantPos});
				} else {
                    m_alldata.put(mutant, newMutant);
				}
			}
		}
		
		// initial states
        GsInitialStateList linit = (GsInitialStateList) ObjectAssociationManager.getInstance().getObject( graph, InitialStateManager.key, false);
		if (linit != null && !linit.isEmpty()) {
			GsInitialStateList newLinit = (GsInitialStateList) ObjectAssociationManager.getInstance().getObject( simplifiedGraph, InitialStateManager.key, true);
            InitialStateList[] inits = {linit.getInitialStates(), linit.getInputConfigs()};
            InitialStateList[] newInits = {newLinit.getInitialStates(), newLinit.getInputConfigs()};

			for (int i=0 ; i<inits.length ; i++) {
                InitialStateList init = inits[i];
                InitialStateList newInit = newInits[i];
    			if (init != null && init.getNbElements(null) > 0) {
    				for (int j=0 ; j<init.getNbElements(null) ; j++) {
    					InitialState istate = (InitialState)init.getElement(null, j);
    					int epos = newInit.add();
    					InitialState newIstate = (InitialState)newInit.getElement(null, epos);
    					newIstate.setName(istate.getName());
    					m_alldata.put(istate, newIstate);
    					Map<RegulatoryNode, List<Integer>> m_init = newIstate.getMap();
    					for (Entry<RegulatoryNode, List<Integer>> e: istate.getMap().entrySet()) {
    						RegulatoryNode o = (RegulatoryNode)copyMap.get(e.getKey());
    						if (o != null) {
    							m_init.put( o, e.getValue());
    						}
    					}
    				}
    			}
			}
		}
		
		// priority classes definition and simulation parameters
		SimulationParameterList params = (SimulationParameterList) ObjectAssociationManager.getInstance().getObject( graph, SimulationParametersManager.key, false);
		if (params != null) {
			PriorityClassManager pcman = params.pcmanager;
			SimulationParameterList new_params = (SimulationParameterList) ObjectAssociationManager.getInstance().getObject( simplifiedGraph, SimulationParametersManager.key, true);
			PriorityClassManager new_pcman = new_params.pcmanager;
			for (int i=2 ; i<pcman.getNbElements(null) ; i++) {
				PriorityClassDefinition pcdef = (PriorityClassDefinition)pcman.getElement(null, i);
				int index = new_pcman.add();
				PriorityClassDefinition new_pcdef = (PriorityClassDefinition)new_pcman.getElement(null, index);
				new_pcdef.setName(pcdef.getName());
				m_alldata.put(pcdef, new_pcdef);
				Map<Reg2dynPriorityClass, Reg2dynPriorityClass> m_pclass = new HashMap<Reg2dynPriorityClass, Reg2dynPriorityClass>();
				// copy all priority classes
				for (int j=0 ; j<pcdef.getNbElements(null) ; j++) {
					Reg2dynPriorityClass pc = (Reg2dynPriorityClass)pcdef.getElement(null, j);
					if (j>0) {
						new_pcdef.add();
					}
					Reg2dynPriorityClass new_pc = (Reg2dynPriorityClass)new_pcdef.getElement(null, j);
					new_pc.setName(pc.getName());
					new_pc.rank = pc.rank;
					new_pc.setMode(pc.getMode());
					m_pclass.put(pc, new_pc);
				}
				
				// properly place nodes
				for (Entry<?,?> e: pcdef.m_elt.entrySet()) {
					RegulatoryNode vertex = (RegulatoryNode)copyMap.get(e.getKey());
					if (vertex != null) {
						new_pcdef.m_elt.put(vertex,	m_pclass.get(e.getValue()));
					}
				}
			}
			int[] t_index = {0};
			new_pcman.remove(null, t_index);
			
			// simulation parameters
			for (int i=0 ; i<params.getNbElements() ; i++) {
			    SimulationParameters param = (SimulationParameters)params.getElement(null, i);
			    int index = new_params.add();
			    SimulationParameters new_param = (SimulationParameters)new_params.getElement(null, index);
			    m_alldata.put("", new_pcman);
			    param.copy_to(new_param, m_alldata);
			}
		}
		return simplifiedGraph;
	}
	
	/**
	 * extract the list of required edges for a given logical function.
	 * @param node
	 */
	private void extractEdgesFromNode(Map<RegulatoryNode,boolean[]> m_edges, OMDDNode node) {
		if (node.next == null) {
			return;
		}
		RegulatoryNode vertex = (RegulatoryNode)oldNodeOrder.get(node.level);
		boolean[] t_threshold = (boolean[])m_edges.get(vertex);
		if (t_threshold == null) {
			t_threshold = new boolean[vertex.getMaxValue()+1];
			for (int i=0 ; i<t_threshold.length ; i++) {
				t_threshold[i] = false;
			}
			m_edges.put(vertex, t_threshold);
		}

		OMDDNode child = null;
		for (int i=0 ; i<node.next.length ; i++) {
			if (child != node.next[i]) {
				if (child != null) {
					t_threshold[i] = true;
				}
				child = node.next[i];
				extractEdgesFromNode(m_edges, node.next[i]);
			}
		}
	}

	/* *************************************************************
	 *  
	 *  The real algo is here
	 *  
	 *  Deleting a node means removing it (by taking into account its logical
	 *  function) from all of its targets
	 *  
	 ***************************************************************/

	/**
	 * Preliminary check: a node should not be self-regulated: check it
	 */
	private void checkNoSelfReg(OMDDNode node, int level) throws GsException {
		if (node.next == null || node.level > level) {
			return;
		}
		if (node.level == level) {
			throw new GsException(GsException.GRAVITY_ERROR, "self regulated node");
		}
		for (int i=0 ; i<node.next.length ; i++) {
			checkNoSelfReg(node.next[i], level);
		}
	}
	/**
	 * Remove <code>regulator</code> from its target <code>node</code>.
	 * This is the first part of the algo: we have not yet found the 
	 * regulator in the logical function.
	 * It will be called recursively until we find it (or go too far)
	 * 
	 * @param node
	 * @param regulator
	 * @param level
	 * @return
	 */
	public OMDDNode remove(OMDDNode node, OMDDNode regulator, int level) throws GsException {
		if (node.next == null || node.level > level) {
			return node;
		}
		if (node.level == level) {
			if (regulator.next == null) {
				return node.next[regulator.value];
			}
			if (regulator.level == level) {
				throw new GsException(GsException.GRAVITY_ERROR, 
						"Can not continue the simplification: a circuit would get lost");
			}
			return remove(node.next, regulator);
		}
		
		OMDDNode ret = new OMDDNode();
		if (regulator.next == null || regulator.level > node.level) {
			ret.level = node.level;
			ret.next = new OMDDNode[node.next.length];
			for (int i=0 ; i<ret.next.length ; i++) {
				ret.next[i] = remove(node.next[i], regulator, level);
			}
		} else if (node.level > regulator.level) {
			ret.level = regulator.level;
			ret.next = new OMDDNode[regulator.next.length];
			for (int i=0 ; i<ret.next.length ; i++) {
				ret.next[i] = remove(node, regulator.next[i], level);
			}
		} else {
			ret.level = node.level;
			ret.next = new OMDDNode[node.next.length];
			for (int i=0 ; i<ret.next.length ; i++) {
				ret.next[i] = remove(node.next[i], regulator.next[i], level);
			}
		}
		return ret;
	}

	/**
	 * Remove <code>regulator</code> from its target <code>node</code>.
	 * This is the second part of the algo: we have found the regulator 
	 * in the logical function.
	 * We must thus follow all branches corresponding to its possible values,
	 * until we can take the final decision.
	 * 
	 * @param t_ori
	 * @param regulator
	 * @return
	 */
	public OMDDNode remove(OMDDNode[] t_ori, OMDDNode regulator) {
		if (regulator.next == null) {
			return t_ori[regulator.value];
		}
		// first, lookup for the best next step
		int best = regulator.level;
		int index = -1;
		for (int i=0 ; i<t_ori.length ; i++) {
			OMDDNode node = t_ori[i];
			if (node.next != null && node.level <= best) { 
				// also update when equal to avoid stupid optimisations...
				best = node.level;
				index = i;
			}
		}
		
		OMDDNode ret = new OMDDNode();
		ret.level = best;
		if (index == -1) {
			ret.next = new OMDDNode[regulator.next.length];
			for (int i=0 ; i<ret.next.length ; i++) {
				ret.next[i] = remove(t_ori, regulator.next[i]);
			}
		} else {
			ret.next = new OMDDNode[t_ori[index].next.length];
			for (int i=0 ; i<ret.next.length ; i++) {
				OMDDNode[] t_recur = new OMDDNode[t_ori.length];
				for (int j=0 ; j<t_recur.length ; j++) {
					OMDDNode node = t_ori[j];
					if (node.next == null || node.level > best) {
						t_recur[j] = node;
					} else {
						t_recur[j] = node.next[i];
					}
				}
				if (regulator.level == best) {
					ret.next[i] = remove(t_recur, regulator.next[i]);
				} else {
					ret.next[i] = remove(t_recur, regulator);
				}
			}
		}
		return ret;
	}
}


class TargetEdgesIterator implements Iterator<RegulatoryNode> {

	LinkedList<RegulatoryNode> queue = new LinkedList<RegulatoryNode>();
	Set<RegulatoryNode> m_visited = new HashSet<RegulatoryNode>();
	Map<RegulatoryNode, List<RegulatoryNode>> m_removed;
	
	RegulatoryNode next;
	
	public TargetEdgesIterator(Map<RegulatoryNode, List<RegulatoryNode>> m_removed) {
		this.m_removed = m_removed;
	}
	
	public boolean hasNext() {
		return next != null;
	}

	public RegulatoryNode next() {
		RegulatoryNode ret = next;
		
		// find the next.
		// it can be a "normal next target" if it was not removed
		// if it was removed, it may be one of the targets of the removed node
		next = null;
		while (queue.size() > 0) {
			RegulatoryNode vertex = queue.removeFirst();
			if (m_visited.contains(vertex)) {
				// this node was checked already, skip it
				continue;
			}
			m_visited.add(vertex);
			List<RegulatoryNode> targets = m_removed.get(vertex);
			if (targets == null) {
				// "clean" node: go for it!
				next = vertex;
				break;
			}
			
			// "dirty" node: enqueue its targets
			for (RegulatoryNode v: targets) {
				queue.addLast(v);
			}
		}
		return ret;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	public void setOutgoingList(Collection<RegulatoryMultiEdge> outgoing) {
		m_visited.clear();
		queue.clear();
		for (RegulatoryMultiEdge e: outgoing) {
			queue.addLast(e.getTarget());
		}
		next();
	}
}

class ParameterGenerator extends LogicalFunctionBrowser {
	private List<LogicalParameter> paramList;
	private int[][] t_values;
	private RegulatoryMultiEdge[] t_me;
	private Map<RegulatoryNode, Integer> m_orderPos;
	
	public ParameterGenerator(List<RegulatoryNode> nodeOrder, Map<RegulatoryNode, Integer> m_orderPos) {
		super(nodeOrder);
		this.m_orderPos = m_orderPos;
	}

	public void browse(Collection<RegulatoryMultiEdge> edges, RegulatoryNode targetNode, OMDDNode node) {
		this.paramList = new ArrayList<LogicalParameter>();
		t_values = new int[edges.size()][4];
		t_me = new RegulatoryMultiEdge[t_values.length];
		
		int i = -1;
		for (RegulatoryMultiEdge me: edges) {
			i++;
			t_me[i] = me;
			t_values[i][0] = m_orderPos.get(me.getSource());
		}

		browse(node);
		targetNode.getV_logicalParameters().setManualParameters(paramList);
	}
	
	protected void leafReached(OMDDNode leaf) {
		if (leaf.value == 0) {
			return;
		}
		// transform constraints on values to constraints on edges
		for (int i=0 ; i<t_values.length ; i++) {
			int nb = t_values[i][0];
			int begin = path[nb][0];
			int end = path[nb][1];
			RegulatoryMultiEdge me = t_me[i];
			nb = me.getEdgeCount();
			
			if (begin == -1) {
				// all values are allowed
				t_values[i][1] = -1;
				t_values[i][2] = nb-1;
			} else {
				// find the first edge
				if (begin == 0) {
					// start before the first edge
					t_values[i][1] = -1;
				} else {
					// lookup the start
					for (int j=0 ; j<nb ; j++) {
						if (me.getMin(j) >= begin) {
							t_values[i][1] = j;
							break;
						}
					}
				}
				// find the last edge
				for (int j=t_values[i][1] ; j<nb ; j++) {
					if (j == -1) {
						if (end < me.getMin(0)) {
							t_values[i][2] = -1;
							break;
						}
						continue;
					}
					int max = me.getMax(j);
					if (max == -1 || end <= max) {
						t_values[i][2] = j;
						break;
					}
				}
			}
		}
		
		// prepare to iterate through logical parameters
		for (int i=0 ; i<t_values.length ; i++) {
			t_values[i][3] = t_values[i][1];
		}
		
		while (true) {
			List<RegulatoryEdge> l = new ArrayList<RegulatoryEdge>();
			int lastIndex = -1;
			for (int i=0 ; i<t_values.length ; i++) {
				if (t_values[i][3] != -1) {
					// add interaction to the vector
					l.add(t_me[i].getEdge(t_values[i][3]));
				}
				if (t_values[i][3] < t_values[i][2]) {
					lastIndex = i;
				}
			}
			
			paramList.add(new LogicalParameter(l, leaf.value));

			// stop if no free value was found
			if (lastIndex == -1) {
				break;
			}
			// go to next step
			t_values[lastIndex][3]++;
			for (int i=lastIndex+1 ; i<t_values.length ; i++) {
				t_values[i][3] = t_values[i][1];
			}
		}
	}
}
