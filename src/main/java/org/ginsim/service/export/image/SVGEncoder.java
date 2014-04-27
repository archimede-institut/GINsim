package org.ginsim.service.export.image;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.colomoto.common.task.AbstractTask;
import org.ginsim.common.utils.ColorPalette;
import org.ginsim.core.graph.Edge;
import org.ginsim.core.graph.Graph;
import org.ginsim.core.graph.view.*;
import org.ginsim.core.graph.view.style.EdgeStyle;
import org.ginsim.core.graph.view.style.NodeStyle;
import org.ginsim.core.graph.view.style.StyleManager;


/**
 * Save a graph view as SVG.
 *
 * @author Frederic Cordeil
 * @author Aurelien Naldi
 */
public class SVGEncoder extends AbstractTask {

    private final Graph graph;
    private final Collection nodes;
    private final Collection<Edge> edges;
    private final String fileName;
    private final StyleManager manager;


    public SVGEncoder(Graph graph, Collection nodes,  Collection<Edge> edges, String fileName) {
        this.graph = graph;
        this.manager = graph.getStyleManager();

        if (nodes == null) {
            this.nodes = graph.getNodes();
        } else {
            this.nodes = nodes;
        }
        if (edges == null) {
            this.edges = graph.getEdges();
        } else {
            this.edges = edges;
        }
        this.fileName = fileName;
    }

    @Override
    protected Object doGetResult() throws Exception {

        Dimension dim = graph.getDimension();
        SVGWriter out = new SVGWriter(fileName, dim);

        // write CSS code for the styles
        out.openTag("style", new String[] {"type", "text/css"});
        out.addContent("");
        List<NodeStyle> nstyles = manager.getNodeStyles();
        for (NodeStyle style: nstyles) {
            out.write(style.getCSS());
        }
        List<EdgeStyle> estyles = manager.getEdgeStyles();
        for (EdgeStyle style: estyles) {
            out.write(style.getCSS());
        }
        out.closeTag(); // style

        NodeAttributesReader vreader = graph.getNodeAttributeReader();
        EdgeAttributesReader ereader = graph.getEdgeAttributeReader();

        Map<Object, Rectangle> boxes = new HashMap<Object, Rectangle>();
        Set<String>  m_marker = new HashSet<String>();
        for (Object obj: nodes) {
            vreader.setNode(obj);
            boxes.put(obj, new Rectangle(vreader.getX(), vreader.getY(), vreader.getWidth(), vreader.getHeight()));
        }
        
        for (Edge<?> edge: edges) {
            ereader.setEdge(edge);
            List<Point> l_point = ViewHelper.getPoints(vreader, ereader, edge);
            writeEdge(out, edge, ereader, m_marker, l_point);
        }
        
        for (Object obj: nodes) {
            vreader.setNode(obj);
            writeNode(out, obj, vreader);
        }
        
        out.close();

        return null;
    }

    /**
     *
     * @param out
     * @param obj
     * @param vreader
     * @throws IOException
     */
    private void writeNode(SVGWriter out, Object obj, NodeAttributesReader vreader) throws IOException {
        String id = obj.toString();
        int x = vreader.getX();
        int y = vreader.getY();
        int h = vreader.getHeight();
        int w = vreader.getWidth();

        String type = manager.getViewNodeStyle(obj).getCSSClass(obj);

        NodeShape shape = vreader.getShape();

        out.openTag("g", new String[]{"id", id});
        if (type != null) {
            out.addAttr("class", type);
        }

        String[] attrs = {"id", id + "_shape", "class", "shape"};
        shape.getShape(x,y, w, h).toSVG(out, attrs);

        out.openTag("text", new String[]{
                " id", id + "_text",
                "x", "" + (x + w / 2),
                "y", "" + (y + h / 2 + 3),
                "text-anchor", "middle"
        });
        out.addContent(id);
        out.closeTag(); // text

        out.closeTag(); // group
    }


    /**
     * 
     * 
     * @param out
     * @param edge
     * @param ereader
     * @param markers
     * @throws IOException
     */
    private void writeEdge(SVGWriter out, Edge edge, EdgeAttributesReader ereader, Set<String> markers, List<Point> l_point) throws IOException {
        String color = ColorPalette.getColorCode(ereader.getLineColor());
        float w = ereader.getLineWidth();
        String marker = addMarker(out, markers, ereader.getLineEnd(), color);

        Point2D pt1 = l_point.get(l_point.size()-2);
        Point2D pt2 = l_point.get(l_point.size()-1);
        
        double dx = pt2.getX()-pt1.getX();
        double dy = pt2.getY()-pt1.getY();
        double l = Math.sqrt(dx*dx + dy*dy);
        if (l != 0) {
            pt2.setLocation(pt2.getX()-w*dx/l, pt2.getY()-w*dy/l);
        }
        
        pt1 = l_point.get(0);
        StringBuffer sb = new StringBuffer();
        sb.append("M "+pt1.getX()+" "+pt1.getY());
        Iterator<Point> it = l_point.iterator();
        if (ereader.isCurve()) {
	        Point2D[] b = new Bezier(l_point).getPoints();
	        if (b != null && b.length > 1) {
	        	Point pt = l_point.get(1);
                sb.append(" Q "+b[0].getX() +","+ b[0].getY() +" "+ pt.getX() +","+ pt.getY());
	            for(int i = 2; i < l_point.size() - 1; i++ ) {
	                Point2D b0 = b[2*i-3];
	                Point2D b1 = b[2*i-2];
	                pt = l_point.get(i);
                    sb.append(" C "+b0.getX()+","+ b0.getY()+" "+ b1.getX() +","+ b1.getY() +" "+ pt.getX() +","+ pt.getY());
	            }
	            pt = l_point.get(l_point.size()-1);
                sb.append(" Q "+b[b.length-1].getX() +","+ b[b.length-1].getY() +" "+ pt.getX() +","+ pt.getY());
	        } else {
	            for (int i=1 ; i<l_point.size() ; i++) {
	                pt1 = l_point.get(i);
                    sb.append(" L "+pt1.getX()+" "+pt1.getY());
	            }
	        }
        } else {
    		it.next();
	        while (it.hasNext()) {
	            pt1 = it.next();
                sb.append(" L "+pt1.getX()+" "+pt1.getY());
	        }
        }
        String type = manager.getViewEdgeStyle(edge).getCSSClass(edge);
        String[] attrs = {
                "class", type,
                "marker-end", "url(#"+marker+")",
                "d", sb.toString()
        };
        out.addTag("path", attrs);
    }
    

    /**
     * ensure the definition of a marker corresponding to our needs and return it's id.
     * @param out
     * @param m_marker
     * @param markerType
     * @param color
     * @return the id of the corresponding marker
     * @throws IOException
     */
	private String addMarker(SVGWriter out, Set<String> m_marker, EdgeEnd markerType, String color) throws IOException {
		String id = "Marker_" + markerType + "_" + color.substring(1);
		if (!m_marker.contains(id)) {
            out.openTag("defs");
            String[] attrs = {
                    "id",id,
                    "viewBox", "-7 -7 12 15",
                    "orient","auto",
                    "markerHeight", "9", "markerWidth","9"
            };
            out.openTag("marker", attrs);
            attrs = new String[] {
                    "stroke", color, "fill", color
            };
            markerType.getShape().toSVG(out, attrs);

			out.closeTag(); // marker
            out.closeTag(); // defs
			m_marker.add(id);
		}
		return id;
	}

}
