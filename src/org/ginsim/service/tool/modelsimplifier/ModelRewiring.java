package org.ginsim.service.tool.modelsimplifier;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;

import org.ginsim.common.exception.GsException;
import org.ginsim.common.utils.log.LogManager;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;
import org.ginsim.service.tool.reg2dyn.SimulationParameterList;
import org.ginsim.service.tool.reg2dyn.SimulationParametersManager;
import org.ginsim.service.tool.reg2dyn.priorityclass.PriorityClassDefinition;
import org.ginsim.service.tool.reg2dyn.priorityclass.PriorityClassManager;
import org.ginsim.service.tool.reg2dyn.priorityclass.Reg2dynPriorityClass;

/**
 * Rewire a RegulatoryGraph to put aside target nodes that do not affect the dynamical behaviour.
 * 
 * @author Aurelien Naldi
 */
public class ModelRewiring extends AbstractModelSimplifier implements Callable<RegulatoryGraph>  {

	private final RegulatoryGraph graph;

	private RewiringAction post;
	private Collection<RegulatoryNode> targets = null;
	private Collection<RegulatoryNode> rewiredtargets = null;
	private LinkedList<RegulatoryNode> orderedPseudoOutputs = null;
	
	public ModelRewiring( RegulatoryGraph graph) {
		this.graph = graph;
		this.post = RewiringAction.None;
	}
	
	public void setRewiringAction(RewiringAction post) {
		this.post = post;
	}
	
	public OMDDNode[] rewirePseudoOutputs() throws GsException {
		
		List<RegulatoryNode> nOrder = graph.getNodeOrder();

		// apply reduction to all pseudo-targets
		OMDDNode[] functions = graph.getAllTrees(true);
		lookupRewired();
		// orderedPseudoOutputs provides a sane pseudo-output order
		while (!orderedPseudoOutputs.isEmpty()) {
			RegulatoryNode n = orderedPseudoOutputs.removeLast();
			int removed_idx = nOrder.indexOf(n);
			for (RegulatoryMultiEdge edge: graph.getOutgoingEdges(n) ) {
				RegulatoryNode target = edge.getTarget();
				int target_idx = nOrder.indexOf(target);
				
					functions[target_idx] = remove(functions[target_idx], functions[removed_idx], removed_idx);
			}
			n.setOutput(true, graph);
		}
		
		return functions;
	}
	
	public void unMarkPseudoOutputs() {
		for (RegulatoryNode n: rewiredtargets) {
			n.setOutput(false, graph);
		}
	}
	
	public RegulatoryGraph call() {
		lookupRewired();
		switch (post) {
		case Delete:
			// Rough solution for now: just delete the targets
			for (RegulatoryNode node: targets) {
				graph.removeNode(node);
			}
			for (RegulatoryNode node: rewiredtargets) {
				graph.removeNode(node);
			}

			break;
		case LowPriority:
			// add a new priority class configuration where the targets are assigned a low priority
			SimulationParameterList paramList = (SimulationParameterList)ObjectAssociationManager.getInstance().getObject(graph, SimulationParametersManager.KEY, true);
			PriorityClassManager pcmanager = paramList.pcmanager;
			PriorityClassDefinition pcdef = pcmanager.getElement(null, pcmanager.add());
			pcdef.add();
			Reg2dynPriorityClass pc = pcdef.getElement(null, pcdef.getNbElements()-1);
			pc.setMode(Reg2dynPriorityClass.SYNCHRONOUS);
			for (RegulatoryNode node: targets) {
				pcdef.m_elt.put(node, pc);
			}
			break;
		case Rewire:
			try {
				rewirePseudoOutputs();
				// TODO: turn these new functions into a real LRG?
			} catch (GsException e) {
				LogManager.error(e);
			}
			
			break;
		}
		return graph;
	}
	

	public void reset() {
		targets = null;
		rewiredtargets = null;
	}
	
	public Collection<RegulatoryNode> lookupTargets() {
		if (targets != null) {
			return targets;
		}

		// lookup targets: nodes with no outgoing edges
		targets = new HashSet<RegulatoryNode>();
		for (RegulatoryNode node: graph.getNodeOrder()) {
			if (node.isInput()) {
				
				// consistency check
				Collection<RegulatoryMultiEdge> inedges = graph.getIncomingEdges(node);
				if (inedges != null && inedges.size() > 0) {
					LogManager.error("Input node should not have incoming edges: "+node);
				}
				
				// just ignore inputs
				continue;
			}
			
			Collection<RegulatoryMultiEdge> outedges = graph.getOutgoingEdges(node);
			int targetCount = outedges==null ? 0 : outedges.size();
			if (targetCount == 0) {
				targets.add(node);
			}
		}
		
		return targets;
	}

	
	public Collection<RegulatoryNode> lookupRewired() {
		if (rewiredtargets != null) {
			return rewiredtargets;
		}
		
		rewiredtargets = new HashSet<RegulatoryNode>();
		orderedPseudoOutputs = new LinkedList<RegulatoryNode>();
		Map<RegulatoryNode, Integer> nodes = new HashMap<RegulatoryNode, Integer>();
		Queue<RegulatoryNode> queue = new ArrayDeque<RegulatoryNode>();
		
		for (RegulatoryNode node: graph.getNodeOrder()) {
			if (node.isInput()) {
				continue;
			}
			
			Collection<RegulatoryMultiEdge> outedges = graph.getOutgoingEdges(node);
			int targetCount = outedges==null ? 0 : outedges.size();
			if (targetCount == 0) {
				queue.add(node);
			} else {
				nodes.put(node, targetCount);
			}
		}
		
		// recursively move nodes from the queue to the list of "target" nodes
		while (!queue.isEmpty()) {
			
			// move the first queued node to the list of targets
			RegulatoryNode node = queue.poll();
			
			// check if any of its regulator becomes a target, enqueue them as needed
			Collection<RegulatoryMultiEdge> inedges = graph.getIncomingEdges(node);
			if (inedges != null) {
				for (RegulatoryMultiEdge me: inedges) {
					RegulatoryNode regulator = me.getSource();
					int targetCount = nodes.get(regulator) - 1;
					if (targetCount == 0) {
						queue.add(regulator);
						rewiredtargets.add(regulator);
						orderedPseudoOutputs.add(regulator);
					} else {
						nodes.put(regulator, targetCount);
					}
				}
			}
		}
		
		return rewiredtargets;
	}

}
