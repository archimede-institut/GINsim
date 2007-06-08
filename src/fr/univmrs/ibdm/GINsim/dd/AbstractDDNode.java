package fr.univmrs.ibdm.GINsim.dd;

import java.util.Map;
import java.util.Vector;

/**
 * common parts for all decision diagramms
 * TODO: share code between DD
 * TODO: add order choose/apply
 */
abstract public class AbstractDDNode {

    /** level of this node (only for non-terminal nodes) */
    public int level;
    /** leaves reachable from this one (only for non-terminal nodes, null otherwise) */
    public AbstractDDNode[] next;
    /** value of the terminal node */
    public short value;
    /** cache for the key, should always be null except while reducing */
    String key = null;
    
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
        return next[status[level]].testStatus(status);
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
    public AbstractDDNode merge(AbstractDDNode other, int op) {
        int [] tkey = {0};
        return merge(other, op, null, tkey);
    }
    
    private AbstractDDNode merge(AbstractDDNode other, int op, Map m, int[] t_key) {
        AbstractDDNode ret;
        if (next == null) {
        }
        if (other.next == null) {
        }
        
        if (level == other.level) { // merge all childs together
            if (next.length != other.next.length) {
                return null;
            }
            ret = newNode();
            ret.level = level;
            ret.next = new AbstractDDNode[next.length];
            for (int i=0 ; i<next.length ; i++) {
                ret.next[i] = next[i].merge(other.next[i], op, m, t_key);
            }
            return ret;
        } else if (level < other.level) {
            ret = newNode();
            ret.level = level;
            ret.next = new AbstractDDNode[next.length];
            for (int i=0 ; i<next.length ; i++) {
                ret.next[i] = next[i].merge(other, op, m, t_key);
            }
            return ret;
        } else {
            ret = newNode();
            ret.level = other.level;
            ret.next = new AbstractDDNode[other.next.length];
            for (int i=0 ; i<other.next.length ; i++) {
                ret.next[i] = other.next[i].merge(this, op, m, t_key);
            }
            return ret;
        }
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
    public AbstractDDNode reduce() {
        int[] t_key = {0};
        Map m = newMap();

        AbstractDDNode reduced = (AbstractDDNode)m.get(getKey(m, t_key));
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
                AbstractDDNode nnext = (AbstractDDNode)m.get(next[i].getKey(m, t_key)); 
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
     * create a new Node of the right class
     * @return a new node
     */
    abstract protected AbstractDDNode newNode();
    /**
     * create a new Map for reduce, add terminal nodes to it
     * @return the new initialized Map
     */
    abstract protected Map newMap();
}
