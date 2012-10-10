package org.ginsim.service.tool.circuit;

import org.colomoto.mddlib.MDDManager;
import org.colomoto.mddlib.MDDVariable;
import org.colomoto.mddlib.NodeRelation;
import org.colomoto.mddlib.operators.AbstractFlexibleOperator;
import org.colomoto.mddlib.operators.AbstractOperator;

/**
 * This operation combines functionality contexts.
 * It is a glorified AND or more accurately a multiplication.
 * Functionality contexts have three possible leaves: 0, 1, and -1 denoting
 * the lack of effect, positive effects and negative effects respectively.
 * 
 * <p>As MDDs can not have negative values, -1 will be represented by leaves of value 2.
 * <ul>
 * <li>Combining 0 with any node gives 0</li>
 * <li>Combining 1 with any node gives this node</li>
 * <li>Combining -1 with -1 (represented by 2 with 2) gives 1</li>
 * </ul>
 * 
 * @author Aurelien Naldi
 */
public class ContextMergeOperator extends AbstractOperator {

	protected ContextMergeOperator() {
		super(false); // TODO: multiple merge
	}

	@Override
	public int combine(MDDManager ddmanager, int first, int other) {
		NodeRelation status = ddmanager.getRelation(first, other);

		switch (status) {
		case LL:
			if (first == 0 || other == 0) {
				return 0;
			}
			
			if (first == 1) {
				return other;
			}
			
			if (other == 1) {
				return first;
			}
			
			// bother are "2" which stands for "-1", thus the merge gives 1
			return 1;
		case LN:
			if (first ==0) {
				return 0;
			}
			if (first == 1) {
				return other;
			}

			return recurse(ddmanager, status, first, other);
		case NL:
			if (other ==0) {
				return 0;
			}
			if (other == 1) {
				return first;
			}

			return recurse(ddmanager, status, first, other);
		default:
			return recurse(ddmanager, status, first, other);
		}
	}

	// TODO: handle multiple merging
}
