package org.ginsim.graph.hierachicaltransitiongraph;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import fr.univmrs.tagc.common.Debugger;

public class HierarchicalSigmaSet {

	/**
	 * The parent node
	 */
	private HierarchicalSigmaSet parent;
	
	/**
	 * Map&lt;HierarchicalNode, HierarchicalSigmaSet&gt;
	 */
	private Map children;
	
	private HierarchicalNode label;
	
	private HierarchicalNode unrecoverable;
	
 	public HierarchicalSigmaSet(HierarchicalNode label, HierarchicalSigmaSet parent) {
 		this.label = label;
 		this.parent = parent;
 		this.children = null;
 		this.unrecoverable = null;
 	}
 	
 	public HierarchicalSigmaSet getChild(HierarchicalNode label) {
 		if (children == null) return null;
 		return (HierarchicalSigmaSet) children.get(label);
 	}
 	
 	public HierarchicalSigmaSet addChild(HierarchicalNode label) {
 		if (children == null) {
 			children = new HashMap();
 		}
 		HierarchicalSigmaSet child = (HierarchicalSigmaSet) children.get(label);
 		if (child == null) {
 			child = new HierarchicalSigmaSet(label, this);
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
	public void setUnrecoverable(HierarchicalNode unrecoverable, Collection nodeSet, HierarchicalSigmaSetFactory sigmaSetFactory, HierarchicalTransitionGraph htg) {
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
	public HierarchicalNode getUnrecoverable() {
		return unrecoverable;
	}
	
	public List getSigmaImage() {
		LinkedList path = new LinkedList();
		if (this.parent == null) {
			Debugger.error( this + " appel sur la root ?");
			return path;
		}		
		HierarchicalSigmaSet current = this;
		do {
			path.addFirst(current.label);
			current = current.parent;
		} while (current.parent != null);
		return path;
	}

	/**
	 * Indicates if the SigmaSet is a signelton (contains only one element), that is, if this HierarchicalSigmaSet's parent is not the root of the factory, ie. it has a parent
	 * The root is always empty.
	 * @return
	 */
	public boolean isSingleton() {
		return parent == null || parent.parent == null;
	}
	
	public String pathToString() {
		StringBuffer sb = new StringBuffer();
		HierarchicalSigmaSet current = this;
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