package org.ginsim.service.tool.circuit;

import org.colomoto.mddlib.MDDManager;
import org.colomoto.mddlib.NodeRelation;
import org.colomoto.mddlib.operators.AbstractOperator;

/**
 * Merge functionality contexts.
 * This operator is similar to a AND, but handles "negative" leaves (with value 2) in a special way.
 */
public class FunctionalityMergeOperator extends AbstractOperator {

    static public final FunctionalityMergeOperator MERGE = new FunctionalityMergeOperator();

    private FunctionalityMergeOperator() {

    }

    @Override
    public int combine(MDDManager ddmanager, int first, int other) {
        NodeRelation status = ddmanager.getRelation(first, other);
        switch (status) {
            case LL:
                if (first == 0 || other == 0) {
                    return 0;
                }
                if (first == other) {
                    return 1;
                }
                return 2;

            case LN:
                if (first == 0) {
                    return 0;
                }
                if (first == 1) {
                    return other;
                }
                break;

            case NL:
                if (other == 0) {
                    return 0;
                }
                if (other == 1) {
                    return first;
                }
                break;
        }
        return recurse(ddmanager, status, first, other);
    }
}
