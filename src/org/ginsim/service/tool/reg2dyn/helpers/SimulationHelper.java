package org.ginsim.service.tool.reg2dyn.helpers;

import org.ginsim.common.application.GsException;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.service.tool.reg2dyn.SimulationQueuedState;


public abstract class SimulationHelper {
	public abstract boolean addNode(SimulationQueuedState item);
	public abstract Graph endSimulation();
	public abstract void setStable();
	public abstract Object getNode();
	public abstract void setNode(Object node);
	public abstract Graph getRegulatoryGraph() throws GsException;
	public abstract Graph getDynamicGraph();
}