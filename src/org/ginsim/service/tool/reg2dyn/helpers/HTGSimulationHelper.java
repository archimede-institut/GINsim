package org.ginsim.service.tool.reg2dyn.helpers;

import java.util.HashMap;
import java.util.Map;

import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.hierachicaltransitiongraph.HierarchicalNode;
import org.ginsim.core.graph.hierachicaltransitiongraph.HierarchicalTransitionGraph;
import org.ginsim.core.graph.hierachicaltransitiongraph.HierarchicalTransitionGraphImpl;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.logicalmodel.LogicalModel;
import org.ginsim.service.tool.reg2dyn.SimulationParameters;
import org.ginsim.service.tool.reg2dyn.SimulationQueuedState;


public class HTGSimulationHelper  implements SimulationHelper {
	protected HierarchicalNode node;
	protected HierarchicalTransitionGraph htg;
	public Map arcs;
	protected LogicalModel model;
	
	public HTGSimulationHelper(LogicalModel model, SimulationParameters params) {
		this.model = model;
		int mode;
		if (params.simulationStrategy == SimulationParameters.STRATEGY_HTG) {
			mode = HierarchicalTransitionGraphImpl.MODE_HTG;
		} else {
			mode = HierarchicalTransitionGraphImpl.MODE_SCC;
		}
		this.htg = GraphManager.getInstance().getNewGraph( HierarchicalTransitionGraph.class, params.nodeOrder, mode);
		
		// FIXME: associated graph based on LogicalModel
		// htg.setAssociatedGraph(model);
		
		NodeAttributesReader vreader = htg.getNodeAttributeReader();
		vreader.setDefaultNodeSize(5+10*params.nodeOrder.size(), 25);
        htg.getAnnotation().setComment(params.getDescr()+"\n");
        arcs = new HashMap();
	}

	public boolean addNode(SimulationQueuedState item) {
		return false;
	}

	public Graph endSimulation() {
		
		return htg;
	}

	public void setStable() {
	}

	public Object getNode() {
		return node;
	}
	
	public void setNode(Object node) {
		this.node = (HierarchicalNode) node;
	}
	
	public Graph getDynamicGraph() {
		
		return this.htg;
	}
}
