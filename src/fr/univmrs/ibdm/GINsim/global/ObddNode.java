package fr.univmrs.ibdm.GINsim.global;
import java.util.HashMap;
import java.util.Map;

/**
 * Ordered Binary Decision Diagram (OBDD) is a tree representation of boolean functions.
 * This is based on this representation but adapted to non-boolean functions:
 *   - non-terminal nodes can have more than two childs (actually they have an array of childs)
 *   - terminal nodes will be integer, not boolean (not yet sure about side effects)
 *
 *
 */
public class ObddNode {

    /** level of this node (only for non-terminal nodes) */
    int level;
    /** leaves reacheable from this one (only for non-terminal nodes, null otherwise) */
    ObddNode[] next;
    /** value of the terminal node */
    boolean value;
    /** cache for the key, should always be null except while reducing */
    String key = null;
    
    // create once for all the terminals nodes.
    static final ObddNode TRUE;
    static final ObddNode FALSE;
    
    static final int OR = 0;
    static final int AND = 1;
    
    static {
        TRUE = new ObddNode();
        TRUE.next = null;
        TRUE.value = true;
        
        FALSE = new ObddNode();
        FALSE.next = null;
        FALSE.value = false;
    }
    
    /**
     * test if a state is OK.
     * 
     * @param status
     * @return true if the state is OK.
     */
    public boolean testStatus (int[] status) {
        
        if (next == null) {
            return value;
        }
        if (status == null || status.length < level) {
            return false;
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
    public ObddNode merge(ObddNode other, int op) {
        Map m = new HashMap();
        return merge(other, op, m);
    }
    
    private ObddNode merge(ObddNode other, int op, Map m) {
        if (next == null) {
            switch (op) {
                case OR:
                    return value ? this : (ObddNode)other.clone();
                case AND:
                    return value ? (ObddNode)other.clone(): this;
            }
        }
        if (other.next == null) {
            switch (op) {
                case OR:
                    return other.value ? other : (ObddNode)clone();
                case AND:
                    return other.value ? (ObddNode)clone(): other;
            }
        }
        
        // FIXME  cache previous results :)
        if (key != null && other.key != null) {
            Object o = m.get(key+"_"+other.key);
            if (o != null) {
                return (ObddNode)o;
            }
            o = m.get(other.key+"_"+key);
            if (o != null) {
                return (ObddNode)o;
            }
        } else {
            if (key == null) {
                key = "";
            }
            if (other.key == null) {
                other.key = "";
            }
        }
        if (level == other.level) { // merge all childs together
            if (next.length != other.next.length) {
                return null;
            }
            ObddNode ret = new ObddNode();
            ret.level = level;
            ret.next = new ObddNode[next.length];
            for (int i=0 ; i<next.length ; i++) {
                ret.next[i] = next[i].merge(other.next[i], op);
            }
            return ret;
        } else if (level < other.level) {
            ObddNode ret = new ObddNode();
            ret.level = level;
            ret.next = new ObddNode[next.length];
            for (int i=0 ; i<next.length ; i++) {
                ret.next[i] = next[i].merge(other, op);
            }
            return ret;
        } else {
            ObddNode ret = new ObddNode();
            ret.level = other.level;
            ret.next = new ObddNode[other.next.length];
            for (int i=0 ; i<other.next.length ; i++) {
                ret.next[i] = other.next[i].merge(this, op);
            }
            return ret;
        }
    }
    
    /**
     * @return a copy of this ObddLeave
     */
    public Object clone() {
        // if this is a terminal node, this is _NOT_ clonable :)
        if (next == null) {
            return this;
        }
        
        ObddNode copy = new ObddNode();
        copy.level = level;
        copy.next = new ObddNode[next.length];
        for (int i=0 ; i<next.length ; i++) {
            if (next[i] != null) {
                copy.next[i] = (ObddNode)next[i].clone();
            }
        }
        
        return copy;
    }
    
    /**
     * add a path to this tree.
     * WIP...
     * 
     * not sure if it is a good idea: merge function should do it already, and doesn't cost so much...
     * 
     * @param path
     * @param index
     * @param value
     * 
     * @return the updated tree (root might have change)
     */
    public ObddNode add2tree(int[][] path, int index, int value) {
        if (path[index][0] > level) {
            ObddNode ret = new ObddNode();
            ret.next = new ObddNode[path[index][1]];
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
     * reduce this tree
     * will use the getKey() function, and will probably need two traversals of the tree,
     * or is it doable in one only? (the second one is on the reduced tree and used to clean up cache)
     * 
     * use a hashmap or two to store node's keys
     * 
     * @return the reduced tree
     */
    public ObddNode reduce() {
        int[] t_key = {2};
        Map m = new HashMap();
        m.put("0", FALSE);
        m.put("1", TRUE);

        ObddNode reduced = (ObddNode)m.get(getKey(m, t_key));
        reduced.cleanKey();
        return reduced;
    }
    
    /**
     * get a uniq key (imply getting the same as an existing similar subtree).
     * 
     * quite broken for now.. :/
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
     *   special case: terminals nodes will just return "0" or "1"
     *      
     * @param m the hashmap to store temporary (long) and uniq (shorter) keys
     * @param t_key int[1]: value of the next uniq key
     * @return the uniq key of this node
     */
    private String getKey(Map m, int[] t_key) {
        if (key != null) {
            return key;
        }
        if (next == null) {
            key = value ? "1":"0";
        } else {
            String skey = next[0].getKey(m, t_key);
            String tempKey = level+"("+skey;
            for (int i=1 ; i<next.length;i++) {
                String skey2 = next[i].getKey(m, t_key);
                if (skey != null && !skey.equals(skey2)) {
                    skey = null;
                }
                tempKey += ","+skey2;
            }
            tempKey += ")";

            if (skey != null) {
                return skey;
            }
            
            this.key = (String)m.get(tempKey);
            if (this.key == null) {
                // replace subtrees
                for (int i=0 ; i<next.length ; i++) {
                    next[i] = (ObddNode)m.get(next[i].getKey(m, t_key));
                }

                this.key = ""+t_key[0]++;
                m.put(tempKey, this.key);
                m.put(this.key, this);
            } else {
            }
        }
        return key;
    }
    
    private void cleanKey() {
        // don't clean terminal nodes or already cleaned trees
        if (next != null && key != null) {
            for (int i=0 ; i<next.length ; i++) {
                next[i].cleanKey();
            }
            key = null;
        }
    }
    
    /**
     * test OBDD
     * 
     * @param args unused
     */
    public static void main(String[] args) {
        
        ObddNode node3_1 = new ObddNode();
        node3_1.next = new ObddNode[2];
        node3_1.level = 3;
        node3_1.next[0] = FALSE;
        node3_1.next[1] = TRUE;
        
        ObddNode node3_2 = new ObddNode();
        node3_2.next = new ObddNode[2];
        node3_2.level = 3;
        node3_2.next[0] = TRUE;
        node3_2.next[1] = FALSE;
        
        ObddNode node = new ObddNode();
        node.level = 1;
        node.next = new ObddNode[2];
        node.next[0] = node3_1;
        node.next[1] = node3_2;
        
        
        ObddNode node2 = new ObddNode();
        node2.level = 2;
        node2.next = new ObddNode[2];
        node2.next[0] = TRUE;
        node2.next[1] = FALSE;
        
        ObddNode node1 = new ObddNode();
        node1.level = 1;
        node1.next = new ObddNode[2];
        node1.next[0] = node2;
        node1.next[1] = node2;

        System.out.println("  w/cp   : "+node.merge(node1, AND));
    }
}
