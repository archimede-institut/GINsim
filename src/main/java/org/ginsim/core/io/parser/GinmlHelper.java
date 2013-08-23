package org.ginsim.core.io.parser;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ginsim.common.utils.ColorPalette;
import org.ginsim.common.xml.XMLWriter;
import org.ginsim.commongui.dialog.GUIMessageUtils;
import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.EdgeEnd;
import org.ginsim.core.graph.view.EdgePattern;
import org.ginsim.core.graph.view.EdgeStyle;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.graph.view.NodeShape;
import org.ginsim.core.graph.view.NodeStyle;
import org.ginsim.core.graph.view.ViewHelper;
import org.xml.sax.Attributes;



/**
 * some help function to parse/write ginml files.
 * it is mainly here to share visual settings's code
 */
public class GinmlHelper {
	
    /** default URL of the DTD */
    public static final String DEFAULT_URL_DTD_FILE = "http://ginsim.org/GINML_2_2.dtd";

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
    					
    					ViewHelper.trimPoints(edge, l, nreader, ereader);
    					ereader.setPoints(l);
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

	public static String getEdgeVS(EdgeAttributesReader eReader, NodeAttributesReader nReader, Edge edge) {
        String svs = "\t\t\t<edgevisualsetting>\n";
        svs += "\t\t\t\t<polyline";
        String s = "";
        List l_point = ViewHelper.getPoints(nReader, eReader, edge);
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
	private static String getFullNodeVS(NodeAttributesReader vReader) {
        StringBuffer svs = new StringBuffer("\t\t\t<nodevisualsetting>\n\t\t\t\t<");
        if (vReader.getShape() == NodeShape.ELLIPSE) {
        	svs.append("ellipse");
        } else {
        	svs.append("rect");
        }
		svs.append(" x=\""+vReader.getX()+
			"\" y=\""+vReader.getY()+
			"\" width=\""+vReader.getWidth()+
			"\" height=\""+vReader.getHeight()+
			"\" backgroundColor=\"#"+ ColorPalette.getColorCode(vReader.getBackgroundColor())
		);
		Color fg = vReader.getForegroundColor();
		Color txt = vReader.getTextColor();
		svs.append("\" foregroundColor=\"#"+ColorPalette.getColorCode(fg));
		if (!txt.equals(fg)) {
			svs.append("\" textColor=\"#"+ColorPalette.getColorCode(txt));
		}
		svs.append("\"/>\n\t\t\t</nodevisualsetting>\n");
        return svs.toString();
	}
	
	public static void edgeStyle2GINML(XMLWriter writer, EdgeStyle style) throws IOException {
		writer.openTag("edgestyle");
		
		int w = style.getWidth(null);
		if (w>0) {
			writer.addAttr("width", ""+w);
		}
		
		EdgePattern pattern = style.getPattern(null);
		if (pattern != null) {
			writer.addAttr("pattern", pattern.toString());
		}
		
		EdgeEnd ending = style.getEnding(null);
		if (ending != null) {
			writer.addAttr("ending", ending.toString());
		}
		
		Color color = style.getColor(null);
		if (color != null) {
			writer.addAttr("color", ColorPalette.getColorCode(color));
		}
		
		writer.closeTag();
	}
	
}
