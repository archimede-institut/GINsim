package fr.univmrs.ibdm.GINsim.jgraph;

import java.awt.event.MouseEvent;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphLayoutCache;

import fr.univmrs.ibdm.GINsim.data.ToolTipsable;

/**
 * custumize jgraph to our needs
 */
public class GsJgraph extends JGraph {

    private static final long serialVersionUID = 645635435678L;
	private boolean edgeLabelDisplayed;
    private boolean nodeLabelDisplayed;
    /**
     * @param graph
     */
    public GsJgraph(GsJgraphtGraphManager graph) {
        super(graph.getM_jgAdapter());
        
        setGraphLayoutCache(new GraphLayoutCache(graph.getM_jgAdapter(), new GsCellViewFactory(graph)));
        
		setGridVisible(true);
		setGridEnabled(false);
		setDisconnectable(false);
		edgeLabelDisplayed = false;
        setAntiAliased(true);

		// WHY is this necessary ??
        setToolTipText("");
    }

    /**
     * get nice tooltips for objects implementing <code>ToolTipsable</code>
     * 
     * @see org.jgraph.JGraph#getToolTipText(java.awt.event.MouseEvent)
     */
    public String getToolTipText(MouseEvent e) {
		if (e!=null) {
			Object cell=getFirstCellForLocation(e.getX(),e.getY());
			if (cell != null && cell instanceof DefaultGraphCell) {
				Object data = ((DefaultGraphCell)cell).getUserObject();
				if (data instanceof ToolTipsable) return ((ToolTipsable)data).toToolTip();
			}
		}
		return null;
    }
    
    /**
     * always refuse to edit cells: cell editing is STUPID in jgraph: replaces the vertex with a string!
     * 
     * @see org.jgraph.JGraph#isCellEditable(java.lang.Object)
     */
    public boolean isCellEditable(Object cell) {
        return false;
    }

    /**
     * @param b
     */
	public void setEdgeLabelDisplayed(boolean b) {
		edgeLabelDisplayed = b;
	}

	/**
	 * @return true if the edge's label should be displayed
	 */
	public boolean isEdgeLabelDisplayed() {
		return edgeLabelDisplayed;
	}
	
	/**
	 * @return true if the node's label should be displayed
	 */
	public boolean isNodeLabelDisplayed() {
		return nodeLabelDisplayed;
	}
	/**
	 * @param b
	 */
	public void setNodeLabelDisplayed(boolean b) {
		nodeLabelDisplayed=b;
	}

}
