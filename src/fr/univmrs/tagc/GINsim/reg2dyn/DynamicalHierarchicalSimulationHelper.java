package fr.univmrs.tagc.GINsim.reg2dyn;

import java.util.HashMap;
import java.util.Map;

import fr.univmrs.tagc.GINsim.dynamicalHierachicalGraph.GsDynamicalHierarchicalGraph;
import fr.univmrs.tagc.GINsim.dynamicalHierachicalGraph.GsDynamicalHierarchicalNode;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;

public class DynamicalHierarchicalSimulationHelper extends SimulationHelper {
	protected GsDynamicalHierarchicalNode node;
	protected GsDynamicalHierarchicalGraph dynHieGraph;
	public Map arcs;
	protected GsRegulatoryGraph regGraph;
	protected byte mergingStrategy;
	
	public DynamicalHierarchicalSimulationHelper(GsRegulatoryGraph regGraph, GsSimulationParameters params) {
		this.regGraph = regGraph;
		this.dynHieGraph = new GsDynamicalHierarchicalGraph(params.nodeOrder);
		this.mergingStrategy = (byte) params.hierarchicalStrategies;
		if (regGraph instanceof GsRegulatoryGraph) {
			dynHieGraph.setAssociatedGraph(regGraph);
		}
		GsVertexAttributesReader vreader = dynHieGraph.getGraphManager().getVertexAttributesReader();
		vreader.setDefaultVertexSize(5+10*params.nodeOrder.size(), 25);
        dynHieGraph.getAnnotation().setComment(params.getDescr()+"\n");
        arcs = new HashMap();
	}

	boolean addNode(SimulationQueuedState item) {
		return false;
	}

	GsGraph endSimulation() {
		return dynHieGraph;
	}

	void setStable() {
	}

	public Object getNode() {
		return node;
	}
	
	public void setNode(Object node) {
		this.node = (GsDynamicalHierarchicalNode) node;
	}
	
	public GsDynamicalHierarchicalGraph getGraph() {
		return this.dynHieGraph;
	}

	public void addEdge(GsDynamicalHierarchicalNode from, GsDynamicalHierarchicalNode to) {
		from.getOutgoingEdges().add(to);
		to.getIncomingEdges().add(from);
	}
}
