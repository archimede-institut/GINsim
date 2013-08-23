package org.ginsim.core.graph.view;

import java.awt.Color;

import org.ginsim.core.graph.common.Edge;

/**
 * Implementation of EdgeStyle to store default visual settings in a graph.
 * Each graph can tweak its own instance or extend the class
 * to make the default depend on the edge properties.
 * 
 * @author Aurelien Naldi
 *
 * @param <V>
 * @param <E>
 */
public interface EdgeStyle<V,E extends Edge<V>> {

	Color getColor(E edge);
	boolean setColor(Color color);
	
	int getWidth(E edge);
	boolean setWidth(int w);
	
	EdgePattern getPattern(E edge);
	boolean setPattern(EdgePattern pattern);
	
	EdgeEnd getEnding(E edge);
	boolean setEnding(EdgeEnd ending);
	
}
