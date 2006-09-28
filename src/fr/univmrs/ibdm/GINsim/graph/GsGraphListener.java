package fr.univmrs.ibdm.GINsim.graph;

/**
 * listen graph events
 */
public interface GsGraphListener {

    /**
     * an adge was added to the graph.
     */
	public void edgeAdded();
    /**
     * an adge was removed from the graph.
     */
	public void edgeRemoved();
    /**
     * a vertex was added to the graph.
     */
	public void vertexAdded();
    /**
     * a vertex was removed from the graph.
     */
	public void vertexRemoved();
}
