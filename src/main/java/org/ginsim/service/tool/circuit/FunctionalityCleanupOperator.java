package org.ginsim.service.tool.circuit;

import org.colomoto.mddlib.MDDManager;
import org.colomoto.mddlib.NodeRelation;
import org.colomoto.mddlib.operators.AbstractOperator;

/**
 * Cleanup functionality contexts to remove constraint on the circuit members
 */
public class FunctionalityCleanupOperator extends AbstractOperator {

    static public final FunctionalityCleanupOperator CLEANUP = new FunctionalityCleanupOperator();

    private FunctionalityCleanupOperator() {

    }

    @Override
    public int combine(MDDManager ddmanager, int first, int other) {
        if (first == other) {
            return first;
        }

        if (first == 0 || other == 0) {
            return 0;
        }

        NodeRelation status = ddmanager.getRelation(first, other);
        return recurse(ddmanager, status, first, other);
    }
}
