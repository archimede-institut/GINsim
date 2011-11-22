package fr.univmrs.tagc.GINsim.graph;

import java.util.List;

/**
 * Event to inform of selection change events.
 * it contains the list of edges and vertices ACTUALLY in the selection
 */
public class GraphSelectionChangeEvent {
    
    private List v_edge;
    private List v_vertex;
    private int nbEdge;
    private int nbNode;
    
    /**
     * @param v_edge
     * @param v_vertex
     */
    public GraphSelectionChangeEvent(List v_edge, List v_vertex) {
        super();
        this.v_edge = v_edge;
        this.v_vertex = v_vertex;
        this.nbEdge = v_edge.size();
        this.nbNode = v_vertex.size();
    }
    /**
     * @return the number of edges in the new selected.
     */
    public int getNbEdge() {
        return nbEdge;
    }
    /**
     * @return the number of vertices in the new selected.
     */
    public int getNbNode() {
        return nbNode;
    }
    /**
     * @return the list of edges in the new selection.
     */
    public List getV_edge() {
        return v_edge;
    }
    /**
     * @return the list of vertices in the new selection.
     */
    public List getV_vertex() {
        return v_vertex;
    }
}
