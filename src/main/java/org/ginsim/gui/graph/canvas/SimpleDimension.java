package org.ginsim.gui.graph.canvas;

import java.awt.Rectangle;

public class SimpleDimension {

	int width = 0;
	int height = 0;
	
	public void extend(Rectangle bounds) {
		int m = bounds.x+bounds.width;
		if (m > width) {
			width = m;
		}
		m = bounds.y + bounds.height;
		if (m > height) {
			height = m;
		}
	}
	
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
}
