package org.ginsim.core.graph.common;


/**
 * Base class for all edges: store the source and target nodes.
 * (Just a wrapper around GsDirectedEdge, waiting to be renamed...
 * 
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

    public V getSource() {
        return source;
    }

    public V getTarget() {
        return target;
    }

    @Override
	public String toToolTip() {
		return "";
	}}
