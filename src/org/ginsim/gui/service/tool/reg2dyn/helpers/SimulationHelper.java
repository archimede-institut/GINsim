package org.ginsim.gui.service.tool.reg2dyn.helpers;

import org.ginsim.exception.GsException;
import org.ginsim.graph.common.Graph;
import org.ginsim.gui.service.tool.reg2dyn.SimulationQueuedState;


public abstract class SimulationHelper {
	public abstract boolean addNode(SimulationQueuedState item);
	public abstract Graph endSimulation();
	public abstract void setStable();
	public abstract Object getNode();
	public abstract void setNode(Object node);
	public abstract Graph getRegulatoryGraph() throws GsException;
	public abstract Graph getDynamicGraph();
}