package fr.univmrs.ibdm.GINsim.piccolo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PPaintContext;
import fr.univmrs.ibdm.GINsim.global.Tools;

public class GsPNode extends PNode {
	private static final long serialVersionUID = -6144529784096070592L;
	
	private static final Rectangle2D.Float RECT = new Rectangle2D.Float();
	private static final Ellipse2D.Float ELLIPSE = new Ellipse2D.Float();
	
	private static final Color dragColor = Color.BLUE;
	private static final Color hgColor = Color.RED;
	private static final Color selColor = Color.GREEN;
	private static final float dragAlpha = (float)0.6;
	
	private static final short HIGHLIGHT = 1 << 1;
	private static final short DRAG = 1 << 2;
	private static final short SELECTED = 1 << 3;
	
	NodeInfo ni;
	int mode = 0;
	
	public GsPNode(NodeInfo ni) {
		this.ni = ni;
		setBounds(ni.x, ni.y, ni.width, ni.height);
	}

	private RectangularShape getGraphics() {
		switch (ni.shape) {
		case NodeInfo.SHAPE_ELLIPSE:
			return ELLIPSE;
		}
		return RECT;
	}
	
	public void startDrag(PInputEvent e) {
		mode = Tools.addMask(mode, DRAG);
	}
	public void endDrag(PInputEvent e) {
		mode = Tools.removeMask(mode, DRAG);
		Point2D pt = e.getPosition();
		ni.x = (int)pt.getX();
		ni.y = (int)pt.getY();
		setBounds(ni.x, ni.y, ni.width, ni.height);
	}
	
	public void highlight(boolean b) {
		if (b) {
			mode = Tools.addMask(mode, HIGHLIGHT);
		} else {
			mode = Tools.removeMask(mode, HIGHLIGHT);
		}
		repaint();
	}

	public void select(boolean b) {
		if (b) {
			mode = Tools.addMask(mode, SELECTED);
		} else {
			mode = Tools.removeMask(mode, SELECTED);
		}
		repaint();
	}

	public void paint(PPaintContext aPaintContext) {
		RectangularShape shape = getGraphics();
		shape.setFrame(ni.x, ni.y, ni.width, ni.height);
		Graphics2D g2 = aPaintContext.getGraphics();
		g2.setPaint(ni.bg);
		g2.fill(shape);
		g2.setPaint(ni.fg);
		g2.draw(shape);
		if (ni.data != null) {
			g2.drawString(ni.data.toString(), ni.x + 3, ni.y+ni.height-3);
		}
	}
	
	public boolean intersects(Rectangle2D aBounds) {
		RectangularShape shape = getGraphics();
		shape.setFrame(ni.x, ni.y, ni.width, ni.height);
		return shape.intersects(aBounds);
	}
}
