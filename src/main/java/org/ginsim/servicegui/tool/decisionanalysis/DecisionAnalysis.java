package org.ginsim.servicegui.tool.decisionanalysis;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.colomoto.biolqm.LogicalModel;
import org.ginsim.core.graph.hierarchicaltransitiongraph.DecisionOnEdge;
import org.ginsim.core.graph.hierarchicaltransitiongraph.HierarchicalNode;
import org.ginsim.core.graph.hierarchicaltransitiongraph.HierarchicalTransitionGraph;
import org.ginsim.service.tool.reg2dyn.SimulationParameters;
import org.ginsim.service.tool.reg2dyn.SimulationQueuedState;
import org.ginsim.service.tool.reg2dyn.updater.BaseSimulationUpdater;
import org.ginsim.service.tool.reg2dyn.updater.SimulationUpdater;


/**
 * Labels a given set of edges from an HTG with the updated genes of their corresponding edges in the STG.
 *
 */
public class DecisionAnalysis extends Thread {

	private HierarchicalTransitionGraph htg;
	private SimulationParameters params;
	private LogicalModel model;

	/**
	 * 
	 */
	public DecisionAnalysis(LogicalModel model, HierarchicalTransitionGraph htg, SimulationParameters params) {
		
		this.htg = htg;
		this.params = params;
		this.model = model;
	}

	public void run( Collection<HierarchicalNode> selected_vertices) {
		
		//Iterate on the selected vertex or all of them f node are selected
		if (selected_vertices == null) selected_vertices = htg.getNodes();
		for (HierarchicalNode source : selected_vertices) {
			List<byte[]> state_list = new LinkedList<byte[]>();
			source.statesSet.statesToFullList(state_list);
			for (byte[] source_state: state_list) {
				for (SimulationUpdater updt = getUpdaterForState(source_state); updt.hasNext();) {
					byte[] target_state = ((SimulationQueuedState)(updt.next())).state;
					HierarchicalNode target = htg.getNodeForState(target_state);
					if (!target.equals(source)) {
						DecisionOnEdge edge = htg.getEdge(source, target);
						 if (edge != null) {
							 edge.computeChange(source_state, target_state);
						 }
					}
				}
			}
		}
	}

	/**
	 * Create and initialize a SimulationUpdater for a given __state__.
	 * @param state
	 * @return
	 */
	private SimulationUpdater getUpdaterForState(byte[] state) {
   		SimulationUpdater updater = params.getPriorityClassDefinition().getUpdater(model);
   		updater.setState(state, 0, null);
   		return updater;
	}
}
