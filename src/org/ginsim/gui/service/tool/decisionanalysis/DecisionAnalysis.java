package org.ginsim.gui.service.tool.decisionanalysis;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.ginsim.exception.GsException;
import org.ginsim.graph.hierachicaltransitiongraph.HierarchicalNode;
import org.ginsim.graph.hierachicaltransitiongraph.HierarchicalTransitionGraph;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.service.tool.reg2dyn.SimulationParameters;
import org.ginsim.gui.service.tool.reg2dyn.SimulationQueuedState;
import org.ginsim.gui.service.tool.reg2dyn.SimulationUpdater;


/**
 * Labels a given set of edges from an HTG with the updated genes of their corresponding edges in the STG.
 *
 */
public class DecisionAnalysis extends Thread {

	private HierarchicalTransitionGraph htg;
	private RegulatoryGraph regGraph;
	private SimulationParameters params;
	private int geneCount;

	/**
	 * 
	 */
	public DecisionAnalysis(HierarchicalTransitionGraph htg, SimulationParameters params) throws GsException{
		
		this.htg = htg;
		this.regGraph = (RegulatoryGraph) htg.getAssociatedGraph();
		this.params = params;
		this.geneCount = htg.getNodeOrderSize();
	}

	public void run( List selected_vertices) {
		
		// No more used
		//List<RegulatoryNode> nodeOrder = htg.getNodeOrder();
		
		//Iterate on the selected vertex or all of them f node are selected
		Iterator<HierarchicalNode> it = selected_vertices.iterator();
		if (! it.hasNext()) {
			it = htg.getNodes().iterator();
		}
		for (; it.hasNext();) {
			HierarchicalNode source = it.next();
			List<byte[]> state_list = new LinkedList<byte[]>();
			source.statesSet.statesToFullList(state_list);
			for (byte[] source_state: state_list) {
				for (SimulationUpdater updt = getUpdaterForState(source_state); updt.hasNext();) {
					byte[] target_state = ((SimulationQueuedState)(updt.next())).state;
					HierarchicalNode target = htg.getNodeForState(target_state);
					if (!target.equals(source)) {
						DecisionOnEdge edge = htg.getEdge(source, target);
						 if (edge != null) {
							 // FIXME: used to call computeChange below, which may create the DecisionOnEdge object
							 // I hope it was properly moved to edge.init() ....
							 edge.init(geneCount);
							 edge.computeChange(source_state, target_state);							 
							 // FIXME: is this still needed?
							 // edge.setUserObject(computeChange(source_state, target_state, (DecisionOnEdge) edge.getUserObject(), nodeOrder));							 
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
//	private DecisionOnEdge computeChange(byte[] source_state, byte[] target_state, DecisionOnEdge decisions, List<RegulatoryNode> nodeOrder) {
//		if (decisions == null) {
//			decisions = new DecisionOnEdge(geneCount, nodeOrder);
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
