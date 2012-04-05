package org.ginsim.gui.graph.canvas;

import java.awt.Point;

public interface CanvasEventManager {

	void click(Point p, boolean alternate);

	void pressed(Point p, boolean alternate);
	
	void released(Point p);
	
	void dragged(Point p);
	
	void cancel();
}
