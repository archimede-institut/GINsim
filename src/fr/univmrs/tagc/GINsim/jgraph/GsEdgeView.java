package fr.univmrs.tagc.GINsim.jgraph;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.EdgeRenderer;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;

import fr.univmrs.tagc.GINsim.graph.GsEdgeAttributesReader;
import fr.univmrs.tagc.GINsim.graph.GsGraphManager;

/**
 * a jgraph edge viewer
 */
public class GsEdgeView extends EdgeView {
	
	private static final long serialVersionUID = 875785889768955L;
	private GsEdgeRenderer renderer;
	/**
	 * create a new EdgeView.
	 * 
	 * @param cell the EdgeCell
	 * @param renderer the renderer for this edge
	 */
	public GsEdgeView(Object cell, GsEdgeRenderer renderer) {
		super(cell);
		this.renderer = renderer;
	}

	public CellViewRenderer getRenderer() {
		return renderer;
	}
}

class GsEdgeRenderer extends EdgeRenderer {

	private static final long serialVersionUID = 6746746786967887L;
	private GsJgraphtGraphManager graph;
	
	protected GsEdgeRenderer(GsJgraphtGraphManager graph) {
		this.graph = graph;
	}
	
    /**
     * 
     * @return the graph for which this renderer works
     */
    public GsGraphManager getGraph() {
        return graph;
    }
    
	public void paintLabel(java.awt.Graphics g, String label, Point2D point, boolean b) {
		if (graph.getJgraph().isEdgeLabelDisplayed()) super.paintLabel(g,label, point, b);
	}
    
    protected Shape createLineEnd(int size, int style, Point2D src, Point2D dst) {
        if (style == GsEdgeAttributesReader.ARROW_DOUBLE) {
            
            Shape pl = createLineEnd(size, GraphConstants.ARROW_TECHNICAL, src, dst);
            if (pl == null) {
                return null;
            }
            GeneralPath path = (GeneralPath)createLineEnd(size, GraphConstants.ARROW_LINE, src, dst);
            path.append(pl,false);
            return path;
        }
        return super.createLineEnd(size, style, src, dst);
    }
}
