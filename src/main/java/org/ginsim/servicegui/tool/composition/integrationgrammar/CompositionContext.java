package org.ginsim.servicegui.tool.composition.integrationgrammar;

import java.util.List;
import java.util.Set;

import org.colomoto.logicalmodel.NodeInfo;

public interface CompositionContext {

	public List<NodeInfo> getLowLevelComponents();

	public Set<Integer> getNeighbourIndices(int instance, int distance);

	public NodeInfo getLowLevelComponentFromName(String componentName,
			int instance);
}
