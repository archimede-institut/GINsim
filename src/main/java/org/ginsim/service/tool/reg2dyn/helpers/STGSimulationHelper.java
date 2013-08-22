package org.ginsim.service.tool.reg2dyn.helpers;

import java.util.List;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.NodeInfo;
import org.ginsim.common.application.GsException;
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
public class STGSimulationHelper implements SimulationHelper {
	protected DynamicNode node;
	protected DynamicGraph stateTransitionGraph;
	protected NodeAttributesReader vreader;
	
	public STGSimulationHelper(LogicalModel model, SimulationParameters params) {
		List<NodeInfo> nodes = model.getNodeOrder();
		stateTransitionGraph = GraphManager.getInstance().getNewGraph( DynamicGraph.class, nodes);
		// FIXME: associated graph in the new simulation
		stateTransitionGraph.setAssociatedGraph(params.param_list.graph);
		stateTransitionGraph.setLogicalModel(model);
		
        vreader = stateTransitionGraph.getNodeAttributeReader();
        // add some default comments to the state transition graph
        stateTransitionGraph.getAnnotation().setComment(params.getDescr(nodes)+"\n");
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

