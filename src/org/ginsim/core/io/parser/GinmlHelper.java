package org.ginsim.core.io.parser;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.ginsim.common.utils.GUIMessageUtils;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.graph.view.NodeShape;
import org.ginsim.core.utils.DataUtils;
import org.xml.sax.Attributes;



/**
 * some help function to parse/write ginml files.
 * it is mainly here to share visual settings's code
 */
public class GinmlHelper {
	
    /** default URL of the DTD */
    public static final String DEFAULT_URL_DTD_FILE = "http://gin.univ-mrs.fr/GINsim/GINML_2_1.dtd";

    /**
     * we are reading node visual settings from a ginml file, apply them on
     * the current node.
     * 
     * @param vareader
     * @param qName
     * @param attributes
     * @return 1 if byte VS, 2 otherwise
     */
	public static int applyNodeVisualSettings(NodeAttributesReader vareader, String qName, Attributes attributes) {
        if (qName.equals("point")) {
            vareader.setPos(Integer.parseInt(attributes.getValue("x")),Integer.parseInt(attributes.getValue("y")));
            return 1;
        } 
        if (qName.equals("rect")) {
        	vareader.setShape(NodeShape.RECTANGLE);
        	vareader.setBackgroundColor(DataUtils.getColorFromCode(attributes.getValue("backgroundColor")));
        	vareader.setForegroundColor(DataUtils.getColorFromCode(attributes.getValue("foregroundColor")));
        	vareader.setSize(Integer.parseInt(attributes.getValue("width")), Integer.parseInt(attributes.getValue("height")));
            vareader.setPos(Integer.parseInt(attributes.getValue("x")),Integer.parseInt(attributes.getValue("y")));
        } else if (qName.equals("ellipse")) {
        	vareader.setShape(NodeShape.ELLIPSE);
        	vareader.setBackgroundColor(DataUtils.getColorFromCode(attributes.getValue("backgroundColor")));
        	vareader.setForegroundColor(DataUtils.getColorFromCode(attributes.getValue("foregroundColor")));
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
	public static void applyEdgeVisualSettings(EdgeAttributesReader ereader, String qName, Attributes attributes) {
		if (qName.equals("polyline")) {
			ereader.setLineColor(DataUtils.getColorFromCode(attributes.getValue("line_color")));
			int i = EdgeAttributesReader.STYLE_STRAIGHT;
			String s = attributes.getValue("line_style");
			if (s.equals("curve") || s.equals("13") || s.equals("12") || s.equals("bezier") || s.equals("spline")) {
			    i = EdgeAttributesReader.STYLE_CURVE;
			}
            try {
                ereader.setLineWidth(Integer.parseInt(attributes.getValue("line_width")));
            } catch (NullPointerException e) {}
              catch (NumberFormatException e) {}
              
			ereader.setStyle(i);
			i = EdgeAttributesReader.ROUTING_AUTO;
			s = attributes.getValue("routage");
			if (s.equals("manual") || s.equals("none") || s.equals("simple")) {
			    i = EdgeAttributesReader.ROUTING_NONE;
			}
			ereader.setRouting(i);
			
			s = attributes.getValue("points");
			if (s != null) {
    			s = s.trim();
    			if (!s.equals("")) {
    			    
    				String[] ts = s.split(" ");
    			    try {
    					List l = new ArrayList();
    					for (int j=0 ; j<ts.length ; j++) {
    					    String[] t_point = ts[j].split(",");
    				        l.add(new Point(Integer.parseInt(t_point[0]),Integer.parseInt(t_point[1])));
    					}
    					ereader.setPoints(l);
    			    } catch (Exception e) {
    			        GUIMessageUtils.openErrorDialog("invalid points");
    			    }
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
	public static String getEdgeVS(EdgeAttributesReader eReader) {
        String svs = "\t\t\t<edgevisualsetting>\n";
        svs += "\t\t\t\t<polyline";
        String s = "";
        List l_point = eReader.getPoints();
        if (l_point != null) {
            for (int i=0 ; i<l_point.size() ; i++) {
                Point2D pt = (Point2D)l_point.get(i); 
                s += (int)pt.getX()+","+(int)pt.getY()+" ";
            }
            if (s.length() > 1) {
                svs += " points=\""+s.substring(0, s.length()-1)+"\"";
            }
        }
        switch (eReader.getStyle()) {
	    	case EdgeAttributesReader.STYLE_CURVE:
	    	    s = "curve";
	    	    break;
        	default:
        	    s = "straight";
        }
        svs += " line_style=\""+s+"\"";
        svs += " line_color=\"#"+DataUtils.getColorCode(eReader.getLineColor())+"\"";
        float[] pattern = eReader.getDash();
        if (pattern != null) {
            svs += " pattern=\"dash\"";
        }
        switch (eReader.getRouting()) {
        	case EdgeAttributesReader.ROUTING_AUTO:
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
	public static String getShortNodeVS(NodeAttributesReader vReader) {
        String svs = "\t\t\t<nodevisualsetting>\n";
        svs += "\t\t\t\t<point x=\""+vReader.getX()+"\" y=\""+vReader.getY()+"\"/>\n";
        svs += "\t\t\t</nodevisualsetting>\n";
		return svs;
	}

	/**
	 * @param vReader
	 * @return the corresponding ginml String
	 */
	public static String getFullNodeVS(NodeAttributesReader vReader) {
        String svs = "\t\t\t<nodevisualsetting>\n";
        switch (vReader.getShape()) {
        	case RECTANGLE:
        		svs += "\t\t\t\t<rect x=\""+vReader.getX()+
					"\" y=\""+vReader.getY()+
					"\" width=\""+vReader.getWidth()+
					"\" height=\""+vReader.getHeight()+
					"\" backgroundColor=\"#"+ DataUtils.getColorCode(vReader.getBackgroundColor()) +
					"\" foregroundColor=\"#"+DataUtils.getColorCode(vReader.getForegroundColor()) +
					"\"/>\n";
        		break;
            case ELLIPSE:
        		svs += "\t\t\t\t<ellipse x=\""+vReader.getX()+
				"\" y=\""+vReader.getY()+
				"\" width=\""+vReader.getWidth()+
				"\" height=\""+vReader.getHeight()+
				"\" backgroundColor=\"#"+ DataUtils.getColorCode(vReader.getBackgroundColor()) +
				"\" foregroundColor=\"#"+DataUtils.getColorCode(vReader.getForegroundColor()) +
				"\"/>\n";
        		break;
        	default:
        		svs += "\t\t\t\t<rect x=\""+vReader.getX()+"\" y=\""+vReader.getY()+"\"/>\n";	
        }
        svs += "\t\t\t</nodevisualsetting>\n";
        return svs;
	}
}
