package org.ginsim.graph.common;


/**
 * Base class for all edges: store the source and target nodes.
 * (Just a wrapper around GsDirectedEdge, waiting to be renamed...
 * 
 * @author Aurelien Naldi
 *
 * @param <V> the vertex type
 */
public class Edge<V> implements ToolTipsable {

    protected final V source, target;

	/**
	 * create a directedEdge.
	 *
	 * @param source the source vertex.
	 * @param target the target vertex.
	 * @param obj data to attach to th edge.
	 */
    public Edge(V source, V target) {
    	this.source = source;
    	this.target = target;
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
