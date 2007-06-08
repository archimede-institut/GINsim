package fr.univmrs.ibdm.GINsim.graph;

import java.util.Iterator;
import java.util.Vector;

import fr.univmrs.ibdm.GINsim.data.GsDirectedEdge;

/**
 * browse selected edges: very usefull to save a subgraph, or copy/paste
 */
public class GsSelectedEdgeIterator implements Iterator {

	Vector v_edge;
	Vector v_vertex;
	int i;
	

	/**
	 * @param v_vertex
	 * @param v_edge
	 */
	public GsSelectedEdgeIterator(Vector v_vertex, Vector v_edge) {
		this.v_vertex = v_vertex;
		this.v_edge = v_edge;
		if ( v_vertex == null || v_edge == null) {
			i = -1;
		} else {
			i = 0;
			doSearchNext();
		}
	}

	private void doSearchNext() {
		if (i != -1) {
			for ( ; i<v_edge.size() ; i++) {
				GsDirectedEdge edge = (GsDirectedEdge)v_edge.get(i);
				if (v_vertex.contains(edge.getSourceVertex()) && v_vertex.contains(edge.getTargetVertex())) {
					return;
				}
			}
		}
		i = -1;
	}
	
	public boolean hasNext() {
		return (i != -1 && i<v_edge.size());
	}

	public Object next() {
		if (i != -1) {
			int index = i++;
			doSearchNext();
			return v_edge.get(index);
		}
		return null;
	}
	/**
	 * remove not supported on this iterator
	 */
	public void remove() {
	}

}
