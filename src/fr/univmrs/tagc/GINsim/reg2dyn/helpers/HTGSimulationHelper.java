package fr.univmrs.tagc.GINsim.reg2dyn.helpers;

import java.util.HashMap;
import java.util.Map;

import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.tagc.GINsim.hierachicalTransitionGraph.GsHierarchicalNode;
import fr.univmrs.tagc.GINsim.hierachicalTransitionGraph.GsHierarchicalTransitionGraph;
import fr.univmrs.tagc.GINsim.reg2dyn.GsSimulationParameters;
import fr.univmrs.tagc.GINsim.reg2dyn.SimulationQueuedState;
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
		mode = GsHierarchicalTransitionGraph.MODE_HTG; //FIXME remove ME
		this.htg = new GsHierarchicalTransitionGraph(params.nodeOrder, mode);
		;
		if (regGraph instanceof GsRegulatoryGraph) {
			htg.setAssociatedGraph(regGraph);
		}
		GsVertexAttributesReader vreader = htg.getGraphManager().getVertexAttributesReader();
		vreader.setDefaultVertexSize(5+10*params.nodeOrder.size(), 25);
        htg.getAnnotation().setComment(params.getDescr()+"\n");
        arcs = new HashMap();
	}

	public boolean addNode(SimulationQueuedState item) {
		return false;
	}

	public GsGraph endSimulation() {
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
	
	public GsGraph getRegulatoryGraph() {
		return this.regGraph;
	}
	
	public GsGraph getDynamicGraph() {
		return this.htg;
	}

	public void addEdge(GsHierarchicalNode from, GsHierarchicalNode to) {
		to.getIncomingEdges().add(from);
	}
}
