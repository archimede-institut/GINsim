package org.ginsim.core.graph.backend;

import java.awt.Point;
import java.util.List;

import org.ginsim.core.graph.Edge;
import org.ginsim.core.graph.view.EdgeViewInfo;
import org.ginsim.core.graph.view.style.EdgeStyle;

public class EdgeViewInfoImpl<V, E extends Edge<V>> implements EdgeViewInfo<V, E> {

	List<Point> points = null;
	private EdgeStyle<V, E> style = null;
	private boolean curve = false;
	
	@Override
	public List<Point> getPoints() {
		return points;
	}

	@Override
	public EdgeStyle<V, E> getStyle() {
		return style;
	}

	@Override
	public void setStyle(EdgeStyle<V, E> style) {
		this.style = style;
	}

	@Override
	public boolean isCurve() {
		return curve ;
	}

	@Override
	public void setCurve(boolean curve) {
		this.curve = curve;
	}

	@Override
	public void setPoints(List<Point> l) {
		this.points = l;
	}
}
