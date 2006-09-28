package fr.univmrs.ibdm.GINsim.export;

import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.manageressources.Translator;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.OmddNode;

/**
 * Export a regulatory graph to petri net (shared methods).
 *
 *<p> translating a regulatory graph to a petri net is done as follow:
 * <ul>
 *  <li>each node will be represented by two places, a negative one and a positive one.
 *      Markers in the positive place represent it's level. if it is not at it's maximum
 *      missing marker(s) will be in it's negative place: the number of markers in the petri net is constant</li>
 *  <li>each logical parameter will be represented by transition(s) with "test" arcs to
 *      non-modified places and "normal" arcs to the positive and negative place of the modified place.</li>
 * </ul>
 * 
 * with some simplifications:
 * <ul>
 *  <li>work on the tree representation of logical parameters and use ranges instead of exact values as precondition of transitions</li>
 *  <li>"input" nodes are specials: no transition will affect them and their basal value will be used as initial markup</li>
 *  <li>autoregulation can trigger some cases where a transition can't be fired, these are not created</li>
 * </ul>
 * 
 *<p>references:
 *<ul> 
 *  <li>Simao, E., Remy, E., Thieffry, D. and Chaouiya, C.: Qualitative modelling of
 *      Regulated Metabolic Pathways: Application to the Tryptophan biosynthesis in E. Coli.</li>
 *  <li>Chaouiya, C., Remy, E. and Thieffry, D.: Petri Net Modelling of Biological Regulatory
 *      Networks</li>
 *</ul>     
 */
public class GsPetriNetExport {
    /**
     * extract transitions from a tree view of logical parameters.
     * 
     * @param v_result
     * @param node tree view of logical parameters on one node
     * @param nodeIndex index of the considered node (in the regulatory graph)
     * @param v_node all nodes
     * @param len number of nodes in the original graph
     */
    protected static void browse(Vector v_result, OmddNode node, int nodeIndex, Vector v_node, int len) {
        if (node.next == null) {
            TransitionData td = new TransitionData();
            td.value = node.value;
            td.nodeIndex = nodeIndex;
            td.t_cst = null;
            v_result.add(td);
        } else {
            int[][] t_cst = new int[len][3];
            for (int i=0 ; i<t_cst.length ; i++) {
                t_cst[i][0] = -1;
            }
            browse(v_result, t_cst, 0, node, nodeIndex, v_node);
        }
    }
    
    private static void browse(Vector v_result, int[][] t_cst, int level, OmddNode node, int nodeIndex, Vector v_node) {
        if (node.next == null) {
            TransitionData td = new TransitionData();
            td.value = node.value;
            td.maxValue = ((GsRegulatoryVertex)v_node.get(nodeIndex)).getMaxValue();
            td.nodeIndex = nodeIndex;
            td.t_cst = new int[t_cst.length][3];
            int ti = 0;
            for (int i=0 ; i<t_cst.length ; i++) {
                int index = t_cst[i][0];
                if (index == -1) {
                    break;
                }
                if (index == nodeIndex) {
                    td.minValue = t_cst[i][1];
                    td.maxValue = t_cst[i][2];
                } else {
                    td.t_cst[ti][0] = index;
                    td.t_cst[ti][1] = t_cst[i][1];
                    td.t_cst[ti][2] = ((GsRegulatoryVertex)v_node.get(index)).getMaxValue() - t_cst[i][2];
                    if (td.t_cst[ti][1] > 0 || td.t_cst[ti][2] > 0) {
                        ti++;
                    }
                }
            }
            if (ti == 0) {
                td.t_cst = null;
            } else {
                td.t_cst[ti][0] = -1;
            }
            v_result.add(td);
            return;
        }
        
        // specify on which node constraints are added
        t_cst[level][0] = node.level;
        for (int i=0 ; i<node.next.length ; i++) {
            OmddNode next = node.next[i];
            int j=i+1;
            while(j<node.next.length) {
                if (node.next[j] == next) {
                    j++;
                } else {
                    break;
                }
            }
            j--;
            t_cst[level][1] = i;
            t_cst[level][2] = j;
            browse(v_result, t_cst, level+1, next, nodeIndex, v_node);
            i = j;
        }
        // "forget" added constraints
        t_cst[level][0] = -1;
    }
    
    protected static boolean prepareExport(GsGraph graph, short[][] t_markup, Vector[] t_transition, OmddNode[] t_tree, Vector v_no) {
        int len = v_no.size();
        for (int i=0 ; i<len ; i++) {
            OmddNode node = t_tree[i];
            GsRegulatoryVertex vertex = ((GsRegulatoryVertex)v_no.get(i));
            if (graph.getGraphManager().getIncomingEdges(vertex).size() == 0) {
                // input node: no transition, use basal value as initial markup
                t_markup[i][0] = vertex.getBaseValue();
                t_markup[i][1] = (short)(vertex.getMaxValue() - vertex.getBaseValue());
            } else {
                // normal node, initial markup = 0
                t_markup[i][0] = 0;
                t_markup[i][1] = vertex.getMaxValue();
                Vector v_transition = new Vector();
                t_transition[i] = v_transition;
                GsPetriNetExport.browse(v_transition, node, i, v_no, len);
            }
        }
        int ret = JOptionPane.showConfirmDialog(null, new PNExportConfigPanel(graph.getNodeOrder(), t_markup), "initial state", JOptionPane.OK_CANCEL_OPTION);
        return ret == JOptionPane.OK_OPTION;
    }
}

class TransitionData {
    /** target value of this transition */
    public int value;

    /** index of the concerned node */
    public int nodeIndex;
    
    /** minvalue for the concerned node (0 unless an autoregulation is present) */
    public int minValue;
    /** maxvalue for the concerned node (same as node's maxvalue unless an autoregulation is present) */
    public int maxValue;
    
    /** constraints of this transition: each row express range constraint for one of the nodes
     * and contains 3 values:
     *  <ul>
     *      <li>index of the node (or -1 after the last constraint)</li>
     *      <li>bottom and top limit of the range (top limit is pre-processed: maxvalue - realLimit)</li>
     *  </ul>
     */
    public int[][] t_cst;
}

class PNExportConfigPanel extends JPanel {
    private static final long serialVersionUID = 9043565812912568136L;

    private PNmarkupModel model;
    
    protected PNExportConfigPanel (Vector nodeOrder, short[][] t_markup) {
        model = new PNmarkupModel(nodeOrder, t_markup);
        initialize();
    }
    
    private void initialize() {
        JScrollPane spane = new JScrollPane();
        spane.setViewportView(new JTable(model));
        this.add(spane);
        setSize(100, 250);
    }
}

class PNmarkupModel extends DefaultTableModel {
    private static final long serialVersionUID = -4867567086739357065L;

    private Vector nodeOrder;
    private short[][] t_markup;
    
    protected PNmarkupModel (Vector nodeorder, short[][] t_markup) {
        this.nodeOrder = nodeorder;
        this.t_markup = t_markup;
    }

    public int getColumnCount() {
        return 2;
    }

    public int getRowCount() {
        if (nodeOrder == null) {
            return 0;
        }
        return nodeOrder.size();
    }

    public Object getValueAt(int row, int column) {
        switch (column) {
            case 0:
                return nodeOrder.get(row);
            case 1:
                return ""+t_markup[row][0];
        }
        return null;
    }

    public boolean isCellEditable(int row, int column) {
        return column == 1;
    }
    
    public String getColumnName(int column) {
        switch (column) {
            case 0: return Translator.getString("STR_name");
            case 1: return Translator.getString("STR_value");
        }
        return super.getColumnName(column);
    }

    public void setValueAt(Object aValue, int row, int column) {
        if (column != 1) {
            return;
        }
        int val = Integer.parseInt((String)aValue);
        int d = val - t_markup[row][0];
        if (val < 0 || d > t_markup[row][1]) {
            return;
        }
        t_markup[row][0] += d;
        t_markup[row][1] -= d;
        fireTableCellUpdated(row, column);
    }
}