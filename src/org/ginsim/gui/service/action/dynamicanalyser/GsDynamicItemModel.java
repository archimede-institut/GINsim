package org.ginsim.gui.service.action.dynamicanalyser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.List;

import javax.swing.JButton;
import javax.swing.table.AbstractTableModel;

import org.ginsim.graph.Edge;
import org.ginsim.graph.dynamicgraph.GsDynamicGraph;
import org.ginsim.graph.dynamicgraph.GsDynamicNode;
import org.ginsim.gui.GUIManager;

import fr.univmrs.tagc.GINsim.data.GsDirectedEdge;

/**
 * table model to display a dynamic node or edge.
 * 
 * TODO: this should move to the GUI
 */
public class GsDynamicItemModel extends AbstractTableModel {

    private static final long serialVersionUID = 8860415338236400531L;
    private List nodeOrder;
    GsDynamicGraph graph;
    private byte[] state;
    private GsDynamicNode[] nextState;
    private GsDynamicNode[] prevState;
    private JButton[] go2Next;
    private int len;
    private int nbNext;
    private int nbRelated;
    
    protected GsDynamicItemModel (GsDynamicGraph graph) {
        this.graph = graph;
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
        	GsDynamicNode node = (GsDynamicNode)obj;
            state = node.state;
            nextState = getRelatedNodes(graph.getOutgoingEdges(node), true);
            prevState = getRelatedNodes(graph.getIncomingEdges(node), false);
            nbNext = nextState != null ? nextState.length : 0;
            nbRelated = nbNext + ( prevState != null ? prevState.length : 0 );
        } else if (obj instanceof Edge){
            Edge<GsDynamicNode> edge = (Edge)obj;
            state = edge.getSource().state;
            nextState = new GsDynamicNode[1];
            nextState[0] = (GsDynamicNode)edge.getTarget();
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
    
    private GsDynamicNode[] getRelatedNodes(Collection<Edge<GsDynamicNode>> l_related, boolean target) {
        if (l_related == null || l_related.size() == 0) {
            return null;
        }
        GsDynamicNode[] ret = new GsDynamicNode[l_related.size()];
        int i=-1;
        for (GsDirectedEdge<GsDynamicNode> edge: l_related) {
        	i++;
        	if (target) {
        		ret[i] = edge.getTarget();
        	} else {
        		ret[i] = edge.getSource();
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
        GUIManager.getInstance().getGraphGUI(graph).selectVertex(node);
    }
    
}
