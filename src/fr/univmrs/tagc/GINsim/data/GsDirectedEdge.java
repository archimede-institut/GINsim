package fr.univmrs.tagc.GINsim.data;


/**
 * As we need a userObject and fast access to source and target of edges, 
 * real edges must implement this interface to provide this functionality.
 */
public class GsDirectedEdge<V> implements ToolTipsable {
    protected final V source, target;

	/**
	 * create a directedEdge.
	 *
	 * @param source the source vertex.
	 * @param target the target vertex.
	 * @param obj data to attach to th edge.
	 */
    public GsDirectedEdge(V source, V target) {
    	this.source = source;
    	this.target = target;
    }

    public V getSource() {
        return source;
    }

    public V getTarget() {
        return target;
    }

	public String toToolTip() {
		return "";
	}
}
