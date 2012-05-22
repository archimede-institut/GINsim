package org.ginsim.core.graph.reducedgraph;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.ginsim.common.utils.CollectionUtils;
import org.ginsim.common.utils.MaskUtils;
import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;



/**
 * this object represents nodes in the reduced graph : the graph of strong connected components
 */
public class NodeReducedData {
	
	//all vertices that are in the strongest connected component together 
	private List content;
	private String id;
	
	public static final int SCC_TYPE_UNIQUE_NODE = 0;
	public static final int SCC_TYPE_SIMPLE_CYCLE = 1;
	public static final int SCC_TYPE_COMPLEX_COMPONENT = 2;
	
	/**
	 * @param id Id of the new node.
	 * @param content list of vertices in this component.
	 */
	public NodeReducedData(String id, Vector content) {
		this.id = id;
		if (content == null) {
			this.content = new Vector(0);
		} else {
			this.content = content;
		}
	}
	
	/**
	 * @param id Id of the new node.
	 * @param set list of vertices in this component.
	 */
	public NodeReducedData(String id, Collection set) {
	    this.content = new Vector(set.size());
	    Iterator it = set.iterator();
	    while (it.hasNext()) {
	        content.add(it.next());
	    }
	    if (id == null) {
	        this.id = content.get(0).toString();
	    } else {
	        this.id = id;
	    }
	}
	
	/**
	 * @param id id of the node.
	 */
	public NodeReducedData(String id) {
		this(id, (Vector)null);
	}
	/**
	 * 
	 * @param id if of the node.
	 * @param s_content comma (,) separated list of vertices in this component.
	 */
	public NodeReducedData (String id, String s_content) {
		this.id = id;
		content = CollectionUtils.getVectorFromArray(s_content.split(","));
	}
	
	public String toString() {
		return id;
	}
	
	/**
	 * @return the content of the connected component.
	 */
	public List getContent() {
		return content;
	}

	/**
	 * @return the id of the connected component.
	 */
	public String getId() {
		return id;
	}

	public int hashCode() {
		return id.hashCode();
	}
	
	public boolean equals(Object obj) {
		if (!(obj instanceof NodeReducedData)) {
			return false;
		}
		return id.equals(((NodeReducedData)obj).id);
	}

	/**
	 * @return String
	 */
	public String getContentString() {
		if (content == null || content.size() == 0) {
			return "";
		}
		String ret = content.get(0).toString();
		for (int i=1 ; i<content.size() ; i++) {
			ret += ","+content.get(i);
		}
		return ret;
	}
	
	/**
	 * Indicates the type of component. Unique node, elementaty cycle or complex component.
	 * @return the type. (see constants)
	 */
	public int getType( Graph gm) {
		if (content.size() == 1) return SCC_TYPE_UNIQUE_NODE;
		for (Iterator it = content.iterator(); it.hasNext();) {
			Object currentNode = it.next();
			if (gm.getOutgoingEdges(currentNode).size() != 1) return SCC_TYPE_COMPLEX_COMPONENT;
		}
		return SCC_TYPE_SIMPLE_CYCLE;
	}
	
	public boolean isTransient( Graph gm) {
		
		for (Iterator it_nodes = content.iterator(); it_nodes.hasNext();) {
			Object currentNode = it_nodes.next();
			for (Edge edge: (Collection<Edge>)gm.getOutgoingEdges(currentNode)) {
				if (!content.contains(edge.getTarget())) return true; //There is a node that is not in the cycle
			}
		}
		return false;
	}

	public boolean isTrivial() {
		return content.size() == 1;
	}

	public String getTypeName( Graph gm) {
		
		switch (getType(gm)) {
			case SCC_TYPE_UNIQUE_NODE: 		return "STR_connectivity_unique_node"; 
			case SCC_TYPE_SIMPLE_CYCLE: 	return "STR_connectivity_simple_cycle";
			case SCC_TYPE_COMPLEX_COMPONENT:return "STR_connectivity_complex_component";
		}
		return null; //Useless, but eclipse want it.
	}
}
