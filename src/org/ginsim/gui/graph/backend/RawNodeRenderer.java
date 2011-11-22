package org.ginsim.gui.graph.backend;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.JLabel;

import org.ginsim.graph.common.NodeAttributesReader;


class RawNodeRenderer extends JLabel {
	
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
		reader.setNode(view.user);
	}
	
	public Rectangle getBounds(Object user) {
		reader.setNode(user);
		return reader.getBounds();
	}

	@Override
	public void paint(Graphics g) {
		// TODO: pick some other shape
		Shape s = new Rectangle(getWidth()-1, getHeight()-1);

		Graphics2D g2d = (Graphics2D)g;
		g2d.setColor(reader.getBackgroundColor());
		g2d.fill(s);
		if (selected) {
			g2d.setColor(Color.red);
		} else {
			g2d.setColor(reader.getForegroundColor());
		}
		g2d.draw(s);
		
		super.paint(g);
	}

	public Rectangle setBounds(Object user, Rectangle bounds) {
		reader.setNode(user);
		Rectangle old = reader.getBounds();
		reader.setBounds(bounds);
		return old;
	}
}
