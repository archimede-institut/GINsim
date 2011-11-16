package org.ginsim.gui.service.tools.reg2dyn.helpers;

import org.ginsim.exception.GsException;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.dynamicgraph.GsDynamicGraph;
import org.ginsim.graph.dynamicgraph.GsDynamicNode;
import org.ginsim.graph.regulatorygraph.GsRegulatoryGraph;
import org.ginsim.gui.service.tools.reg2dyn.GsSimulationParameters;
import org.ginsim.gui.service.tools.reg2dyn.SimulationQueuedState;

import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;


/**
 * This is the SimulationHelper used for the simulation of STG.
 * 
 * @author Duncan Berenguier
 */
public class GsSTGSimulationHelper extends SimulationHelper {
	protected GsDynamicNode node;
	protected GsDynamicGraph stateTransitionGraph;
	protected GsVertexAttributesReader vreader;
	
	public GsSTGSimulationHelper(GsRegulatoryGraph regGraph, GsSimulationParameters params) {
		stateTransitionGraph = new GsDynamicGraph(params.nodeOrder);
		stateTransitionGraph.setAssociatedGraph((GsRegulatoryGraph)regGraph);
		
        vreader = stateTransitionGraph.getVertexAttributeReader();
	    vreader.setDefaultVertexSize(5+10*params.nodeOrder.size(), 25);
        // add some default comments to the state transition graph
        stateTransitionGraph.getAnnotation().setComment(params.getDescr()+"\n");
	}

	public boolean addNode(SimulationQueuedState item) {
		node = new GsDynamicNode(item.state);
		boolean isnew = stateTransitionGraph.addVertex(node);
		if (item.previous != null) {
			stateTransitionGraph.addEdge((GsDynamicNode)item.previous, node, item.multiple);
		}
		return isnew;
	}

	public Graph endSimulation() {
		
		return stateTransitionGraph;
	}

	public void setStable() {
		node.setStable(true, vreader);
	}

	public Graph getRegulatoryGraph() throws GsException{
		
		return this.stateTransitionGraph.getAssociatedGraph();
	}
	
	public Graph getDynamicGraph() {
		
		return this.stateTransitionGraph;
	}

	public Object getNode() {
		return node;
	}
	
	public void setNode(Object node) {
		this.node = (GsDynamicNode) node;
	}
}

