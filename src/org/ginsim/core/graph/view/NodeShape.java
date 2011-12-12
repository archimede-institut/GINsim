package org.ginsim.core.graph.view;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public enum NodeShape {

	RECTANGLE, ELLIPSE;
	
	public Shape getShape(int x, int y, int width, int height) {
		
		switch (this) {
		case ELLIPSE:
			return new Ellipse(x, y, width, height);
		}
		return new Rectangle(x, y, width, height);
	}
}


class Ellipse extends Ellipse2D {

	int x,y;
	int width, height;
	
	public Ellipse(int x, int y, int width, int height) {
		setFrame(x, y, width, height);
	}

	@Override
	public Rectangle2D getBounds2D() {
		return new Rectangle(x, y, width, height);
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public double getWidth() {
		return width;
	}

	@Override
	public double getHeight() {
		return height;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public void setFrame(double x, double y, double w, double h) {
		setFrame((int)x, (int)y, (int)w, (int)h); 
	}
	
	private void setFrame(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
	}
}
