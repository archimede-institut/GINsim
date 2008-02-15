package fr.univmrs.tagc.common.mdd;

import java.util.Vector;


public class MDDLeaf extends MDDNode {

	public final int value;

    public MDDLeaf(int value, Long key) {
    	super(key);
    	this.value = value;
    }

    /**
     * not so nice way to write this tree in a "readable" form
     * @return a printed form of the tree
     */
    public String toString() {
            return ""+value;
    }

    public String getString(int ilevel, Vector names) {
    	return toString();
    }

	public MDDNode merge(DecisionDiagramInfo ddi, MDDVarNode other,	DecisionDiagramAction action) {
    	MDDNode[] newnext;

    	// perform the corresponding action, depending on the merge mode
        int doaction = action.t[LN];
        while (true) {
        	switch (doaction) {
	    		case THIS:
	    			return this;
	    		case OTHER:
	    			return other;
	    		case NOTTHIS:
	    			// TODO: not this
	    			break;
	    		case NOTOTHER:
	    			// TODO: not other
	    			break;
	    		case MULT:
	    		case ADD:
	    		case MIN:
	    		case MAX:
	    			// not allowed here!
	    			System.out.println("Debug: value-operation on Leaf+Node");
	    			return null;
	    		case ISEQ:
	    			// TODO: iseq
	    			break;
	    		case ISDIFF:
	    			// TODO: isdiff
	    			break;
	    		case CHILDTHIS:
	    		case CHILDBOTH:
	    			return null;
	    		case CHILDOTHER:
	    			newnext = new MDDNode[other.next.length];
	    			for (int i=0 ; i<newnext.length ; i++) {
	    				newnext[i] = merge(ddi, other.next[i], action);
	    			}
	    			return ddi.getNewNode(other.vinfo.order, newnext);
	    		case ASKME:
	    			doaction = action.ask(this, other, LN);
	    			continue;
	    		case CUSTOM:
	    			return action.custom(ddi, this, other, LN);
        	}
        	System.out.println("doMerge was not defined :/");
        	return null;
        }
	}

	public MDDNode merge(DecisionDiagramInfo ddi, MDDLeaf other, DecisionDiagramAction action) {
        // perform the corresponding action, depending on the merge mode
        int doaction = action.t[LL];
        while (true) {
        	switch (doaction) {
	    		case THIS:
	    			return this;
	    		case OTHER:
	    			return other;
	    		case NOTTHIS:
	    			// TODO: not this
	    			break;
	    		case NOTOTHER:
	    			// TODO: not other
	    			break;
	    		case MULT:
	    			return ddi.getNewNode(value*other.value, null);
	    		case ADD:
	    			return ddi.getNewNode(value+other.value, null);
	    		case MIN:
	    			return ddi.getNewNode(Math.min(value,other.value), null);
	    		case MAX:
	    			return ddi.getNewNode(Math.max(value,other.value), null);
	    		case ISEQ:
	    			// TODO: iseq
	    			break;
	    		case ISDIFF:
	    			// TODO: isdiff
	    			break;
	    		case CHILDTHIS:
	    		case CHILDOTHER:
	    		case CHILDBOTH:
	    			return null;
	    		case ASKME:
	    			doaction = action.ask(this, other, LL);
	    			continue;
	    		case CUSTOM:
	    			return action.custom(ddi, this, other, LL);
        	}
        	System.out.println("doMerge was not defined :/");
        	return null;
        }
	}

}
