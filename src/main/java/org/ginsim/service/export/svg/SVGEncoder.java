package org.ginsim.service.export.svg;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ginsim.common.utils.ColorPalette;
import org.ginsim.common.utils.MaskUtils;
import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.EdgeEnd;
import org.ginsim.core.graph.view.EdgePattern;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.graph.view.ViewHelper;
import org.ginsim.core.graph.view.Bezier;



public class SVGEncoder {


    /**
     * Export the graph to a SVG file
     * 
     * @param graph the graph to export
	 * @param nodes the list of nodes that must be exported
	 * @param edges the list of edges that must be exported
	 * @param fileName the path to the output file
	 */
	public void exportSVG( Graph graph, Collection nodes,  Collection<Edge> edges, String fileName) throws IOException{

        FileWriter out = new FileWriter(fileName);

        NodeAttributesReader vreader = graph.getNodeAttributeReader();
        EdgeAttributesReader ereader = graph.getEdgeAttributeReader();
        if (edges == null) {
            edges = graph.getEdges();
        }
        if (nodes == null) {
            nodes = graph.getNodes();
        }
        Dimension dim = graph.getDimension();
    	int width = (int)dim.getWidth();
    	int height = (int)dim.getHeight();

        out.write("<?xml version='1.0' encoding='iso-8859-1' ?>\n");
        out.write("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 20001102//EN\" \"http://www.w3.org/TR/2000/CR-SVG-20001102/DTD/svg-20001102.dtd\">\n");
        out.write("<svg width=\""+width+"\" height=\""+height+"\" xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n");
        
        Map<Object, Rectangle> boxes = new HashMap<Object, Rectangle>();
        Set<String>  m_marker = new HashSet<String>();
        for (Object obj: nodes) {
            vreader.setNode(obj);
            writeNode(out, obj, vreader);
            boxes.put(obj, new Rectangle(vreader.getX(), vreader.getY(), vreader.getWidth(), vreader.getHeight()));
        }
        
        for (Edge<?> edge: edges) {
            Rectangle box1 = boxes.get(edge.getSource());
            Rectangle box2 = boxes.get(edge.getTarget());
            ereader.setEdge(edge);
            writeEdge(out, box1, box2, ereader, m_marker);
        }
        
        out.write("</svg>");
        out.close();
    }

    /**
     * 
     * @param out
     * @param obj
     * @param vreader
     * @throws IOException
     */
    private void writeNode(FileWriter out, Object obj, NodeAttributesReader vreader) throws IOException {
        String id = obj.toString();
        int x = vreader.getX();
        int y = vreader.getY();
        int h = vreader.getHeight();
        int w = vreader.getWidth();
        
        String bgCol = "#"+ColorPalette.getColorCode(vreader.getBackgroundColor());
        String fgCol = "#"+ColorPalette.getColorCode(vreader.getForegroundColor());
        String txtCol = "#"+ColorPalette.getColorCode(vreader.getTextColor());
        
	    out.write("  <g id=\""+id+"\">\n");
        switch (vreader.getShape()) {
            case ELLIPSE:
                out.write("    <ellipse " +
                        " id=\""+id+"_shape\"" +
                        " rx=\""+w/2+"\"" +
                        " ry=\""+h/2+"\"" +
                        " cx=\""+(x+w/2)+"\"" +
                        " cy=\""+(y+h/2)+"\"" +
                        " fill=\""+bgCol+"\"" +
                        " stroke=\""+fgCol+"\"" +
                        " />\n");
                break;
        	default:
        	    out.write("    <rect " +
        	            " id=\""+id+"_shape\"" +
        	            " width=\""+w+"\"" +
        	            " height=\""+h+"\"" +
        	            " x=\""+x+"\"" +
        	            " y=\""+y+"\"" +
        	            " fill=\""+bgCol+"\"" +
        	            " stroke=\""+fgCol+"\"" +
        	    		" />\n");
        }
	    out.write("    <text " +
	    		" id=\""+id+"_text\" " +
	            " x=\""+(x+w/2)+"\"" +
	            " y=\""+(y+h/2+3)+"\"" +
	            " text-anchor=\"middle\"" +
	            " fill=\""+txtCol+"\">" +
	    		obj+
	    		"</text>\n");
	    out.write("  </g>\n");
        
    }
    
    
    /**
     * 
     * 
     * @param out
     * @param box1
     * @param box2
     * @param ereader
     * @param markers
     * @throws IOException
     */
    private void writeEdge(FileWriter out, Rectangle box1, Rectangle box2, EdgeAttributesReader ereader, Set<String> markers) throws IOException {
        String color = "#"+ColorPalette.getColorCode(ereader.getLineColor());
        float w = ereader.getLineWidth();
        String marker = addMarker(out, markers, ereader.getLineEnd(), color, true);
        out.write("    <path " +
                " stroke=\""+color+"\""+
                " stroke-width=\""+w*2+"\""+
                " fill=\"none\""+
        		" marker-end=\"url(#"+marker+")\"");
        
        float[] dashPattern = null;
        EdgePattern pattern = ereader.getDash();
        if (pattern != null) {
        	dashPattern = pattern.getPattern();
        }
        if (dashPattern != null && dashPattern.length > 0) {
            String s = " style=\"stroke-dasharray:"+dashPattern[0];
        		for (int i=1 ; i<dashPattern.length ; i++) {
        			s += ","+dashPattern[i];
        		}
            s += "\"";
            out.write(s);
        }
        
        List<Point> l_point = ereader.getPoints();
        if (l_point == null) {
            l_point = new ArrayList<Point>();
        }
        // add the first and last points
        l_point.add(0, new Point((int)box1.getCenterX(), (int)box1.getCenterY()));
        l_point.add(new Point((int)box2.getCenterX(), (int)box2.getCenterY()));
        
        boolean intersect = l_point.size() < 3 || ereader.isCurve();
        // replace first and last points by bounding box points
        if (box1 != null) {
            l_point.set(0, ViewHelper.getIntersection(box1, l_point.get(1), intersect, w));
        }
        if (box2 != null) {
            l_point.set(l_point.size()-1, ViewHelper.getIntersection(box2, l_point.get(l_point.size()-2), intersect, w));
        }
        Point2D pt1 = l_point.get(l_point.size()-2);
        Point2D pt2 = l_point.get(l_point.size()-1);
        
        double dx = pt2.getX()-pt1.getX();
        double dy = pt2.getY()-pt1.getY();
        double l = Math.sqrt(dx*dx + dy*dy);
        if (l != 0) {
            pt2.setLocation(pt2.getX()-w*dx/l, pt2.getY()-w*dy/l);
        }
        
        pt1 = l_point.get(0);
        out.write(" d=\"M "+pt1.getX()+" "+pt1.getY());
        Iterator<Point> it = l_point.iterator();
        if (ereader.isCurve()) {
	        Point2D[] b = new Bezier(l_point).getPoints();
	        if (b != null && b.length > 1) {
	        	Point pt = l_point.get(1);
	            out.write(" Q "+b[0].getX() +","+ b[0].getY() +" "+ pt.getX() +","+ pt.getY());
	            for(int i = 2; i < l_point.size() - 1; i++ ) {
	                Point2D b0 = b[2*i-3];
	                Point2D b1 = b[2*i-2];
	                pt = l_point.get(i);
	                out.write(" C "+b0.getX()+","+ b0.getY()+" "+ b1.getX() +","+ b1.getY() +" "+ pt.getX() +","+ pt.getY());
	            }
	            pt = l_point.get(l_point.size()-1);
	            out.write(" Q "+b[b.length-1].getX() +","+ b[b.length-1].getY() +" "+ pt.getX() +","+ pt.getY());
	        } else {
	            for (int i=1 ; i<l_point.size() ; i++) {
	                pt1 = l_point.get(i);
	                out.write(" L "+pt1.getX()+" "+pt1.getY());
	            }
	        }
        } else {
    		it.next();
	        while (it.hasNext()) {
	            pt1 = it.next();
	            out.write(" L "+pt1.getX()+" "+pt1.getY());
	        }
        }        
    	out.write("\"/>\n");
    }
    

    
    /**
     * ensure the definition of a marker corresponding to our needs and return it's id.
     * @param out
     * @param m_marker
     * @param markerType
     * @param color
     * @param fill
     * @return the id of the corresponding marker
     * @throws IOException
     */
	private String addMarker(FileWriter out, Set<String> m_marker, EdgeEnd markerType, String color,
			boolean fill) throws IOException {
		String id = "Marker_" + markerType + "_" + color.substring(1) + "_" + fill;
		if (!m_marker.contains(id)) {
			out.write("  <defs>\n");
			out.write("    <marker id=\"" + id + "\" viewBox=\"-7 -7 12 15\" orient=\"auto\" markerHeight=\"5\" markerWidth=\"5\">\n"
					+ "      <path stroke=\"" + color + "\" fill=\"" + color + "\" ");
			
			// TODO: if the lineEnd could return a shape, that can be saved in SVG it would be cleaner...
			switch (markerType) {
			case NEGATIVE:
				out.write("d=\"M -1 -7 L 1 -7 L 1 7 L -1 7 z\"/>\n");
				break;
			case UNKNOWN:
				out.write("d=\"M -3 -1 C  7,-15 7,15 -3,1\"/>\n");
				break;
			case DUAL:
				out.write("d=\"M -3 -7 L 3 0 L -3 7 z  M -6 -7 L -5 -7 L -5 7 L -6 7 z\"/>\n");
				break;
			default:
				out.write("d=\"M -5 -7 L 3 0 L -5 7 L -2 0 z\"/>\n");
			}
			out.write("    </marker>\n");
			out.write("  </defs>\n");
			m_marker.add(id);
		}
		return id;
	}
}
