package fr.univmrs.tagc.GINsim.xml;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Vector;

import org.xml.sax.Attributes;

import fr.univmrs.tagc.GINsim.graph.GsEdgeAttributesReader;
import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;
import fr.univmrs.tagc.common.Tools;

/**
 * some help function to parse/write ginml files.
 * it is mainly here to share visual settings's code
 */
public class GsGinmlHelper {
	
    /** default URL of the DTD */
    public static final String DEFAULT_URL_DTD_FILE = "http://gin.univ-mrs.fr/GINsim/GINML_2_1.dtd";

    /**
     * we are reading node visual settings from a ginml file, apply them on
     * the current vertex.
     * 
     * @param vareader
     * @param qName
     * @param attributes
     * @return 1 if short VS, 2 otherwise
     */
	public static int applyNodeVisualSettings(GsVertexAttributesReader vareader, String qName, Attributes attributes) {
        if (qName.equals("point")) {
            vareader.setPos(Integer.parseInt(attributes.getValue("x")),Integer.parseInt(attributes.getValue("y")));
            return 1;
        } 
        if (qName.equals("rect")) {
        	vareader.setShape(0);
        	vareader.setBackgroundColor(Tools.getColorFromCode(attributes.getValue("backgroundColor")));
        	vareader.setForegroundColor(Tools.getColorFromCode(attributes.getValue("foregroundColor")));
        	vareader.setSize(Integer.parseInt(attributes.getValue("width")), Integer.parseInt(attributes.getValue("height")));
            vareader.setPos(Integer.parseInt(attributes.getValue("x")),Integer.parseInt(attributes.getValue("y")));
        } else if (qName.equals("ellipse")) {
        	vareader.setShape(1);
        	vareader.setBackgroundColor(Tools.getColorFromCode(attributes.getValue("backgroundColor")));
        	vareader.setForegroundColor(Tools.getColorFromCode(attributes.getValue("foregroundColor")));
        	vareader.setSize(Integer.parseInt(attributes.getValue("width")), Integer.parseInt(attributes.getValue("height")));
            vareader.setPos(Integer.parseInt(attributes.getValue("x")),Integer.parseInt(attributes.getValue("y")));
        } 
        return 2;
	}

	/**
     * we are reading edge visual settings from a ginml file, apply them on
     * the current edge.
     * 
	 * @param ereader
	 * @param qName
	 * @param attributes
	 */
	public static void applyEdgeVisualSettings(GsEdgeAttributesReader ereader, String qName, Attributes attributes) {
		if (qName.equals("polyline")) {
			ereader.setLineColor(Tools.getColorFromCode(attributes.getValue("line_color")));
			int i = GsEdgeAttributesReader.STYLE_STRAIGHT;
			String s = attributes.getValue("line_style");
			if (s.equals("curve") || s.equals("13") || s.equals("12") || s.equals("bezier") || s.equals("spline")) {
			    i = GsEdgeAttributesReader.STYLE_CURVE;
			}
            try {
                ereader.setLineWidth(Integer.parseInt(attributes.getValue("line_width")));
            } catch (NullPointerException e) {}
              catch (NumberFormatException e) {}
              
			ereader.setStyle(i);
			i = GsEdgeAttributesReader.ROUTING_AUTO;
			s = attributes.getValue("routage");
			if (s.equals("manual") || s.equals("none") || s.equals("simple")) {
			    i = GsEdgeAttributesReader.ROUTING_NONE;
			}
			ereader.setRouting(i);
			
			s = attributes.getValue("points").trim();
			if (!s.equals("")) {
				String[] ts = s.split(" ");
			    try {
					List l = new Vector();
					for (int j=0 ; j<ts.length ; j++) {
					    String[] t_point = ts[j].split(",");
				        l.add(new Point(Integer.parseInt(t_point[0]),Integer.parseInt(t_point[1])));
					}
					ereader.setPoints(l);
			    } catch (Exception e) {
			        Tools.error("invalid points", null);
			    }
			}
			
	         s = attributes.getValue("pattern");
	         if (s != null) {
	             ereader.setDash(ereader.getPattern(1));
	         }

			ereader.refresh();
		}
	}

	/**
	 * @param eReader
	 * @return the corresponding ginml String
	 */
	public static String getEdgeVS(GsEdgeAttributesReader eReader) {
        String svs = "\t\t\t<edgevisualsetting>\n";
        svs += "\t\t\t\t<polyline";
        String s = "";
        List l_point = eReader.getPoints(false);
        for (int i=0 ; i<l_point.size() ; i++) {
            Point2D pt = (Point2D)l_point.get(i); 
            s += (int)pt.getX()+","+(int)pt.getY()+" ";
        }
        if (s.length() > 1) {
            svs += " points=\""+s.substring(0, s.length()-1)+"\"";
        }
        switch (eReader.getStyle()) {
	    	case GsEdgeAttributesReader.STYLE_CURVE:
	    	    s = "curve";
	    	    break;
        	default:
        	    s = "straight";
        }
        svs += " line_style=\""+s+"\"";
        svs += " line_color=\"#"+Tools.getColorCode(eReader.getLineColor())+"\"";
        float[] pattern = eReader.getDash();
        if (pattern != null) {
            svs += " pattern=\"dash\"";
        }
        switch (eReader.getRouting()) {
        	case GsEdgeAttributesReader.ROUTING_AUTO:
        	    s = "auto";
        	    break;
        	default:
        	    s = "manual";
        }
        svs += " line_width=\""+(int)eReader.getLineWidth()+"\"";
        svs += " routage=\""+s+"\"";
        svs += "/>\n";
        svs += "\t\t\t</edgevisualsetting>\n";
		return svs;
	}
	
	/**
	 * @param vReader
	 * @return the corresponding ginml String
	 */
	public static String getShortNodeVS(GsVertexAttributesReader vReader) {
        String svs = "\t\t\t<nodevisualsetting>\n";
        svs += "\t\t\t\t<point x=\""+vReader.getX()+"\" y=\""+vReader.getY()+"\"/>\n";
        svs += "\t\t\t</nodevisualsetting>\n";
		return svs;
	}

	/**
	 * @param vReader
	 * @return the corresponding ginml String
	 */
	public static String getFullNodeVS(GsVertexAttributesReader vReader) {
        String svs = "\t\t\t<nodevisualsetting>\n";
        switch (vReader.getShape()) {
        	case GsVertexAttributesReader.SHAPE_RECTANGLE:
        		svs += "\t\t\t\t<rect x=\""+vReader.getX()+
					"\" y=\""+vReader.getY()+
					"\" width=\""+vReader.getWidth()+
					"\" height=\""+vReader.getHeight()+
					"\" backgroundColor=\"#"+ Tools.getColorCode(vReader.getBackgroundColor()) +
					"\" foregroundColor=\"#"+Tools.getColorCode(vReader.getForegroundColor()) +
					"\"/>\n";
        		break;
            case GsVertexAttributesReader.SHAPE_ELLIPSE:
        		svs += "\t\t\t\t<ellipse x=\""+vReader.getX()+
				"\" y=\""+vReader.getY()+
				"\" width=\""+vReader.getWidth()+
				"\" height=\""+vReader.getHeight()+
				"\" backgroundColor=\"#"+ Tools.getColorCode(vReader.getBackgroundColor()) +
				"\" foregroundColor=\"#"+Tools.getColorCode(vReader.getForegroundColor()) +
				"\"/>\n";
        		break;
        	default:
        		svs += "\t\t\t\t<rect x=\""+vReader.getX()+"\" y=\""+vReader.getY()+"\"/>\n";	
        }
        svs += "\t\t\t</nodevisualsetting>\n";
        return svs;
	}
}
