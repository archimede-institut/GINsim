package fr.univmrs.tagc.GINsim.hierachicalTransitionGraph;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GsHierarchicalSigmaSet {

	/**
	 * The parent node
	 */
	private GsHierarchicalSigmaSet parent;
	
	/**
	 * Map&lt;GsHierarchicalNode, GsHierarchicalSigmaSet&gt;
	 */
	private Map children;
	
	private GsHierarchicalNode label;
	
	private GsHierarchicalNode unrecoverable;
	
 	public GsHierarchicalSigmaSet(GsHierarchicalNode label, GsHierarchicalSigmaSet parent) {
 		this.label = label;
 		this.parent = parent;
 		this.children = null;
 		this.unrecoverable = null;
 	}
 	
 	public GsHierarchicalSigmaSet getChild(GsHierarchicalNode label) {
 		if (children == null) return null;
 		return (GsHierarchicalSigmaSet) children.get(label);
 	}
 	
 	public GsHierarchicalSigmaSet addChild(GsHierarchicalNode label) {
 		if (children == null) {
 			children = new HashMap();
 		}
 		GsHierarchicalSigmaSet child = (GsHierarchicalSigmaSet) children.get(label);
 		if (child == null) {
 			child = new GsHierarchicalSigmaSet(label, this);
 			this.children.put(label, child);
 		}
 		return child;
 	}

	public boolean hasChildren() {
		return children != null;
	}

	/**
	 * @param unrecoverable the unrecoverable component associated to this SigmaSet to set
	 * @param shouldCompactSCC
	 * @param nodeSet 
	 * @param htg 
	 */
	public void setUnrecoverable(GsHierarchicalNode unrecoverable, Collection nodeSet, GsHierarchicalSigmaSetFactory sigmaSetFactory, GsHierarchicalTransitionGraph htg) {
		if (this.unrecoverable != null) {
			this.unrecoverable.merge(unrecoverable, nodeSet, sigmaSetFactory, htg);
			this.unrecoverable.statesSet.reduce();

		} else {
			this.unrecoverable = unrecoverable;			
		}
	}

	/**
	 * @return the unrecoverable component associated to this SigmaSet
	 */
	public GsHierarchicalNode getUnrecoverable() {
		return unrecoverable;
	}
	
	public List getSigmaImage() {
		LinkedList path = new LinkedList();
		if (this.parent == null) {
			System.out.println(this+" appel sur la root ?");
			return path;
		}		
		GsHierarchicalSigmaSet current = this;
		do {
			path.addFirst(current.label);
			current = current.parent;
		} while (current.parent != null);
		return path;
	}

	/**
	 * Indicates if the SigmaSet is a signelton (contains only one element), that is, if this GsHierarchicalSigmaSet's parent is not the root of the factory, ie. it has a parent
	 * The root is always empty.
	 * @return
	 */
	public boolean isSingleton() {
		return parent == null || parent.parent == null;
	}
	
	public String pathToString() {
		StringBuffer sb = new StringBuffer();
		GsHierarchicalSigmaSet current = this;
		if (current.parent == null) {
			sb.append("<root>");
			return sb.toString();
		}
		do {
			sb.append(current+",");
			current = current.parent;
		} while (current.parent != null);
		return sb.toString();
	}
	public String toString() {
		return "<"+label+","+unrecoverable+">";
	}
	
}
