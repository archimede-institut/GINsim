package fr.univmrs.tagc.GINsim.jgraph;

import java.awt.event.MouseEvent;

import org.jgraph.JGraph;
import org.jgraph.graph.CellViewFactory;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphLayoutCache;
import org.jgrapht.ext.JGraphModelAdapter;

import fr.univmrs.tagc.GINsim.data.ToolTipsable;
import fr.univmrs.tagc.common.OptionStore;

/**
 * customize jgraph to our needs
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
        setCellViewFactory(graph.getM_jgAdapter(), new GsCellViewFactory(graph));
    }
    
    public GsJgraph(JGraphModelAdapter ma) {
        super(ma);
        setCellViewFactory(ma, new GsCellViewFactory(this));
    }

    private void setCellViewFactory(JGraphModelAdapter ma, CellViewFactory cvf) {
        
        setGraphLayoutCache(new GraphLayoutCache(ma, cvf));
		setGridVisible(((Boolean)OptionStore.getOption("display.grid", Boolean.FALSE)).booleanValue());
		setGridEnabled(((Boolean)OptionStore.getOption("display.gridactive", Boolean.FALSE)).booleanValue());
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
				if (data instanceof ToolTipsable) {
					return ((ToolTipsable)data).toToolTip();
				}
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
