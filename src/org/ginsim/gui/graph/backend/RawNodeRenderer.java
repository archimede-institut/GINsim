package org.ginsim.gui.graph.backend;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.JLabel;

import org.ginsim.core.graph.view.NodeAttributesReader;


class RawNodeRenderer extends JLabel {

	public static final int SW = 5;      // width of the selection mark
	public static final int hSW = SW/2;  // half width of the selection mark
	
	private final NodeAttributesReader reader;
	
	private Color fgColor, bgColor;
	private boolean selected, preview;

	private int pcount;
	
	
	public RawNodeRenderer(NodeAttributesReader vertexAttributeReader) {
		this.reader = vertexAttributeReader;
		setHorizontalTextPosition(CENTER);
		setHorizontalAlignment(CENTER);
	}

	public void setView(RawNodeView view, boolean selected, boolean preview) {
		
		setText(view.toString());
		this.selected = selected;
		this.preview = preview;
		
		Rectangle rect = view.getBounds();
		super.setBounds(rect.x, rect.y, rect.width, rect.height);
		if (selected) {
			super.setBounds(rect.x-hSW, rect.y-hSW, rect.width+SW, rect.height+SW);
		} else {
		}
		reader.setNode(view.user);
	}
	
	public Rectangle getBounds(Object user) {
		reader.setNode(user);
		return reader.getBounds();
	}

	@Override
	public void paint(Graphics g) {
		// TODO: pick some other shape
		int w = getWidth()-1;
		int h = getHeight()-1;
		Shape s = new Rectangle(w, h);

		Graphics2D g2d = (Graphics2D)g;
		g2d.setColor(reader.getBackgroundColor());
		g2d.fill(s);
		g2d.setColor(reader.getForegroundColor());
		g2d.draw(s);

		if (selected) {
			g2d.setColor(Color.red);
			g2d.fillRect(0-hSW, 0-hSW, SW, SW);
			g2d.fillRect(0-hSW, h-hSW, SW, SW);
			g2d.fillRect(w-hSW, h-hSW, SW, SW);
			g2d.fillRect(w-hSW, 0-hSW, SW, SW);
		}

		super.paint(g);
	}

	public Rectangle setBounds(Object user, Rectangle bounds) {
		reader.setNode(user);
		Rectangle old = reader.getBounds();
		reader.setBounds(bounds);
		return old;
	}
}
