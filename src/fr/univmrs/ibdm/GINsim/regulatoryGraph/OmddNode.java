package fr.univmrs.ibdm.GINsim.regulatoryGraph;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Ordered Binary Decision Diagram (OBDD) is a tree representation of boolean functions.
 * Ordered Multivalued Decidion Diagram (OMDD) is based on this representation but adapted 
 * for a (limited) use of non-boolean functions:
 *   - non-terminal nodes can have more than two childs (actually they have an array of childs)
 *   - terminal nodes are digits: ie can take the values 0, 1, ..., 9
 *
 * this is designed to represent logical parameters in a quickly tested way!
 */
public class OmddNode {

    /** level of this node (only for non-terminal nodes) */
    public int level;
    /** leaves reacheable from this one (only for non-terminal nodes, null otherwise) */
    public OmddNode[] next;
    /** value of the terminal node */
    public short value;
    /** cache for the key, should always be null except while reducing */
    String key = null;
    
    /** constraint: min allowed value */
    public short min=-1;
    /** constraint: max allowed value */
    public short max=-1;
    
    // create once for all the terminals nodes.
    /** all terminal nodes */
    public static final OmddNode[] TERMINALS;
    public static final OmddNode MINUSONE;
    
    /**  */
    public static final int OR = 0;
    /**  */
    public static final int AND = 1;
    /**  */
    public static final int CONSTRAINT = 2;
    
    static {
    	MINUSONE = new OmddNode();
    	MINUSONE.next = null;
    	MINUSONE.value = -1;
    	MINUSONE.key = "MO";
    	
        TERMINALS = new OmddNode[10];
        
        TERMINALS[0] = new OmddNode();
        TERMINALS[0].next = null;
        TERMINALS[0].value = 0;
        TERMINALS[0].key = "Z";
        
        TERMINALS[1] = new OmddNode();
        TERMINALS[1].next = null;
        TERMINALS[1].value = 1;
        TERMINALS[1].key = "O";
        
        TERMINALS[2] = new OmddNode();
        TERMINALS[2].next = null;
        TERMINALS[2].value = 2;
        TERMINALS[2].key = "TW";
        
        TERMINALS[3] = new OmddNode();
        TERMINALS[3].next = null;
        TERMINALS[3].value = 3;
        TERMINALS[3].key = "TH";
        
        TERMINALS[4] = new OmddNode();
        TERMINALS[4].next = null;
        TERMINALS[4].value = 4;
        TERMINALS[4].key = "FO";
        
        TERMINALS[5] = new OmddNode();
        TERMINALS[5].next = null;
        TERMINALS[5].value = 5;
        TERMINALS[5].key = "FI";
        
        TERMINALS[6] = new OmddNode();
        TERMINALS[6].next = null;
        TERMINALS[6].value = 6;
        TERMINALS[6].key = "SI";
        
        TERMINALS[7] = new OmddNode();
        TERMINALS[7].next = null;
        TERMINALS[7].value = 7;
        TERMINALS[7].key = "SE";
        
        TERMINALS[8] = new OmddNode();
        TERMINALS[8].next = null;
        TERMINALS[8].value = 8;
        TERMINALS[8].key = "E";
        
        TERMINALS[9] = new OmddNode();
        TERMINALS[9].next = null;
        TERMINALS[9].value = 9;
        TERMINALS[9].key = "N";
    }
    
    /**
     * test if a state is OK.
     * 
     * @param status
     * @return true if the state is OK.
     */
    public short testStatus (int[] status) {
        
        if (next == null) {
            return value;
        }
        if (status == null || status.length < level) {
            return 0;
        }
        return next[ status[level] ].testStatus(status);
    }
    
    /**
     * merge two trees into a (new?) one.
     * this will create new nodes, as existing one might be merged with different part, they can't be reused.
     * 
     * @param other
     * @param op boolean operation to apply (avaible: AND, OR)
     *
     * @return the merged tree
     */
    public OmddNode merge(OmddNode other, int op) {
//        Map m = new HashMap();
//        m.put(NEGATIVE.key, NEGATIVE);
//        m.put(FALSE.key, FALSE);
//        m.put(POSITIVE.key, POSITIVE);
        int [] tkey = {0};
        return merge(other, op, null, tkey);
        
//        // reduce after merge??
//        // it doesn't seems to help here...        
//        other.cleanKey();
//        other.reduce();
//        return other;
    }
    
    private OmddNode merge(OmddNode other, int op, Map m, int[] t_key) {
        
//        // previous value caching doesn't seems too efficient in this special case
//        // context trees are not (yet) reduced, this might help... 
//        // UPDATE: it doesn't really help, it is even worse!
//        if (key != null) {
//            if (other.key != null) {
//                Object ret = m.get(key+"_"+other.key);
//                if (ret != null) {
//                    return (OmddNode)ret;
//                }
//                ret = m.get(other.key+"_"+key);
//                if (ret != null) {
//                    return (OmddNode)ret;
//                } 
//            } else {
//                other.key = ""+t_key[0]++;
//            }
//        } else {
//            key = ""+t_key[0]++;
//            if (other.key == null) {
//                other.key = ""+t_key[0]++;
//            }
//        }
        OmddNode ret;
        if (next == null) {
            switch (op) {
                case CONSTRAINT:
                    if (other.next == null) {
                        // just check that the constraint is verified
                        if (other.min == -1 || (value >= other.min && value <= other.max)) {
                            return this;
                        }
                        if (value <= other.min) {
                            return TERMINALS[other.min];
                        }
                        return TERMINALS[other.max];
                    }
                    // the constraint is not yet clear, deploy it
                    ret = new OmddNode();
                    ret.level = other.level;
                    ret.next = new OmddNode[other.next.length];
                    for (int i=0 ; i<ret.next.length ; i++) {
                        ret.next[i] = merge(other.next[i], CONSTRAINT, m, t_key);
                    }
                    return ret;
                case OR:
                    switch (value) {
                        case 0:
                            return other;
                        case 1:    
                        case 2:    // if everything is fine, 1/-1 should ONLY get merged with 0 => no problem
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                        case 9:
                            return this;
                        default:
                            return null;
                    }
                case AND:
                    switch (value) {
                        case 0:
                            return this;
                        case 1: 
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                        case 9:
                            return other;
                        case -1:
                            ret = other.revert();
//                            m.put(key+"_"+other.key, ret);
                            return ret;
                        default:
                            return null;
                    }
            }
        }
        if (other.next == null) {
            switch (op) {
                case CONSTRAINT:
                    ret = new OmddNode();
                    ret.level = level;
                    ret.next = new OmddNode[next.length];
                    for (int i=0 ; i<next.length ; i++) {
                        ret.next[i] = next[i].merge(other, CONSTRAINT, m, t_key);
                    }
                    return ret;
                case OR:
                    switch (other.value) {
                        case 0:
                            return this;
                        case 1:    
                        case 2:    // if evereything is fine, 1/-1 should ONLY get merged with 0 => no problem
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                        case 9:
                            return other;
                        default:
                            return null;
                    }
                case AND:
                    switch (other.value) {
                        case 0:
                            return other;
                        case 1: 
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                        case 9:
                            return this;
                        case -1:
                            ret = revert();
//                            m.put(key+"_"+other.key, ret);
                            return ret;
                        default:
                            return null;
                    }
            }
        }
        
        if (level == other.level) { // merge all childs together
            if (next.length != other.next.length) {
                return null;
            }
            ret = new OmddNode();
            ret.level = level;
//            ret.t_order = t_order;
            ret.next = new OmddNode[next.length];
            for (int i=0 ; i<next.length ; i++) {
                ret.next[i] = next[i].merge(other.next[i], op, m, t_key);
            }
//          m.put(key+"_"+other.key, ret);
            return ret;
        } else if (level < other.level) {
            ret = new OmddNode();
            ret.level = level;
//            ret.t_order = t_order;
            ret.next = new OmddNode[next.length];
            for (int i=0 ; i<next.length ; i++) {
                ret.next[i] = next[i].merge(other, op, m, t_key);
            }
//          m.put(key+"_"+other.key, ret);
            return ret;
        } else {
            ret = new OmddNode();
            ret.level = other.level;
//            ret.t_order = t_order;
            ret.next = new OmddNode[other.next.length];
            for (int i=0 ; i<other.next.length ; i++) {
                ret.next[i] = merge(other.next[i], op, m, t_key);
            }
//          m.put(key+"_"+other.key, ret);
            return ret;
        }
    }
    
    /**
     * should be unused for now: nodes are never modified so they don't need to be cloned, just reused.
     * 
     * 
     * @return a copy of this ObddLeave
     */
    public Object clone() {
        // if this is a terminal node, this is _NOT_ clonable
        if (next == null) {
            return this;
        }
        
        OmddNode copy = new OmddNode();
        copy.level = level;
//        copy.t_order = (short[])t_order.clone();
        copy.next = new OmddNode[next.length];
        for (int i=0 ; i<next.length ; i++) {
            if (next[i] != null) {
                copy.next[i] = (OmddNode)next[i].clone();
            }
        }
        return copy;
    }
    
    private OmddNode revert() {
        // if this is a terminal node, the end is near
        if (next == null) {
            if (value < 10) {
                return TERMINALS[value];
            }
            return this;
        }
        
        OmddNode copy = new OmddNode();
        copy.level = level;
//        copy.t_order = (short[])t_order.clone();
        copy.next = new OmddNode[next.length];
        for (int i=0 ; i<next.length ; i++) {
            if (next[i] != null) {
                copy.next[i] = next[i].revert();
            }
        }
        return copy;
    }
        
    /**
     * un-nice way to write this tree in a "readable" form
     * @return a printed form of the tree
     */
    public String toString() {
        if (this.next == null) {
            return ""+value;
        }
        
        String s = "(";
        for (int i=0 ; i<next.length ; i++) {
            s += "(N["+level+"]="+i+" && "+next[i]+") ; ";
        }
        s = s.substring(0, s.length()-3)+")";
        return s;
    }
    
    /**
     * @param ilevel the indentation level
     * @param names vector of node name
     * @return a printed form of the tree
     */
    public String getString(int ilevel, Vector names) {
        if (this.next == null) {
            if (value == 0) {
                return null;
            }
            return ""+value;
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

    /**
     * reduce this tree
     * will use the getKey() function, and will probably need two traversals of the tree,
     * or is it doable in one only? (the second one is on the reduced tree and used to clean up cache)
     * 
     * use a hashmap or two to store node's keys
     * 
     * @return the reduced tree
     */
    public OmddNode reduce() {
        int[] t_key = {0};
        Map m = new HashMap();
        for (int i=0 ; i<TERMINALS.length ; i++) {
            m.put(TERMINALS[i].key, TERMINALS[i]);
        }

        OmddNode reduced = (OmddNode)m.get(getKey(m, t_key));
        reduced.cleanKey();
        return reduced;
    }
    
    /**
     * get a uniq key (imply getting the same as an existing similar subtree).
     * 
     *    first attribute a temporary key builded from the level and uniq keys of childs
     *    if this key already exists
     *         this subtree is similar to an existing one and should have the same uniq key, return this key
     *    else (this is a new subtree):
     *         replace all childs by correct nodes for their uniq key 
     *           (uniq keys are cached to make it faster, we have then to clean this cache)
     *         if all childs are equals 
     *             this node is useless, return the uniq key of the first child
     *         else return a new (incremented) uniq key
     *      
     *   special case: terminals nodes will just return their value (ie "-1", "0" or "1"), these values 
     *   should then be prefilled in the Map.
     *      
     * @param m the hashmap to store temporary (long) and uniq (shorter) keys
     * @param t_key int[1]: value of the next uniq key
     * @return the uniq key of this node
     */
    private String getKey(Map m, int[] t_key) {
        if (key != null) {
            return key;
        }
        String skey = next[0].getKey(m, t_key);
        String tempKey = level+"("+skey;
        for (int i=1 ; i<next.length;i++) {
            String skey2 = next[i].getKey(m, t_key);
            // test if all childs are equals
            if (skey != null && !skey.equals(skey2)) {
                skey = null;
            }
            tempKey += ","+skey2;
        }
        tempKey += ")";

        // if all childs are equals, we ARE the child
        if (skey != null) {
            return skey;
        }
        
        key = (String)m.get(tempKey);
        if (key == null) {
            // replace subtrees
            for (int i=0 ; i<next.length ; i++) {
                OmddNode nnext = (OmddNode)m.get(next[i].getKey(m, t_key)); 
                if (nnext != null) {
                    next[i] = nnext;
                }
            }

            key = ""+t_key[0]++;
            m.put(tempKey, key);
            m.put(key, this);
        }
        return key;
    }
    
    /**
     */
    public void cleanKey() {
        // don't clean terminal nodes or already cleaned trees
        if (key != null && next != null) {
            for (int i=0 ; i<next.length ; i++) {
                if (next [i] != null) {
                    next[i].cleanKey();
                }
            }
            key = null;
        }
    }

    /**
     * test if two diagramms conflict: usefull to store logical parameters as diagramms and detect conflicts
     * 
     * @param node
     * @return true if these diagramm should not be mixed
     */
    public boolean conflict (OmddNode node) {
        if (next == null) {
            if (value == 0) {
                return false;
            }
            if (node.next == null) {
                return (node.value == 0 || node.value  == value);
            }
            for (int i=0 ; i<node.next.length ; i++) {
                if (node.next[i].conflict(this)) {
                    return true;
                }
                return false;
            }
        }
        
        if (node.next == null) {
            if (node.value == 0) {
                return false;
            }
            for (int i=0 ; i<next.length ; i++) {
                if (next[i].conflict(node)) {
                    return true;
                }
                return false;
            }
        }
        
        return false;
    }
    
    public OmddNode buildNonFocalTree(int targetLevel, int len) {
    	if (next == null || this.level > targetLevel) {
    		// the targetnode was not found, insert it
    		OmddNode ret = new OmddNode();
    		ret.level = targetLevel;
    		ret.next = new OmddNode[len];
    		for (int i=0 ; i<len ; i++) {
    			ret.next[i] = this.updateForNonFocal(i);
    		}
    		return ret;
    	}
    	// on the right level
		OmddNode ret = new OmddNode();
		ret.level = targetLevel;
		ret.next = new OmddNode[len];
		for (int i=0 ; i<len ; i++) {
			ret.next[i] = next[i].updateForNonFocal(i);
		}
		return ret;
    }
    public OmddNode updateForNonFocal(int value) {
    	if (next == null) {
    		if (this.value == value) {
    			return TERMINALS[0];
    		}
    		if (this.value < value) {
    			return TERMINALS[1];
    		}
    		return MINUSONE;
    	}
    	OmddNode ret = new OmddNode();
    	ret.level = level;
    	ret.next = new OmddNode[this.next.length];
    	for (int i=0 ; i<next.length ; i++) {
    		ret.next[i] = next[i].updateForNonFocal(value);
    	}
    	return ret;
    }
}
