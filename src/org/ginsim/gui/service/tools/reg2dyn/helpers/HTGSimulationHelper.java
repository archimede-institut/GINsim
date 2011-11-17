package org.ginsim.gui.service.tools.reg2dyn.helpers;

import java.util.HashMap;
import java.util.Map;

import org.ginsim.graph.GraphManager;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.common.VertexAttributesReader;
import org.ginsim.graph.hierachicaltransitiongraph.GsHierarchicalNode;
import org.ginsim.graph.hierachicaltransitiongraph.GsHierarchicalTransitionGraph;
import org.ginsim.graph.hierachicaltransitiongraph.HierarchicalTransitionGraphImpl;
import org.ginsim.graph.regulatorygraph.GsRegulatoryGraph;
import org.ginsim.gui.service.tools.reg2dyn.GsSimulationParameters;
import org.ginsim.gui.service.tools.reg2dyn.SimulationQueuedState;


public class HTGSimulationHelper  extends SimulationHelper {
	protected GsHierarchicalNode node;
	protected GsHierarchicalTransitionGraph htg;
	public Map arcs;
	protected GsRegulatoryGraph regGraph;
	
	public HTGSimulationHelper(GsRegulatoryGraph regGraph, GsSimulationParameters params) {
		this.regGraph = regGraph;
		int mode;
		if (params.simulationStrategy == GsSimulationParameters.STRATEGY_HTG) {
			mode = HierarchicalTransitionGraphImpl.MODE_HTG;
		} else {
			mode = HierarchicalTransitionGraphImpl.MODE_SCC;
		}
		this.htg = GraphManager.getInstance().getNewGraph( GsHierarchicalTransitionGraph.class, params.nodeOrder, mode);
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
		this.node = (GsHierarchicalNode) node;
	}
	
	public Graph getRegulatoryGraph() {
		
		return this.regGraph;
	}
	
	public Graph getDynamicGraph() {
		
		return this.htg;
	}
}
