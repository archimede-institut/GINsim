package fr.univmrs.tagc.GINsim.export.generic;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import org.jgraph.util.Bezier;

import fr.univmrs.tagc.GINsim.data.GsDirectedEdge;
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

	        Iterator itNodes, itEdges;
            GsVertexAttributesReader vreader = graph.getGraphManager().getVertexAttributesReader();
            GsEdgeAttributesReader ereader = graph.getGraphManager().getEdgeAttributesReader();
            if (selectedOnly) {
                itNodes = graph.getGraphManager().getSelectedVertexIterator();
                itEdges = graph.getGraphManager().getSelectedEdgeIterator();
            } else {
                itNodes = graph.getGraphManager().getVertexIterator();
                itEdges = graph.getGraphManager().getEdgeIterator();
            }
            int[] tmax = getmax(itNodes, itEdges, vreader, ereader);

	        out.write("<?xml version='1.0' encoding='iso-8859-1' ?>\n");
	        out.write("<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 20001102//EN\" \"http://www.w3.org/TR/2000/CR-SVG-20001102/DTD/svg-20001102.dtd\">\n");
	        out.write("<svg width=\""+tmax[0]+"\" height=\""+tmax[1]+"\" xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n");
	        
	        Map boxes = new HashMap();
	        Map m_marker = new HashMap();
            if (selectedOnly) {
                itNodes = graph.getGraphManager().getSelectedVertexIterator();
                itEdges = graph.getGraphManager().getSelectedEdgeIterator();
            } else {
                itNodes = graph.getGraphManager().getVertexIterator();
                itEdges = graph.getGraphManager().getEdgeIterator();
            }
	        while (itNodes.hasNext()) {
	            Object obj = itNodes.next();
	            vreader.setVertex(obj);
	            writeVertex(out, obj, vreader);
	            boxes.put(obj, new Rectangle(vreader.getX(), vreader.getY(), vreader.getWidth(), vreader.getHeight()));
	        }
	        
	        while (itEdges.hasNext()) {
	            Object obj = itEdges.next();
	            Rectangle2D box1=null,  box2=null;
	            if (obj instanceof GsDirectedEdge) {
	                GsDirectedEdge e = (GsDirectedEdge)obj;
                    box1 = (Rectangle2D)boxes.get(e.getSourceVertex());
                    box2 = (Rectangle2D)boxes.get(e.getTargetVertex());
	            }
	            ereader.setEdge(obj);
	            writeEdge(out, box1, box2, ereader, m_marker);
	        }
	        
	        out.write("</svg>");
	        out.close();
		} catch (IOException e) {}
    }

    /**
     * Browse the graph to find max coordinates.
     * 
     * @param itNodes nodes iterator
     * @param itEdges edges iterator
     * @param vreader vertex attribute reader
     * @param ereader edge attribute reader
     * @return a integer array containing x,y max coordinates
     */
    public static int[] getmax(Iterator itNodes, Iterator itEdges, GsVertexAttributesReader vreader, GsEdgeAttributesReader ereader) {
        int[] tmax = new int[2];
        int value;
        while (itNodes.hasNext()) {
            vreader.setVertex(itNodes.next());
            value = vreader.getX() + vreader.getWidth();
            if (value > tmax[0]) {
                tmax[0] = value;
            }
            value = vreader.getY() + vreader.getHeight();
            if (value > tmax[1]) {
                tmax[1] = value;
            }
        }
    
        while (itEdges.hasNext()) {
            ereader.setEdge(itEdges.next());
            List points = ereader.getPoints();
            if (points == null) {
                continue;
            }
            for (Iterator itp=points.iterator() ; itp.hasNext() ; ) {
                Point2D pt = (Point2D)itp.next();
                value = (int)pt.getX();
                if (value > tmax[0]) {
                    tmax[0] = value;
                }
                value = (int)pt.getY();
                if (value > tmax[1]) {
                    tmax[1] = value;
                }
            }
        }
        return tmax;
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
	            " fill=\""+fgCol+"\">" +
	    		obj+
	    		"</text>\n");
	    out.write("  </g>\n");
        
    }
    
    private static void writeEdge(FileWriter out, Rectangle2D box1, Rectangle2D box2, GsEdgeAttributesReader ereader, Map markers) throws IOException {
        String color = "#"+Tools.getColorCode(ereader.getLineColor());
        float w = ereader.getLineWidth();
        String marker = addMarker(out, markers, ereader.getLineEnd(), color, true, w);
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
        
        List l_point = ereader.getPoints();
        if (l_point == null) {
            l_point = new ArrayList();
            l_point.add(0, new Point((int)box1.getCenterX(), (int)box1.getCenterY()));
            l_point.add(new Point((int)box2.getCenterX(), (int)box2.getCenterY()));
        }
        boolean intersect = l_point.size() < 3 || ereader.getStyle() == GsEdgeAttributesReader.STYLE_CURVE;
        // replace first and last points by bounding box points
        if (box1 != null) {
            l_point.set(0, getIntersection(box1, (Point2D)l_point.get(1), intersect, w));
        }
        if (box2 != null) {
            l_point.set(l_point.size()-1, getIntersection(box2, (Point2D)l_point.get(l_point.size()-2), intersect, w));
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
        Iterator it = l_point.iterator();
        switch (ereader.getStyle()) {
	    	case GsEdgeAttributesReader.STYLE_CURVE:
                Point2D[] p = new Point2D[l_point.size()];
                for (int i=0 ; it.hasNext() ; i++) {
                    p[i] = (Point2D)it.next();
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
        		it.next();
		        for (int i=1 ; it.hasNext() ; i++) {
		            pt1 = (Point2D)it.next();
		            out.write(" L "+pt1.getX()+" "+pt1.getY());
		        }
        }        
    	out.write("\"/>\n");
    }
    
    private static Point2D getIntersection(Rectangle2D box, Point2D point, boolean intersect, float width) {
        if (box == null || box.contains(point)) {
            return point;
        }
        
        double minx, miny, maxx, maxy, px, py, resultx, resulty;
        minx = box.getMinX();
        miny = box.getMinY();
        maxx = box.getMaxX();
        maxy = box.getMaxY();
        px = point.getX();
        py = point.getY();
        double offset = width/2 + 2;

        if (intersect) {        // compute intersection of the box with the line from its center to the point
            double centerx = box.getCenterX(), centery = box.getCenterY();
            double dx = px-centerx, dy = py-centery;
            if (dy == 0) {
                resulty = centery;
                resultx = dx > 0 ? minx : maxx;
            }
            double ratio = dx/dy;
            double boxRatio = box.getWidth() / box.getHeight();
            if (Math.abs(ratio) > boxRatio) {     // crosses on one of the vertical sides
                if (dx > 0) {
                    resultx = maxx + offset;
                    resulty = centery + (maxx-centerx)/ratio;
                } else {
                    resultx = minx - offset;
                    resulty = centery - (maxx-centerx)/ratio;
                }
            } else {                    // crosses on one of the horizontal sides
                if (dy > 0) {
                    resulty = maxy + offset;
                    resultx = centerx + (maxy-centery)*ratio;
                } else {
                    resulty = miny - offset;
                    resultx = centerx - (maxy-centery)*ratio;
                }
            }
            
        } else {                        // find the closest point in the bounding box
            if (px > maxx) {
                resultx = maxx + offset;
            } else if (px < minx) {
                resultx = minx - offset;
            } else {
                resultx = px;   // FIXME: slip by a few pixels ?
            }
    
            if (py > maxy) {
                resulty = maxy + offset;
            } else if (py < miny) {
                resulty = miny - offset;
            } else {
                resulty = py;  // FIXME: slip by a few pixels ?
            }
        }
        Point r = new Point((int)resultx, (int)resulty);
        return r;
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
    private static String addMarker(FileWriter out, Map m_marker, int markerType, String color, boolean fill, float stroke) throws IOException {
        String id = "Marker_"+markerType+"_"+color.substring(1)+"_"+fill+"_"+stroke;
        if (!m_marker.containsKey(id)) {
	        out.write("  <defs>\n");
	        out.write("    <marker\n"+
                      "      markerWidth=\"2\""+
                      "      markerHeight=\"2\""+
                      "      markerUnits=\"userSpaceOnUse\"" +
	                  "      id=\""+id+"\"" +
	                  "      orient=\"auto\">\n" +
	                  "      <path stroke=\""+color+"\" fill=\""+color+"\" ");
	        double v,w;
            float offset = stroke/2;
            switch (markerType) {
	        	case GsEdgeAttributesReader.ARROW_NEGATIVE:
	        	    v = stroke<3 ? 5 : 1.8*stroke;
			        out.write("stroke-width=\""+stroke+"\" d=\"M 0 -"+v+" L 0 "+v+" z\"/>\n");
			        break;
	        	case GsEdgeAttributesReader.ARROW_UNKNOWN:
                    v = stroke<3 ? 4 : 1.5*stroke;
			        out.write("d=\"M -4,0 a2,2 -30 1,0 0,-0.1\"/>\n");
			        break;
                case GsEdgeAttributesReader.ARROW_DOUBLE:
                    v = stroke<3 ? 4 : 1.5*stroke;
                    out.write("d=\"M -7 -"+v+" L -7 "+v+"  -7 0 -5 0 -5 -3 L 0 0 L -5 3 -5 0 z\"/>\n");
                    break;
            	default:
                    v = (stroke<3 ? 6 : 2.2*stroke) - offset;
            	    w = stroke<3 ? 4.5 : 1.3*stroke;
			        out.write("d=\"M -"+v+" -"+w+" L "+offset+" 0 L -"+v+" "+w+" L -"+0.2*v+" 0 z\"/>\n");
            }
	        out.write("    </marker>\n");
	        out.write("  </defs>\n");
	        m_marker.put(id, null);
        }
        return id;
    }
}
