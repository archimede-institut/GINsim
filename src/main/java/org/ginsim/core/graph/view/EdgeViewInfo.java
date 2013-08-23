package org.ginsim.core.graph.view;

import java.awt.Point;
import java.util.List;

import org.ginsim.core.graph.common.Edge;

/**
 * Store basic view information on an edge: intermediate points and optional style.
 * 
 * @author Aurelien Naldi
 *
 * @param <V>
 * @param <E>
 */
public interface EdgeViewInfo<V, E extends Edge<V>> {

	List<Point> getPoints();
	
	EdgeStyle<V, E> getStyle();
	
	void setStyle(EdgeStyle<V,E> style);
	
	boolean isCurve();
	
	void setCurve(boolean curve);

	void setPoints(List<Point> l);
}
