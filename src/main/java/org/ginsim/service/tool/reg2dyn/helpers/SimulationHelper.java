package org.ginsim.service.tool.reg2dyn.helpers;

import org.ginsim.common.application.GsException;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.service.tool.reg2dyn.SimulationQueuedState;


public interface SimulationHelper {
	
	boolean addNode(SimulationQueuedState item);
	Graph endSimulation();
	void setStable();
	Object getNode();
	void setNode(Object node);
	Graph getDynamicGraph();
}