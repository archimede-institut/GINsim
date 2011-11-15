package org.ginsim.gui.service.tools.decisionanalysis;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.ginsim.graph.hierachicaltransitiongraph.GsHierarchicalNode;
import org.ginsim.graph.hierachicaltransitiongraph.GsHierarchicalTransitionGraph;
import org.ginsim.graph.regulatorygraph.GsRegulatoryGraph;
import org.ginsim.gui.service.tools.reg2dyn.GsSimulationParameters;
import org.ginsim.gui.service.tools.reg2dyn.SimulationQueuedState;
import org.ginsim.gui.service.tools.reg2dyn.SimulationUpdater;


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
		this.geneCount = htg.getNodeOrderSize();
	}

	public void run() {
		
		// No more used
		//List<GsRegulatoryVertex> nodeOrder = htg.getNodeOrder();
		
		//Iterate on the selected vertex or all of them f node are selected
		Iterator<GsHierarchicalNode> it = htg.getGraphManager().getSelectedVertexIterator();
		if (! it.hasNext()) {
			it = htg.getVertices().iterator();
		}
		for (; it.hasNext();) {
			GsHierarchicalNode source = it.next();
			List<byte[]> state_list = new LinkedList<byte[]>();
			source.statesSet.statesToFullList(state_list);
			for (byte[] source_state: state_list) {
				for (SimulationUpdater updt = getUpdaterForState(source_state); updt.hasNext();) {
					byte[] target_state = ((SimulationQueuedState)(updt.next())).state;
					GsHierarchicalNode target = htg.getNodeForState(target_state);
					if (!target.equals(source)) {
						GsDecisionOnEdge edge = htg.getEdge(source, target);
						 if (edge != null) {
							 // FIXME: used to call computeChange below, which may create the DecisionOnEdge object
							 // I hope it was properly moved to edge.init() ....
							 edge.init(geneCount);
							 edge.computeChange(source_state, target_state);							 
							 // FIXME: is this still needed?
							 // edge.setUserObject(computeChange(source_state, target_state, (GsDecisionOnEdge) edge.getUserObject(), nodeOrder));							 
						 }
					}
				}
			}
		}
	}


	/*
	 * FIXME: this is still here just to remind to check for missing initialisation
	 * should not be used anymore...
	 */
//	private GsDecisionOnEdge computeChange(byte[] source_state, byte[] target_state, GsDecisionOnEdge decisions, List<GsRegulatoryVertex> nodeOrder) {
//		if (decisions == null) {
//			decisions = new GsDecisionOnEdge(geneCount, nodeOrder);
//		}
//		decisions.computeChange(source_state, target_state);
//		return decisions;
//	}

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