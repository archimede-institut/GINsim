package org.ginsim.core.graph.view.style;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	private StyleProvider<V, E> provider;
	
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
		String name = style.getName();
		if (name != null) {
			writer.addAttr("name", name);
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
		String name = null;
		Map<String,String> mattrs = new HashMap<String, String>();
		for (int i=0 ; i<attributes.getLength() ; i++) {
			String qname = attributes.getQName(i);
			String value = attributes.getValue(i);
			if (qname == "name") {
				name = value;
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
			if (name == null) {
				style = defaultNodeStyle;
			} else {
				style = new NodeStyleImpl<V>(name, defaultNodeStyle);
				nodeStyles.add((NodeStyle)style);
			}
		} else if ("edgestyle".equals(qName)) {
			if (name == null) {
				style = defaultEdgeStyle;
			} else {
				style = new EdgeStyleImpl<V, E>(name, defaultEdgeStyle);
				edgeStyles.add((EdgeStyle)style);
			}
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

	/**
	 * Create a new node style.
	 * @return the new style
	 */
	public NodeStyle<V> addNodeStyle() {
		String name = findName("Node style ",  nodeStyles);
		NodeStyle<V> style = new NodeStyleImpl<V>(name, defaultNodeStyle);
		nodeStyles.add(style);
		return style;
	}
	/**
	 * Create a new edge style.
	 * @return the new style
	 */
	public EdgeStyle<V,E> addEdgeStyle() {
		String name = findName("Edge style ",  edgeStyles);
		EdgeStyle<V,E> style = new EdgeStyleImpl<V,E>(name, defaultEdgeStyle);
		edgeStyles.add(style);
		return style;
	}
	
	/**
	 * Search for a unique name for a new style.
	 * 
	 * @param basename
	 * @param styles
	 * @return
	 */
	private <S extends Style> String findName(String basename, Collection<S> styles) {
		
		int ext = 1;
		String name = basename;
		while ( nameExists(name, styles) ) {
			ext++;
			name = basename+ext;
		}
		return name;
	}
	
	/**
	 * Retrieve a node style based on its name.
	 * Note that this is not the regular way to retrieve a style, and should only be used by the parser.
	 * 
	 * @param name
	 * @return the corresponding style, or null if not found/default
	 */
	public NodeStyle<V> getNodeStyle(String name) {
		if (name == null) {
			return null;
		}
		
		for (NodeStyle<V> style: nodeStyles) {
			if ( name.equals( style.getName() ) ) {
				return style;
			}
		}
		return null;
	}
	
	/**
	 * Retrieve an edge style based on its name.
	 * Note that this is not the regular way to retrieve a style, and should only be used by the parser.
	 * 
	 * @param name
	 * @return the corresponding style, or null if not found/default
	 */
	public EdgeStyle<V,E> getEdgeStyle(String name) {
		if (name == null) {
			return defaultEdgeStyle;
		}
		
		for (EdgeStyle<V,E> style: edgeStyles) {
			if (name.equals( style.getName() )) {
				return style;
			}
		}
		return null;
	}

	/**
	 * Find a node style corresponding to the desired attributes.
	 * If needed, a new style will be created.
	 * Note that this is not the regular way to retrieve a style,
	 * it should only used when parsing old models (without styles)
	 * to assign the same style to elements with similar attributes.
	 * 
	 * @param qName
	 * @param attributes
	 * @return a matching style (can be a new one)
	 */
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
    	style.setProperty(StyleProperty.SHAPE, shape);
    	style.setProperty(StyleProperty.BACKGROUND, bg);
    	style.setProperty(StyleProperty.FOREGROUND, fg);
    	style.setProperty(StyleProperty.TEXT, text);
    	style.setProperty(StyleProperty.WIDTH, w);
    	style.setProperty(StyleProperty.HEIGHT, h);
		return style;
	}

	/**
	 * Find an edge style corresponding to the desired attributes.
	 * If needed, a new style will be created.
	 * Note that this is not the regular way to retrieve a style,
	 * it should only used when parsing old models (without styles)
	 * to assign the same style to elements with similar attributes.
	 * 
	 * @param qName
	 * @param attributes
	 * @return a matching style (can be a new one)
	 */
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
    	style.setProperty(StyleProperty.COLOR, color);
    	style.setProperty(StyleProperty.PATTERN, pattern);
    	style.setProperty(StyleProperty.LINEWIDTH, width);
		return style;
	}

	/**
	 * Get the list of all node styles
	 * @return the list of node styles
	 */
	public List<NodeStyle<V>> getNodeStyles() {
		return nodeStyles;
	}

	/**
	 * Get the list of all edge styles
	 * @return the list of edge styles
	 */
	public List<EdgeStyle<V,E>> getEdgeStyles() {
		return edgeStyles;
	}

	/**
	 * Get the node style used for the view.
	 * The returned style can be the core one or a style
	 * provided by a StyleProvider.
	 * 
	 * @param node
	 * @return the style for this node.
	 */
	public NodeStyle<V> getViewNodeStyle(V node) {
		NodeStyle<V> base = getUsedNodeStyle(node);
		if (provider != null) {
			return provider.getNodeStyle(node, base);
		}
		return base;
	}

	
	/**
	 * Get the node style stored in the graph.
	 * The returned style ignores the presence of a StyleProvider.
	 * 
	 * @param node
	 * @return the style for this node.
	 */
	public NodeStyle<V> getUsedNodeStyle(V node) {
		NodeViewInfo info = backend.getNodeViewInfo(node);
		NodeStyle<V> style = info.getStyle();
		if (style == null) {
			return defaultNodeStyle;
		}
		return style;
	}

	/**
	 * Get the edge style used for the view.
	 * The returned style can be the core one or a style
	 * provided by a StyleProvider.
	 * 
	 * @param node
	 * @return the style for this edge.
	 */
	public EdgeStyle getViewEdgeStyle(E edge) {
		EdgeStyle<V,E> base = getUsedEdgeStyle(edge);
		if (provider != null) {
			return provider.getEdgeStyle(edge, base);
		}
		return base;
	}
	/**
	 * Get the edge style stored in the graph.
	 * The returned style ignores the presence of a StyleProvider.
	 * 
	 * @param node
	 * @return the style for this edge.
	 */
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

	/**
	 * Apply a style to a single node.
	 * 
	 * @param node
	 * @param style
	 */
	public void applyNodeStyle(V node, NodeStyle<V> style) {
		if (style == null) {
			style = defaultNodeStyle;
		}
		backend.damage(node);
		backend.getNodeViewInfo(node).setStyle(style);
		backend.damage(node);
		
		backend.repaint();
	}

	/**
	 * Apply a style to a single edge.
	 * 
	 * @param edge
	 * @param style
	 */
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

		backend.repaint();
	}

	/**
	 * Called when a style has been modified, it will refresh the view.
	 * @param style
	 */
	public void styleUpdated(Style style) {

		// TODO: fire metadata change
		backend.damage(null);
		backend.repaint();
	}

	/**
	 * Define the style provider, it will override graph's style until it is removed.
	 * 
	 * @param provider the provider to use or null to remove it
	 */
	public void setStyleProvider(StyleProvider<V, E> provider) {
		this.provider = provider;
		backend.damage(null);
	}
	
	public void renameStyle(Style style, String newname) {
		String oldname = style.getName();
		if (newname == null || oldname == null|| newname.equals(style.getName())) {
			return;
		}
		
		if (style instanceof NodeStyle) {
			if (nameExists(newname, nodeStyles)) {
				return;
			}
		} else if (nameExists(newname, edgeStyles)) {
			return;
		}
		
		// TODO: fire metadata change
		style.setName(newname);
	}
	
	/**
	 * Test if a style name already exists.
	 * 
	 * @param name
	 * @param styles the list of styles (nodestyles or edgestyles)
	 * @return
	 */
	private <S extends Style> boolean nameExists(String name, Collection<S> styles) {

		for (Style style: styles) {
			if (name.equals(style.getName())) {
				return true;
			}
		}
		return false;
	}
}
