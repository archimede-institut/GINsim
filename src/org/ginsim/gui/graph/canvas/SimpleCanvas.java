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
import java.awt.image.renderable.RenderableImage;

import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;

public class SimpleCanvas extends JComponent {

	private CanvasRenderer renderer = null;
	private final CanvasMouseListener mouseListener;
	
	/** zoom factor */
	private double zoom = 1;
	/** position of the top-left corner in screen coordinates */
	private int dx=0, dy=0;
	
	/** cached image for double buffering and easy overlay */
	private BufferedImage img;

	/** part of the canvas that was changed and should be repainted */
    private Rectangle damagedRegion = null;
    
	private Point lastPoint, draggedPoint;
	
	public SimpleCanvas() {
		mouseListener = new CanvasMouseListener(this);
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);
		addMouseWheelListener(mouseListener);
	}

	public void setRenderer(CanvasRenderer renderer) {
		this.renderer = renderer;
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
		Rectangle area = new Rectangle(dx, dy, getWidth(), getHeight());
    	paintAreaInBuffer(img, area);
	}
	
	private void paintAreaInBuffer(BufferedImage img, Rectangle area) {
		
    	Graphics2D g = img.createGraphics();
    	g.clip(area);
    	
		Rectangle canvasArea = area;
		if (zoom != 1) {
			canvasArea = new Rectangle((int)(area.x*zoom), (int)(area.y*zoom), (int)(area.width*zoom), (int)(area.height*zoom));
		}
		
    	
		g.translate(dx, dy);
		g.scale(zoom, zoom);

		if (renderer != null) {
			renderer.render(g,canvasArea);
		}

		this.img = img;
		
		if (true) {
			BufferedImage img2 = getNewBufferedImage();
			g = img2.createGraphics();
			g.drawImage(img, 0, 0, null);
			g.setColor(Color.RED);
	    	g.draw(area);
	    	
			this.img = img2;
		}
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
		if (lastPoint != null && draggedPoint != null) {
			Rectangle rect = getRectangle(lastPoint, draggedPoint);
			g.drawRect(rect.x, rect.y, rect.width, rect.height);
		}
	}

	private Rectangle getRectangle(Point lastPoint, Point draggedPoint) {
		int x1 = lastPoint.x;
		int x2 = draggedPoint.x;
		int y1 = lastPoint.y;
		int y2 = draggedPoint.y;
		
		if (x2 < x1) {
			x1 = x2;
			x2 = lastPoint.x;
		}
		if (y2 < y1) {
			y1 = y2;
			y2 = lastPoint.y;
		}
		return new Rectangle(x1, y1, x2-x1, y2-y1);
	}
	
	public void setEventManager(CanvasEventManager evtManager) {
		mouseListener.setEventManager(evtManager);
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
	public void setDraggedPoint(Point p) {
		this.draggedPoint = p;
		repaint();
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
	
	public void damageCanvas(Rectangle area) {
		int x = (int)(area.x / zoom) - dx - 1;
		int y = (int)(area.y / zoom) - dy - 1;
		
		int width = (int)(area.width/zoom) +2;
		int height = (int)(area.height/zoom) +2;

		damageScreen(new Rectangle(x,y, width, height));
	}
	
	public void damageScreen(Rectangle area) {
		if (damagedRegion == null) {
			damagedRegion = area;
		} else {
			damagedRegion = damagedRegion.union(area);
		}
	}

	public void clearOffscreen() {
		img = null;
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
		canvas.setLastPoint(null);
		canvas.setDraggedPoint(null);
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

		canvas.setDraggedPoint(e.getPoint());
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

}
