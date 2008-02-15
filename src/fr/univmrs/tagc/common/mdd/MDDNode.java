package fr.univmrs.tagc.common.mdd;

import java.util.Vector;


/**
 * common parts for all decision diagrams
 * TODO: add order choose/apply
 */
public abstract class MDDNode {

	/**
	 * supported action while merging.
	 *
	 * Please note that
	 * <ul>
	 *  <li> MULT, ADD, MIN and MAX only apply to leaves.</li>
	 *  <li>CHILDTHIS, CHILDOTHER and CHILDBOTH require that the corresponding
	 * 		node is NOT a leave. </li>
	 *  <li>ASKME will call the <code>action.ask</code> method to determine
	 *  	which operation will be performed</li>
	 *  <li>CUSTOM will call the <code>action.custom</code> function to perform
	 *  	the operation</li>
	 * </ul>
	 *
	 */
	public static final int THIS=0, OTHER=1, NOTTHIS=2, NOTOTHER=3,
		MULT=10, ADD=11, MIN=12, MAX=13, ISEQ=14, ISDIFF=15,
		CHILDTHIS=20, CHILDOTHER=21, CHILDBOTH=22,
		ASKME=30, CUSTOM=31;

	/**
	 * possible status of the two merged nodes:
	 * FF = 2 leaves;
	 * LN = Leave + Node;
	 * NNn = 2 nodes, the second comes next;
	 * NNp = 2 nodes, the second comes first;
	 */
	public static final int LL=0, LN=1, NL=2, NN=3, NNn=4, NNf=5;

	/**
	 * the number of possible status, to help building Actions.
	 */
	public static final int NBSTATUS = 6;

    /** identifier of this node */
    final Long key;

    public MDDNode(Long key) {
    	this.key = key;
    }

    /**
     * merge two trees into a (new?) one.
     * this will create new nodes, as existing one might be merged with different part, they can't be reused.
     *
     * @param other
     * @param op boolean operation to apply (available: AND, OR)
     *
     * @return the merged tree
     */
    public MDDNode merge(DecisionDiagramInfo ddi, MDDNode other, DecisionDiagramAction action) {
    	if (other instanceof MDDLeaf) {
    		return merge(ddi, (MDDLeaf)other, action);
    	}
    	return merge(ddi, (MDDVarNode)other, action);
    }

    abstract public MDDNode merge(DecisionDiagramInfo ddi, MDDVarNode other, DecisionDiagramAction action);
    abstract public MDDNode merge(DecisionDiagramInfo ddi, MDDLeaf other, DecisionDiagramAction action);


    /**
     * "pretty printing" of the MDD, using node names instead of orders,
     * showing the result on several lines, using indentation to highlight relationships
     * @param ilevel the indentation level
     * @param names vector of node name
     * @return a printed form of the tree
     */
    public abstract String getString(int ilevel, Vector names);

}
