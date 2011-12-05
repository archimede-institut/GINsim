package org.ginsim.servicegui.tool.reg2dyn;

import java.util.HashMap;
import java.util.Map;

import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.dynamicalhierarchicalgraph.DynamicalHierarchicalGraph;
import org.ginsim.core.graph.dynamicalhierarchicalgraph.DynamicalHierarchicalNode;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.servicegui.tool.reg2dyn.helpers.SimulationHelper;


public class DynamicalHierarchicalSimulationHelper extends SimulationHelper {
	
	protected DynamicalHierarchicalNode node;
	protected DynamicalHierarchicalGraph dynHieGraph;
	public Map arcs;
	protected RegulatoryGraph regGraph;
	protected byte mergingStrategy;
	
	public DynamicalHierarchicalSimulationHelper(RegulatoryGraph regGraph, SimulationParameters params) {
		this.regGraph = regGraph;
		this.dynHieGraph = GraphManager.getInstance().getNewGraph( DynamicalHierarchicalGraph.class, params.nodeOrder);
//		this.mergingStrategy = (byte) params.hierarchicalStrategies;
		if (regGraph instanceof RegulatoryGraph) {
			dynHieGraph.setAssociatedGraph(regGraph);
		}
		NodeAttributesReader vreader = dynHieGraph.getNodeAttributeReader();
		vreader.setDefaultNodeSize(5+10*params.nodeOrder.size(), 25);
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
		this.node = (DynamicalHierarchicalNode) node;
	}
	
	public Graph getRegulatoryGraph() {
		
		return this.regGraph;
	}
	public Graph getDynamicGraph() {
		
		return this.dynHieGraph;
	}

	public void addEdge(DynamicalHierarchicalNode from, DynamicalHierarchicalNode to) {
		from.getOutgoingEdges().add(to);
		to.getIncomingEdges().add(from);
	}
}
