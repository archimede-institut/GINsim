package fr.univmrs.ibdm.GINsim.graph;

import fr.univmrs.ibdm.GINsim.gui.GsMainFrame;

/**
 * describes a kind of graphManager
 */
abstract public class GsGraphManagerDescriptor {

	
    /**
     * @return the short name of this kind of graph.
     */
    abstract public String getGraphManagerName();
    
    /**
     * @param graph
     * @param m
     * @return a new graph.
     */
    abstract public GsGraphManager getNew(GsGraph graph, GsMainFrame m);

}
