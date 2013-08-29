package org.ginsim.core.graph.view.style;

import java.awt.Color;
import java.io.IOException;

import org.ginsim.common.xml.XMLWriter;
import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.view.EdgeEnd;
import org.ginsim.core.graph.view.EdgePattern;
import org.xml.sax.Attributes;

/**
 * Define the visual parameters of an edge.
 * 
 * @author Aurelien Naldi
 *
 * @param <V>
 * @param <E>
 */
public interface EdgeStyle<V,E extends Edge<V>> extends Style {

	Color getColor(E edge);
	int getWidth(E edge);
	EdgePattern getPattern(E edge);
	EdgeEnd getEnding(E edge);

	
	boolean enforceColor();
	boolean enforceEnding();
	boolean enforcePattern();
	boolean enforceWidth();

	
	boolean setColor(Color color);
	boolean setWidth(int w);
	boolean setPattern(EdgePattern pattern);
	boolean setEnding(EdgeEnd ending);
	
	boolean matches(Color color, EdgePattern pattern, int width);

}
