package fr.univmrs.tagc.GINsim.dynamicGraph;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.table.AbstractTableModel;

import fr.univmrs.tagc.GINsim.data.GsDirectedEdge;
import fr.univmrs.tagc.GINsim.graph.GsGraphManager;

/**
 * table model to display a dynamic node or edge.
 */
public class GsDynamicItemModel extends AbstractTableModel {

    private static final long serialVersionUID = 8860415338236400531L;
    private List nodeOrder;
    GsDynamicGraph graph;
    GsGraphManager graphManager;
    private short[] state;
    private GsDynamicNode[] nextState;
    private GsDynamicNode[] prevState;
    private JButton[] go2Next;
    private int len;
    private int nbNext;
    private int nbRelated;
    
    protected GsDynamicItemModel (GsDynamicGraph graph) {
        this.graph = graph;
        this.graphManager = graph.getGraphManager();
        this.nodeOrder = graph.getNodeOrder();
        len = nodeOrder.size()+1;
    }
    
    public int getRowCount() {
        if (state == null) {
            return 0;
        }
        return nbRelated+1;
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
        if (rowIndex > nbNext) {
        	int r = rowIndex - nbNext;
        	if (prevState == null || r > prevState.length) {
        		return null;
        	}
        	return ""+prevState[r-1].state[columnIndex-1];
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
    	nbRelated = 0;
        if (obj instanceof GsDynamicNode) {
            state = ((GsDynamicNode)obj).state;
            nextState = getRelatedNodes(graphManager.getOutgoingEdges(obj), true);
            prevState = getRelatedNodes(graphManager.getIncomingEdges(obj), false);
            nbNext = nextState != null ? nextState.length : 0;
            nbRelated = nbNext + ( prevState != null ? prevState.length : 0 );
        } else if (obj instanceof GsDirectedEdge){
            GsDirectedEdge edge = (GsDirectedEdge)obj;
            state = ((GsDynamicNode)edge.getSourceVertex()).state;
            nextState = new GsDynamicNode[1];
            nextState[0] = (GsDynamicNode)edge.getTargetVertex();
            prevState = null;
            nbNext = nextState != null ? nextState.length : 0;
            nbRelated = nbNext;
        }
        if (nbRelated == 0) {
            go2Next = null;
        } else {
            go2Next = new JButton[nbRelated];
            for (int i=0 ; i<nbNext ; i++) {
                go2Next[i] = new JButton("->");
                go2Next[i].addActionListener(new Go2ActionListener(graph, nextState[i]));
            }
            if (prevState != null) {
	            for (int i=0 ; i<prevState.length ; i++) {
	                go2Next[nbNext+i] = new JButton("<-");
	                go2Next[nbNext+i].addActionListener(new Go2ActionListener(graph, prevState[i]));
	            }
            }
        }
        fireTableDataChanged();
    }
    
    private GsDynamicNode[] getRelatedNodes(List l_related, boolean target) {
        if (l_related == null || l_related.size() == 0) {
            return null;
        }
        GsDynamicNode[] ret = new GsDynamicNode[l_related.size()];
        for (int i=0 ; i<ret.length ; i++) {
        	if (target) {
        		ret[i] = (GsDynamicNode)((GsDirectedEdge)l_related.get(i)).getTargetVertex();
        	} else {
        		ret[i] = (GsDynamicNode)((GsDirectedEdge)l_related.get(i)).getSourceVertex();
        	}
        }
        return ret;
    }

	public boolean isOutgoing(int row) {
		return row > 0 && row<=nbNext;
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
