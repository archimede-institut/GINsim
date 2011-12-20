package org.ginsim.service.tool.reg2dyn.helpers;

import org.ginsim.common.exception.GsException;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.dynamicgraph.DynamicGraph;
import org.ginsim.core.graph.dynamicgraph.DynamicNode;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.service.tool.reg2dyn.SimulationParameters;
import org.ginsim.service.tool.reg2dyn.SimulationQueuedState;



/**
 * This is the SimulationHelper used for the simulation of STG.
 * 
 * @author Duncan Berenguier
 */
public class STGSimulationHelper extends SimulationHelper {
	protected DynamicNode node;
	protected DynamicGraph stateTransitionGraph;
	protected NodeAttributesReader vreader;
	
	public STGSimulationHelper(RegulatoryGraph regGraph, SimulationParameters params) {
		stateTransitionGraph = GraphManager.getInstance().getNewGraph( DynamicGraph.class, params.nodeOrder);
		stateTransitionGraph.setAssociatedGraph(regGraph);
		
        vreader = stateTransitionGraph.getNodeAttributeReader();
	    vreader.setDefaultNodeSize(5+10*params.nodeOrder.size(), 25);
        // add some default comments to the state transition graph
        stateTransitionGraph.getAnnotation().setComment(params.getDescr()+"\n");
	}

	public boolean addNode(SimulationQueuedState item) {
		node = new DynamicNode(item.state);
		boolean isnew = stateTransitionGraph.addNode(node);
		if (item.previous != null) {
			stateTransitionGraph.addEdge((DynamicNode)item.previous, node, item.multiple);
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
		this.node = (DynamicNode) node;
	}
}

