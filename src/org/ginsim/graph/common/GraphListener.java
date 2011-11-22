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
     * a node was added to the graph.
     * @param data
     * @return an object to describe/undo cascade event
     */
    public GraphEventCascade nodeAdded(V data);
    /**
     * a node was removed from the graph.
     * @param data
     * @return an object to describe/undo cascade event
     */
    public GraphEventCascade nodeRemoved(V data);
    /**
     * a node was updated.
     * @param data
     * @return an object to describe/undo cascade event
     */
    public GraphEventCascade nodeUpdated(V data);
    
    /**
     * the graph was merged with another one
     * @param nodes list of all merged in vertices
     * @return an object to describe/undo cascade event
     */
    public GraphEventCascade graphMerged(Collection<V> nodes);
    
	/** graph parsing is finished */
    public void endParsing();
}
