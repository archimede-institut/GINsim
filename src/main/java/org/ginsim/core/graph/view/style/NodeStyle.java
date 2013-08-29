package org.ginsim.core.graph.view.style;

import java.awt.Color;
import java.io.IOException;

import org.ginsim.common.xml.XMLWriter;
import org.ginsim.core.graph.view.NodeBorder;
import org.ginsim.core.graph.view.NodeShape;
import org.xml.sax.Attributes;

/**
 * Define the visual parameters of a node.
 * 
 * @author Aurelien Naldi
 *
 * @param <V>
 * @param <E>
 */
public interface NodeStyle<V> extends Style {

	Color getBackground(V obj);
	Color getForeground(V obj);
	Color getTextColor(V obj);
	
	int getWidth(V obj);
	int getHeight(V obj);
	
	NodeShape getNodeShape(V obj);
	NodeBorder getNodeBorder(V obj);

	boolean setBackground(Color bg);
	boolean setForeground(Color bg);
	boolean setTextColor(Color bg);

	boolean setDimension(int w, int h);
	
	boolean setNodeShape(NodeShape shape);

	boolean setNodeBorder(NodeBorder border);
	
	boolean enforceColors();
	boolean enforceShape();
	boolean enforceSize();
	boolean enforceBorder();
	
	boolean matches(NodeShape shape, Color bg, Color fg, Color text, int w, int h);
}
