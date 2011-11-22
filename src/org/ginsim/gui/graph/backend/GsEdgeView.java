package org.ginsim.gui.graph.backend;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import org.ginsim.graph.common.EdgeAttributesReader;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.EdgeRenderer;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;


/**
 * a jgraph edge viewer
 */
public class GsEdgeView extends EdgeView {
	
	private static final long serialVersionUID = 875785889768955L;
	private EdgeRenderer renderer;
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

	@Override
	public CellViewRenderer getRenderer() {
		return renderer;
	}
}

class GsEdgeRenderer extends EdgeRenderer {

	private static final long serialVersionUID = 6746746786967887L;
	private GsJgraph jgraph;
	private final EdgeAttributesReader reader;
	
	protected GsEdgeRenderer(GsJgraph jgraph, EdgeAttributesReader edgeAttributesReader) {
		this.jgraph = jgraph;
		this.reader = edgeAttributesReader;
	}
	
	@Override
	public void paintLabel(java.awt.Graphics g, String label, Point2D point, boolean b) {
		if (jgraph.isEdgeLabelDisplayed()) super.paintLabel(g,label, point, b);
	}
    
	@Override
    protected Shape createLineEnd(int size, int style, Point2D src, Point2D dst) {
        if (style == EdgeAttributesReader.ARROW_DOUBLE) {
            
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
