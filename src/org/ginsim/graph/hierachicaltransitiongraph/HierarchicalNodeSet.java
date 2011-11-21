package org.ginsim.graph.hierachicaltransitiongraph;

import java.util.HashSet;
import java.util.Iterator;


/**
 * 
 * <p>Define a set of nodes for the Hierarchical Transition Graph.<br>
 * It is used to have an HashSet of HierarchicalNode.</p>
 * 
 * <p>A common question is to know if a certain state has already been encountered, this question is answered using the function :</p>
 * <pre>		public DynamicalHierarchicalNode getHNodeForState(byte[] state)</pre>
 *
 *
 * <p><i>The java 1.5 definition should be HierarchicalNodeSet&lt;HierarchicalNode&gt;</i></p>
 */
public class HierarchicalNodeSet extends HashSet {
	private static final long serialVersionUID = -6542206623359579872L;
	
	private int hashCode = 0;
	public HierarchicalNodeSet() {
		super();
		buildHashCode(); //FIXME: Should no be here, because it is called before any elements are added
	}
	
	/**
	 * 
	 * <p>Iterate over all the DynamicalHierarchicalNode in the HashSet and test if it 
	 * contains <b>state</b> using the function contains(state) of HierarchicalNode</p>
	 * 
	 * <p>The complexity is equal to the count of HierarchicalNode times the function contains. That is O(|States|x|Genes|).</p>
	 * 
	 * 
	 * @param state The state to test
	 * @return The node containing the state or null if it is not found
	 */
	public HierarchicalNode getHNodeForState(byte[] state) {
		HierarchicalNode hnode = null;
		boolean found = false;
		for (Iterator it = iterator(); it.hasNext();) {
			hnode = (HierarchicalNode) it.next();
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
	 * Initialize the value of the hashCode from the content of the set. Basically its just the sum of the HierarchicalNode's uids.
	 */
	public void buildHashCode() {
		hashCode = 0;
		for (Iterator it = iterator(); it.hasNext();) {
			HierarchicalNode node = (HierarchicalNode) it.next();
			hashCode += node.getUniqueId();
		}
	}
	
	public String toString() {
		StringBuffer s = new StringBuffer();
		for (Iterator iterator = this.iterator(); iterator.hasNext();) {
			HierarchicalNode node = (HierarchicalNode) iterator.next();
			s.append(node+"{"+node.getSigma()+"};");
		}
		return s.toString();
	}

}
