package fr.univmrs.ibdm.GINsim.regulatoryGraph;

import fr.univmrs.ibdm.GINsim.data.GsDirectedEdge;

/**
 * correctly restoring edges and interaction when parsing a regulatory ginml file 
 * isn't a trivial task.
 * to make it more simple, the regulatory graph should return both an edge index 
 * and a GsDirectedEdge when adding an GsRegulatoryEdge.
 * 
 * this the only point of this class, if you are not a parser you shouldn't need to use it :)
 */
public class GsRegulatoryEdgeInfo {

    /** edge (for the graphmanager), use it to restore visual settings  */
    public final GsDirectedEdge dedge;
    /** edgeIndex: use it to restore correct interactions */
    public final GsEdgeIndex edgeIndex;
    
    /**
     * @param dedge
     * @param edgeIndex
     */
    public GsRegulatoryEdgeInfo (GsDirectedEdge dedge, GsEdgeIndex edgeIndex) {
        this.dedge = dedge;
        this.edgeIndex = edgeIndex;
    }
    
}
