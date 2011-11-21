package org.ginsim.gui.service.tools.reg2dyn.helpers;

import java.util.HashMap;
import java.util.Map;

import org.ginsim.graph.GraphManager;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.common.VertexAttributesReader;
import org.ginsim.graph.hierachicaltransitiongraph.HierarchicalNode;
import org.ginsim.graph.hierachicaltransitiongraph.HierarchicalTransitionGraph;
import org.ginsim.graph.hierachicaltransitiongraph.HierarchicalTransitionGraphImpl;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.service.tools.reg2dyn.SimulationParameters;
import org.ginsim.gui.service.tools.reg2dyn.SimulationQueuedState;


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
		VertexAttributesReader vreader = htg.getVertexAttributeReader();
		vreader.setDefaultVertexSize(5+10*params.nodeOrder.size(), 25);
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
