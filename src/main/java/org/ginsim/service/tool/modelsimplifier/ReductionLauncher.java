package org.ginsim.service.tool.modelsimplifier;

import java.util.List;

import org.colomoto.logicalmodel.NodeInfo;

public interface ReductionLauncher {

	boolean showPartialReduction(List<NodeInfo> l_todo);
}
