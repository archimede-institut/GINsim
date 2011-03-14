package fr.univmrs.tagc.GINsim.hierachicalTransitionGraph;

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
 * <p><i>The java 1.5 definition should be GsHierarchicalNodeSet&lt;GsDynamicalHierarchicalNode&gt;</i></p>
 */
public class GsHierarchicalNodeSet extends HashSet {
	private static final long serialVersionUID = -6542206623359579872L;

	private int hashCode;
	public GsHierarchicalNodeSet() {
		super();
		buildHashCode();
	}
	
	/**
	 * 
	 * <p>Iterate over all the GsDynamicalHierarchicalNode in the HashSet and test if it 
	 * contains <b>state</b> using the function GsDynamicalHierarchicalNode.contains(state)</p>
	 * 
	 * <p>The complexity is equal to the count of GsDynamicalHierarchicalNode times the function contains.</p>
	 * 
	 * 
	 * @param state
	 * @return the node containg the state or null if it is not found
	 */
	public GsHierarchicalNode getHNodeForState(byte[] state) {
		GsHierarchicalNode HNode = null;
		boolean found = false;
		for (Iterator it = iterator(); it.hasNext();) {
			HNode = (GsHierarchicalNode) it.next();
			if (HNode != null && HNode.contains(state)) {
				found = true;
				break;
			}
		}
		if (!found) return null;
		return HNode;
	}
	
	public int hashCode() {
		return hashCode;
	}

	public void buildHashCode() {
		hashCode = 0;
		for (Iterator it = iterator(); it.hasNext();) {
			GsHierarchicalNode node = (GsHierarchicalNode) it.next();
			hashCode += node.getUniqueId();
		}
	}

}
