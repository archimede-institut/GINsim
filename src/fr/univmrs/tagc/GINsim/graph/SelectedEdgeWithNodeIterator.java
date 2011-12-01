package fr.univmrs.tagc.GINsim.graph;

import java.util.Iterator;
import java.util.List;

import org.ginsim.graph.common.Edge;

/**
 * browse selected edges: very useful to save a subgraph, or copy/paste
 */
public class SelectedEdgeWithNodeIterator implements Iterator {

	List v_edge;
	List v_vertex;
	int i;
	

	/**
	 * @param v_vertex
	 * @param v_edge
	 */
	public SelectedEdgeWithNodeIterator(List v_vertex, List v_edge) {
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
				Edge edge = (Edge)v_edge.get(i);
				if (v_vertex.contains(edge.getSource()) && v_vertex.contains(edge.getTarget())) {
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