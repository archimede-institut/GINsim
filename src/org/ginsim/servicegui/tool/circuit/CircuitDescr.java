package org.ginsim.servicegui.tool.circuit;
import java.util.List;
import java.util.Vector;

import org.ginsim.core.graph.regulatorygraph.RegulatoryEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.gui.resource.Translator;


public class CircuitDescr {
    /** vertices in this circuit */
    public RegulatoryNode[] t_vertex;
    /** GsRegulatoryMultiEdges in this circuit */
    public RegulatoryMultiEdge[] t_me;

    protected static final int FALSE = 0;
    protected static final int ALL = 1;
    protected static final int FUNCTIONNAL = 2;
    protected static final int POSITIVE = 3;
    protected static final int NEGATIVE = 4;
    protected static final int DUAL = 5;
    
    public static final String[] SIGN_NAME = {
    	Translator.getString("STR_not-functional"),
    	Translator.getString("STR_all"),
    	Translator.getString("STR_functional"),
    	Translator.getString("STR_positive"), 
    	Translator.getString("STR_negative"),
    	Translator.getString("STR_dual")};
    
    // data on all subcircuits
    protected OmsddNode[] t_context;
    protected long[][] t_mark;
    protected int[][] t_sub;
    
    // which sub circuits go in which category ?
    Vector v_positive = null;
    Vector v_negative = null;
    Vector v_dual = null;
    Vector v_all = new Vector();
    Vector v_functionnal = null;
    
    // to iterate through "subcircuits"
    private int[] t_pos;
    private int[] t_posMax;

    long score;
    int sign;

    /**
     * print the circuit in a nice order.
     * 
     * @param nodeOrder
     * @return the tree of members of the circuit
     */
    public String printMe(Vector nodeOrder) {
        int min = nodeOrder.indexOf(t_vertex[0]);
        int minIndex = 0;
        for (int i = 0; i < t_vertex.length; i++) {
            int tmp = nodeOrder.indexOf(t_vertex[i]);
            if (tmp < min) {
                min = tmp;
                minIndex = i;
            }
        }

        String s = "";
        for (int i = minIndex; i < t_vertex.length; i++) {
            s += "" + (nodeOrder.indexOf(t_vertex[i]) + 1);
        }
        for (int i = 0; i < minIndex; i++) {
            s += "" + (nodeOrder.indexOf(t_vertex[i]) + 1);
        }
        return s;
    }

    /**
     * check if this circuit is functional.
     * 
     * @param algo
     * @param nodeOrder 
     * 
     * @return true if the circuit is functional
     */
    public boolean check(CircuitAlgo algo, List nodeOrder) {
        t_pos = new int[t_me.length];
        t_posMax = new int[t_me.length];
        int nbSub = 1;
        for (int i = 0; i < t_pos.length; i++) {
            t_posMax[i] = t_me[i].getEdgeCount() - 1;
            nbSub *= t_posMax[i]+1;
            t_pos[i] = 0;
        }

        int[] t_circuit = new int[nodeOrder.size()]; // filled with "0"
        for (int i=0 ; i< t_me.length ; i++) {
            t_circuit[ nodeOrder.indexOf(t_me[i].getSource()) ] = t_me[i].getMin(0);
        }
        RegulatoryEdge edge, next_edge;
        boolean goon;
        int sub = 0;
        t_context = new OmsddNode[nbSub];
        t_mark = new long[nbSub][];
        t_sub = new int[nbSub][];
        do {
            OmsddNode context = OmsddNode.POSITIVE;
            edge = t_me[t_me.length - 1].getEdge(t_pos[t_pos.length - 1]);
            for (int i = 0; i < t_me.length; i++) {
            	next_edge = t_me[i].getEdge(t_pos[i]);
                OmsddNode node = algo.checkEdge(edge, t_circuit,
                        next_edge.getMin(), next_edge.getMax());
                edge = next_edge;
                context = context.merge(node, OmsddNode.AND);
            }

            CircuitDescrInTree cdtree = new CircuitDescrInTree(this, false, sub);
            v_all.add(cdtree);
            if (algo.do_cleanup) {
            	t_context[sub] = context.cleanup(t_circuit).reduce();
            } else {
            	t_context[sub] = context.reduce();
            }
            t_mark[sub] = algo.score(t_context[sub]);
            t_sub[sub] = (int[])t_pos.clone();
            switch ((int)t_mark[sub][1]) {
                case POSITIVE:
                    if (v_positive == null) {
                        v_positive = new Vector();
                    }
                    v_positive.add(cdtree);
                    if (v_functionnal == null) {
                        v_functionnal = new Vector();
                    }
                    v_functionnal.add(cdtree);
                    break;
                case NEGATIVE:
                    if (v_negative == null) {
                        v_negative = new Vector();
                    }
                    v_negative.add(cdtree);
                    if (v_functionnal == null) {
                        v_functionnal = new Vector();
                    }
                    v_functionnal.add(cdtree);
                    break;
                case DUAL:
                    if (v_dual == null) {
                        v_dual = new Vector();
                    }
                    v_dual.add(cdtree);
                    if (v_functionnal == null) {
                        v_functionnal = new Vector();
                    }
                    v_functionnal.add(cdtree);
                    break;
            }
            if (t_mark[sub][0] > score) {
                score = t_mark[sub][0];
            }

            // find next subcircuit
            goon = false;
            for (int i = t_pos.length - 1; i >= 0; i--) {
                if (t_pos[i] == t_posMax[i]) {
                    t_pos[i] = 0;
                    t_circuit[ nodeOrder.indexOf(t_me[i].getSource()) ] = t_me[i].getMin(0);
                } else {
                    t_pos[i]++;
                    t_circuit[ nodeOrder.indexOf(t_me[i].getSource()) ] = t_me[i].getMin(t_pos[i]);
                    goon = true;
                    break;
                }
            }
            sub++;
        } while (goon);
        
        return v_positive != null || v_negative != null || v_dual != null;
    }

    protected int getChildCount(int key) {
        switch (key) {
            case ALL:
                break;
            case POSITIVE:
                break;
            case NEGATIVE:
                break;
        }
        return 0;
    }

	public void clear() {
		if (v_all != null) {
			v_all.clear();
		}
    	if (v_functionnal != null) {
    		v_functionnal.clear();
    		v_functionnal = null;
    	}
    	if (v_positive != null) {
    		v_positive.clear();
    		v_positive = null;
    	}
    	if (v_negative != null) {
    		v_negative.clear();
    		v_negative = null;
    	}
    	if (v_dual != null) {
    		v_dual.clear();
    		v_dual = null;
    	}
	}

	public OmsddNode[] getContext() {
		return t_context;
	}

	public void setContext(OmsddNode[] tContext) {
		t_context = tContext;
	}
	
	
}
