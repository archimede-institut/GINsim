package fr.univmrs.tagc.GINsim.graph;

import java.util.Iterator;
import java.util.Vector;

/**
 * browse selected vertices: very usefull to save a subgraph, or copy/paste
 */
public class GsSelectedVertexIterator implements Iterator {

	Vector v_vertex;
	int i;
	
	/**
	 * @param v_vertex
	 */
	public GsSelectedVertexIterator(Vector v_vertex) {
		this.v_vertex = v_vertex;
	}

	public boolean hasNext() {
		return v_vertex != null && i < v_vertex.size();
	}

	public Object next() {
		if (hasNext()) {
			return v_vertex.get(i++);
		}
		return null;
	}

	/**
	 * remove not supported on this iterator
	 */
	public void remove() {
	}

}
