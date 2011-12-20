package org.ginsim.gui.graph.backend;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import org.ginsim.common.OptionStore;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.common.ToolTipsable;
import org.jgraph.JGraph;
import org.jgraph.graph.CellViewFactory;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphLayoutCache;
import org.jgrapht.ext.JGraphModelAdapter;


/**
 * customize jgraph to our needs
 */
public class GsJgraph extends JGraph {

    private static final long serialVersionUID = 645635435678L;
	private boolean edgeLabelDisplayed;
    private boolean nodeLabelDisplayed;
    
    private Rectangle2D dirty = null;
    
    public GsJgraph(JGraphModelAdapter ma, Graph<?,?> g) {
        super(ma);
        setCellViewFactory(ma, new GsCellViewFactory(this, g));
    }

    private void setCellViewFactory(JGraphModelAdapter ma, CellViewFactory cvf) {
        
        setGraphLayoutCache(new GraphLayoutCache(ma, cvf));
		setGridVisible((Boolean)OptionStore.getOption("display.grid", false));
		setGridEnabled((Boolean)OptionStore.getOption("display.gridactive", false));
		setDisconnectable(false);
		edgeLabelDisplayed = false;
        setAntiAliased(true);

		// this is necessary to be able to show tooltips for graph items later on
        setToolTipText("");
    }

    /**
     * get tooltips for objects implementing <code>ToolTipsable</code>
     */
    @Override
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
     * Refuse to edit cells (jgraph would replace the node with a string, unless we provide a custom editor)
     */
    @Override
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

	@Override
	public Graphics getOffgraphics() {
		if (dirty != null) {
			// this should not happen
			addOffscreenDirty(dirty);
			dirty = null;
		}
		
		Graphics g = super.getOffgraphics();
		
		if (dirty != null) {
			// fully delete the offscreen buffer as it is the only working solution I found for now
			offscreen = null;
			g = super.getOffgraphics();

			// the following is supposed to work... but doesn't
			//addOffscreenDirty(dirty);
			//clearOffscreen();
			
			dirty = null;
		}
		
		return g;
	}
	
	public void addCustomDirty(Rectangle2D rect) {
		if (dirty == null) {
			dirty = rect;
		} else {
			dirty.add(rect);
		}
	}
	
}
