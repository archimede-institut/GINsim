package org.ginsim.gui.service.tools.reg2dyn.helpers;

import org.ginsim.exception.GsException;
import org.ginsim.graph.GraphManager;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.common.VertexAttributesReader;
import org.ginsim.graph.dynamicgraph.DynamicGraph;
import org.ginsim.graph.dynamicgraph.DynamicNode;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.service.tools.reg2dyn.SimulationParameters;
import org.ginsim.gui.service.tools.reg2dyn.SimulationQueuedState;



/**
 * This is the SimulationHelper used for the simulation of STG.
 * 
 * @author Duncan Berenguier
 */
public class GsSTGSimulationHelper extends SimulationHelper {
	protected DynamicNode node;
	protected DynamicGraph stateTransitionGraph;
	protected VertexAttributesReader vreader;
	
	public GsSTGSimulationHelper(RegulatoryGraph regGraph, SimulationParameters params) {
		stateTransitionGraph = GraphManager.getInstance().getNewGraph( DynamicGraph.class, params.nodeOrder);
		stateTransitionGraph.setAssociatedGraph(regGraph);
		
        vreader = stateTransitionGraph.getVertexAttributeReader();
	    vreader.setDefaultVertexSize(5+10*params.nodeOrder.size(), 25);
        // add some default comments to the state transition graph
        stateTransitionGraph.getAnnotation().setComment(params.getDescr()+"\n");
	}

	public boolean addNode(SimulationQueuedState item) {
		node = new DynamicNode(item.state);
		boolean isnew = stateTransitionGraph.addVertex(node);
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

