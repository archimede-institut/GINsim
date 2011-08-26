package fr.univmrs.tagc.GINsim.hierachicalTransitionGraph.DecisionAnalysis;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import fr.univmrs.tagc.GINsim.data.GsDirectedEdge;
import fr.univmrs.tagc.GINsim.graph.GsGraphManager;
import fr.univmrs.tagc.GINsim.hierachicalTransitionGraph.GsHierarchicalNode;
import fr.univmrs.tagc.GINsim.hierachicalTransitionGraph.GsHierarchicalTransitionGraph;
import fr.univmrs.tagc.GINsim.reg2dyn.GsSimulationParameters;
import fr.univmrs.tagc.GINsim.reg2dyn.SimulationQueuedState;
import fr.univmrs.tagc.GINsim.reg2dyn.SimulationUpdater;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;

/**
 * Labels a given set of edges from an HTG with the updated genes of their corresponding edges in the STG.
 *
 */
public class GsDecisionAnalysis extends Thread {

	private GsHierarchicalTransitionGraph htg;
	private GsRegulatoryGraph regGraph;
	private GsSimulationParameters params;
	private int geneCount;

	/**
	 * 
	 */
	public GsDecisionAnalysis(GsHierarchicalTransitionGraph htg, GsSimulationParameters params) {
		this.htg = htg;
		this.regGraph = (GsRegulatoryGraph) htg.getAssociatedGraph();
		this.params = params;
		this.geneCount = htg.getNodeOrder().size();
	}

	public void run() {
		GsGraphManager gm = htg.getGraphManager();
		
		//Iterate on the selected vertex or all of them f node are selected
		Iterator it = gm.getSelectedVertexIterator();
		if (! it.hasNext()) {
			it = gm.getVertexIterator();
		}
		for (; it.hasNext();) {
			GsHierarchicalNode source = (GsHierarchicalNode) it.next();
			List state_list = new LinkedList();
			source.statesSet.statesToFullList(state_list);
			for (Iterator it_states = state_list.iterator(); it_states.hasNext();) {
				byte[] source_state = (byte[]) it_states.next();
				for (SimulationUpdater updt = getUpdaterForState(source_state); updt.hasNext();) {
					byte[] target_state = ((SimulationQueuedState)(updt.next())).state;
					GsHierarchicalNode target = htg.getNodeForState(target_state);
					if (!target.equals(source)) {
						 GsDirectedEdge edge = (GsDirectedEdge) gm.getEdge(source, target);
						 if (edge != null) {
							 edge.setUserObject(computeChange(source_state, target_state, (GsDecisionOnEdge) edge.getUserObject()));							 
						 }
					}
				}
				
			}
		}
	}


	private GsDecisionOnEdge computeChange(byte[] source_state, byte[] target_state, GsDecisionOnEdge decisions) {
		if (decisions == null) {
			decisions = new GsDecisionOnEdge(geneCount);
		}
		decisions.computeChange(source_state, target_state);
		return decisions;
	}

	/**
	 * Create and initialize a SimulationUpdater for a given __state__.
	 * @param state
	 * @return
	 */
	private SimulationUpdater getUpdaterForState(byte[] state) {
   		SimulationUpdater updater = SimulationUpdater.getInstance(regGraph, params);
   		updater.setState(state, 0, null);
   		return updater;
	}
}
