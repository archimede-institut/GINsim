package fr.univmrs.tagc.GINsim.jgraph;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.List;

import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.PortView;

import fr.univmrs.tagc.GINsim.data.GsDirectedEdge;

/**
 * The class to route edges 
 */
public class GsParallelEdgeRouting extends DefaultEdge.DefaultRouting {
	
    private static final long serialVersionUID = 97867454338056365L;
    
	protected List routeEdge(GraphLayoutCache cache, EdgeView edge) {
        if (edge.isLoop()) {
            return null;
        }
        GsDirectedEdge de = (GsDirectedEdge)((DefaultGraphCell)edge.getCell()).getUserObject();
        boolean isdouble = false;
        if (edge instanceof GsEdgeView && ((GsEdgeView)edge).getRenderer() instanceof GsEdgeRenderer) {
            isdouble = ((GsEdgeRenderer)((GsEdgeView)edge).getRenderer()).getGraph().containsEdge(de.getTarget(), de.getSource());
        }
        
        if (isdouble) {
            Point2D from;
            Point2D to;
            if (edge.getSource() instanceof PortView) {
                from = ((PortView)edge.getSource()).getLocation();
            } else {
                from = edge.getPoint(0);
            }
            if (edge.getTarget() instanceof PortView) {
                to = ((PortView)edge.getTarget()).getLocation();
            } else {
                to = edge.getPoint(edge.getPointCount() - 1);    
            }
    
            if (from != null && to != null) {
                double px;
                double py;
                double dx = to.getX()-from.getX();
                double dy = to.getY()-from.getY();
                double D  = Math.sqrt(dx*dx+dy*dy);
                double ex = D < 20 ? dy/2 : 15*dy/D;  
                double ey = D < 20 ? dx/2 : 15*dx/D;  
                dx /= 2;
                dy /= 2;
                
                px = from.getX() + dx - ex;
                py = from.getY() + dy + ey;
                
                switch (edge.getPointCount()) {
                    case 2:
                        edge.addPoint(1, new Point((int)px, (int)py));
                        break;
                    case 3:
                        edge.setPoint(1, new Point((int)px, (int)py));
                        break;
                } 
            }
        }
        return edge.getPoints();
    }
}

