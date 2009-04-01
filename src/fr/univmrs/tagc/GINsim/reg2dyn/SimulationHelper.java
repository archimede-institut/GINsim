package fr.univmrs.tagc.GINsim.reg2dyn;

import fr.univmrs.tagc.GINsim.graph.GsGraph;

public abstract class SimulationHelper {
	abstract boolean addNode(SimulationQueuedState item);
	abstract GsGraph endSimulation();
	abstract void setStable();
	abstract Object getNode();
	abstract void setNode(Object node);
}