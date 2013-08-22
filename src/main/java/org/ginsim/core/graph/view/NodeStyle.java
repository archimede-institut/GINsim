package org.ginsim.core.graph.view;

import java.awt.Color;
import java.awt.Dimension;

public interface NodeStyle<V> {

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
}
