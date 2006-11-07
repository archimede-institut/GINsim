package fr.univmrs.ibdm.GINsim.circuit;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsEdgeIndex;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsLogicalParameter;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.OmddNode;

/**
 * analyse circuits to detect their functionnality/functionnal range.
 * 
 * <p>on circuits:
 * <ul>
 *  <li>a circuit is non-functionnal if any of it's edge is inactive</li>
 *  <li>a circuit is negative if it contains 2k+1 negative interactions</li>
 * </ul>
 * 
 * <p>on interactions:
 * <ul>
 *  <li>an interaction is non-functionnal if the main range is non-functionnal</li>
 *  <li>an interaction is negative if the negative range is wider than the positive one</li>
 * </ul>
 * 
 * <p>about interaction's contexts:
 * <ul>
 *  <li>a context is non-functionnal if in this context, the activation of the interaction
 *      (considering min and max of it's activation range)
 *      has no effect on the activity of the target interaction</li>
 *  <li>a context is functionnal, positive (resp negative) if in this context, the activation of the interaction
 *      triggers (resp inhibits) the activation of the target interaction<li>
 * </ul>
 */
public class GsCircuitAlgo {

	private static final boolean testOutLimit = false;
	
    private Map m_report = new HashMap();
    private short[][] t_constraint;
    
    // some context data
    Vector v_interactions;
    GsRegulatoryVertex target;    
    GsRegulatoryMultiEdge me;
    GsLogicalParameter gsi;
    GsRegulatoryGraph graph;
    
    /**
     * @param graph the studied graph
     * @param t_constraint constraints on the nodes
     */
    public GsCircuitAlgo(GsRegulatoryGraph graph, short[][] t_constraint) {
        this.graph = graph;
        this.t_constraint = t_constraint;
    }
    
    /**
     * @param ei the edge to test
     * @param t_circuit members of the circuit
     * @param nextmin 
     * @param nextmax 
     * 
     * @return the functionnal context for this edge
     */
    public OmsddNode checkEdge(GsEdgeIndex ei, int[] t_circuit, int nextmin, int nextmax) {
        me = ei.data;
        // first look for a previous test on this interaction with the same constraints
        Vector[] t_report = (Vector[])m_report.get(me);
        if (!testOutLimit || nextmax == -1) {
            nextmax = me.getTarget().getMaxValue();
        }
        if (t_report != null) {
            if (t_report[ei.index] != null) {
                Vector v_lr = t_report[ei.index];
                for (int i=0 ; i<v_lr.size() ; i++) {
                	subReport sr = (subReport)v_lr.get(i);
                	if (sr.min == nextmin && sr.max == nextmax) {
                		return sr.node;
                	}
                }
                // this edge had previously been tested but for different parameters
                // the next step will add a relevant test
            } else {
                // some tests did exist on the same multiedge, but not exactly the right "subedge"
                t_report[ei.index] = new Vector();
            }
        } else { // no report existed on this multiedge, create a new one
            t_report = new Vector[me.getEdgeCount()];
            t_report[ei.index] = new Vector();
            m_report.put(me, t_report);
        }
        
        GsRegulatoryVertex source = me.getSource();
        target = me.getTarget();
        v_interactions = target.getV_logicalParameters();

        short min = me.getMin(ei.index);
        short max = me.getMax(ei.index);
        short smax = source.getMaxValue();
        if (max == -1) max = smax;
        
        // t_lc = where are local contexts, its length depends on the context:
        //   * 2 if only the down limit to test ==> under, inside
        //   * 3 if min == max  ==> under, inside, out
        //   * 4 if min != max  ==> under, inside bottom, inside top, out
        short[] t_lc;
        if (max < smax) {
            if (max > min) {
                t_lc = new short[4];
                t_lc[2] = max;
                t_lc[3] = (short) (max+1);
            } else {
                t_lc = new short[3];
                t_lc[2] = (short) (max+1);
            }
        } else {
            t_lc = new short[2];
        }
        t_lc[0] = (short) (min-1);
        t_lc[1] = min;

        Vector[] t_ei = new Vector[t_lc.length];
        for (int i=0 ; i<t_lc.length ; i++) {
            Vector v = new Vector();
            int val = t_lc[i];
            for (int j=0 ; j<me.getEdgeCount() ; j++) {
                if (val >= me.getMin(j) && (me.getMax(j) == -1 || val <= me.getMax(j))) {
                    v.add(new GsEdgeIndex(me, j));
                }
            }
            t_ei[i] = v;
        }
        if (t_ei.length == 4) {
            // if both inside refers to the same parameters, remove the unnecessary one
            if (t_ei[1].size() == t_ei[2].size() && t_ei[1].containsAll(t_ei[2])) {
                t_ei = new Vector[] { t_ei[0], t_ei[1], t_ei[3]};
            }
            // TODO: what if both inside/outside are inverted ? (==> action will also be inverted)
            // Has all this still a meaning now ??
        }
        if (t_ei.length == 3) {
            // if both outside refers to the same parameters, remove the unnecessary one
            if (t_ei[0].size() == t_ei[2].size() && t_ei[0].containsAll(t_ei[2])) {
                t_ei = new Vector[] { t_ei[0], t_ei[1]};
            }
        }
        
        // get the context tree
        OmsddNode node = getContextFromParameters(target.getTreeParameters(graph), graph.getNodeOrder().indexOf(source), min, t_circuit, nextmin, nextmax);
        node = checkConstraint(node).reduce();
        // cache the result
        subReport sr = new subReport();
        sr.min = nextmin;
        sr.max = nextmax;
        sr.node = node;
        t_report[ei.index].add(sr);
        return node;
    }

    /**
     * build the context of functionnality from the regulation tree.
     * In this context, we have not yet meet the considered gene.
     * 
     *  - take as input the tree view of logical parameters.
     *  - build from it the "activity" tree of this node in this circuit by finding branches 
     *    that differ only by the current node.
     *    
     * @param node
     * @param level level of the considered gene
     * @param thresold thresold of activity for the considered gene
     * @param t_circuit 
     * @param nextmin 
     * @param nextmax 
     * @return the context of functionnality
     */
    private OmsddNode getContextFromParameters(OmddNode node, int level, int thresold, int[] t_circuit, int nextmin, int nextmax) {
        if (node.next == null || node.level > level) { // no meeting: not functionnal
            return OmsddNode.FALSE;
        }
        if (node.level < level ) { // may still meet it later
            OmsddNode ret = new OmsddNode();
            ret.level = node.level;
            ret.next = new OmsddNode[node.next.length];
            for (int i=0 ; i<ret.next.length ; i++) {
                ret.next[i] = getContextFromParameters(node.next[i], level, thresold, t_circuit, nextmin, nextmax);
            }
            return ret;
        }
        // now level == node.level.
        return getContextFromParameters(node.next[thresold-1], node.next[thresold], t_circuit, nextmin, nextmax);
    }

    /**
     * build the context of functionnality from the regulation tree.
     * In this context, we are analysing two parallel branches with the considered node being 
     * inactif in the first one and actif in the second.
     * 
     *  - take as input the tree view of logical parameters.
     *  - build from it the "activity" tree of this node in this circuit by finding branches 
     *    that differ only by the current node.
     *    
     * @param node
     * @param next
     * @param t_circuit 
     * @param nextmin 
     * @param nextmax 
     * @return the context of functionnality
     */
    private OmsddNode getContextFromParameters(OmddNode node, OmddNode next, int[] t_circuit, int nextmin, int nextmax) {
        
        if (node.next == null) {
            if (next.next == null) {
                // the real end: choose the sign.
                // activate next edge = positive, desactivate it = negative.
            	if (testOutLimit) {
	                if (node.value < nextmin || node.value > nextmax) {
	                    if (next.value >= nextmin && next.value <= nextmax) {
	                        return OmsddNode.POSITIVE;
	                    }
	                    return OmsddNode.FALSE;
	                }
	                if (next.value < nextmin || next.value > nextmax) {
	                    return OmsddNode.NEGATIVE;
	                }
            	} else {
	                if (node.value < nextmin) {
	                    if (next.value >= nextmin) {
	                        return OmsddNode.POSITIVE;
	                    }
	                    return OmsddNode.FALSE;
	                }
	                if (next.value < nextmin) {
	                    return OmsddNode.NEGATIVE;
	                }
            	}
                return OmsddNode.FALSE;
            }
        }

        if (node.next == null || node.level < next.level) {
            OmsddNode ret = new OmsddNode();
            ret.level = next.level;
            ret.next = new OmsddNode[next.next.length];
            for (int i=0 ; i<ret.next.length ; i++) {
                ret.next[i] = getContextFromParameters(node, next.next[i], t_circuit, nextmin, nextmax);
            }
            return ret;
        }
        if (next.next == null || node.level > next.level) {
            OmsddNode ret = new OmsddNode();
            ret.level = node.level;
            ret.next = new OmsddNode[node.next.length];
            for (int i=0 ; i<ret.next.length ; i++) {
                ret.next[i] = getContextFromParameters(node.next[i], next, t_circuit, nextmin, nextmax);
            }
            return ret;
        }
        
        // both are non-terminal of the same level
        OmsddNode ret = new OmsddNode();
        ret.level = next.level;
        ret.next = new OmsddNode[next.next.length];
        for (int i=0 ; i<ret.next.length ; i++) {
            ret.next[i] = getContextFromParameters(node.next[i], next.next[i], t_circuit, nextmin, nextmax);
        }
        return ret;
    }
    
    /**
     * compute a score for the context.
     * lower is better (score increases with length of branches)
     * return: [score, sign]
     * sign can be: 0, 1, 2 or -1
     * 
     * @param node
     * @return a score along with a sign indication
     */
    public int[] score (OmsddNode node) {
        if (node.next == null) {
            if (node == OmsddNode.FALSE) {
                return new int[] {10, GsCircuitDescr.FALSE};
            }
            if (node == OmsddNode.POSITIVE) {
                return new int[] {10, GsCircuitDescr.POSITIVE};
            }
            return new int[] {10, GsCircuitDescr.NEGATIVE};
        }
        int[] ret = score(node.next[0]);;
        for (int i=1 ; i<node.next.length ; i++) {
            int[] o = score(node.next[i]);
            ret[0] += o[0];
            switch (ret[1]) {
                case GsCircuitDescr.FALSE:
                    ret[1] = o[1];
                    break;
                case GsCircuitDescr.DUAL:
                    break;
                case GsCircuitDescr.NEGATIVE:
                case GsCircuitDescr.POSITIVE:
                    if (o[1] == GsCircuitDescr.DUAL || (o[1] != GsCircuitDescr.FALSE && o[1] != ret[1])) {
                        ret[1] = GsCircuitDescr.DUAL;
                    }
            }
        }
        ret[0] = 10 + ret[0]/node.next.length;
        return ret;
    }
    
    /**
     * *very* quickly added constraint on all genes.
     * 
     * @param node
     * @return the new node
     */
    private OmsddNode checkConstraint(OmsddNode node) {
        if (t_constraint == null || node.next == null) {
            return node;
        }
        for (int i=0 ; i<t_constraint[node.level][0] ; i++) {
            node.next[i] = OmsddNode.FALSE;
        }
        for (int i=t_constraint[node.level][0] ; i<=t_constraint[node.level][1] ; i++) {
            node.next[i] = checkConstraint(node.next[i]);
        }
        for (int i=t_constraint[node.level][1]+1 ; i<node.next.length ; i++) {
            node.next[i] = OmsddNode.FALSE;
        }
        return node;
    }
    
    private class subReport {
    	int min;
    	int max;
    	OmsddNode node;
    }
    
}
