package fr.univmrs.ibdm.GINsim.graph;

/**
 * listen graph events
 */
public interface GsGraphListener {

    /**
     * an adge was added to the graph.
     * @param data
     * @return an object to describe/undo cascade event
     */
	public GsGraphEventCascade edgeAdded(Object data);
    /**
     * an adge was removed from the graph.
     * @param data
     * @return an object to describe/undo cascade event
     */
	public GsGraphEventCascade edgeRemoved(Object data);
    /**
     * a vertex was added to the graph.
     * @param data
     * @return an object to describe/undo cascade event
     */
    public GsGraphEventCascade vertexAdded(Object data);
    /**
     * a vertex was removed from the graph.
     * @param data
     * @return an object to describe/undo cascade event
     */
    public GsGraphEventCascade vertexRemoved(Object data);
    /**
     * a vertex was updated.
     * @param data
     * @return an object to describe/undo cascade event
     */
    public GsGraphEventCascade vertexUpdated(Object data);
    /**
     * the graph was merged with another one
     * @param data vector of all merged in vertices
     * @return an object to describe/undo cascade event
     */
    public GsGraphEventCascade graphMerged(Object data);
    /**
     * an edge was updated.
     * @param data
     * @return an object to describe/undo cascade event
     */
    public GsGraphEventCascade edgeUpdated(Object data);
}
