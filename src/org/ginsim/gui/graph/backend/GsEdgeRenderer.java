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
import org.jgraph.graph.GraphConstants;

public class GsEdgeRenderer extends EdgeRenderer {

	private static final long serialVersionUID = 6746746786967887L;
	private GsJgraph jgraph;
	private final EdgeAttributesReader reader;
	
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

	protected void installAttributes(CellView view) {
		
		if (view instanceof GsEdgeView) {
			Edge<?> e = ((GsEdgeView)view).edge;
			if (e == null) {
				System.err.println("which edge?");
				return;
			}
			reader.setEdge(e);
			AttributeMap map = view.getAllAttributes();
			
			GraphConstants.setLineColor(map, reader.getLineColor());
			float[] dash = reader.getDash();
			if (dash != null) {
				GraphConstants.setDashPattern(map, dash);
			}
			GraphConstants.setLineWidth(map, reader.getLineWidth());
			List<?> points = reader.getPoints();
			// FIXME: points are not really supported...
			if (points == null) {
				if (GraphConstants.getPoints(map) != null) {
					reader.setPoints(GraphConstants.getPoints(map));
					System.out.println("RESET points");
				}
			}
			if (points != null) {
				GraphConstants.setPoints(map, points);
			}
		}
		
		super.installAttributes(view);
	}
}
