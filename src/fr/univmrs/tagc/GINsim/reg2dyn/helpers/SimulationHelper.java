package fr.univmrs.tagc.GINsim.reg2dyn.helpers;

import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.reg2dyn.SimulationQueuedState;

public abstract class SimulationHelper {
	public abstract boolean addNode(SimulationQueuedState item);
	public abstract GsGraph endSimulation();
	public abstract void setStable();
	public abstract Object getNode();
	public abstract void setNode(Object node);
	public abstract GsGraph getRegulatoryGraph();
	public abstract GsGraph getDynamicGraph();
}