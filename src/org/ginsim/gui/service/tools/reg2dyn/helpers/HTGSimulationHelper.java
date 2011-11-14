package org.ginsim.gui.service.tools.reg2dyn.helpers;

import java.util.HashMap;
import java.util.Map;

import org.ginsim.graph.common.Graph;
import org.ginsim.graph.hierachicaltransitiongraph.GsHierarchicalNode;
import org.ginsim.graph.hierachicaltransitiongraph.GsHierarchicalTransitionGraph;
import org.ginsim.gui.service.tools.reg2dyn.GsSimulationParameters;
import org.ginsim.gui.service.tools.reg2dyn.SimulationQueuedState;

import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;

public class HTGSimulationHelper  extends SimulationHelper {
	protected GsHierarchicalNode node;
	protected GsHierarchicalTransitionGraph htg;
	public Map arcs;
	protected GsRegulatoryGraph regGraph;
	
	public HTGSimulationHelper(GsRegulatoryGraph regGraph, GsSimulationParameters params) {
		this.regGraph = regGraph;
		int mode;
		if (params.simulationStrategy == GsSimulationParameters.STRATEGY_HTG) {
			mode = GsHierarchicalTransitionGraph.MODE_HTG;
		} else {
			mode = GsHierarchicalTransitionGraph.MODE_SCC;
		}
		this.htg = new GsHierarchicalTransitionGraph(params.nodeOrder, mode);
		htg.setAssociatedGraph(regGraph);
		GsVertexAttributesReader vreader = htg.getVertexAttributeReader();
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
