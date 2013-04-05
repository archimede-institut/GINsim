package org.ginsim.servicegui.tool.composition.integrationgrammar;

import java.util.List;
import java.util.Set;

import org.colomoto.logicalmodel.NodeInfo;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;

public interface CompositionContext {
	public List<RegulatoryNode> getComponents();
	public List<NodeInfo> getLowLevelComponents();
	public Set<Integer> getNeighbourIndices(int instance, int distance);
	public NodeInfo getLowLevelComponentFromName(String componentName, int instance);
}
