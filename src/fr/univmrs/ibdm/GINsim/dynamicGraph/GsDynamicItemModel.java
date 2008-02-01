package fr.univmrs.ibdm.GINsim.dynamicGraph;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.table.AbstractTableModel;

import fr.univmrs.ibdm.GINsim.data.GsDirectedEdge;

/**
 * table model to display a dynamic node or edge.
 */
public class GsDynamicItemModel extends AbstractTableModel {

    private static final long serialVersionUID = 8860415338236400531L;
    private List nodeOrder;
    GsDynamicGraph graph;
    private int[] state;
    private GsDynamicNode[] nextState;
    private JButton[] go2Next;
    private int len;
    
    protected GsDynamicItemModel (GsDynamicGraph graph) {
        this.graph = graph;
        this.nodeOrder = graph.getNodeOrder();
        len = nodeOrder.size()+1;
    }
    
    public int getRowCount() {
        if (state == null) {
            return 0;
        }
        if (nextState == null) {
            return 1;
        }
        return nextState.length+1;
    }

    public int getColumnCount() {
        return len;
    }

    public Class getColumnClass(int columnIndex) {
        if (columnIndex == 0) {
            return JButton.class;
        }
        return super.getColumnClass(columnIndex);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex >= len) {
            return null;
        }
        if (columnIndex == 0) {
            if (rowIndex > 0) {
                return go2Next[rowIndex-1];
            }
            return "";
        }
        if (rowIndex == 0) {
            return ""+state[columnIndex-1];
        }
        if (nextState == null || rowIndex > nextState.length) {
            return null;
        }
        return ""+nextState[rowIndex-1].state[columnIndex-1];
    }

    public String getColumnName(int column) {
        if (column >= len) {
            return null;
        }
        if (column == 0) {
            return "";
        }
        return ""+nodeOrder.get(column-1);
    }

    /**
     * @param obj the edited object
     */
    public void setContent(Object obj) {
        if (obj instanceof GsDynamicNode) {
            List l_next = graph.getGraphManager().getOutgoingEdges(obj);
            if (l_next == null || l_next.size() == 0) {
                state = ((GsDynamicNode)obj).state;
                nextState = null;
            } else {
                nextState = new GsDynamicNode[l_next.size()];
                for (int i=0 ; i<nextState.length ; i++) {
                    nextState[i] = (GsDynamicNode)((GsDirectedEdge)l_next.get(i)).getTargetVertex();
                }
                state = ((GsDynamicNode)obj).state;
            }
        } else if (obj instanceof GsDirectedEdge){
            GsDirectedEdge edge = (GsDirectedEdge)obj;
            state = ((GsDynamicNode)edge.getSourceVertex()).state;
            nextState = new GsDynamicNode[1];
            nextState[0] = (GsDynamicNode)edge.getTargetVertex();
        }
        if (nextState == null) {
            go2Next = null;
        } else {
            go2Next = new JButton[nextState.length];
            for (int i=0 ; i<go2Next.length ; i++) {
                go2Next[i] = new JButton("->");
                go2Next[i].addActionListener(new Go2ActionListener(graph, nextState[i]));
            }
        }
        fireTableDataChanged();
    }
}

class Go2ActionListener implements ActionListener {

    GsDynamicGraph graph;
    GsDynamicNode node;
    
    /**
     * @param graph
     * @param node
     */
    public Go2ActionListener(GsDynamicGraph graph, GsDynamicNode node) {
        this.graph = graph;
        this.node = node;
    }

    public void actionPerformed(ActionEvent e) {
        graph.getGraphManager().select(node);
    }
    
}
