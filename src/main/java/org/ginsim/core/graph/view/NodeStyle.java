package org.ginsim.core.graph.view;

import java.awt.Color;
import java.awt.Dimension;

public interface NodeStyle<V> {

	Color getBackground(V obj);
	void  setBackground(Color bg);
	
	Color getForeground(V obj);
	void  setForeground(Color bg);
	
	Color getTextColor(V obj);
	void  setTextColor(Color bg);
	
	Dimension getDimension(V obj);
	void setDimension(int w, int h);
	void setDimension(Dimension d);
	
	NodeShape getNodeShape(V obj);
	void setNodeShape(NodeShape shape);

}
