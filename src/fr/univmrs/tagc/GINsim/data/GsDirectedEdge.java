package fr.univmrs.tagc.GINsim.data;

/**
 * As we need a userObject and fast access to source and target of edges, 
 * real edges must implement this interface to provide this functionnality.
 */
public interface GsDirectedEdge {
    /**
     * @return the object attached to the edge.
     */
    public Object getUserObject();

    /**
     * @param obj the new userObject
     */
    public void setUserObject(Object obj);

    /**
     * @return the source vertex.
     */
    public Object getSourceVertex();
    
    /**
     * @return the target vertex.
     */
    public Object getTargetVertex();
}
