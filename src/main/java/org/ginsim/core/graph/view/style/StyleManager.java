package org.ginsim.core.graph.view.style;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.biojava.bio.seq.db.ViewingSequenceDB;
import org.ginsim.common.utils.ColorPalette;
import org.ginsim.common.xml.XMLWriter;
import org.ginsim.core.graph.backend.GraphBackend;
import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.view.EdgePattern;
import org.ginsim.core.graph.view.EdgeViewInfo;
import org.ginsim.core.graph.view.NodeShape;
import org.ginsim.core.graph.view.NodeViewInfo;
import org.xml.sax.Attributes;

/**
 * Store and handle styles for a Graph.
 * 
 * @author Aurelien Naldi
 *
 * @param <V>
 * @param <E>
 */
public class StyleManager<V, E extends Edge<V>> {

	private final GraphBackend<V, E> backend;
	
	private final NodeStyle<V> defaultNodeStyle;
	private final EdgeStyle<V,E> defaultEdgeStyle;

	private final List<NodeStyle<V>> nodeStyles;
	private final List<EdgeStyle<V,E>> edgeStyles;
	
	private int nextkey = 1;
	
	/**
	 * Create a style manager, defining default styles.
	 * 
	 * @param nodeStyle
	 * @param edgeStyle
	 */
	public StyleManager(GraphBackend<V,E> backend) {
		this.backend = backend;
		this.defaultNodeStyle = backend.getDefaultNodeStyle();
		this.nodeStyles = new ArrayList<NodeStyle<V>>();
		nodeStyles.add(defaultNodeStyle);

		this.defaultEdgeStyle = backend.getDefaultEdgeStyle();
		this.edgeStyles = new ArrayList<EdgeStyle<V,E>>();
		edgeStyles.add(defaultEdgeStyle);
	}

	/**
	 * Retrieve the default style for nodes
	 * @return the default style for nodes
	 */
	public NodeStyle<V> getDefaultNodeStyle() {
		return defaultNodeStyle;
	}

	/**
	 * Retrieve the default style for edges
	 * @return the default style for edges
	 */
	public EdgeStyle<V,E> getDefaultEdgeStyle() {
		return defaultEdgeStyle;
	}

	/**
	 * Save all styles to GINML
	 * 
	 * @param writer
	 * @throws IOException
	 */
	public void styles2ginml(XMLWriter writer) throws IOException {
		// save node styles
		style2ginml(writer, defaultNodeStyle, "nodestyle");
		for (NodeStyle<V> style: nodeStyles) {
			style2ginml(writer, style, "nodestyle");
		}
		
		// save edge styles
		style2ginml(writer, defaultEdgeStyle, "edgestyle");
		for (EdgeStyle<V,E> style: edgeStyles) {
			style2ginml(writer, style, "edgestyle");
		}
	}
	
	private void style2ginml(XMLWriter writer, Style style, String styletype) throws IOException {
		writer.openTag(styletype);
		int key = style.getKey();
		if (key != 0) {
			writer.addAttr("name", ""+key);
		}

		StringBuffer sb_custom = null;
		for (StyleProperty prop: style.getProperties()) {
			Object val = style.getProperty(prop);
			if (val != null) {
				if (prop.isCore) {
					writer.addAttr(prop.name, prop.getString(val));
				} else {
					if (sb_custom == null) {
						sb_custom = new StringBuffer();
					} else {
						sb_custom.append(" ");
					}
					sb_custom.append(prop.name + ":" + prop.getString(val));
				}
			}
		}
		
		if (sb_custom != null) {
			writer.addAttr("properties", sb_custom.toString());
		}

		writer.closeTag();
	}

	public void parseStyle(String qName, Attributes attributes) {
		int key=0;
		Map<String,String> mattrs = new HashMap<String, String>();
		for (int i=0 ; i<attributes.getLength() ; i++) {
			String qname = attributes.getQName(i);
			String value = attributes.getValue(i);
			if (qname == "name") {
				try {
					key = Integer.parseInt(value);
				} catch (Exception e) {}
			} else if (qname == "properties") {
				String[] props = value.split(" ");
				for (String sp: props) {
					String[] pairs = sp.split(":");
					if (pairs.length == 2) {
						mattrs.put(pairs[0].trim(), pairs[1].trim());
					}
				}
			} else {
				mattrs.put(qname, value);
			}
		}
		
		Style style = null;
		if ("nodestyle".equals(qName)) {
			if (key == 0) {
				style = defaultNodeStyle;
			} else {
				style = new NodeStyleImpl<V>(key, defaultNodeStyle);
				nodeStyles.add((NodeStyle)style);
			}
		} else if ("edgestyle".equals(qName)) {
			if (key == 0) {
				style = defaultEdgeStyle;
			} else {
				style = new EdgeStyleImpl<V, E>(key, defaultEdgeStyle);
				edgeStyles.add((EdgeStyle)style);
			}
		}
		
		if (key >= nextkey) {
			nextkey = key+1;
		}

		if (style != null) {
			fillStyle(style, attributes);
		}
	}
	
	
	
	private void fillStyle(Style style, Attributes attrs) {
		for (StyleProperty prop: style.getProperties()) {
			String val = attrs.getValue(prop.name);
			if (val != null) {
				style.setProperty(prop, prop.getValue(val));
			}
		}
	}

	public NodeStyle<V> addNodeStyle() {
		NodeStyle<V> style = new NodeStyleImpl<V>(nextkey++, defaultNodeStyle);
		nodeStyles.add(style);
		return style;
	}
	public EdgeStyle<V,E> addEdgeStyle() {
		EdgeStyle<V,E> style = new EdgeStyleImpl<V,E>(nextkey++, defaultEdgeStyle);
		edgeStyles.add(style);
		return style;
	}
	
	public NodeStyle<V> getNodeStyle(String name) {
		int key = Integer.parseInt(name);
		for (NodeStyle<V> style: nodeStyles) {
			if (style.getKey() == key) {
				return style;
			}
		}
		return null;
	}
	public EdgeStyle<V,E> getEdgeStyle(String name) {
		int key = Integer.parseInt(name);
		for (EdgeStyle<V,E> style: edgeStyles) {
			if (style.getKey() == key) {
				return style;
			}
		}
		return null;
	}

	public NodeStyle guessNodeStyle(String qName, Attributes attributes) {
		NodeShape shape = null;
        if (qName.equals("rect")) {
        	shape = NodeShape.RECTANGLE;
        } else if (qName.equals("ellipse")) {
        	shape = NodeShape.ELLIPSE;
        }

    	Color bg = ColorPalette.getColorFromCode(attributes.getValue("backgroundColor"));
    	Color fg = ColorPalette.getColorFromCode(attributes.getValue("foregroundColor"));
    	String s_textColor = attributes.getValue("textColor");
    	Color text = s_textColor == null ? fg : ColorPalette.getColorFromCode(s_textColor);
    	
    	int w = Integer.parseInt(attributes.getValue("width"));
    	int h = Integer.parseInt(attributes.getValue("height"));

        
    	for (NodeStyle<V> style: nodeStyles) {
    		if ( style.matches(shape, bg, fg, text, w,h) ) {
    			return style;
    		}
    	}
    	
    	NodeStyle<V> style = addNodeStyle();
    	style.setNodeShape(shape);
    	style.setBackground(bg);
    	style.setForeground(fg);
    	style.setTextColor(text);
    	style.setDimension(w, h);
		return style;
	}

	public EdgeStyle guessEdgeStyle(String qName, Attributes attributes) {
		Color color = ColorPalette.getColorFromCode(attributes.getValue("line_color"));
		EdgePattern pattern = EdgePattern.SIMPLE;
		if (attributes.getValue("pattern") != null) {
		    pattern = EdgePattern.DASH;
		}
		int width = 1;
        try {
            width = Integer.parseInt(attributes.getValue("line_width"));
        } catch (NullPointerException e) {}
          catch (NumberFormatException e) {}

        
    	for (EdgeStyle<V,E> style: edgeStyles) {
    		if ( style.matches(color, pattern, width) ) {
    			return style;
    		}
    	}
    	
    	EdgeStyle<V,E> style = addEdgeStyle();
    	style.setColor(color);
    	style.setPattern(pattern);
    	style.setWidth(width);
		return style;
	}

	public List<NodeStyle<V>> getNodeStyles() {
		return nodeStyles;
	}
	public List<EdgeStyle<V,E>> getEdgeStyles() {
		return edgeStyles;
	}

	public NodeStyle getUsedNodeStyle(V node) {
		NodeViewInfo info = backend.getNodeViewInfo(node);
		NodeStyle<V> style = info.getStyle();
		if (style == null) {
			return defaultNodeStyle;
		}
		return style;
	}

	public EdgeStyle getUsedEdgeStyle(E edge) {
		EdgeViewInfo<V, E> info = backend.getEdgeViewInfo(edge);
		if (info == null) {
			return defaultEdgeStyle;
		}
		EdgeStyle<V, E> style = info.getStyle();
		if (style == null) {
			return defaultEdgeStyle;
		}
		return style;
	}

	public void applyNodeStyle(V node, NodeStyle<V> style) {
		if (style == null) {
			style = defaultNodeStyle;
		}
		backend.getNodeViewInfo(node).setStyle(style);
		backend.damage(node);
	}

	public void applyEdgeStyle(E edge, EdgeStyle<V,E> style) {
		if (style == null || style == defaultEdgeStyle) {
			EdgeViewInfo<V, E> info = backend.getEdgeViewInfo(edge);
			if (info == null) {
				return;
			}
			info.setStyle(defaultEdgeStyle);
		} else {
			backend.ensureEdgeViewInfo(edge).setStyle(style);
		}
		backend.damage(edge);
	}
}
