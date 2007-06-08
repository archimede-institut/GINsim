package fr.univmrs.ibdm.GINsim.graph;

import java.util.Vector;

/**
 * Event to inform of selection change events.
 * it contains the list of edges and vertices ACTUALLY in the selection
 */
public class GsGraphSelectionChangeEvent {
    
    private Vector v_edge;
    private Vector v_vertex;
    private int nbEdge;
    private int nbVertex;
    
    /**
     * @param v_edge
     * @param v_vertex
     * @param nbEdge
     * @param nbVertex
     */
    public GsGraphSelectionChangeEvent(Vector v_edge, Vector v_vertex,
            int nbEdge, int nbVertex) {
        super();
        this.v_edge = v_edge;
        this.v_vertex = v_vertex;
        this.nbEdge = nbEdge;
        this.nbVertex = nbVertex;
    }
    public GsGraphSelectionChangeEvent(Vector v_edge, Vector v_vertex) {
    	this(v_edge, v_vertex, v_edge.size(), v_vertex.size());
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
    public int getNbVertex() {
        return nbVertex;
    }
    /**
     * @return the list of edges in the new selection.
     */
    public Vector getV_edge() {
        return v_edge;
    }
    /**
     * @return the list of vertices in the new selection.
     */
    public Vector getV_vertex() {
        return v_vertex;
    }
}
