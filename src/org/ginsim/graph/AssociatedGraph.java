package org.ginsim.graph;

public interface AssociatedGraph<V, E extends Edge<V>> {

    //----------------------   ASSOCIATED GRAPH METHODS --------------------------------------------
	
    
    /**
     * Associate the given graph to the current one
     * 
     * @param associated_graph
     */
    public void setAssociatedGraph( Graph<V,E> associated_graph);
    
    
    /**
     * Given access to the graph that has been associated to the current graph
     * 
     * @return the graph associated with this one.
     */
    public Graph<V,E> getAssociatedGraph();

    
    public String getAssociatedGraphID();
    public void setAssociatedGraphID(String value);
}
