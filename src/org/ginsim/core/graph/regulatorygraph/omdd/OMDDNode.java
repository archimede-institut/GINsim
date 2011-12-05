package org.ginsim.core.graph.regulatorygraph.omdd;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

/**
 * Ordered Binary Decision Diagram (OBDD) is a tree representation of boolean functions.
 * Ordered Multivalued Decision Diagram (OMDD) is based on this representation but adapted 
 * for a (limited) use of non-boolean functions:
 *   - non-terminal nodes can have more than two children (actually they have an array of children)
 *   - terminal nodes are digits: ie can take the values 0, 1, ..., 9
 *
 * this is designed to represent logical parameters in a quickly tested way!
 */
public class OMDDNode {

    /** level of this node (only for non-terminal nodes) */
    public int level = -1;
    /** leaves reachable from this one (only for non-terminal nodes, null otherwise) */
    public OMDDNode[] next;
    /** value of the terminal node */
    public byte value;
    /** cache for the key, should always be null except while reducing */
    private String key = null;
    
    /** constraint: min allowed value */
    public byte min=-1;
    /** constraint: max allowed value */
    public byte max=-1;
    
    // create once for all the terminals nodes.
    /** all terminal nodes */
    public static final OMDDNode[] TERMINALS;
    public static final OMDDNode MINUSONE;
    
    /**  */
    public static final int OR = 0;
    /**  */
    public static final int AND = 1;
    /**  */
    public static final int CONSTRAINT = 2;
    public static final int CONSTRAINTOR = 3;

    public static final int MAX = 4;

    static {
    	MINUSONE = new OMDDNode();
    	MINUSONE.next = null;
    	MINUSONE.value = -1;
    	MINUSONE.key = "MO";
    	
        TERMINALS = new OMDDNode[10];
        
        TERMINALS[0] = new OMDDNode();
        TERMINALS[0].next = null;
        TERMINALS[0].value = 0;
        TERMINALS[0].key = "Z";
        
        TERMINALS[1] = new OMDDNode();
        TERMINALS[1].next = null;
        TERMINALS[1].value = 1;
        TERMINALS[1].key = "O";
        
        TERMINALS[2] = new OMDDNode();
        TERMINALS[2].next = null;
        TERMINALS[2].value = 2;
        TERMINALS[2].key = "TW";
        
        TERMINALS[3] = new OMDDNode();
        TERMINALS[3].next = null;
        TERMINALS[3].value = 3;
        TERMINALS[3].key = "TH";
        
        TERMINALS[4] = new OMDDNode();
        TERMINALS[4].next = null;
        TERMINALS[4].value = 4;
        TERMINALS[4].key = "FO";
        
        TERMINALS[5] = new OMDDNode();
        TERMINALS[5].next = null;
        TERMINALS[5].value = 5;
        TERMINALS[5].key = "FI";
        
        TERMINALS[6] = new OMDDNode();
        TERMINALS[6].next = null;
        TERMINALS[6].value = 6;
        TERMINALS[6].key = "SI";
        
        TERMINALS[7] = new OMDDNode();
        TERMINALS[7].next = null;
        TERMINALS[7].value = 7;
        TERMINALS[7].key = "SE";
        
        TERMINALS[8] = new OMDDNode();
        TERMINALS[8].next = null;
        TERMINALS[8].value = 8;
        TERMINALS[8].key = "E";
        
        TERMINALS[9] = new OMDDNode();
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
    public byte testStatus (byte[] status) {
        
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
     * @param op boolean operation to apply (available: AND, OR)
     *
     * @return the merged tree
     */
    public OMDDNode merge(OMDDNode other, int op) {
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
    
    public OMDDNode merge(OMDDNode other, int op, Map m, int[] t_key) {
        
//        // previous value caching doesn't seems too efficient in this special case
//        // context trees are not (yet) reduced, this might help... 
//        // UPDATE: it doesn't really help, it is even worse!
//        if (key != null) {
//            if (other.key != null) {
//                Object ret = m.get(key+"_"+other.key);
//                if (ret != null) {
//                    return (OMDDNode)ret;
//                }
//                ret = m.get(other.key+"_"+key);
//                if (ret != null) {
//                    return (OMDDNode)ret;
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
        OMDDNode ret;
        if (next == null) {
            switch (op) {
                case CONSTRAINT:
                    if (other.next == null) {
                        // just check that the constraint is verified
                        if (other.min == -1 || value >= other.min && value <= other.max) {
                            return this;
                        }
                        if (value <= other.min) {
                            return TERMINALS[other.min];
                        }
                        return TERMINALS[other.max];
                    }
                    // the constraint is not yet clear, deploy it
                    ret = new OMDDNode();
                    ret.level = other.level;
                    ret.next = new OMDDNode[other.next.length];
                    for (int i=0 ; i<ret.next.length ; i++) {
                        ret.next[i] = merge(other.next[i], CONSTRAINT, m, t_key);
                    }
                    return ret;
                case CONSTRAINTOR:
                    if (other.next == null) {
                        // just check that the constraint is verified
                        if (other.min == -1 || value >= other.min && value <= other.max) {
                            return this;
                        }
                        return other;
                    }
                    // the constraint is not yet clear, deploy it
                    ret = new OMDDNode();
                    ret.level = other.level;
                    ret.next = new OMDDNode[other.next.length];
                    for (int i=0 ; i<ret.next.length ; i++) {
                        ret.next[i] = merge(other.next[i], CONSTRAINTOR, m, t_key);
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
                case MAX:
                	if (other.next == null) {
                		if (other.value > this.value) {
                			return other;
                		}
                		return this;
                	}
                    ret = new OMDDNode();
                    ret.level = other.level;
                    ret.next = new OMDDNode[other.next.length];
                    for (int i=0 ; i<ret.next.length ; i++) {
                        ret.next[i] = merge(other.next[i], op, m, t_key);
                    }
                    return ret;
            }
        }
        if (other.next == null) {
            switch (op) {
                case CONSTRAINT:
                    ret = new OMDDNode();
                    ret.level = level;
                    ret.next = new OMDDNode[next.length];
                    for (int i=0 ; i<next.length ; i++) {
                        ret.next[i] = next[i].merge(other, CONSTRAINT, m, t_key);
                    }
                    return ret;
                case CONSTRAINTOR:
                    ret = new OMDDNode();
                    ret.level = level;
                    ret.next = new OMDDNode[next.length];
                    for (int i=0 ; i<next.length ; i++) {
                        ret.next[i] = next[i].merge(other, CONSTRAINTOR, m, t_key);
                    }
                    return ret;
                case OR:
                    switch (other.value) {
                        case 0:
                            return this;
                        case 1:    
                        case 2:    // if everything is fine, 1/-1 should ONLY get merged with 0 => no problem
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
                case MAX:
                    ret = new OMDDNode();
                    ret.level = this.level;
                    ret.next = new OMDDNode[this.next.length];
                    for (int i=0 ; i<ret.next.length ; i++) {
                        ret.next[i] = this.next[i].merge(other, op, m, t_key);
                    }
                    return ret;
            }
        }
        
        if (level == other.level) { // merge all children together
            if (next.length != other.next.length) {
                return null;
            }
            ret = new OMDDNode();
            ret.level = level;
//            ret.t_order = t_order;
            ret.next = new OMDDNode[next.length];
            for (int i=0 ; i<next.length ; i++) {
                ret.next[i] = next[i].merge(other.next[i], op, m, t_key);
            }
//          m.put(key+"_"+other.key, ret);
            return ret;
        } else if (level < other.level) {
            ret = new OMDDNode();
            ret.level = level;
//            ret.t_order = t_order;
            ret.next = new OMDDNode[next.length];
            for (int i=0 ; i<next.length ; i++) {
                ret.next[i] = next[i].merge(other, op, m, t_key);
            }
//          m.put(key+"_"+other.key, ret);
            return ret;
        } else {
            ret = new OMDDNode();
            ret.level = other.level;
//            ret.t_order = t_order;
            ret.next = new OMDDNode[other.next.length];
            for (int i=0 ; i<other.next.length ; i++) {
                ret.next[i] = merge(other.next[i], op, m, t_key);
            }
//          m.put(key+"_"+other.key, ret);
            return ret;
        }
    }
    
    /**
     * Merge an array
     * @deprecated
     * @see multi_or
     */
    public OMDDNode mergeMultiple(OMDDNode[] others, int op) {
    	OMDDNode newNode = others[0];
    	for (int i = 1; i < others.length; i++) {
			newNode = newNode.merge(others[i], op);
		}
		return this.merge(newNode, op);
    }
    /**
     * Merge an collection
     * @deprecated
     * @see multi_or
     */
    public OMDDNode mergeMultiple(Collection others, int op) {
    	Iterator it = others.iterator();
    	if (!it.hasNext()) return this;
    	OMDDNode newNode = (OMDDNode) it.next();
    	for (; it.hasNext();) {
			newNode = newNode.merge((OMDDNode) it.next(), op);
		}
   		return this.merge(newNode, op);
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
        
        OMDDNode copy = new OMDDNode();
        copy.level = level;
//        copy.t_order = (byte[])t_order.clone();
        copy.next = new OMDDNode[next.length];
        for (int i=0 ; i<next.length ; i++) {
            if (next[i] != null) {
                copy.next[i] = (OMDDNode)next[i].clone();
            }
        }
        return copy;
    }
    
    private OMDDNode revert() {
        // if this is a terminal node, the end is near
        if (next == null) {
            if (value < 10) {
                return TERMINALS[value];
            }
            return this;
        }
        
        OMDDNode copy = new OMDDNode();
        copy.level = level;
//        copy.t_order = (byte[])t_order.clone();
        copy.next = new OMDDNode[next.length];
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
        	if (min != -1) {
        		return "["+min+","+max+"]";
        	}
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
     * @param ilevel the indentation level
     * @param names vector of node name
     * @return a printed form of the tree
     */
    public String getString(int ilevel) {
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
            String s2 = next[i].getString(ilevel+1);
            if (s2 != null) {
                if (s2.length() < 3) {
                    s += prefix+level+"="+i+" ==> "+s2+"\n";
                } else {
                    s += prefix+level+"="+i+"\n"+s2;
                }
            }
        }
        return s;
    }
    
    public StringBuffer write() {
        StringBuffer s = new StringBuffer();
        if (next == null) {
    		s.append(value);
    		return s;
    	}
        s.append('(');
        s.append(level);
        s.append(',');
        for (int i=0 ; i<next.length-1 ; i++) {
            if (next[i] != null) s.append(next[i].write());
            else s.append("&");
            s.append(',');
        }
        if (next[next.length-1] != null)s.append(next[next.length-1].write());
        else s.append("&");
        s.append(')');
        return s;
    }
    
    public static OMDDNode read(String s, byte[] childCount) throws ParseException {
    	if (s.length() == 1) {
    		return TERMINALS[Byte.parseByte(s)];
    	}
    	
    	int length = s.length();
    	int deep = -1;
    	
    	byte[] childVisited = new byte[childCount.length];
    	Stack heap = new Stack();
    	OMDDNode o = null, op;
    	int i = 0;
    	while (i < length) {
    		char c = s.charAt(i);
    		if (c == '(') {
       			o = new OMDDNode();
    			heap.add(o);
    			deep++;
				i = readLevel(s, o, i+1, length);
				for (int j = deep; j < o.level; j++) {
					childVisited[j] = -1; //the child is skipped
				}
				deep = o.level;
    			if (i >= length || s.charAt(i) != ',') {
    				throw new ParseException("Missing , after opening a new node", i-1);
    			}
    		} else if (c == ')') {
    			if (childVisited[deep] != childCount[deep]) {
    				throw new ParseException("Wrong number of child found", i);
    			}
    			do {
        			childVisited[deep] = 0;
    				deep--;
    			} while (deep >= 0 && childVisited[deep] == -1); //while the child has been skipped skipped
        		op = (OMDDNode) heap.pop();
        		if (heap.size() > 0) {
            		o = (OMDDNode) heap.peek();
            		if (o.next == null) {
            			o.next = new OMDDNode[childCount[deep]];
            		}       			
            		o.next[childVisited[deep]++] = op;
        		}
    			if (deep == -2) {
    				throw new ParseException("Opening parenthese missing", i);
    			}
    		} else if (c == ',') { 
    		} else {
    			readTerminal(s, (OMDDNode) heap.peek(), i, deep, childVisited, childCount, length);
   		}
    		i++;
    	}
    	if (deep != -1) {
			throw new ParseException("End of string reached too early", i);
    	}  	
    	return o;
    }

	private static void readTerminal(String s, OMDDNode o, int i, int deep, byte[] childVisited, byte[] childCount, int length) throws ParseException {
		byte val;
		switch (s.charAt(i)) {
		case '0': val = 0; break;
		case '1': val = 1; break;
		case '2': val = 2; break;
		case '3': val = 3; break;
		case '4': val = 4; break;
		case '5': val = 5; break;
		case '6': val = 6; break;
		case '7': val = 7; break;
		default:
			throw new ParseException("Missing terminal value", i);
		}
		if (o.next == null) {
			o.next = new OMDDNode[childCount[deep]];
		}
		int k = childVisited[deep]++;
		if (k >= childCount[deep]) {
			throw new ParseException("Too much child at depth "+deep+" - "+k+" - "+childCount[deep]+" - "+childVisited[deep], i);
		}
		o.next[k] = TERMINALS[val];
	}


	/**
     * 
     * @param s the string
     * @param o current OMDDNode to fill with level
     * @param i current index in s
     * @param length the length of s
     * @return the index of the next non number character
     */
    private static int readLevel(String s, OMDDNode o, int i, int length) {
    	int res = 0;
    	char c = s.charAt(i++);
    	do {
			switch (c) {
			case '0': break;
			case '1': res += 1; break;
			case '2': res += 2; break;
			case '3': res += 3; break;
			case '4': res += 4; break;
			case '5': res += 5; break;
			case '6': res += 6; break;
			case '7': res += 7; break;
			case '8': res += 8; break;
			case '9': res += 9; break;
			default:
				o.level = res/10;
				return i-1;
			}
    		res *= 10;
    		c = s.charAt(i++);
    	} while (i < length);
		return length+1; //FAIL in the while
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
    public OMDDNode reduce() {
        long[] t_key = {0};
        Map m = new HashMap();
        for (int i=0 ; i<TERMINALS.length ; i++) {
            m.put(TERMINALS[i].key, TERMINALS[i]);
        }

        OMDDNode reduced = (OMDDNode)m.get(getKey(m, t_key));
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
     * @param m the hashmap to store temporary (long) and uniq (byteer) keys
     * @param t_key int[1]: value of the next uniq key
     * @return the uniq key of this node
     */
    private String getKey(Map m, long[] t_key) {
        if (key != null) {
            return key;
        }
        String skey = next[0].getKey(m, t_key);
        String tempKey = level+"("+skey;
        for (int i=1 ; i<next.length;i++) {
            String skey2 = next[i].getKey(m, t_key);
            // test if all children are equals
            if (skey != null && !skey.equals(skey2)) {
                skey = null;
            }
            tempKey += ","+skey2;
        }
        tempKey += ")";

        // if all children are equals, this IS the child
        if (skey != null) {
            return skey;
        }
        
        key = (String)m.get(tempKey);
        if (key == null) {
            // replace subtrees
            for (int i=0 ; i<next.length ; i++) {
                OMDDNode nnext = (OMDDNode)m.get(next[i].getKey(m, t_key)); 
                if (nnext != null) {
                	next[i] = nnext;
                }
            }

            key = ""+t_key[0]++;
            m.put(tempKey, key);
            m.put(key, this);
        } else {
        	tempKey = key;
        	key = null;
        	return tempKey;
        }
        return key;
    }
    
    /**
     */
    public void cleanKey() {
        // don't clean terminal nodes or already cleaned trees
    	// is the "key != null" test a good one ?
        if (key != null && next != null) {
            for (int i=0 ; i<next.length ; i++) {
                if (next[i] != null) {
                    next[i].cleanKey();
                }
            }
            key = null;
        }
    }

    /**
     * test if two diagrams conflict: useful to store logical parameters as diagrams and detect conflicts
     * 
     * @param node
     * @return true if these diagram should not be mixed
     */
    public boolean conflict (OMDDNode node) {
        if (next == null) {
            if (value == 0) {
                return false;
            }
            if (node.next == null) {
                return node.value == 0 || node.value  == value;
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
    
    public OMDDNode buildNonFocalTree(int targetLevel, int len) {
    	if (next == null || this.level > targetLevel) {
    		// the targetnode was not found, insert it
    		OMDDNode ret = new OMDDNode();
    		ret.level = targetLevel;
    		ret.next = new OMDDNode[len];
    		for (int i=0 ; i<len ; i++) {
    			ret.next[i] = this.updateForNonFocal(i);
    		}
    		return ret;
    	}
    	// on the right level
		OMDDNode ret = new OMDDNode();
		ret.level = targetLevel;
		ret.next = new OMDDNode[len];
		for (int i=0 ; i<len ; i++) {
			ret.next[i] = next[i].updateForNonFocal(i);
		}
		return ret;
    }
    public OMDDNode updateForNonFocal(int value) {
    	if (next == null) {
    		if (this.value == value) {
    			return TERMINALS[0];
    		}
    		if (this.value < value) {
    			return TERMINALS[1];
    		}
    		return MINUSONE;
    	}
    	OMDDNode ret = new OMDDNode();
    	ret.level = level;
    	ret.next = new OMDDNode[this.next.length];
    	for (int i=0 ; i<next.length ; i++) {
    		ret.next[i] = next[i].updateForNonFocal(value);
    	}
    	return ret;
    }


	public int remove(byte[] state) {
		return 0;
	}
	
	/**
	 * Perform an or between a list of states
	 * @param nodes an array of states (byte[][])
	 * @return an OMDDNode
	 */
	public static OMDDNode multi_or(byte[][] states, byte[] childsCount)  {
		return multi_or(states, childsCount, 0);
	}
	private static OMDDNode multi_or(byte[][] states, byte[] childsCount, int depth)  {
		if (states == null || states.length == 0) {
			return OMDDNode.TERMINALS[0];
		}
		if (depth == childsCount.length) {
			return OMDDNode.TERMINALS[1];
		}

		int[] nbnexts = new int[childsCount[depth]];
		for (int i=0 ; i<states.length ; i++) {
			nbnexts[states[i][depth]]++;
		}
		OMDDNode[] next = new OMDDNode[childsCount[depth]];
		for (byte value=0 ; value<next.length ; value++) {
			byte[][] next_states = new byte[nbnexts[value]][];
			int c = 0;
			for (int i=0 ; i<states.length ; i++) {
				if (states[i][depth] == value) {
					next_states[c++] = states[i];
				}
			}
			next[value] = multi_or(next_states, childsCount, depth+1);
		}
		OMDDNode ret = new OMDDNode();
		ret.level = depth;
		ret.next = next;
		return ret;
	}
	/**
	 * Perform an or between a list of states
	 * @param nodes a list of state (byte[])
	 * @return an OMDDNode
	 */
	public static OMDDNode multi_or(List states, byte[] childsCount)  {
		if (states == null || states.size() == 0) {
			return OMDDNode.TERMINALS[0];
		}
		if (0 == childsCount.length) {
			return OMDDNode.TERMINALS[1];
		}

		int[] nbnexts = new int[childsCount[0]];
		for (Iterator it = states.iterator(); it.hasNext();) {
			byte[] state = (byte[]) it.next();
			nbnexts[state[0]]++;
		}
		OMDDNode[] next = new OMDDNode[childsCount[0]];
		for (byte value=0 ; value<next.length ; value++) {
			byte[][] next_states = new byte[nbnexts[value]][];
			int c = 0;
			for (Iterator it = states.iterator(); it.hasNext();) {
				byte[] state = (byte[]) it.next();
				if (state[0] == value) {
					next_states[c++] = state;
				}
			}
			next[value] = multi_or(next_states, childsCount, 1);
		}
		OMDDNode ret = new OMDDNode();
		ret.level = 0;
		ret.next = next;
		return ret;
	}

	 /**
	  * Perform an or between a list of OMDDNode
	  * @param nodes a list of OMDDNode
	  * @return an OMDDNode
	  */
	public static OMDDNode multi_or(List nodes)  {
		 int nbnodes = nodes.size();
		 if (nbnodes == 0) {
			 return null;
		 }
		 if (nbnodes == 1) {
			 return (OMDDNode)nodes.get(0);
		 }
		 OMDDNode ref = null;
		 for (Iterator it=nodes.iterator() ; it.hasNext() ; ) {
			 OMDDNode node = (OMDDNode)it.next();
			 if (node.next == null) {
				 if (node.value > 0) {
					 return node;
				 }
			 } else if (ref == null || node.level < ref.level ) {
				 ref = node;
			 }
		 }
		 if (ref == null) {
			 return OMDDNode.TERMINALS[0];
		 }
		 OMDDNode ret = new OMDDNode();
		 ret.level = ref.level;
		 ret.next = new OMDDNode[ref.next.length];
		 List next_nodes = new ArrayList();
		 for (int value=0 ; value<ref.next.length ; value++) {
			 next_nodes.clear();
			 for (Iterator it=nodes.iterator() ; it.hasNext() ; ) {
				 OMDDNode node = (OMDDNode)it.next();
				 if (node.next != null) {
					 if (node.level == ref.level) {
						 next_nodes.add(node.next[value]);
					 } else {
						 next_nodes.add(node);
					 }
				 }
			 }
			 ret.next[value] = multi_or(next_nodes);
		 }
		 return ret;
	}
}
