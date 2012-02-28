package org.ginsim.service.tool.modelsimplifier;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;

import org.ginsim.common.utils.log.LogManager;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
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
public class ModelRewiring implements Callable<RegulatoryGraph> {

	private final RegulatoryGraph graph;

	private RewiringAction post;
	private Collection<RegulatoryNode> targets = null;
	private Collection<RegulatoryNode> rewiredtargets = null;
	
	public ModelRewiring( RegulatoryGraph graph) {
		this.graph = graph;
		this.post = RewiringAction.None;
	}
	
	public void setRewiringAction(RewiringAction post) {
		this.post = post;
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
			SimulationParameterList paramList = (SimulationParameterList)ObjectAssociationManager.getInstance().getObject(graph, SimulationParametersManager.KEY, true);
			PriorityClassManager pcmanager = paramList.pcmanager;
			System.out.println(pcmanager.getNbElements());
			PriorityClassDefinition pcdef = pcmanager.getElement(null, pcmanager.add());
			pcdef.add();
			Reg2dynPriorityClass pc = pcdef.getElement(null, pcdef.getNbElements()-1);
			System.out.println(pc);
			pc.setMode(Reg2dynPriorityClass.SYNCHRONOUS);
			
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
					} else {
						nodes.put(regulator, targetCount);
					}
				}
			}
		}
		
		return rewiredtargets;
	}

}
