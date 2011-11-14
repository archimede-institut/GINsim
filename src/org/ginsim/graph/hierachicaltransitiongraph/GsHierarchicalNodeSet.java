package org.ginsim.graph.hierachicaltransitiongraph;

import java.util.HashSet;
import java.util.Iterator;


/**
 * 
 * <p>Define a set of nodes for the Hierarchical Transition Graph.<br>
 * It is used to have an HashSet of GsHierarchicalNode.</p>
 * 
 * <p>A common question is to know if a certain state has already been encountered, this question is answered using the function :</p>
 * <pre>		public GsDynamicalHierarchicalNode getHNodeForState(byte[] state)</pre>
 *
 *
 * <p><i>The java 1.5 definition should be GsHierarchicalNodeSet&lt;GsHierarchicalNode&gt;</i></p>
 */
public class GsHierarchicalNodeSet extends HashSet {
	private static final long serialVersionUID = -6542206623359579872L;
	
	private int hashCode = 0;
	public GsHierarchicalNodeSet() {
		super();
		buildHashCode(); //FIXME: Should no be here, because it is called before any elements are added
	}
	
	/**
	 * 
	 * <p>Iterate over all the GsDynamicalHierarchicalNode in the HashSet and test if it 
	 * contains <b>state</b> using the function contains(state) of GsHierarchicalNode</p>
	 * 
	 * <p>The complexity is equal to the count of GsHierarchicalNode times the function contains. That is O(|States|x|Genes|).</p>
	 * 
	 * 
	 * @param state The state to test
	 * @return The node containing the state or null if it is not found
	 */
	public GsHierarchicalNode getHNodeForState(byte[] state) {
		GsHierarchicalNode hnode = null;
		boolean found = false;
		for (Iterator it = iterator(); it.hasNext();) {
			hnode = (GsHierarchicalNode) it.next();
			if (hnode != null && hnode.contains(state)) {
				found = true;
				break;
			}
		}
		if (!found) return null;
		return hnode;
	}
	
	public int hashCode() {
		return hashCode;
	}

	/**
	 * Initialize the value of the hashCode from the content of the set. Basically its just the sum of the GsHierarchicalNode's uids.
	 */
	public void buildHashCode() {
		hashCode = 0;
		for (Iterator it = iterator(); it.hasNext();) {
			GsHierarchicalNode node = (GsHierarchicalNode) it.next();
			hashCode += node.getUniqueId();
		}
	}
	
	public String toString() {
		StringBuffer s = new StringBuffer();
		for (Iterator iterator = this.iterator(); iterator.hasNext();) {
			GsHierarchicalNode node = (GsHierarchicalNode) iterator.next();
			s.append(node+"{"+node.getSigma()+"};");
		}
		return s.toString();
	}

}
