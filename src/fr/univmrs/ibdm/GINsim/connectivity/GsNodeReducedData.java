package fr.univmrs.ibdm.GINsim.connectivity;

import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import fr.univmrs.tagc.global.Tools;

/**
 * this object represents nodes in the reduced graph : the graph of strong connected components
 */
public class GsNodeReducedData {
	
	//all vertices that are in the strongest connected component together 
	private Vector content;
	private String id;
	
	/**
	 * @param id Id of the new node.
	 * @param content list of vertices in this component.
	 */
	public GsNodeReducedData(String id, Vector content) {
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
	public GsNodeReducedData(String id, Set set) {
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
	public GsNodeReducedData(String id) {
		this(id, (Vector)null);
	}
	/**
	 * 
	 * @param id if of the node.
	 * @param s_content comma (,) separated list of vertices in this component.
	 */
	public GsNodeReducedData (String id, String s_content) {
		this.id = id;
		content = Tools.getVectorFromArray(s_content.split(","));
	}
	
	public String toString() {
		return id;
	}
	
	/**
	 * @return the content of the connected component.
	 */
	public Vector getContent() {
		return content;
	}

	public int hashCode() {
		return id.hashCode();
	}
	
	public boolean equals(Object obj) {
		if (!(obj instanceof GsNodeReducedData)) {
			return false;
		}
		return id.equals(((GsNodeReducedData)obj).id);
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
}
