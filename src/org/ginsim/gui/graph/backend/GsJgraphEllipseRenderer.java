package org.ginsim.gui.graph.backend;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import org.jgraph.graph.VertexRenderer;
/**
 *	ellipse shape for vertices.
 *	based on code from JGraph's website, adapted to draw a (sometimes stupid) border
 */
class GsJgraphEllipseRenderer extends VertexRenderer {
	
	private static final long serialVersionUID = 445658585547885554L;
	private transient boolean isLabelDiplayed=true;

	public void paint(Graphics g)	 {
		int b = borderWidth;
		Dimension d = getSize();
		boolean tmp = selected;
        Border border = getBorder();
        if (border != null) {
            if (border instanceof LineBorder) {
                g.setColor(((LineBorder)border).getLineColor());
                g.fillOval(0, 0, d.width, d.height);
                b = ((LineBorder)border).getThickness();
            } else if (border instanceof BevelBorder) {
                g.setColor(super.getBackground().darker());
                g.fillOval(0, 0, d.width, d.height);
            }
        }
		if (isOpaque()) {
			g.setColor(super.getBackground());
			g.fillOval(b, b, d.width - 2*b, d.height - 2*b);
		}
		try {
			setBorder(null);
			setOpaque(false);
			selected = false;
			super.paint(g);
		} finally {
			selected = tmp;
            setBorder(border);
		}
	}

	/*
	 * @see javax.swing.JLabel#getText()
	 */
	public String getText() {
		if (isLabelDiplayed) return super.getText();
		return null;
	}

}
