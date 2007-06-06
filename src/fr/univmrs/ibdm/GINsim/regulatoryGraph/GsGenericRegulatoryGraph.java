package fr.univmrs.ibdm.GINsim.regulatoryGraph;

import java.util.Vector;

/**
 * a generic interface for regulatory graph and similar to implement.
 * It allows the simulation tool to use other kind of graph as source
 * with no additional work.
 * 
 * A "simulable" graph should provides two things: a node order and
 * logical functions for all nodes.
 */
public interface GsGenericRegulatoryGraph {

	public Vector getNodeOrderForSimulation();
	public OmddNode[] getParametersForSimulation(boolean focal);
}
