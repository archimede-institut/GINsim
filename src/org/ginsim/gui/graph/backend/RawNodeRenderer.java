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
	
	
	public RawNodeRenderer(NodeAttributesReader vertexAttributeReader) {
		this.reader = vertexAttributeReader;
		setHorizontalTextPosition(CENTER);
		setHorizontalAlignment(CENTER);
	}

	public void setView(RawNodeView view) {
		
		setText(view.toString());
		setBounds(view.getBounds());
		reader.setNode(view.user);
	}
	
	public Rectangle getBounds(Object user) {
		reader.setNode(user);
		return new Rectangle(reader.getX(), reader.getY(), reader.getWidth()+1, reader.getHeight()+1);
	}

	@Override
	public void paint(Graphics g) {
		// TODO: pick some other shape
		Shape s = new Rectangle(getWidth()-1, getHeight()-1);

		Graphics2D g2d = (Graphics2D)g;
		g2d.setColor(reader.getBackgroundColor());
		g2d.fill(s);
		g2d.setColor(reader.getForegroundColor());
		g2d.draw(s);
		
		super.paint(g);
	}
}
