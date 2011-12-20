package org.ginsim.service.tool.reg2dyn.helpers;

import java.util.HashMap;
import java.util.Map;

import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.hierachicaltransitiongraph.HierarchicalNode;
import org.ginsim.core.graph.hierachicaltransitiongraph.HierarchicalTransitionGraph;
import org.ginsim.core.graph.hierachicaltransitiongraph.HierarchicalTransitionGraphImpl;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.service.tool.reg2dyn.SimulationParameters;
import org.ginsim.service.tool.reg2dyn.SimulationQueuedState;


public class HTGSimulationHelper  extends SimulationHelper {
	protected HierarchicalNode node;
	protected HierarchicalTransitionGraph htg;
	public Map arcs;
	protected RegulatoryGraph regGraph;
	
	public HTGSimulationHelper(RegulatoryGraph regGraph, SimulationParameters params) {
		this.regGraph = regGraph;
		int mode;
		if (params.simulationStrategy == SimulationParameters.STRATEGY_HTG) {
			mode = HierarchicalTransitionGraphImpl.MODE_HTG;
		} else {
			mode = HierarchicalTransitionGraphImpl.MODE_SCC;
		}
		this.htg = GraphManager.getInstance().getNewGraph( HierarchicalTransitionGraph.class, params.nodeOrder, mode);
		htg.setAssociatedGraph(regGraph);
		NodeAttributesReader vreader = htg.getNodeAttributeReader();
		vreader.setDefaultNodeSize(5+10*params.nodeOrder.size(), 25);
        htg.getAnnotation().setComment(params.getDescr()+"\n");
        arcs = new HashMap();
	}

	public boolean addNode(SimulationQueuedState item) {
		return false;
	}

	public Graph endSimulation() {
		
		return htg;
	}

	public void setStable() {
	}

	public Object getNode() {
		return node;
	}
	
	public void setNode(Object node) {
		this.node = (HierarchicalNode) node;
	}
	
	public Graph getRegulatoryGraph() {
		
		return this.regGraph;
	}
	
	public Graph getDynamicGraph() {
		
		return this.htg;
	}
}
