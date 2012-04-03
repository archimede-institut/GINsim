package org.ginsim.gui.graph.canvas;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;

public class SimpleCanvas extends JComponent {

	private final CanvasRenderer renderer;
	private final CanvasMouseListener mouseListener;
	
	private double zoom = 1;
	private int dx=0, dy=0;
	private Image img;
	
	private Point lastPoint;
	
	public SimpleCanvas(CanvasRenderer renderer) {
		this.renderer = renderer;

		mouseListener = new CanvasMouseListener(this);
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);
		addMouseWheelListener(mouseListener);
	}

	private BufferedImage getNewBufferedImage() {
		int width = getWidth();
		int height = getHeight();
		
    	BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    	Graphics2D g = img.createGraphics();
    	g.setColor(Color.white);
    	g.fill(new Rectangle(width, height));
    	
    	return img;
	}
	
	private void paintBuffer() {
    	BufferedImage img = getNewBufferedImage();
		Rectangle area = new Rectangle((int)dx, (int)dy, getWidth(), getHeight());
    	paintAreaInBuffer(img, area);
	}
	
	private void paintAreaInBuffer(BufferedImage img, Rectangle area) {
		
		// TODO: restrict to visible area
		
    	Graphics2D g = img.createGraphics();
		if (zoom != 1) {
			g.scale(zoom, zoom);
		}
		
		g.translate((int)dx, (int)dy);

		renderer.render(g,area);

		this.img = img;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		if (img == null) {
			paintBuffer();
		}
		g.drawImage(img, 0, 0, null);
	}

	public void setEventManager(CanvasEventManager evtManager) {
		mouseListener.setEventManager(evtManager);
	}
	
	public void zoom(int n) {
		if (n > 0) {
			zoom = zoom * 1.1;
		} else if (n < 0) {
			zoom = zoom * .9;
		}
		img = null;
		repaint();
	}
	
	public void translate(double dx, double dy) {
		moveImage((int)(dx/zoom), (int)(dy/zoom));
	}
	
	public void setLastPoint(Point p) {
		this.lastPoint = p;
	}
	
	public void drag(Point p) {
		moveImage(p.x-lastPoint.x, p.y-lastPoint.y);
		this.lastPoint = p;
	}

	private void moveImage(int dx, int dy) {
		this.dx += (int)(dx*zoom);
		this.dy += (int)(dy*zoom);
		
		if (img == null) {
			return;
		}
		
    	BufferedImage img = getNewBufferedImage();
    	Graphics2D g = img.createGraphics();
    	
    	g.drawImage(this.img, dx, dy, null);
    	
		// TODO: redraw only the sides
		Rectangle area = new Rectangle((int)dx, (int)dy, getWidth(), getHeight());
    	paintAreaInBuffer(img, area);

    	this.img = img;
    	repaint();
	}
	
	protected Point window2canvas(Point p) {
		if (zoom == 0) {
			if (dx == 0 && dy==0) {
				return p;
			}
			
			return new Point(p.x+(int)dx, p.y+(int)dy);
		}
		
		return new Point((int)(dx + p.x*zoom), (int)(dy + p.y*zoom));
	}

	public void scroll(int wheelRotation, boolean alternate) {
		int tr = 10*wheelRotation;
		if (alternate) {
			moveImage(tr, 0);
		} else {
			moveImage(0, tr);
		}
	}
}


class CanvasMouseListener implements MouseInputListener, MouseWheelListener {
	
	private final SimpleCanvas canvas;
	private CanvasEventManager evtManager;
	
	private int mouseButton = -1;
	
	CanvasMouseListener(SimpleCanvas canvas) {
		this.canvas = canvas;
	}
	
	public void setEventManager(CanvasEventManager evtManager) {
		this.evtManager = evtManager;
	}

	private boolean alternate(MouseEvent e) {
		return e.isControlDown() || e.getButton() == MouseEvent.BUTTON2;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (evtManager != null) {
			evtManager.click(canvas.window2canvas(e.getPoint()), alternate(e));
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		Point p = e.getPoint();
		canvas.setLastPoint(p);
		mouseButton = e.getButton();
		if (evtManager != null) {
			evtManager.pressed(canvas.window2canvas(p), alternate(e));
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mouseButton = -1;
		if (evtManager != null) {
			evtManager.released(canvas.window2canvas(e.getPoint()));
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (mouseButton == MouseEvent.BUTTON2) {
			canvas.drag(e.getPoint());
			return;
		}
		
		if (evtManager != null) {
			evtManager.dragged(canvas.window2canvas(e.getPoint()));
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (!e.isControlDown()) {
			canvas.scroll(e.getWheelRotation(), e.isAltDown());
			return;
		}
		
		canvas.zoom(e.getWheelRotation());
	}

}
