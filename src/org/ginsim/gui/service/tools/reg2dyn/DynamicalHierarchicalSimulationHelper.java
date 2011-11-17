package org.ginsim.gui.service.tools.reg2dyn;

import java.util.HashMap;
import java.util.Map;

import org.ginsim.graph.common.Graph;
import org.ginsim.graph.common.VertexAttributesReader;
import org.ginsim.graph.dynamicalhierarchicalgraph.DynamicalHierarchicalGraphImpl;
import org.ginsim.graph.dynamicalhierarchicalgraph.GsDynamicalHierarchicalGraph;
import org.ginsim.graph.dynamicalhierarchicalgraph.GsDynamicalHierarchicalNode;
import org.ginsim.graph.regulatorygraph.GsRegulatoryGraph;
import org.ginsim.gui.service.tools.reg2dyn.helpers.SimulationHelper;


public class DynamicalHierarchicalSimulationHelper extends SimulationHelper {
	
	protected GsDynamicalHierarchicalNode node;
	protected GsDynamicalHierarchicalGraph dynHieGraph;
	public Map arcs;
	protected GsRegulatoryGraph regGraph;
	protected byte mergingStrategy;
	
	public DynamicalHierarchicalSimulationHelper(GsRegulatoryGraph regGraph, GsSimulationParameters params) {
		this.regGraph = regGraph;
		this.dynHieGraph = new DynamicalHierarchicalGraphImpl(params.nodeOrder);
//		this.mergingStrategy = (byte) params.hierarchicalStrategies;
		if (regGraph instanceof GsRegulatoryGraph) {
			dynHieGraph.setAssociatedGraph(regGraph);
		}
		VertexAttributesReader vreader = dynHieGraph.getVertexAttributeReader();
		vreader.setDefaultVertexSize(5+10*params.nodeOrder.size(), 25);
        dynHieGraph.getAnnotation().setComment(params.getDescr()+"\n");
        arcs = new HashMap();
	}

	public boolean addNode(SimulationQueuedState item) {
		return false;
	}

	public Graph endSimulation() {
		
		return dynHieGraph;
	}

	public void setStable() {
	}

	public Object getNode() {
		return node;
	}
	
	public void setNode(Object node) {
		this.node = (GsDynamicalHierarchicalNode) node;
	}
	
	public Graph getRegulatoryGraph() {
		
		return this.regGraph;
	}
	public Graph getDynamicGraph() {
		
		return this.dynHieGraph;
	}

	public void addEdge(GsDynamicalHierarchicalNode from, GsDynamicalHierarchicalNode to) {
		from.getOutgoingEdges().add(to);
		to.getIncomingEdges().add(from);
	}
}
