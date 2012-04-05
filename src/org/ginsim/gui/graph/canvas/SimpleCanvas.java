package org.ginsim.gui.graph.canvas;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.RenderableImage;

import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;

public class SimpleCanvas extends JComponent {

	private CanvasRenderer renderer = null;
	private final CanvasEventListener mouseListener;
	
	/** zoom factor */
	private double zoom = 1;
	/** position of the top-left corner in screen coordinates */
	private int dx=0, dy=0;
	
	/** cached images for double buffering and easy overlay */
	private BufferedImage img;

	/** part of the canvas that was changed and should be repainted */
    private Rectangle damagedRegion = null;
    
	private Point lastPoint, draggedPoint;

	private Color backgroundColor = Color.white;

	public SimpleCanvas() {
		mouseListener = new CanvasEventListener(this);
	}

	public void setRenderer(CanvasRenderer renderer) {
		this.renderer = renderer;
		mouseListener.setEventManager(renderer);
	}

	public BufferedImage getNewBufferedImage() {
    	return new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
	}
	
	private void paintBuffer() {
    	BufferedImage img = getNewBufferedImage();
		Rectangle area = new Rectangle(0, 0, getWidth(), getHeight());
    	paintAreaInBuffer(img, area);
	}
	
	private void paintAreaInBuffer(BufferedImage img, Rectangle area) {
		
    	Graphics2D g = img.createGraphics();
    	g.clip(area);
		// erase the whole area
		g.setColor(backgroundColor);
		g.fill(area);
		
		if (renderer != null) {
			g.translate(dx, dy);
			g.scale(zoom, zoom);
			renderer.render(g,getCanvasRectangle(area));
		}

		this.img = img;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		if (img == null) {
			paintBuffer();
		} else if (damagedRegion != null){
			paintAreaInBuffer(img, damagedRegion);
		}
		
		damagedRegion = null;
		g.drawImage(img, 0, 0, null);
		
		// add overlay layer to the image, by the renderer
		if (renderer != null) {
			Graphics2D g2 = (Graphics2D)g;
			g2.translate(dx, dy);
			g2.scale(zoom, zoom);

			renderer.overlay(g2, getCanvasRectangle(new Rectangle(0,0, getWidth(), getHeight())));
		}
	}
	
	private Rectangle getCanvasRectangle(Rectangle area) {
		Rectangle canvasArea = area;
		if (zoom != 1) {
			canvasArea = new Rectangle((int)((dx+area.x)*zoom), (int)((dy+area.y)*zoom), (int)(area.width*zoom), (int)(area.height*zoom));
		}
		return canvasArea;
	}

	public static Rectangle getRectangle(Point lastPoint, Point draggedPoint) {
		int x1 = lastPoint.x;
		int x2 = draggedPoint.x;
		int y1 = lastPoint.y;
		int y2 = draggedPoint.y;
		
		return getRectangle(x1, y1, x2, y2);
	}
	public static Rectangle getRectangle(int x1, int y1, int x2, int y2) {
		int tmp;
		if (x2 < x1) {
			tmp = x1;
			x1 = x2;
			x2 = tmp;
		}
		if (y2 < y1) {
			tmp = y1;
			y1 = y2;
			y2 = tmp;
		}
		return new Rectangle(x1, y1, x2-x1, y2-y1);
	}
	
	public void zoom(int n) {
		if (n > 0) {
			zoom = zoom * 1.1;
		} else if (n < 0) {
			zoom = zoom / 1.1;
		} else {
			zoom = 1;
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
		this.dx += dx;
		this.dy += dy;
		
		if (img == null) {
			return;
		}
		
    	BufferedImage img = getNewBufferedImage();
    	Graphics2D g = img.createGraphics();
    	
    	g.drawImage(this.img, dx, dy, null);
    	
		// redraw only the sides
    	if (dx > 0) {
    		Rectangle area = new Rectangle(0, 0, dx, getHeight());
        	paintAreaInBuffer(img, area);
    	} else if (dx < 0) {
    		Rectangle area = new Rectangle(getWidth()-dx, 0, dx, getHeight());
        	paintAreaInBuffer(img, area);
    	}

    	if (dy > 0) {
    		Rectangle area = new Rectangle(0, 0, getWidth()-dx, dy);
        	paintAreaInBuffer(img, area);
    	} else if (dy < 0) {
    		Rectangle area = new Rectangle(0, getHeight()-dy, getWidth()-dx, dy);
        	paintAreaInBuffer(img, area);
    	}

    	this.img = img;
    	repaint();
	}
	
	protected Point window2canvas(Point p) {
		if (zoom == 1) {
			if (dx == 0 && dy==0) {
				return p;
			}
			
			return new Point(p.x+dx, p.y+dy);
		}
		
		return new Point((int)((p.x+dx)*zoom), (int)((p.y+dy)*zoom));
	}

	public void scroll(int wheelRotation, boolean alternate) {
		int tr = 10*wheelRotation;
		if (alternate) {
			moveImage(tr, 0);
		} else {
			moveImage(0, tr);
		}
	}
	
	/**
	 * Mark a part of the canvas as needing to be redrawn.
	 * It will transform the canvas area into screen coordinates and call damageScreen.
	 * Note: this does NOT call repaint to let several calls happen before the actual redraw.
	 * 
	 * @param area
	 */
	public void damageCanvas(Rectangle area) {
		int x = (int)(area.x / zoom) - dx - 1;
		int y = (int)(area.y / zoom) - dy - 1;
		
		int width = (int)(area.width/zoom) +2;
		int height = (int)(area.height/zoom) +2;

		damageScreen(new Rectangle(x,y, width, height));
	}
	
	/**
	 * Mark a part of the screen as needing to be redrawn.
	 * Note: this does NOT call repaint to let several calls happen before the actual redraw.
	 * 
	 * @param area
	 */
	public void damageScreen(Rectangle area) {
		if (damagedRegion == null) {
			damagedRegion = area;
		} else {
			damagedRegion = damagedRegion.union(area);
		}
	}

	/**
	 * Throw away the cached image.
	 * The next repaint will have to build the image from scratch.
	 * Should be called when changing the zoom level or after a general layout change.
	 */
	public void clearOffscreen() {
		img = null;
	}

	public void cancel() {
		renderer.cancel();
	}

}


class CanvasEventListener implements MouseInputListener, MouseWheelListener, KeyListener {
	
	private final SimpleCanvas canvas;
	private CanvasEventManager evtManager;
	
	private int mouseButton = -1;
	
	CanvasEventListener(SimpleCanvas canvas) {
		this.canvas = canvas;
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
		canvas.addMouseWheelListener(this);
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
			int n = e.getWheelRotation();
			if (e.isShiftDown()) {
				n *= 5;
			}
			canvas.scroll(n, e.isAltDown());
			return;
		}
		
		canvas.zoom(e.getWheelRotation());
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
		if (e.isControlDown()) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_ADD:
				canvas.zoom(1);
				break;
			case KeyEvent.VK_SUBTRACT:
				canvas.zoom(-1);
				break;
			case KeyEvent.VK_MULTIPLY:
			case KeyEvent.VK_EQUALS:
				canvas.zoom(0);
				break;

			default:
				break;
			}
		}
		
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
			canvas.cancel();
			break;
		}

	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

}
