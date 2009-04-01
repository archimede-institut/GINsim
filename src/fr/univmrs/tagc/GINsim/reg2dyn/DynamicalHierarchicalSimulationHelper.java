package fr.univmrs.tagc.GINsim.reg2dyn;

import fr.univmrs.tagc.GINsim.dynamicalHierachicalGraph.GsDynamicalHierarchicalGraph;
import fr.univmrs.tagc.GINsim.dynamicalHierachicalGraph.GsDynamicalHierarchicalNode;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;

public class DynamicalHierarchicalSimulationHelper extends SimulationHelper {
	protected GsDynamicalHierarchicalNode node;
	protected GsDynamicalHierarchicalGraph dynHieGraph;
	
	public DynamicalHierarchicalSimulationHelper(GsRegulatoryGraph regGraph, GsSimulationParameters params) {
		dynHieGraph = new GsDynamicalHierarchicalGraph(params.nodeOrder);
		if (regGraph instanceof GsGraph) {
			dynHieGraph.setAssociatedGraph((GsGraph)regGraph);
		}
//        vreader = dynHieGraph.getGraphManager().getVertexAttributesReader();
//	    vreader.setDefaultVertexSize(5+10*params.nodeOrder.size(), 25);
        // add some default comments to the state transition graph
        dynHieGraph.getAnnotation().setComment(params.getDescr()+"\n");
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

}