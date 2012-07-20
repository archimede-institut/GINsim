package org.ginsim.gui.graph.regulatorygraph.models;

import javax.swing.AbstractListModel;

import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;


/**
 * model for the table displaying all directed edges of a multiedge
 */
public class DirectedEdgeListModel extends AbstractListModel {

	private static final long serialVersionUID = 1155066949151354432L;
	private RegulatoryMultiEdge edge = null;
	
	public int getSize() {
	    if (edge == null) {
	        return 0;
	    }
		return edge.getEdgeCount();
	}
	
	public Object getElementAt(int i) {
	    if (edge == null || i >= edge.getEdgeCount()) {
	        return null;
	    }
	    if (i<10) {
	    		return " "+i+" "+edge.getEdgeName(i);
	    }
	    return i+" "+edge.getEdgeName(i);
	}

    /**
     * @param edge
     */
    public void setEdge(RegulatoryMultiEdge edge) {
        this.edge = edge;
        fireContentsChanged(this, 0, edge.getEdgeCount());
    }
    /**
     * update the list
     */
    public void update() {
        fireContentsChanged(this, 0, edge.getEdgeCount());
    }
}
