package org.ginsim.graph.common;

import java.util.Collection;

import fr.univmrs.tagc.GINsim.graph.GraphEventCascade;

/**
 * listen graph events
 */
public interface GraphListener<V,E extends Edge<V>> {

    /**
     * an edge was added to the graph.
     * @param data
     * @return an object to describe/undo cascade event
     */
	public GraphEventCascade edgeAdded(E data);
    /**
     * an edge was removed from the graph.
     * @param data
     * @return an object to describe/undo cascade event
     */
	public GraphEventCascade edgeRemoved(E data);
    /**
     * an edge was updated.
     * @param data
     * @return an object to describe/undo cascade event
     */
    public GraphEventCascade edgeUpdated(E data);

    /**
     * a vertex was added to the graph.
     * @param data
     * @return an object to describe/undo cascade event
     */
    public GraphEventCascade vertexAdded(V data);
    /**
     * a vertex was removed from the graph.
     * @param data
     * @return an object to describe/undo cascade event
     */
    public GraphEventCascade vertexRemoved(V data);
    /**
     * a vertex was updated.
     * @param data
     * @return an object to describe/undo cascade event
     */
    public GraphEventCascade vertexUpdated(V data);
    
    /**
     * the graph was merged with another one
     * @param nodes list of all merged in vertices
     * @return an object to describe/undo cascade event
     */
    public GraphEventCascade graphMerged(Collection<V> nodes);
    
	/** graph parsing is finished */
    public void endParsing();
}
