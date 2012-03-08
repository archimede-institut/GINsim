package org.ginsim.core.mdd;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Ordered Binary Decision Diagram (OBDD) is a tree representation of boolean functions.
 * Ordered Multi-valued Signed Decision Diagram (OMSDD) is based on this representation but adapted
 * for a (limited) use of non-boolean functions:
 * <ul>
 *   <li> non-terminal nodes can have more than two children (actually they have an array of children)</li>
 *   <li> terminal nodes are "signed boolean": ie can take the values -1, 0 or +1</li>
 * </ul>
 */
public class OmsddNode {

    /** level of this node (only for non-terminal nodes) */
    public int level;
    /** leaves reachable from this one (only for non-terminal nodes, null otherwise) */
    public OmsddNode[] next;
    /** value of the terminal node */
    public byte value;
    /** cache for the key, should always be null except while reducing */
    String key = null;

    // create once for all the terminals nodes.
    /**  */
    public static final OmsddNode POSITIVE;
    /**  */
    public static final OmsddNode NEGATIVE;
    /**  */
    public static final OmsddNode FALSE;

    /**  */
    public static final int OR = 0;
    /**  */
    public static final int AND = 1;
    /**  */
    public static final int CLEANUP = 2;

    static {
        POSITIVE = new OmsddNode();
        POSITIVE.next = null;
        POSITIVE.value = 1;
        POSITIVE.key = "P";

        NEGATIVE = new OmsddNode();
        NEGATIVE.next = null;
        NEGATIVE.value = -1;
        NEGATIVE.key = "N";

        FALSE = new OmsddNode();
        FALSE.next = null;
        FALSE.value = 0;
        FALSE.key = "F";
    }

    /**
     * test if a state is OK.
     *
     * @param status
     * @return true if the state is OK.
     */
    public byte testStatus (int[] status) {

        if (next == null) {
            return value;
        }
        if (status == null || status.length < level) {
            return 0;
        }
        return next[status[level]].testStatus(status);
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
    public OmsddNode merge(OmsddNode other, int op) {
        int [] tkey = {0};
        return merge(other, op, null, tkey);
    }

    private OmsddNode merge(OmsddNode other, int op, Map m, int[] t_key) {
        OmsddNode ret;
        if (next == null) {
            switch (op) {
                case OR:
                    switch (value) {
                        case 0:
                            return other;
                        case  1:
                        case -1:    // if everything is fine, 1/-1 should ONLY get merged with 0.
                            return this;
                        default:
                            return null;
                    }
                case AND:
                    switch (value) {
                        case 0:
                            return this;
                        case 1:
                            return other;
                        case -1:
                            ret = other.revert();
                            return ret;
                        default:
                            return null;
                    }
                case CLEANUP:
                    switch (value) {
                        case 0:
                            return this;
                        case 1:
                        case -1:
                            if (other.next == null) {
                                if (other.value == value) {
                                    return this;
                                }
                                return FALSE;
                            }
                            ret = new OmsddNode();
                            ret.level = other.level;
                            ret.next = new OmsddNode[other.next.length];
                            for (int i=0 ; i<other.next.length ; i++) {
                                ret.next[i] = this.merge(other.next[i], op, m, t_key);
                            }
                            return ret;
                        default:
                            return null;
                    }
            }
        }
        if (other.next == null) {
            switch (op) {
                case OR:
                    switch (other.value) {
                        case 0:
                            return this;
                        case  1:
                        case -1:    // if everything is fine, 1/-1 should ONLY get merged with 0.
                            return this;
                        default:
                            return null;
                    }
                case AND:
                    switch (other.value) {
                        case 0:
                            return other;
                        case 1:
                            return this;
                        case -1:
                            ret = revert();
                            return ret;
                        default:
                            return null;
                    }
                case CLEANUP:
                    switch (other.value) {
                        case 0:
                            return other;
                        case 1:
                        case -1:
                            ret = new OmsddNode();
                            ret.level = level;
                            ret.next = new OmsddNode[next.length];
                            for (int i=0 ; i<next.length ; i++) {
                                ret.next[i] = next[i].merge(other, op, m, t_key);
                            }
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
            ret = new OmsddNode();
            ret.level = level;
            ret.next = new OmsddNode[next.length];
            for (int i=0 ; i<next.length ; i++) {
                ret.next[i] = next[i].merge(other.next[i], op, m, t_key);
            }
            return ret;
        } else if (level < other.level) {
            ret = new OmsddNode();
            ret.level = level;
            ret.next = new OmsddNode[next.length];
            for (int i=0 ; i<next.length ; i++) {
                ret.next[i] = next[i].merge(other, op, m, t_key);
            }
            return ret;
        } else {
            ret = new OmsddNode();
            ret.level = other.level;
            ret.next = new OmsddNode[other.next.length];
            for (int i=0 ; i<other.next.length ; i++) {
                ret.next[i] = this.merge(other.next[i], op, m, t_key);
            }
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

        OmsddNode copy = new OmsddNode();
        copy.level = level;
        copy.next = new OmsddNode[next.length];
        for (int i=0 ; i<next.length ; i++) {
            if (next[i] != null) {
                copy.next[i] = (OmsddNode)next[i].clone();
            }
        }
        return copy;
    }

    private OmsddNode revert() {
        // if this is a terminal node, the end is near
        if (next == null) {
            switch(value) {
                case -1:
                   return POSITIVE;
                case 0:
                    return FALSE;
                case 1:
                    return NEGATIVE;
            }
            return this;
        }

        OmsddNode copy = new OmsddNode();
        copy.level = level;
        copy.next = new OmsddNode[next.length];
        for (int i=0 ; i<next.length ; i++) {
            if (next[i] != null) {
                copy.next[i] = next[i].revert();
            }
        }
        return copy;
    }

    /**
     * add a path to this tree.
     * WIP...
     *
     * not sure if it is a good idea: merge function should do it already,
     * maybe not optimally but additional cost is non-blocker, and at least it works.
     *
     * @param path
     * @param index
     * @param value
     *
     * @return the updated tree (root might have change)
     */
    public OmsddNode add2tree(int[][] path, int index, int value) {
        if (path[index][0] > level) {
            OmsddNode ret = new OmsddNode();
            ret.next = new OmsddNode[path[index][1]];
            int a = path[index][2];
            int i=0;
            for ( ; i<a ; i++) {
                ret.next[i] = FALSE;
            }
            ret.next[i++] = add2tree(path, index+1, value);
            for ( ; i<ret.next.length ; i++) {
                ret.next[i] = FALSE;
            }
            return ret;
        }
        return this;
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
    public String getString(int ilevel, List names) {
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
        boolean[] t_tested = new boolean[next.length];
        for (int i=0 ; i<next.length ; i++) {
        	if (!t_tested[i]) {
        		String curval = null;
        		for (int j=i+1 ; j<next.length ; j++) {
        			if (next[j] == next[i]) {
        				t_tested[j] = true;
        				if (curval == null) {
        					curval = "("+i+" OR "+j;
        				} else {
        					curval += " OR "+j;
        				}
        			}
        		}
        		if (curval == null) {
        			curval = ""+i;
        		} else {
        			curval += ")";
        		}
	            String s2 = next[i].getString(ilevel+1, names);
	            if (s2 != null) {
                    if (s2.equals("1")) {
                        s += prefix+names.get(level)+"="+curval+" ==> POSITIVE\n";
                    } else if (s2.equals("-1")) {
                        s += prefix+names.get(level)+"="+curval+" ==> NEGATIVE\n";
                    } else {
                        s += prefix+names.get(level)+"="+curval+"\n"+s2;
	                }
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
    public OmsddNode reduce() {
        int[] t_key = {0};
        Map m = new HashMap();
        m.put(NEGATIVE.key, NEGATIVE);
        m.put(FALSE.key, FALSE);
        m.put(POSITIVE.key, POSITIVE);

        OmsddNode reduced = (OmsddNode)m.get(getKey(m, t_key));
        reduced.cleanKey();
        return reduced;
    }

    /**
     * get a unique key (implies getting the same as an existing similar subtree).
     *
     *    first attribute a temporary key builded from the level and unique keys of children
     *    if this key already exists
     *         this subtree is similar to an existing one and should have the same unique key, return this key
     *    else (this is a new subtree):
     *         replace all children by correct nodes for their unique key
     *           (unique keys are cached to make it faster, we have then to clean this cache)
     *         if all children are equals
     *             this node is useless, return the unique key of the first child
     *         else return a new (incremented) unique key
     *
     *   special case: terminals nodes will just return their value (ie "-1", "0" or "1"), these values
     *   should then be prefilled in the Map.
     *
     * @param m the hashmap to store temporary (long) and unique (byteer) keys
     * @param t_key int[1]: value of the next unique key
     * @return the unique key of this node
     */
    private String getKey(Map m, int[] t_key) {
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

        // if all children are equals, we ARE the child
        if (skey != null) {
            return skey;
        }

        key = (String)m.get(tempKey);
        if (key == null) {
            // replace subtrees
            for (int i=0 ; i<next.length ; i++) {
                OmsddNode nnext = (OmsddNode)m.get(next[i].getKey(m, t_key));
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
     *
     *
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
     * remove annoying constraints on nodes from the circuit from context
     * @param t_circuit
     * @return a cleaned context.
     */
    public OmsddNode cleanup(int[] t_circuit) {
        if (next == null) {
            return this;
        }
        int cst = t_circuit[level];
        if (cst != 0) {
        	OmsddNode fixedNode = next[cst-1].merge(next[cst], CLEANUP);
            return fixedNode.cleanup(t_circuit);
        }
        for (int i=0 ; i<next.length ; i++) {
            next[i] = next[i].cleanup(t_circuit);
        }
        return this;
    }
    
    public StringBuffer write() {
        StringBuffer s = new StringBuffer();
        if (next == null) {
        	switch (value) {
			case 1: s.append('1'); break;
			case -1: s.append('2'); break;
			case 0: s.append('0'); break;
			}
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

    public static OmsddNode read(String s, byte[] childCount) throws ParseException {
    	if (s.length() == 1) {
    		switch (s.charAt(0)) {
			case '1':
				return POSITIVE;
			case '2':
				return NEGATIVE;
			case '0':
				return FALSE;

			default:
				throw new ParseException("Wrong terminal value, expected 1,-1,0, got "+s.charAt(0), 0);
			}
       	} 
    	
    	int length = s.length();
    	int deep = -1;
    	
    	byte[] childVisited = new byte[childCount.length];
    	Stack<OmsddNode> heap = new Stack<OmsddNode>();
    	OmsddNode o = null, op;
    	int i = 0;
    	while (i < length) {
    		char c = s.charAt(i);
    		if (c == '(') {
       			o = new OmsddNode();
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
        		op = (OmsddNode) heap.pop();
        		if (heap.size() > 0) {
            		o = (OmsddNode) heap.peek();
            		if (o.next == null) {
            			o.next = new OmsddNode[childCount[deep]];
            		}       			
            		o.next[childVisited[deep]++] = op;
        		}
    			if (deep == -2) {
    				throw new ParseException("Opening parenthese missing", i);
    			}
    		} else if (c == ',') { 
    		} else {
    			readTerminal(s, (OmsddNode) heap.peek(), i, deep, childVisited, childCount, length);
   		}
    		i++;
    	}
    	if (deep != -1) {
			throw new ParseException("End of string reached too early", i);
    	}  	
    	return o;
    }
    
	private static void readTerminal(String s, OmsddNode o, int i, int deep, byte[] childVisited, byte[] childCount, int length) throws ParseException {
		OmsddNode terminal;
		switch (s.charAt(i)) {
		case '1': terminal = POSITIVE; break;
		case '2': terminal = NEGATIVE; break;
		case '0': terminal = FALSE; break;
		default:
			throw new ParseException("Missing terminal value", i);
		}
		if (o.next == null) {
			o.next = new OmsddNode[childCount[deep]];
		}
		int k = childVisited[deep]++;
		if (k >= childCount[deep]) {
			throw new ParseException("Too much child at depth "+deep+" - "+k+" - "+childCount[deep]+" - "+childVisited[deep], i);
		}
		o.next[k] = terminal;
	}


	/**
     * 
     * @param s the string
     * @param o current OMDDNode to fill with level
     * @param i current index in s
     * @param length the length of s
     * @return the index of the next non number character
     */
    private static int readLevel(String s, OmsddNode o, int i, int length) {
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

}
