package fr.univmrs.ibdm.GINsim.piccolo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * The main layer in our canvas.
 * It can draw an optionnal grid.
 */
public class GsPLayer extends PLayer {
	private static final long serialVersionUID = -4374326916352497189L;
	
	static protected float gridSpacing = 40;
	static protected Line2D gridLine = new Line2D.Double();
	private static final float[] t_dash = {1, gridSpacing}; 
	static protected BasicStroke gridStroke = new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, t_dash, 0.0f);
	static protected Color gridPaint = Color.GRAY;
	
	private boolean gridVisible = true;
	private boolean edgesAbove = false;
	
	PCamera camera;
	
	protected GsPLayer(PCamera camera) {
		this.camera = camera;
		setBounds(camera.getViewBounds());
	}
	
	protected void paint(PPaintContext paintContext) {
		PropertyChangeListener pchange = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				updateBounds();
			}
		};
		camera.addPropertyChangeListener(PNode.PROPERTY_BOUNDS, pchange);
		camera.addPropertyChangeListener(PCamera.PROPERTY_VIEW_TRANSFORM, pchange);

		if (gridVisible) {
			paintGrid(paintContext, camera.getViewBounds());
		}
		if (edgesAbove) {
			paintNodes(paintContext);
			paintEdges(paintContext);
		} else {
			paintEdges(paintContext);
			paintNodes(paintContext);
		}
	}
	
	protected void updateBounds() {
		if (gridVisible) {
			setBounds(camera.getViewBounds());
		}
	}
	
	protected void setGrid(boolean visible) {
		gridVisible = visible;
	}
	
	private void paintGrid(PPaintContext paintContext, Rectangle2D rect) {
		// TODO: find a way to always draw a nice grid...
		double bx = (rect.getX() - (rect.getX() % gridSpacing)) - gridSpacing;
		double by = (rect.getY() - (rect.getY() % gridSpacing)) - gridSpacing;
		double rightBorder = rect.getX() + rect.getWidth() + gridSpacing;
		double bottomBorder = rect.getY() + rect.getHeight() + gridSpacing;

		Graphics2D g2 = paintContext.getGraphics();
		Rectangle2D clip = paintContext.getLocalClip();
		
		Stroke tmpStroke = g2.getStroke();
		Paint tmpPaint = g2.getPaint();
		g2.setStroke(gridStroke);
		g2.setPaint(gridPaint);

		for (double x = bx; x < rightBorder; x += gridSpacing) {
			gridLine.setLine(x, by, x, bottomBorder);
			if (clip.intersectsLine(gridLine)) {
				g2.draw(gridLine);
			}
		}
		g2.setStroke(tmpStroke);
		g2.setPaint(tmpPaint);
	}
	
	private void paintNodes(PPaintContext pc) {
		
	}
	private void paintEdges(PPaintContext pc) {
		
	}
	
}
