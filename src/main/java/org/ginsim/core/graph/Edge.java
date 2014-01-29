package org.ginsim.core.graph;

import org.ginsim.common.utils.ToolTipsable;

/**
 * Base class for all edges: store the source and target nodes.
 * 
 * @author Lionel Spinelli
 * @author Aurelien Naldi
 *
 * @param <V> the node type
 */
public class Edge<V> implements ToolTipsable {

    protected final V source, target;

	/**
	 * create a edge.
	 *
	 * @param source the source node.
	 * @param target the target node.
	 */
    public Edge(Graph<V, ?> g, V source, V target) {
    	this.source = g == null ? source : g.getExistingNode(source);
    	this.target = g == null ? target : g.getExistingNode(target);
    }

    /**
     * Get the source of this edge.
     * 
     * @return the source node
     */
    public V getSource() {
        return source;
    }

    /**
     * Get the target of this edge.
     * 
     * @return the target node.
     */
    public V getTarget() {
        return target;
    }

    @Override
	public String toToolTip() {
		return "";
	}
}
