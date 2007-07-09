package fr.univmrs.tagc.mdd;

import java.util.Vector;


public class MDDVarNode extends MDDNode {

    /** info on the corresponding decision variable (if non-terminal)*/
    public final MDDVarInfo vinfo;

    /** all children of this node (null for terminal nodes) */
    public final MDDNode[] next;

    /** p, n: prev and next MDD used to store existing MDD in a balanced tree */
    public MDDVarNode p,n;


    public MDDVarNode(MDDVarInfo vinfo, MDDNode[] next, Long key) {
    	super(key);
    	this.vinfo = vinfo;
    	this.next = next;
    }

    /**
     * not so nice way to write this tree in a "readable" form
     * @return a printed form of the tree
     */
    public String toString() {
        String s = "(";
        for (int i=0 ; i<next.length ; i++) {
            s += "(N["+vinfo.order+"]="+i+" && "+next[i]+") ; ";
        }
        s = s.substring(0, s.length()-3)+")";
        return s;
    }

    public String getString(int ilevel, Vector names) {
        String prefix = "";
        for (int i=0 ; i<ilevel ; i++) {
            prefix += "  ";
        }
        String s = "";
        for (int i=0 ; i<next.length ; i++) {
            String s2 = next[i].getString(ilevel+1, names);
            if (s2 != null) {
                if (s2.equals("1") || s2.equals("-1")) {
                    s += prefix+names.get(vinfo.order)+"="+i+" ==> "+s2+"\n";
                } else {
                    s += prefix+names.get(vinfo.order)+"="+i+"\n"+s2;
                }
            }
        }
        return s;
    }

	public MDDNode merge(DecisionDiagramInfo ddi, MDDVarNode other,	DecisionDiagramAction action) {
    	MDDNode[] newnext;
    	int type;

    	// first find the relationship between the two MDD
    	if (vinfo.order == other.vinfo.order) {
        	type = NN;
        } else if (vinfo.order > other.vinfo.order) {
        	type = NNf;
        } else {
        	type = NNn;
        }

        // then perform the corresponding action, depending on the merge mode
        int doaction = action.t[type];
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
	    			return null;
	    		case ISEQ:
	    			// TODO: iseq
	    			break;
	    		case ISDIFF:
	    			// TODO: isdiff
	    			break;
	    		case CHILDTHIS:
	    			newnext = new MDDNode[next.length];
	    			for (int i=0 ; i<newnext.length ; i++) {
	    				newnext[i] = next[i].merge(ddi, other, action);
	    			}
	    			return ddi.getNewNode(vinfo.order, newnext);
	    		case CHILDOTHER:
	    			newnext = new MDDNode[other.next.length];
	    			for (int i=0 ; i<newnext.length ; i++) {
	    				newnext[i] = merge(ddi, other.next[i], action);
	    			}
	    			return ddi.getNewNode(other.vinfo.order, newnext);
	    		case CHILDBOTH:
	    			newnext = new MDDNode[next.length];
	    			for (int i=0 ; i<newnext.length ; i++) {
	    				newnext[i] = next[i].merge(ddi, other.next[i], action);
	    			}
	    			return ddi.getNewNode(vinfo.order, newnext);
	    		case ASKME:
	    			doaction = action.ask(this, other, type);
	    			continue;
	    		case CUSTOM:
	    			return action.custom(ddi, this, other, type);
        	}
        	System.out.println("doMerge was not defined :/");
        	return null;
        }
	}

	public MDDNode merge(DecisionDiagramInfo ddi, MDDLeaf other, DecisionDiagramAction action) {
    	MDDNode[] newnext;

        // perform the corresponding action, depending on the merge mode
        int doaction = action.t[NL];
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
	    			return null;
	    		case ISEQ:
	    			// TODO: iseq
	    			break;
	    		case ISDIFF:
	    			// TODO: isdiff
	    			break;
	    		case CHILDTHIS:
	    			newnext = new MDDNode[next.length];
	    			for (int i=0 ; i<newnext.length ; i++) {
	    				newnext[i] = next[i].merge(ddi, other, action);
	    			}
	    			return ddi.getNewNode(vinfo.order, newnext);
	    		case CHILDOTHER:
	    		case CHILDBOTH:
	    			return null;
	    		case ASKME:
	    			doaction = action.ask(this, other, NL);
	    			continue;
	    		case CUSTOM:
	    			return action.custom(ddi, this, other, NL);
        	}
        	System.out.println("doMerge was not defined :/");
        	return null;
        }
	}

}
