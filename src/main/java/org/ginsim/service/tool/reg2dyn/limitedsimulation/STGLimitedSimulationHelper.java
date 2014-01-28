package org.ginsim.service.tool.reg2dyn.limitedsimulation;

import org.colomoto.logicalmodel.LogicalModel;
import org.ginsim.core.graph.dynamicgraph.DynamicNode;
import org.ginsim.core.graph.hierarchicaltransitiongraph.HierarchicalNode;
import org.ginsim.core.graph.hierarchicaltransitiongraph.HierarchicalTransitionGraph;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.graph.view.NodeShape;
import org.ginsim.service.tool.reg2dyn.SimulationParameters;
import org.ginsim.service.tool.reg2dyn.SimulationQueuedState;
import org.ginsim.service.tool.reg2dyn.helpers.STGSimulationHelper;

public class STGLimitedSimulationHelper extends STGSimulationHelper {

	private SimulationConstraint constraint;
	private final HierarchicalTransitionGraph htg;
	private NodeAttributesReader htg_vreader;

	public STGLimitedSimulationHelper(LogicalModel model, HierarchicalTransitionGraph htg, SimulationParameters params, SimulationConstraint constraint) {
		super(model, params);
		this.htg = htg;
		this.htg_vreader = htg.getNodeAttributeReader();
		this.constraint = constraint;
	}
	
	@Override
	public boolean addNode(SimulationQueuedState item) {
		DynamicNode previous = ((DynamicNode)item.previous);
		byte[] previous_state = null;
		if (previous != null) {
			previous_state  = previous.state;
		}
		int status = constraint.shouldAdd(item.state, previous_state);
		if (status == 0) {
			return false;
		}
		node = new DynamicNode(item.state);
		boolean isnew = stateTransitionGraph.addNode(node);
		vreader.setNode(node);
		HierarchicalNode hierarchicalNode = htg.getNodeForState(item.state);
		htg_vreader.setNode(hierarchicalNode);
		vreader.copyFrom(htg_vreader);
		if (status == 1) {
			vreader.setShape(NodeShape.RECTANGLE);
		} else {
			vreader.setShape(NodeShape.ELLIPSE);
		}
		if (item.previous != null) {
			stateTransitionGraph.addEdge((DynamicNode)item.previous, node, item.multiple);
		}
		return isnew;
	}


}
