package fr.univmrs.tagc.GINsim.regulatoryGraph.modelModifier;

import java.text.DateFormat;
import java.util.*;
import java.util.Map.Entry;

import fr.univmrs.tagc.GINsim.annotation.Annotation;
import fr.univmrs.tagc.GINsim.data.GsDirectedEdge;
import fr.univmrs.tagc.GINsim.export.regulatoryGraph.LogicalFunctionBrowser;
import fr.univmrs.tagc.GINsim.graph.GsEdgeAttributesReader;
import fr.univmrs.tagc.GINsim.graph.GsGraphManager;
import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.tagc.GINsim.reg2dyn.*;
import fr.univmrs.tagc.GINsim.regulatoryGraph.*;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.GsInitialState;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.GsInitialStateList;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.GsInitialStateManager;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.InitialStateList;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.GsRegulatoryMutantDef;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.GsRegulatoryMutants;
import fr.univmrs.tagc.common.GsException;

class RemovedInfo {
	GsRegulatoryVertex vertex;
	int pos;
	List targets;
	public RemovedInfo(GsRegulatoryVertex vertex, int pos, List targets) {
		super();
		this.vertex = vertex;
		this.pos = pos;
		this.targets = targets;
	}
}

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

	GsGraphManager manager;
	ModelSimplifierConfigDialog dialog;
	int[] t_remove = null;

	GsRegulatoryGraph graph;
	List oldNodeOrder;
	GsRegulatoryGraph simplifiedGraph;
	Map m_modified = new HashMap();
	Map m_edges = new HashMap();
	Map copyMap = new HashMap();
	Map m_removed;
	boolean strict;
	ParameterGenerator pgen;

	public ModelSimplifier(GsRegulatoryGraph graph, ModelSimplifierConfig config, ModelSimplifierConfigDialog dialog, boolean start) {
		this.graph = graph;
		this.oldNodeOrder = graph.getNodeOrder();
		this.dialog = dialog;
		this.m_removed = new HashMap(config.m_removed);
		this.strict = config.strict;
		manager = graph.getGraphManager();
		if (start) {
		    start();
		}
	}

    public void run() {
        GsRegulatoryGraph simplifiedGraph = do_reduction();
        if (dialog != null) {
            dialog.endSimu(simplifiedGraph, null);
        }
    }
    public GsRegulatoryGraph do_reduction() {
		Iterator it;
		try {
			// first do the "real" simplification work
			Map m_affected = new HashMap();
			String s_comment = "";
			it = m_removed.entrySet().iterator();
			TargetEdgesIterator it_targets = new TargetEdgesIterator(m_removed);
			List l_removed = new ArrayList();
			List l_todo = new ArrayList();
			while (it.hasNext()) {
				Entry entry = (Entry)it.next();
				GsRegulatoryVertex vertex = (GsRegulatoryVertex)entry.getKey();
				l_todo.add(new RemovedInfo(vertex, graph.getNodeOrder().indexOf(vertex),
						manager.getOutgoingEdges(vertex)));
			}
			boolean tryagain = true;
			while (tryagain && l_todo.size() > 0) {
				tryagain = false;
				it = l_todo.iterator();
				l_todo = new ArrayList();
				while (it.hasNext()) {
					RemovedInfo ri = (RemovedInfo)it.next();
					GsRegulatoryVertex vertex = ri.vertex;
					List targets = new ArrayList();
					OmddNode deleted = (OmddNode)m_affected.get(vertex);
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
							GsRegulatoryVertex target = (GsRegulatoryVertex)it_targets.next();
							if (!target.equals(vertex)) {
								targets.add(target);
								OmddNode targetNode = (OmddNode)m_affected.get(target);
								if (targetNode == null) {
									targetNode = target.getTreeParameters(graph);
								}
								m_affected.put(target, remove(targetNode, deleted, ri.pos).reduce());
							}
						}
						m_removed.put(ri.vertex, new ArrayList(targets));
						l_removed.add(vertex);
						tryagain = true;
					} catch (GsException e) {
						// this removal failed, remember that we may get a second chance
						l_todo.add(ri);
					}
				}
			}
			// the "main" part is done, did it finish or fail ?
			if (l_todo.size() > 0) {
				// it failed, trigger an error message
				GsException e = new GsException(GsException.GRAVITY_ERROR, "Removal failed.");
				StringBuffer sb = new StringBuffer();
				for (Iterator it_done = l_removed.iterator() ; it_done.hasNext() ; ) {
					sb.append(" "+it_done.next());
				}
				e.addMessage("already removed:"+sb);
				sb = new StringBuffer();
				for (it=l_todo.iterator(); it.hasNext() ; ) {
					sb.append(" "+((RemovedInfo)it.next()).vertex);
				}
				e.addMessage("remaining:"+sb);
				throw e;
			}
			
			// create the new regulatory graph
			simplifiedGraph = new GsRegulatoryGraph();
			Annotation note = simplifiedGraph.getAnnotation();
			note.copyFrom(graph.getAnnotation());
			if (s_comment.length() > 2) {
				note.setComment("Model Generated by GINsim on "+
						DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(new Date()) +
						", by removing the following nodes: "+s_comment.substring(2)+
						"\n\n"+note.getComment());
			}
			
			GsGraphManager simplifiedManager = simplifiedGraph.getGraphManager();
			List simplified_nodeOrder = simplifiedGraph.getNodeOrder();
			
			// Create all the nodes of the new model
			GsVertexAttributesReader vreader = manager.getVertexAttributesReader();
			GsVertexAttributesReader simplified_vreader = simplifiedManager.getVertexAttributesReader();
			it = graph.getNodeOrder().iterator();
			while (it.hasNext()) {
				GsRegulatoryVertex vertex = (GsRegulatoryVertex)it.next();
				if (!m_removed.containsKey(vertex)) {
					GsRegulatoryVertex clone = (GsRegulatoryVertex)vertex.clone();
					simplifiedManager.addVertex(clone);
					vreader.setVertex(vertex);
					simplified_vreader.setVertex(clone);
					simplified_vreader.copyFrom(vreader);
					copyMap.put(vertex, clone);
					simplified_nodeOrder.add(clone);
				}
			}
			
			// copy all unaffected edges
			GsEdgeAttributesReader ereader = manager.getEdgeAttributesReader();
			GsEdgeAttributesReader simplified_ereader = simplifiedManager.getEdgeAttributesReader();
			it = manager.getEdgeIterator();
			while (it.hasNext()) {
				GsRegulatoryMultiEdge me = (GsRegulatoryMultiEdge)((GsDirectedEdge)it.next()).getUserObject();
				GsRegulatoryVertex src = (GsRegulatoryVertex)copyMap.get(me.getSource());
				GsRegulatoryVertex target = (GsRegulatoryVertex)copyMap.get(me.getTarget());
				if (src != null && target != null) {
					GsRegulatoryMultiEdge me_clone = new GsRegulatoryMultiEdge(src, target);
					me_clone.copyFrom(me);
					simplifiedManager.addEdge(src, target, me_clone);
					copyMap.put(me, me_clone);
					ereader.setEdge(me);
					simplified_ereader.setEdge(me_clone);
					simplified_ereader.copyFrom(ereader);
				}
			}

			// build a mapping between new nodes and old position
			Map m_orderPos = new HashMap();
			Iterator it_oldOrder = oldNodeOrder.iterator();
			Iterator it_newOrder = simplified_nodeOrder.iterator();
			int pos = -1;
			while (it_newOrder.hasNext()) {;
				GsRegulatoryVertex vertex = (GsRegulatoryVertex)it_newOrder.next();
				String id = vertex.getId();
				while (true) {
					pos++;
					GsRegulatoryVertex oldVertex = (GsRegulatoryVertex)it_oldOrder.next();
					if (id.equals(oldVertex.getId())) {
						m_orderPos.put(vertex, new Integer(pos));
						break;
					}
				}
			}
			// create the parameter generator with it
			pgen = new ParameterGenerator(oldNodeOrder, m_orderPos);

			// copy parameters/logical functions on the unaffected nodes
			it = oldNodeOrder.iterator();
			while (it.hasNext()) {
				GsRegulatoryVertex vertex = (GsRegulatoryVertex)it.next();
				GsRegulatoryVertex clone = (GsRegulatoryVertex)copyMap.get(vertex);
				if (m_removed.containsKey(vertex)) {
					continue;
				}
				if (!m_affected.containsKey(vertex)) {
					vertex.cleanupInteractionForNewGraph(copyMap);
					continue;
				}
				
				// this node needs new parameters
				OmddNode newNode = (OmddNode)m_affected.get(vertex);
	
				// make sure that the needed edges target the affected node
				m_edges.clear();
				extractEdgesFromNode(newNode);
				GsRegulatoryVertex target = (GsRegulatoryVertex)copyMap.get(vertex);
				Iterator it_newEdges = m_edges.entrySet().iterator();
				while (it_newEdges.hasNext()) {
					Entry e = (Entry)it_newEdges.next();
					GsRegulatoryVertex src = (GsRegulatoryVertex)copyMap.get(e.getKey());
					GsDirectedEdge de = (GsDirectedEdge)simplifiedManager.getEdge(src, target);
					GsRegulatoryMultiEdge new_me;
					if (de == null) {
						new_me = new GsRegulatoryMultiEdge(src, target);
						simplifiedManager.addEdge(src, target, new_me);
					} else {
						new_me = (GsRegulatoryMultiEdge)de.getUserObject();
					}
					boolean[] t_required = (boolean[])e.getValue();
					new_me.copyFrom(t_required);
				}
				// rebuild the parameters
				m_edges.clear();
				List edges = simplifiedManager.getIncomingEdges(clone);
				Iterator it2 = edges.iterator();
				while (it2.hasNext()) {
					GsDirectedEdge e = (GsDirectedEdge)it2.next();
					GsRegulatoryVertex src = (GsRegulatoryVertex)e.getSourceVertex();
					int[] t_val = {0, src.getMaxValue()};
					m_edges.put(src, t_val);
				}
				pgen.browse(edges, clone, newNode);
			}
			
			// get as much of the associated data as possible
			Map m_alldata = new HashMap();
			// mutants: only copy mutants that don't affect removed nodes
			GsRegulatoryMutants mutants = (GsRegulatoryMutants)graph.getObject(GsMutantListManager.key, false);
			if (mutants != null && mutants.getNbElements(null) > 0) {
				GsRegulatoryMutants newMutants = (GsRegulatoryMutants)simplifiedGraph.getObject(GsMutantListManager.key, true);
				GsRegulatoryMutantDef mutant, newMutant;
				int mutantPos=0;
				for (int i=0 ; i<mutants.getNbElements(null) ; i++) {
					mutant = (GsRegulatoryMutantDef)mutants.getElement(null, i);
					mutantPos = newMutants.add();
					newMutant = (GsRegulatoryMutantDef)newMutants.getElement(null, mutantPos);
					newMutant.setName(mutant.getName());
					boolean ok = true;
					for (int j=0 ; j<mutant.getNbChanges() ; j++ ) {
						String id = mutant.getName(j);
						GsRegulatoryVertex vertex = null;
						Iterator it_nodes = simplified_nodeOrder.iterator();
						while (it_nodes.hasNext()) {
							GsRegulatoryVertex v = (GsRegulatoryVertex)it_nodes.next();
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
            GsInitialStateList linit = (GsInitialStateList)graph.getObject(GsInitialStateManager.key, false);
			if (!linit.isEmpty()) {
    			GsInitialStateList newLinit = (GsInitialStateList)simplifiedGraph.getObject(GsInitialStateManager.key, true);
                InitialStateList[] inits = {linit.getInitialStates(), linit.getInputConfigs()};
                InitialStateList[] newInits = {newLinit.getInitialStates(), newLinit.getInputConfigs()};
    
    			for (int i=0 ; i<inits.length ; i++) {
                    InitialStateList init = inits[i];
                    InitialStateList newInit = newInits[i];
        			if (init != null && init.getNbElements(null) > 0) {
        				for (int j=0 ; j<init.getNbElements(null) ; j++) {
        					GsInitialState istate = (GsInitialState)init.getElement(null, j);
        					int epos = newInit.add();
        					GsInitialState newIstate = (GsInitialState)newInit.getElement(null, epos);
        					newIstate.setName(istate.getName());
        					m_alldata.put(istate, newIstate);
        					Map m_init = newIstate.getMap();
        					Iterator it_entry = istate.getMap().entrySet().iterator();
        					while (it_entry.hasNext()) {
        						Entry e = (Entry)it_entry.next();
        						Object o = copyMap.get(e.getKey());
        						if (o != null) {
        							m_init.put(o, e.getValue());
        						}
        					}
        				}
        			}
    			}
			}
			
			// priority classes definition and simulation parameters
			GsSimulationParameterList params = (GsSimulationParameterList)graph.getObject(GsSimulationParametersManager.key, false);
			if (params != null) {
				PriorityClassManager pcman = params.pcmanager;
				GsSimulationParameterList new_params = (GsSimulationParameterList)simplifiedGraph.getObject(GsSimulationParametersManager.key, true);
				PriorityClassManager new_pcman = new_params.pcmanager;
				for (int i=2 ; i<pcman.getNbElements(null) ; i++) {
					PriorityClassDefinition pcdef = (PriorityClassDefinition)pcman.getElement(null, i);
					int index = new_pcman.add();
					PriorityClassDefinition new_pcdef = (PriorityClassDefinition)new_pcman.getElement(null, index);
					new_pcdef.setName(pcdef.getName());
					m_alldata.put(pcdef, new_pcdef);
					Map m_pclass = new HashMap();
					// copy all priority classes
					for (int j=0 ; j<pcdef.getNbElements(null) ; j++) {
						GsReg2dynPriorityClass pc = (GsReg2dynPriorityClass)pcdef.getElement(null, j);
						if (j>0) {
							new_pcdef.add();
						}
						GsReg2dynPriorityClass new_pc = (GsReg2dynPriorityClass)new_pcdef.getElement(null, j);
						new_pc.setName(pc.getName());
						new_pc.rank = pc.rank;
						new_pc.setMode(pc.getMode());
						m_pclass.put(pc, new_pc);
					}
					
					// properly place nodes
					Iterator it_entry = pcdef.m_elt.entrySet().iterator();
					while (it_entry.hasNext()) {
						Entry e = (Entry)it_entry.next();
						Object vertex = copyMap.get(e.getKey());
						if (vertex != null) {
							new_pcdef.m_elt.put(vertex,	m_pclass.get(e.getValue()));
						}
					}
				}
				int[] t_index = {0};
				new_pcman.remove(null, t_index);
				
				// simulation parameters
				for (int i=0 ; i<params.getNbElements() ; i++) {
				    GsSimulationParameters param = (GsSimulationParameters)params.getElement(null, i);
				    int index = new_params.add();
				    GsSimulationParameters new_param = (GsSimulationParameters)new_params.getElement(null, index);
				    m_alldata.put("", new_pcman);
				    param.copy_to(new_param, m_alldata);
				}
			}
			return simplifiedGraph;
		} catch (Exception e) {
		    e.printStackTrace();
		    return null;
		}
	}
	
	/**
	 * extract the list of required edges for a given logical function.
	 * @param node
	 */
	private void extractEdgesFromNode(OmddNode node) {
		if (node.next == null) {
			return;
		}
		GsRegulatoryVertex vertex = (GsRegulatoryVertex)oldNodeOrder.get(node.level);
		boolean[] t_threshold = (boolean[])m_edges.get(vertex);
		if (t_threshold == null) {
			t_threshold = new boolean[vertex.getMaxValue()+1];
			for (int i=0 ; i<t_threshold.length ; i++) {
				t_threshold[i] = false;
			}
			m_edges.put(vertex, t_threshold);
		}

		OmddNode child = null;
		for (int i=0 ; i<node.next.length ; i++) {
			if (child != node.next[i]) {
				if (child != null) {
					t_threshold[i] = true;
				}
				child = node.next[i];
				extractEdgesFromNode(node.next[i]);
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
	private void checkNoSelfReg(OmddNode node, int level) throws GsException {
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
	public OmddNode remove(OmddNode node, OmddNode regulator, int level) throws GsException {
		
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
		
		OmddNode ret = new OmddNode();
		if (regulator.next == null || regulator.level > node.level) {
			ret.level = node.level;
			ret.next = new OmddNode[node.next.length];
			for (int i=0 ; i<ret.next.length ; i++) {
				ret.next[i] = remove(node.next[i], regulator, level);
			}
		} else if (node.level > regulator.level) {
			ret.level = regulator.level;
			ret.next = new OmddNode[regulator.next.length];
			for (int i=0 ; i<ret.next.length ; i++) {
				ret.next[i] = remove(node, regulator.next[i], level);
			}
		} else {
			ret.level = node.level;
			ret.next = new OmddNode[node.next.length];
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
	public OmddNode remove(OmddNode[] t_ori, OmddNode regulator) {
		if (regulator.next == null) {
			return t_ori[regulator.value];
		}
		// first, lookup for the best next step
		int best = regulator.level;
		int index = -1;
		for (int i=0 ; i<t_ori.length ; i++) {
			OmddNode node = t_ori[i];
			if (node.next != null && node.level <= best) { 
				// also update when equal to avoid stupid optimisations...
				best = node.level;
				index = i;
			}
		}
		
		OmddNode ret = new OmddNode();
		ret.level = best;
		if (index == -1) {
			ret.next = new OmddNode[regulator.next.length];
			for (int i=0 ; i<ret.next.length ; i++) {
				ret.next[i] = remove(t_ori, regulator.next[i]);
			}
		} else {
			ret.next = new OmddNode[t_ori[index].next.length];
			for (int i=0 ; i<ret.next.length ; i++) {
				OmddNode[] t_recur = new OmddNode[t_ori.length];
				for (int j=0 ; j<t_recur.length ; j++) {
					OmddNode node = t_ori[j];
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


class TargetEdgesIterator implements Iterator {

	LinkedList queue = new LinkedList();
	Map m_visited = new HashMap();
	Map m_removed;
	
	Object next;
	
	public TargetEdgesIterator(Map m_removed) {
		this.m_removed = m_removed;
	}
	
	public boolean hasNext() {
		return next != null;
	}

	public Object next() {
		Object ret = next;
		
		// find the next.
		// it can be a "normal next target" if it was not removed
		// if it was removed, it may be one of the targets of the removed node
		next = null;
		while (queue.size() > 0) {
			Object vertex = queue.removeFirst();
			if (m_visited.containsKey(vertex)) {
				// this node was checked already, skip it
				continue;
			}
			m_visited.put(vertex, null);
			Object targets = m_removed.get(vertex);
			if (targets == null) {
				// "clean" node: go for it!
				next = vertex;
				break;
			}
			
			// "dirty" node: enqueue its targets
			Iterator it = ((List)targets).iterator();
			while (it.hasNext()) {
				queue.addLast(it.next());
			}
		}
		return ret;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	public void setOutgoingList(List outgoing) {
		Iterator outgoingIterator = outgoing.iterator();
		m_visited.clear();
		queue.clear();
		while (outgoingIterator.hasNext()) {
			queue.addLast(((GsDirectedEdge)outgoingIterator.next()).getTargetVertex());
		}
		next();
	}
}

class ParameterGenerator extends LogicalFunctionBrowser {
	private ArrayList paramList;
	private int[][] t_values;
	private GsRegulatoryMultiEdge[] t_me;
	private Map m_orderPos;
	
	public ParameterGenerator(List nodeOrder, Map m_orderPos) {
		super(nodeOrder);
		this.m_orderPos = m_orderPos;
	}

	public void browse(List edges, GsRegulatoryVertex targetVertex, OmddNode node) {
		this.paramList = new ArrayList();
		t_values = new int[edges.size()][4];
		t_me = new GsRegulatoryMultiEdge[t_values.length];
		
		for (int i=0 ; i<t_values.length ; i++) {
			GsDirectedEdge de = (GsDirectedEdge)edges.get(i);
			GsRegulatoryMultiEdge me = (GsRegulatoryMultiEdge)de.getUserObject();
			t_me[i] = me;
			t_values[i][0] = ((Integer)m_orderPos.get(me.getSource())).intValue();
		}

		browse(node);
		targetVertex.getV_logicalParameters().setManualParameters(paramList);
	}
	
	protected void leafReached(OmddNode leaf) {
		if (leaf.value == 0) {
			return;
		}
		// transform constraints on values to constraints on edges
		for (int i=0 ; i<t_values.length ; i++) {
			int nb = t_values[i][0];
			int begin = path[nb][0];
			int end = path[nb][1];
			GsRegulatoryMultiEdge me = t_me[i];
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
			List l = new ArrayList();
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
			
			paramList.add(new GsLogicalParameter(l, leaf.value));

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
