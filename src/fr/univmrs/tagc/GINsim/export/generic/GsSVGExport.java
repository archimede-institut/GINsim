package fr.univmrs.tagc.GINsim.export.generic;

import java.awt.geom.Point2D;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jgraph.util.Bezier;

import fr.univmrs.tagc.GINsim.graph.GsEdgeAttributesReader;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.tagc.common.Tools;

/**
 * export the graph to SVG
 */
public class GsSVGExport {

    /**
     * @param graph
     * @param selectedOnly
     * @param fileName
     */
    public static void exportSVG(GsGraph graph, boolean selectedOnly, String fileName) {
		try {
	        FileWriter out = new FileWriter(fileName);
	        
	        out.write("<?xml version='1.0' encoding='iso-8859-1' ?>\n");
	        out.write("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 20001102//EN\" \"http://www.w3.org/TR/2000/CR-SVG-20001102/DTD/svg-20001102.dtd\">\n");
	        out.write("<svg width=\"100%\" height=\"100%\" xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n");
	        
	        Map m_marker = new HashMap();
	        Iterator it;
	        if (selectedOnly) {
	            it = graph.getGraphManager().getSelectedVertexIterator();
	        } else {
	            it = graph.getGraphManager().getVertexIterator();
	        }
	        GsVertexAttributesReader vreader = graph.getGraphManager().getVertexAttributesReader();
	        while (it.hasNext()) {
	            Object obj = it.next();
	            vreader.setVertex(obj);
	            writeVertex(out, obj, vreader);
	        }
	        
	        GsEdgeAttributesReader ereader = graph.getGraphManager().getEdgeAttributesReader();
	        if (selectedOnly) {
	            it = graph.getGraphManager().getSelectedEdgeIterator();
	        } else {
	            it = graph.getGraphManager().getEdgeIterator();
	        }
	        while (it.hasNext()) {
	            Object obj = it.next();
	            ereader.setEdge(obj);
	            writeEdge(out, ereader, m_marker);
	        }
	        
	        out.write("</svg>");
	        out.close();
		} catch (IOException e) {}
    }
    
    private static void writeVertex(FileWriter out, Object obj, GsVertexAttributesReader vreader) throws IOException {
        String id = obj.toString();
        int x = vreader.getX();
        int y = vreader.getY();
        int h = vreader.getHeight();
        int w = vreader.getWidth();
        
        String bgCol = "#"+Tools.getColorCode(vreader.getBackgroundColor());
        String fgCol = "#"+Tools.getColorCode(vreader.getForegroundColor());
        
	    out.write("  <g id=\""+id+"\">\n");
        switch (vreader.getShape()) {
            case GsVertexAttributesReader.SHAPE_ELLIPSE:
                out.write("    <ellipse " +
                        " id=\""+id+"_shape\"" +
                        " rx=\""+(w/2)+"\"" +
                        " ry=\""+(h/2)+"\"" +
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
	            " y=\""+(y+(h/2)+3)+"\"" +
	            " text-anchor=\"middle\"" +
	            " fill=\""+fgCol+"\">" +
	    		obj+
	    		"</text>\n");
	    out.write("  </g>\n");
        
    }
    
    private static void writeEdge(FileWriter out, GsEdgeAttributesReader ereader, Map markers) throws IOException {
        List l_point = ereader.getPoints(true);
        String color = "#"+Tools.getColorCode(ereader.getLineColor());
        float w = ereader.getLineWidth();
        String marker = addMarker(out, markers, ereader.getLineEnd(), color, true);
        out.write("    <path " +
                " stroke=\""+color+"\""+
                " stroke-width=\""+w+"\""+
                " fill=\"none\""+
        		" marker-end=\"url(#"+marker+")\"");
        
        float[] dashPattern = ereader.getDash();
        if (dashPattern != null && dashPattern.length > 0) {
            String s = " style=\"stroke-dasharray:"+dashPattern[0];
        		for (int i=1 ; i<dashPattern.length ; i++) {
        			s += ","+dashPattern[i];
        		}
            s += "\"";
            out.write(s);
        }
        
        Point2D pt1 = (Point2D)l_point.get(l_point.size()-2);
        Point2D pt2 = (Point2D)l_point.get(l_point.size()-1);
        
        double dx = pt2.getX()-pt1.getX();
        double dy = pt2.getY()-pt1.getY();
        double l = Math.sqrt(dx*dx + dy*dy);
        if (l != 0) {
            pt2.setLocation(pt2.getX()-w*dx/l, pt2.getY()-w*dy/l);
        }
        
        pt1 = (Point2D)l_point.get(0);
        out.write(" d=\"M "+pt1.getX()+" "+pt1.getY());
        switch (ereader.getStyle()) {
	    	case GsEdgeAttributesReader.STYLE_CURVE:
                Object[] t = l_point.toArray();
                Point2D[] p = new Point2D[t.length];
                for (int i=0 ; i<t.length ; i++) {
                    p[i] = (Point2D)t[i];
                }
                Point2D[] b = new Bezier(p).getPoints();
                if (b != null && b.length > 1) {
                    out.write(" Q "+b[0].getX() +","+ b[0].getY() +" "+ p[1].getX() +","+ p[1].getY());
                    for(int i = 2; i < p.length - 1; i++ ) {
                        Point2D b0 = b[2*i-3];
                        Point2D b1 = b[2*i-2];
                        out.write(" C "+b0.getX()+","+ b0.getY()+" "+ b1.getX() +","+ b1.getY() +" "+ p[i].getX() +","+ p[i].getY());
                    }
                    out.write(" Q "+b[b.length-1].getX() +","+ b[b.length-1].getY() +" "+ p[p.length - 1].getX() +","+ p[p.length - 1].getY());
                } else {
                    for (int i=1 ; i<l_point.size() ; i++) {
                        pt1 = (Point2D)l_point.get(i);
                        out.write(" L "+pt1.getX()+" "+pt1.getY());
                    }
                }
        		break;
        	default:
	        for (int i=1 ; i<l_point.size() ; i++) {
	            pt1 = (Point2D)l_point.get(i);
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
    private static String addMarker(FileWriter out, Map m_marker, int markerType, String color, boolean fill) throws IOException {
        String id = "Marker_"+markerType+"_"+color.substring(1)+"_"+fill;
        if (!m_marker.containsKey(id)) {
	        out.write("  <defs>\n");
	        out.write("    <marker\n"+
                      "      markerWidth=\"2\""+
                      "      markerHeight=\"2\""+
	                  "      id=\""+id+"\"\n");
            switch (markerType) {
	        	case GsEdgeAttributesReader.ARROW_NEGATIVE:
			        out.write("      orient=\"auto\">\n"+
			                  "      <path stroke=\""+color+"\" fill=\""+color+"\" d=\"M 0 -4 L 0 4 z\"/>\n");
			        break;
	        	case GsEdgeAttributesReader.ARROW_UNKNOWN:
			        out.write("      orient=\"auto\">\n"+
                              "      <path stroke=\""+color+"\" fill=\""+color+"\" d=\"M -4,0 a2,2 -30 1,0 0,-0.1\"/>\n");
			        break;
                case GsEdgeAttributesReader.ARROW_DOUBLE:
                    out.write("      orient=\"auto\">\n"+
                              "      <path stroke=\""+color+"\" fill=\""+color+"\" d=\"M -7 -4 L -7 4  -7 0 -5 0 -5 -3 L 0 0 L -5 3 -5 0 z\"/>\n");
                    break;
            	default:
			        out.write("      orient=\"auto\">\n"+
			                  "      <path stroke=\""+color+"\" fill=\""+color+"\" d=\"M -5 -3 L 0 0 L -5 3 z\"/>\n");
            }
	        out.write("    </marker>\n");
	        out.write("  </defs>\n");
	        m_marker.put(id, null);
        }
        return id;
    }
}
