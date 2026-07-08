package org.ginsim.gui.annotation.classes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JButton;

/**
 * Produces a triangle button. It is used to hide/remove stuff
 *
 * @author Martin Boutroux
 */
class TriangleButton extends JButton {
	private static final long serialVersionUID = 1L;
	
	private boolean show = true;
	private Color color = Color.blue;

    TriangleButton() {
    	Dimension size = getPreferredSize();
    	size.width = size.height = Math.max(size.width, size.height)/3;
    	setPreferredSize(size);
    	setContentAreaFilled(false);
    	
    	setBorder(null);
    	setFocusable(false);
    }
    
    TriangleButton(boolean initialShow, Color originalColor) {
    	this();
    	show = initialShow;
    	color = originalColor;
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(color);
        
        int xPoints[];
        int yPoints[];
        if (show) {
        	xPoints = new int[]{getSize().width/2, getSize().width, 0};
        	yPoints = new int[]{getSize().height, 0, 0};
        }
        else {
        	xPoints = new int[]{0, 0, getSize().width};
        	yPoints = new int[]{0, getSize().height, getSize().height/2};
        }
        g.fillPolygon(xPoints, yPoints, xPoints.length);
        super.paintComponent(g);
   }
   
   @Override
   protected void paintBorder(Graphics g) {
        g.setColor(getForeground());
        
        int xPoints[];
        int yPoints[];
        if (show) {
        	xPoints = new int[]{getSize().width/2, getSize().width, 0};
        	yPoints = new int[]{getSize().height, 0, 0};
        }
        else {
        	xPoints = new int[]{0, 0, getSize().width};
        	yPoints = new int[]{0, getSize().height, getSize().height/2};
        }
        g.drawPolygon(xPoints, yPoints, xPoints.length);
   }
   
   @Override
   public boolean contains(int x, int y) {
	   return false;
   }
   
   boolean getShow() {
	   return show;
   }
   
   void setShow() {
	   show = !show;
   }
}
