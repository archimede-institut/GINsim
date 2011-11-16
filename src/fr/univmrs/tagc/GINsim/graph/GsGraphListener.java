package fr.univmrs.tagc.GINsim.graph;

import java.util.Collection;

import org.ginsim.graph.common.Edge;

/**
 * listen graph events
 */
public interface GsGraphListener<V,E extends Edge<V>> {

    /**
     * an edge was added to the graph.
     * @param data
     * @return an object to describe/undo cascade event
     */
	public GsGraphEventCascade edgeAdded(E data);
    /**
     * an edge was removed from the graph.
     * @param data
     * @return an object to describe/undo cascade event
     */
	public GsGraphEventCascade edgeRemoved(E data);
    /**
     * an edge was updated.
     * @param data
     * @return an object to describe/undo cascade event
     */
    public GsGraphEventCascade edgeUpdated(E data);

    /**
     * a vertex was added to the graph.
     * @param data
     * @return an object to describe/undo cascade event
     */
    public GsGraphEventCascade vertexAdded(V data);
    /**
     * a vertex was removed from the graph.
     * @param data
     * @return an object to describe/undo cascade event
     */
    public GsGraphEventCascade vertexRemoved(V data);
    /**
     * a vertex was updated.
     * @param data
     * @return an object to describe/undo cascade event
     */
    public GsGraphEventCascade vertexUpdated(V data);
    
    /**
     * the graph was merged with another one
     * @param nodes list of all merged in vertices
     * @return an object to describe/undo cascade event
     */
    public GsGraphEventCascade graphMerged(Collection<V> nodes);
    
	/** graph parsing is finished */
    public void endParsing();
}
