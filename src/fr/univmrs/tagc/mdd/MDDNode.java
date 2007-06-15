package fr.univmrs.tagc.mdd;

import java.util.Vector;

/**
 * common parts for all decision diagrams
 * TODO: add order choose/apply
 */
public class MDDNode {

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

    /** level (if non-terminal) or value (otherwise) of this node */
    public final int level;
    /** all children of this node (null for terminal nodes) */
    public final MDDNode[] next;

    /** p, n: prev and next MDD used to store existing MDD in a balanced tree */
    public MDDNode p,n;

    /** identifier of this node */
    final Long key;

    public MDDNode(int level, MDDNode[] next, Long key) {
    	this.level = level;
    	this.next = next;
    	this.key = key;
    }

    public MDDNode(int value, Long key) {
    	this.level = value;
    	this.next = null;
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
    	MDDNode[] newnext;
    	int type;

    	// first find the relationship between the two MDD
        if (next == null) {
        	if(other.next == null) {
        		type = LL;
        	} else {
            	type=LN;
        	}
        } else if (other.next == null) {
        	type = NL;
        } else if (level == other.level) {
        	type = NN;
        } else if (level > other.level) {
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
	    			return ddi.getNewNode(level*other.level, null);
	    		case ADD:
	    			return ddi.getNewNode(level+other.level, null);
	    		case MIN:
	    			return ddi.getNewNode(Math.min(level,other.level), null);
	    		case MAX:
	    			return ddi.getNewNode(Math.max(level,other.level), null);
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
	    			return ddi.getNewNode(level, newnext);
	    		case CHILDOTHER:
	    			newnext = new MDDNode[next.length];
	    			for (int i=0 ; i<newnext.length ; i++) {
	    				newnext[i] = merge(ddi, other.next[i], action);
	    			}
	    			return ddi.getNewNode(other.level, newnext);
	    		case CHILDBOTH:
	    			newnext = new MDDNode[next.length];
	    			for (int i=0 ; i<newnext.length ; i++) {
	    				newnext[i] = next[i].merge(ddi, other.next[i], action);
	    			}
	    			return ddi.getNewNode(level, newnext);
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

    /**
     * not so nice way to write this tree in a "readable" form
     * @return a printed form of the tree
     */
    public String toString() {
        if (this.next == null) {
            return ""+level;
        }

        String s = "(";
        for (int i=0 ; i<next.length ; i++) {
            s += "(N["+level+"]="+i+" && "+next[i]+") ; ";
        }
        s = s.substring(0, s.length()-3)+")";
        return s;
    }

    /**
     * "pretty printing" of the MDD, using node names instead of orders,
     * showing the result on several lines, using indentation to highlight relationships
     * @param ilevel the indentation level
     * @param names vector of node name
     * @return a printed form of the tree
     */
    public String getString(int ilevel, Vector names) {
        if (this.next == null) {
            if (level == 0) {
                return null;
            }
            return ""+level;
        }

        String prefix = "";
        for (int i=0 ; i<ilevel ; i++) {
            prefix += "  ";
        }
        String s = "";
        for (int i=0 ; i<next.length ; i++) {
            String s2 = next[i].getString(ilevel+1, names);
            if (s2 != null) {
                if (s2.equals("1") || s2.equals("-1")) {
                    s += prefix+names.get(level)+"="+i+" ==> "+s2+"\n";
                } else {
                    s += prefix+names.get(level)+"="+i+"\n"+s2;
                }
            }
        }
        return s;
    }

//    /**
//     * reduce this tree
//     * will use the getKey() function, and will probably need two traversals of the tree,
//     * or is it doable in one only? (the second one is on the reduced tree and used to clean up cache)
//     *
//     * use a hashmap or two to store node's keys
//     *
//     * @return the reduced tree
//     */
//    public MDDNode reduce() {
//        int[] t_key = {0};
//        Map m = newMap();
//
//        MDDNode reduced = (MDDNode)m.get(getKey(m, t_key));
//        reduced.cleanKey();
//        return reduced;
//    }
//
//    /**
//     * get a unique key (imply getting the same as an existing similar subtree).
//     *
//     *    first attribute a temporary key builded from the level and unique keys of children
//     *    if this key already exists
//     *         this subtree is similar to an existing one and should have the same unique key, return this key
//     *    else (this is a new subtree):
//     *         replace all children by correct nodes for their unique key
//     *           (unique keys are cached to make it faster, we have then to clean this cache)
//     *         if all children are equals
//     *             this node is useless, return the unique key of the first child
//     *         else return a new (incremented) unique key
//     *
//     *   special case: terminals nodes will just return their value (ie "-1", "0" or "1"), these values
//     *   should then be prefilled in the Map.
//     *
//     * @param m the hashmap to store temporary (long) and unique (shorter) keys
//     * @param t_key int[1]: value of the next unique key
//     * @return the unique key of this node
//     */
//    private String getKey(Map m, int[] t_key) {
//        if (key != null) {
//            return key;
//        }
//        String skey = next[0].getKey(m, t_key);
//        String tempKey = level+"("+skey;
//        for (int i=1 ; i<next.length;i++) {
//            String skey2 = next[i].getKey(m, t_key);
//            // test if all children are equals
//            if (skey != null && !skey.equals(skey2)) {
//                skey = null;
//            }
//            tempKey += ","+skey2;
//        }
//        tempKey += ")";
//
//        // if all children are equals, we ARE the child
//        if (skey != null) {
//            return skey;
//        }
//
//        key = (String)m.get(tempKey);
//        if (key == null) {
//            // replace subtrees
//            for (int i=0 ; i<next.length ; i++) {
//                MDDNode nnext = (MDDNode)m.get(next[i].getKey(m, t_key));
//                if (nnext != null) {
//                    next[i] = nnext;
//                }
//            }
//
//            key = ""+t_key[0]++;
//            m.put(tempKey, key);
//            m.put(key, this);
//        }
//        return key;
//    }
//
//    /**
//     *
//     *
//     */
//    public void cleanKey() {
//        // don't clean terminal nodes or already cleaned trees
//        if (key != null && next != null) {
//            for (int i=0 ; i<next.length ; i++) {
//                if (next [i] != null) {
//                    next[i].cleanKey();
//                }
//            }
//            key = null;
//        }
//    }
}
