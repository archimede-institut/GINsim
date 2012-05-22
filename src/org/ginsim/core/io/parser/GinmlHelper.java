package org.ginsim.core.io.parser;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.ginsim.common.utils.ColorPalette;
import org.ginsim.common.utils.MaskUtils;
import org.ginsim.commongui.dialog.GUIMessageUtils;
import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.EdgePattern;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.graph.view.NodeShape;
import org.ginsim.core.graph.view.ViewHelper;
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
        	vareader.setBackgroundColor(ColorPalette.getColorFromCode(attributes.getValue("backgroundColor")));
        	vareader.setForegroundColor(ColorPalette.getColorFromCode(attributes.getValue("foregroundColor")));
        	String s_textColor = attributes.getValue("textColor");
        	Color col_text = s_textColor == null ? vareader.getForegroundColor() : ColorPalette.getColorFromCode(s_textColor);
        	vareader.setTextColor(col_text);
        	vareader.setSize(Integer.parseInt(attributes.getValue("width")), Integer.parseInt(attributes.getValue("height")));
            vareader.setPos(Integer.parseInt(attributes.getValue("x")),Integer.parseInt(attributes.getValue("y")));
        } else if (qName.equals("ellipse")) {
        	vareader.setShape(NodeShape.ELLIPSE);
        	vareader.setBackgroundColor(ColorPalette.getColorFromCode(attributes.getValue("backgroundColor")));
        	vareader.setForegroundColor(ColorPalette.getColorFromCode(attributes.getValue("foregroundColor")));
        	String s_textColor = attributes.getValue("textColor");
        	Color col_text = s_textColor == null ? vareader.getForegroundColor() : ColorPalette.getColorFromCode(s_textColor);
        	vareader.setTextColor(col_text);
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
	public static void applyEdgeVisualSettings(Edge<?> edge, EdgeAttributesReader ereader, NodeAttributesReader nreader, String qName, Attributes attributes) {
		if (qName.equals("polyline")) {
			ereader.setLineColor(ColorPalette.getColorFromCode(attributes.getValue("line_color")));
			boolean isCurved = false;
			String s = attributes.getValue("line_style");
			if (s.equals("curve") || s.equals("13") || s.equals("12") || s.equals("bezier") || s.equals("spline")) {
			    isCurved = true;
			}
            try {
                ereader.setLineWidth(Integer.parseInt(attributes.getValue("line_width")));
            } catch (NullPointerException e) {}
              catch (NumberFormatException e) {}
              
			ereader.setCurve(isCurved);
			
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
    					ViewHelper.trimPoints(edge, nreader, ereader);
    			    } catch (Exception e) {
    			        GUIMessageUtils.openErrorDialog("invalid points");
    			    }
    			}
			}
			
			s = attributes.getValue("pattern");
			if (s != null) {
			    ereader.setDash(EdgePattern.DASH);
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
            } else {
            	svs += " points=\"\"";
            }
        } else {
        	svs += " points=\"\"";
        }
        if (eReader.isCurve()) {
    	    s = "curve";
        } else {
    	    s = "straight";
        }
        svs += " line_style=\""+s+"\"";
        svs += " line_color=\"#"+ColorPalette.getColorCode(eReader.getLineColor())+"\"";
        EdgePattern pattern = eReader.getDash();
        if (pattern == EdgePattern.DASH) {
            svs += " pattern=\"dash\"";
        }
        svs += " line_width=\""+(int)eReader.getLineWidth()+"\"";
        svs += " routage=\"auto\""; // FIXME: should we remove it completely or need a new system?
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
					"\" backgroundColor=\"#"+ ColorPalette.getColorCode(vReader.getBackgroundColor()) +
					"\" foregroundColor=\"#"+ColorPalette.getColorCode(vReader.getForegroundColor()) +
					"\" textColor=\"#"+ColorPalette.getColorCode(vReader.getTextColor()) +
					"\"/>\n";
        		break;
            case ELLIPSE:
        		svs += "\t\t\t\t<ellipse x=\""+vReader.getX()+
				"\" y=\""+vReader.getY()+
				"\" width=\""+vReader.getWidth()+
				"\" height=\""+vReader.getHeight()+
				"\" backgroundColor=\"#"+ ColorPalette.getColorCode(vReader.getBackgroundColor()) +
				"\" foregroundColor=\"#"+ColorPalette.getColorCode(vReader.getForegroundColor()) +
				"\" textColor=\"#"+ColorPalette.getColorCode(vReader.getTextColor()) +
				"\"/>\n";
        		break;
        	default:
        		svs += "\t\t\t\t<rect x=\""+vReader.getX()+"\" y=\""+vReader.getY()+"\"/>\n";	
        }
        svs += "\t\t\t</nodevisualsetting>\n";
        return svs;
	}
}
