package org.ginsim.core.graph.common;

import org.ginsim.common.application.GsException;

/**
 * Defines a graph associated with the implementor of this interface.
 * This interface should be implemented by all objects which depend on a separate Graph for interpretation.
 * This association will ensure that this associated graph can be retrieved properly.
 * 
 * @author Lionel Spinelli
 * @author Aurelien Naldi
 *
 * @param <AG>
 * @param <AV>
 * @param <AE>
 */
public interface GraphAssociation<AG extends Graph<AV, AE>, AV, AE extends Edge<AV>> {

    //----------------------   ASSOCIATED GRAPH METHODS --------------------------------------------
	
    
    /**  
     * Associate the given graph to the current one
     * 
     * @param associated_graph
     */
    public void setAssociatedGraph( AG associated_graph);
    
    
    /**
     * Given access to the graph that has been associated to the current graph
     * 
     * @return the graph associated with this one.
     */
    public AG getAssociatedGraph() throws GsException;

    
    public String getAssociatedGraphID() throws GsException;
    
    public void setAssociatedGraphID(String value);
}
