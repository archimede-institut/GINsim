package org.ginsim.core.graph.view.style;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.ginsim.common.xml.XMLWriter;
import org.ginsim.core.graph.common.Edge;
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

	private final NodeStyle<V> defaultNodeStyle;
	private final EdgeStyle<V,E> defaultEdgeStyle;

	private final Map<String, NodeStyle<V>> nodeStyles;
	private final Map<String, EdgeStyle<V,E>> edgeStyles;
	
	/**
	 * Create a style manager, defining default styles.
	 * 
	 * @param nodeStyle
	 * @param edgeStyle
	 */
	public StyleManager(NodeStyle<V> nodeStyle, EdgeStyle<V, E> edgeStyle) {
		this.defaultNodeStyle = nodeStyle;
		this.defaultEdgeStyle = edgeStyle;
		this.nodeStyles = new HashMap<String, NodeStyle<V>>();
		this.edgeStyles = new HashMap<String, EdgeStyle<V,E>>();
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
	 * Retrieve a named style for nodes
	 * @param name
	 * @return the style associated with this name
	 */
	public NodeStyle<V> getNodeStyle(String name) {
		NodeStyle<V> style = nodeStyles.get(name);
		if (style == null) {
			style = defaultNodeStyle;
		}
		return style;
	}

	/**
	 * Retrieve a named style for edges
	 * @param name
	 * @return the style associated with this name
	 */
	public EdgeStyle<V,E> getEdgeStyle(String name) {
		EdgeStyle<V,E> style = edgeStyles.get(name);
		if (style == null) {
			style = defaultEdgeStyle;
		}
		return style;
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
		for (NodeStyle<V> style: nodeStyles.values()) {
			style2ginml(writer, style, "nodestyle");
		}
		
		// save edge styles
		style2ginml(writer, defaultEdgeStyle, "edgestyle");
		for (EdgeStyle<V,E> style: edgeStyles.values()) {
			style2ginml(writer, style, "edgestyle");
		}
	}
	
	private void style2ginml(XMLWriter writer, Style style, String styletype) throws IOException {
		boolean started = false;
		for (StyleProperty prop: style.getProperties()) {
			Object val = style.getProperty(prop);
			if (val != null) {
				if (!started) {
					writer.openTag(styletype);
					started = true;
				}
				writer.addAttr(prop.name, prop.getString(val));
			}
		}
		
		if (started) {
			writer.closeTag();
		}
	}

	public void parseStyle(String qName, Attributes attributes) {
		Map<String,String> mattrs = new HashMap<String, String>();
		for (int i=0 ; i<attributes.getLength() ; i++) {
			String qname = attributes.getQName(i);
			String value = attributes.getValue(i);
			mattrs.put(qname, value);
		}
		if ("nodestyle".equals(qName)) {
			NodeStyle<V> style = defaultNodeStyle;
			fillStyle(style, attributes);
		} else if ("edgestyle".equals(qName)) {
			EdgeStyle<V,E> style = defaultEdgeStyle;
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
}
