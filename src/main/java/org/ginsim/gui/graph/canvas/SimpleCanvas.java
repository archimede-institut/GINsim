package org.ginsim.gui.graph.canvas;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.Date;

import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;

/**
 * A simple canvas component.
 * This component has a zoom level and translation and lets a separate renderer
 * draw the content (without having to know about coordinate transformations)
 * Mouse events that are not caught for zooming and dragging are also passed
 * to the renderer (which can use them for example to select and move items).
 * 
 * The Base canvas is also in charge for the scroll bars.
 * Scroll bars appear when the renderer declared area is not entirely visible.
 * The canvas allows scrolling further than the declared area, by a margin depending
 * on the window size and zoom level.
 * Note: scroll bars are not interactive yet.
 * 
 * @author Aurelien Naldi
 */
public class SimpleCanvas extends JComponent {

	private final static double MAXZOOM = 10;
	private final static double MINZOOM = 0.1;
	
	private final static int SCROLLMARGIN = 4;
	private final static boolean SCROLLBARS = true;
	private final double CANVASMARGIN = 0.5;
	
	private final static RenderingHints RENDER_HINTS;
	
	static {
		RENDER_HINTS = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		RENDER_HINTS.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	}
	
	private CanvasRenderer renderer = null;
	private final CanvasEventListener mouseListener;
	
	/** zoom factor */
	private double zoom = 1;
	
	/** translation: the top-left corner is (-tr_x, -tr_y). Only negative translations are accepted */
	private int tr_x=0, tr_y=0;
	
	/** cached image for double buffering and easy overlay */
	private BufferedImage img;

	/** part of the canvas that was changed and should be repainted */
    private Rectangle damagedRegion = null;
    
	private Point lastPoint;
	private CanvasScrollPosition lastScrollPosition = null;

	private Color backgroundColor = Color.white;
	
	private boolean showHelp = false;

	public SimpleCanvas() {
		mouseListener = new CanvasEventListener(this);
		setFocusable(true);
	}

	public void setRenderer(CanvasRenderer renderer) {
		this.renderer = renderer;
		mouseListener.setEventManager(renderer);
	}

	public BufferedImage getNewBufferedImage() {
    	return new BufferedImage(getWidth()-SCROLLMARGIN, getHeight()-SCROLLMARGIN, BufferedImage.TYPE_INT_RGB);
	}
	
	private void paintBuffer() {
    	BufferedImage img = getNewBufferedImage();
		Rectangle area = new Rectangle(0, 0, getWidth()-SCROLLMARGIN, getHeight()-SCROLLMARGIN);
    	paintAreaInBuffer(img, area);
	}
	
	private void paintAreaInBuffer(BufferedImage img, Rectangle area) {
		
    	Graphics2D g = img.createGraphics();
    	g.setRenderingHints(RENDER_HINTS);
    	g.clip(area);
		// erase the whole area
		g.setColor(backgroundColor);
		g.fill(area);
		
		if (renderer != null) {
			g.translate(tr_x, tr_y);
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

		if (renderer == null) {
			return;
		}
		
		// draw size indicators (pseudo scroll bars)
		Dimension bounds = renderer.getBounds();
		// canvas and scroll area size
		int cw = getWidth()-SCROLLMARGIN;
		int ch = getHeight()-SCROLLMARGIN;
		int sw = SCROLLMARGIN-1;

		// renderer size, including canvas margin
		int vert_cmargin = (int)(CANVASMARGIN*cw/zoom);
		int hor_cmargin = (int)(CANVASMARGIN*ch/zoom);
		int rw = bounds.width + vert_cmargin;
		int rh = bounds.height + hor_cmargin;

		// visible area
		int vx = (-tr_x * 100) / rw;
		double vmaxx = ((-tr_x+(cw/zoom))*100) / rw;
		
		int vy = (-tr_y * 100) / rh;
		double vmaxy = ((-tr_y+(ch/zoom))*100) / rh;
		int cmaxy = tr_y+ch;
		
		// make sure to stay in the 0-100 range
		if (vmaxx > 100) {
			vmaxx = 100;
		}
		if (vmaxy > 100) {
			vmaxy = 100;
		}

		// minimum scrollbar size
		if (vmaxx - vx < 10) {
			vx = (int)vmaxx - 10;
		}
		if (vmaxy - vy < 10) {
			vy = (int)vmaxy - 10;
		}
		

		// do we need scroll bars?
		if (vx > 0 || vmaxx < 100 || vy > 0 || vmaxy < 100) {
			// extract width and height
			int vw = (int)vmaxx - vx;
			int vh = (int)vmaxy - vy;
	
			// translate into screen coordinates
			vx = vx * cw / 100;
			vw = vw * cw / 100;
			vy = vy * ch / 100;
			vh = vh * ch / 100;
		
			// scroll information background
			g.setColor(Color.BLACK);
			g.drawLine(0, ch, cw, ch);
			g.drawLine(cw, 0, cw, ch);
			g.setColor(Color.WHITE);
			g.fillRect(0, ch+1, cw, sw);
			g.fillRect(cw+1, 0, sw, ch);
			
			// actual scroll information
			g.setColor(Color.BLUE);
			g.fillRect(vx, ch+1, vw, sw);
			g.fillRect(cw+1, vy, sw, vh);
			
			// position of markers for the real visible area
			g.setColor(Color.GREEN);
			int vmark = (bounds.height*ch) / rh;
			g.fillRect(cw+1, vmark-1, sw, 2);
			vmark = (bounds.width*cw) / rw;
			g.fillRect(vmark-1, ch+1, 2, sw);
		}
		
		// add overlay layer to the image, by the renderer
		Graphics2D g2 = (Graphics2D)g;
		g2.translate(tr_x, tr_y);
		g2.scale(zoom, zoom);

		if (showHelp) {
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.9f));
			renderer.helpOverlay(g2, getCanvasRectangle(new Rectangle(0,0, getWidth(), getHeight())));
			showHelp = false;
		} else {
			renderer.overlay(g2, getCanvasRectangle(new Rectangle(0,0, getWidth(), getHeight())));
		}
	}
	
	private Rectangle getCanvasRectangle(Rectangle area) {
		Rectangle canvasArea = area;
		if (zoom != 1) {
			canvasArea = new Rectangle((int)((area.x-tr_x)/zoom), (int)((area.y-tr_y)/zoom), (int)(area.width/zoom), (int)(area.height/zoom));
		} else if (tr_x != 0 || tr_y != 0) {
			canvasArea = new Rectangle(area.x-tr_x, area.y-tr_y, area.width, area.height);
		}
		return canvasArea;
	}

	public void zoom(int n) {
		if (n < 0) {
			if (zoom >= MAXZOOM) {
				return;
			}
			zoom = zoom * 1.1;
		} else if (n > 0) {
			if (zoom <= MINZOOM) {
				return;
			}
			zoom = zoom / 1.1;
		} else if (zoom == 1) {
			return;
		} else {
			zoom = 1;
		}
		img = null;
		repaint();
	}
	
	/**
	 * Translate the canvas.
	 *   dx > 0 will reveal what is to the right of the current view (horizontal scroll)
	 *   dy > 0 will reveal what is below the current view (scroll down)
	 * Note: the zoom level affects this.
	 *   
	 * @param dx
	 * @param dy
	 */
	public void translate(double dx, double dy) {
		moveImage((int)(dx/zoom), (int)(dy/zoom));
	}
	
	private CanvasScrollPosition getScrollPosition(Point p) {
		
		if (p.x > getWidth() - SCROLLMARGIN) {
			if (p.y > getHeight() - SCROLLMARGIN) {
				return CanvasScrollPosition.CORNER;
			}
			
			
			return CanvasScrollPosition.VERT_HANDLE;
		}
		
		if (p.y > getHeight() - SCROLLMARGIN) {
			
			
			return CanvasScrollPosition.HOR_HANDLE;
		}
		
		return CanvasScrollPosition.INTERNAL;
	}
	
	protected boolean mousePressed(int mouseButton, Point p) {
		this.lastPoint = p;
		if (p == null) {
			lastScrollPosition = null;
		} else {
			lastScrollPosition = getScrollPosition(p);
		}
		
		if (lastScrollPosition == null || lastScrollPosition == CanvasScrollPosition.INTERNAL) {
			return false;
		}

		// TODO: handle the click
		
		return true;
	}
	
	protected boolean click(int mouseButton, Point p) {
		if (lastScrollPosition == null || lastScrollPosition == CanvasScrollPosition.INTERNAL) {
			return false;
		}
		
		// TODO: handle the click
		
		return true;
	}
	
	protected boolean mouseReleased() {
		if (lastScrollPosition == null || lastScrollPosition == CanvasScrollPosition.INTERNAL) {
			return false;
		}
		
		// TODO: handle the event
		
		return true;
	}


	/**
	 * Drag to scroll the view.
	 * Note: only the main event listener should call this method, the renderer should use "translate".
	 * 
	 * @param p
	 */
	public void drag(Point p) {
		moveImage(lastPoint.x - p.x, lastPoint.y - p.y);
		this.lastPoint = p;
	}

	private void moveImage(int dx, int dy) {
		Dimension dim = renderer.getBounds();
		
		/* ****  ugly hack to decide where we should block the scrolling  **** */
		
		// start by getting the size of the visible area and allowed margin
		// consistent with the one used when drawing scroll bars
		int cw = getWidth()-SCROLLMARGIN;
		int ch = getHeight()-SCROLLMARGIN;
		int vert_cmargin = (int)(CANVASMARGIN*cw/zoom);
		int hor_cmargin = (int)(CANVASMARGIN*ch/zoom);

		cw /= zoom;
		ch /= zoom;
		
		int maxdx = -(int)dim.getWidth() + 35 - this.tr_x;
		int maxdy = -(int)dim.getHeight() + 35 - this.tr_y;
		// prevent going to negative coordinates
		if (dx > 0 && dx > -this.tr_x) {
			dx = -this.tr_x;
		} else if (dx < 0 && dx < maxdx) {
			dx = maxdx;
		}
		
		if (dy > 0 && dy > -this.tr_y) {
			dy = -this.tr_y;
		} else if (dy < 0 && dy < maxdy) {
			dy = maxdy;
		}
		
		// moving coordinates
		this.tr_x += dx;
		this.tr_y += dy;
		
		if (img == null || (dx == 0 && dy == 0)) {
			return;
		}
		
    	BufferedImage img = getNewBufferedImage();
    	Graphics2D g = img.createGraphics();
    	
    	g.drawImage(this.img, dx, dy, null);
    	
		// redraw only the sides
    	// Note: the corner is re-painted twice
    	if (dx > 0) {
    		Rectangle area = new Rectangle(0, 0, dx, getHeight()-SCROLLMARGIN);
        	paintAreaInBuffer(img, area);
    	} else if (dx < 0) {
    		Rectangle area = new Rectangle(getWidth()+dx-SCROLLMARGIN, 0, -dx, getHeight()-SCROLLMARGIN);
        	paintAreaInBuffer(img, area);
    	}

    	if (dy > 0) {
    		Rectangle area = new Rectangle(0, 0, getWidth()-SCROLLMARGIN, dy);
        	paintAreaInBuffer(img, area);
    	} else if (dy < 0) {
    		Rectangle area = new Rectangle(0, getHeight()+dy-SCROLLMARGIN, getWidth()-SCROLLMARGIN, -dy);
        	paintAreaInBuffer(img, area);
    	}

    	this.img = img;
    	repaint();
	}
	
	protected Point window2canvas(Point p) {
		if (zoom == 1) {
			return new Point(p.x-tr_x, p.y-tr_y);
		}
		return new Point((int)((p.x-tr_x)/zoom), (int)((p.y-tr_y)/zoom));
	}

	public void scroll(int wheelRotation, boolean alternate) {
		Dimension dim = renderer.getBounds();
		int tr = -10*wheelRotation;
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
	 * @param area the canvas area to mark as damaged
	 */
	public void damageCanvas(Rectangle area) {
		int x = (int)(area.x * zoom) + tr_x - 1;
		int y = (int)(area.y * zoom) + tr_y - 1;
		
		int width = (int)(area.width*zoom) +2;
		int height = (int)(area.height*zoom) +2;

		// only take into account visible parts
		if (x < 0) {
			width -= x;
			x = 0;
		}
		if (y < 0) {
			height -= y;
			y = 0;
		}
		
		if (width > 0 && height > 0) {
			damageScreen(new Rectangle(x,y, width, height));
		}
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

	public void help() {
		showHelp = true;
		repaint();
	}

	@Override
	public void reshape(int x, int y, int w, int h) {
		if (img != null) {
			img = null;
		}
		super.reshape(x, y, w, h);
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
		canvas.addKeyListener(this);
	}
	
	public void setEventManager(CanvasEventManager evtManager) {
		this.evtManager = evtManager;
	}

	private boolean alternate(MouseEvent e) {
		return e.isControlDown() || e.isMetaDown() || e.getButton() == MouseEvent.BUTTON2;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		Point p = e.getPoint();
		
		if (canvas.click(mouseButton, p)) {
			return;
		}
		
		if (evtManager != null) {
			evtManager.click(canvas.window2canvas(p), alternate(e));
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		canvas.requestFocusInWindow();
		Point p = e.getPoint();
		mouseButton = e.getButton();

		if (canvas.mousePressed(mouseButton, p)) {
			return;
		}
		
		if (evtManager != null) {
			evtManager.pressed(canvas.window2canvas(p), alternate(e));
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mouseButton = -1;
		if (canvas.mouseReleased()) {
			return;
		}
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
			case KeyEvent.VK_F1:
			case KeyEvent.VK_HELP:
			case KeyEvent.VK_H:
				canvas.help();
				break;
			}
		}
		
		switch (e.getKeyChar()) {
		case KeyEvent.VK_ESCAPE:
			canvas.cancel();
			break;
		case '?':
		case 'h':
		case 'H':
			canvas.help();
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
