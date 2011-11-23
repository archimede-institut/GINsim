package org.ginsim.gui.graph.backend;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.List;

import org.ginsim.graph.common.Edge;
import org.ginsim.graph.common.EdgeAttributesReader;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.CellView;
import org.jgraph.graph.EdgeRenderer;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.util.Bezier;
import org.jgraph.util.Spline2D;

public class GsEdgeRenderer extends EdgeRenderer {

	private static final long serialVersionUID = 6746746786967887L;
	private GsJgraph jgraph;
	protected final EdgeAttributesReader reader;
	
	protected GsEdgeRenderer(GsJgraph jgraph, EdgeAttributesReader reader) {
		this.jgraph = jgraph;
		this.reader = reader;
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
