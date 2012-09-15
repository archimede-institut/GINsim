package org.ginsim.gui.graph.canvas;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
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
public class SimpleCanvas extends JComponent implements VirtualScrollable {

	private final static double MAXZOOM = 10;
	private final static double MINZOOM = 0.1;
	
	private final static RenderingHints RENDER_HINTS;
	
	static {
		RENDER_HINTS = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		RENDER_HINTS.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	}
	
	private CanvasRenderer renderer = null;
	private final CanvasEventListener mouseListener;
	private VirtualScrollPane scrollPane = null;
	
	/** zoom factor */
	private double zoom = 1;
	
	/** translation: the top-left corner is (-tr_x, -tr_y). Only negative translations are accepted */
	private int tr_x=0, tr_y=0;
	
	/** cached image for double buffering and easy overlay */
	private BufferedImage img;

	/** part of the canvas that was changed and should be repainted */
    private Rectangle damagedRegion = null;
    private final Rectangle visibleArea = new Rectangle();
    private final Dimension virtualDimension = new Dimension();
    
    private boolean visibleAreaUpdated = true, virtualDimensionUpdated = true;
    
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

	public void setScrollPane(VirtualScrollPane scrollPane) {
		this.scrollPane = scrollPane;
	}

	public BufferedImage getNewBufferedImage() {
    	return new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
	}
	
	private void paintBuffer() {
    	BufferedImage img = getNewBufferedImage();
    	paintAreaInBuffer(img, null);
	}
	
	private void paintAreaInBuffer(BufferedImage img, Rectangle area) {
		if (area == null) {
			area = getVisibleArea();
		} else {
			area = getVisibleArea().intersection(area);
		}
    	Graphics2D g = img.createGraphics();
    	g.setRenderingHints(RENDER_HINTS);
		// erase the whole area
		g.setColor(backgroundColor);
		
		if (renderer != null) {
			transform(g);
	    	g.clip(area);
			g.fill(area);
			renderer.render(g,area);
			virtualDimensionUpdated = true;
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
		
		if (scrollPane != null) {
			scrollPane.fireViewUpdated();
		}

		// add overlay layer to the image, by the renderer
		Graphics2D g2 = (Graphics2D)g;
		transform(g2);

		Rectangle overlayRect = new Rectangle(0,0, (int)(getWidth()/zoom), (int)(getHeight()/zoom));
		if (showHelp) {
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.9f));
			renderer.helpOverlay(g2, overlayRect);
			showHelp = false;
		} else {
			renderer.overlay(g2, overlayRect);
		}
	}

	private void transform(Graphics2D g) {
		g.translate(-tr_x*zoom, -tr_y*zoom);
		g.scale(zoom, zoom);
	}
	
	@Override
	public Component getComponent() {
		return this;
	}
	
	@Override
	public Dimension getVirtualDimension() {
		if (virtualDimensionUpdated) {
			getVisibleArea();
			virtualDimension.setSize(renderer.getBounds());
			// minimum: visible area
			int min = 1 + visibleArea.width / 2;
			if (virtualDimension.width <= min) {
				virtualDimension.width = visibleArea.width;
			} else {
				virtualDimension.width += min;
			}
			
			min = 1 + visibleArea.height / 2;
			if (virtualDimension.height < min) {
				virtualDimension.height = visibleArea.height;
			} else {
				virtualDimension.height += min;
			}
			virtualDimensionUpdated = false;
		}
		return virtualDimension;
	}

	@Override
	public Rectangle getVisibleArea() {
		if (visibleAreaUpdated) {
			visibleArea.x = tr_x;
			visibleArea.y = tr_y;
			visibleArea.width = (int)(getWidth()/zoom);
			visibleArea.height = (int)(getHeight()/zoom);
			visibleAreaUpdated = false;
		}
		return visibleArea;
	}
	
	public void zoom(int n) {
		if (n > 0) {
			if (zoom >= MAXZOOM) {
				return;
			}
			zoom += .1;
		} else if (n < 0) {
			if (zoom <= MINZOOM) {
				return;
			}
			zoom -= .1;
		} else if (zoom == 1) {
			return;
		} else {
			zoom = 1;
		}
		img = null;
		visibleAreaUpdated = true;
		repaint();
	}
	

	@Override
	public void setScrollPosition(int x, int y) {
		if (x == tr_x && y == tr_y) {
			return;
		}
		
		Rectangle visible = getVisibleArea();
		Dimension dim = getVirtualDimension();

		int maxx = dim.width - visible.width;
		int maxy = dim.height - visible.height;
		
		if (x < 0) {
			x = 0;
		} else if (x > maxx) {
			x = maxx;
		}
		
		if (y < 0) {
			y = 0;
		} else if (y > maxy) {
			y = maxy;
		}
		
		int dx = x - this.tr_x;
		int dy = y - this.tr_y;
		
		if (dx == 0 && dy == 0) {
			return;
		}
		
		this.tr_x = x;
		this.tr_y = y;
		visibleAreaUpdated = true;

		if (img == null) {
			return;
		}
		
		dim = getVirtualDimension();
		
		// FIXME: move image and redraw only the revealed parts
		paintAreaInBuffer(img, null);
		
//    	BufferedImage img = getNewBufferedImage();
//    	Graphics2D g = img.createGraphics();
//    	
//    	dx *= zoom;
//    	dy *= zoom;
//    	
//    	g.drawImage(this.img, dx, dy, null);
//    	
//		// redraw only the sides
//    	// Note: the corner is re-painted twice
//    	if (dx > 0) {
//    		Rectangle area = new Rectangle(0, 0, dx, getHeight());
//        	paintAreaInBuffer(img, area);
//    	} else if (dx < 0) {
//    		Rectangle area = new Rectangle(getWidth()+dx, 0, -dx, getHeight());
//        	paintAreaInBuffer(img, area);
//    	}
//
//    	if (dy > 0) {
//    		Rectangle area = new Rectangle(0, 0, getWidth(), dy);
//        	paintAreaInBuffer(img, area);
//    	} else if (dy < 0) {
//    		Rectangle area = new Rectangle(0, getHeight()+dy, getWidth(), -dy);
//        	paintAreaInBuffer(img, area);
//    	}
//
//    	this.img = img;
		
		
		repaint();
	}

	protected Point window2canvas(Point p) {
		Point ret = new Point(tr_x + (int)(p.x/zoom), tr_y + (int)(p.y/zoom));
		return ret;
	}

	public void scroll(int wheelRotation, boolean alternate) {
		int tr = (int)(10*wheelRotation*zoom);
		Rectangle visible = getVisibleArea();
		if (alternate) {
			setScrollPosition(visible.x+tr, visible.y);
		} else {
			setScrollPosition(visible.x, visible.y + tr);
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
		visibleAreaUpdated = true;
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
		
		if (evtManager != null) {
			evtManager.click(canvas.window2canvas(p), alternate(e));
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		canvas.requestFocusInWindow();
		Point p = e.getPoint();
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
		
		canvas.zoom(-e.getWheelRotation());
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
