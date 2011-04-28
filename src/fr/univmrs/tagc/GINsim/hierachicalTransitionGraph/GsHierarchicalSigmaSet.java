package fr.univmrs.tagc.GINsim.hierachicalTransitionGraph;



/**
 * This class is used to store a nodeSet representing a sigma, along with a reference to the unrecoverable node (if any) that has this sigma. As two unrecoverable nodes can't have the same sigma, when two
 * @author duncan
 *
 */
public class GsHierarchicalSigmaSet extends GsHierarchicalNodeSet {
	private static final long serialVersionUID = 1463744512416655213L;

	/**
	 * The unrecoverable hierarchical node referenced by the sigma.
	 */
	private GsHierarchicalNode ref = null;

	public GsHierarchicalNode getRefNode() {
		return ref;
	}
	/**
	 * Set the refNode to a unrecoverable node, and merge with the old one if needed
	 * @param new_hnode
	 * @param nodeSet
	 */
	public void setRefNode(GsHierarchicalNode new_hnode, GsHierarchicalNodeSet nodeSet) {
		if (ref == null) {
			ref = new_hnode;
		} else if (!ref.equals(new_hnode)) { //Then there is two disconnected unrecoverable nodes with the same sigma
			new_hnode.merge(ref, nodeSet);
		}	
	}

}
