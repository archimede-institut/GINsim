package fr.univmrs.tagc.GINsim.reg2dyn;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
	
	public DynamicalHierarchicalSimulationHelper(GsRegulatoryGraph regGraph, GsSimulationParameters params) {
		this.regGraph = regGraph;
		dynHieGraph = new GsDynamicalHierarchicalGraph(params.nodeOrder);
		if (regGraph instanceof GsGraph) {
			dynHieGraph.setAssociatedGraph((GsGraph)regGraph);
		}
		GsVertexAttributesReader vreader = dynHieGraph.getGraphManager().getVertexAttributesReader();
		vreader.setDefaultVertexSize(5+10*params.nodeOrder.size(), 25);
        // add some default comments to the state transition graph
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
		Set s = (Set) arcs.get(from);
		if (s == null) {
			s = new HashSet();
			arcs.put(from, s);
		}
		s.add(to);
	}

	public Set arcsFrom(GsDynamicalHierarchicalNode from) {
		return (Set) arcs.get(from);
	}

}