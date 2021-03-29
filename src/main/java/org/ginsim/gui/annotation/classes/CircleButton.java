package org.ginsim.gui.annotation.classes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import javax.swing.JButton;

/**
 * Produces a circle button. It is used to create/remove stuff (annotations...)
 *
 * @author Martin Boutroux
 */
class CircleButton extends JButton {
	private static final long serialVersionUID = 1L;
	
	private Shape shape;
	private boolean create;
	
    CircleButton(String label, boolean createValue) {
		super(label);
		Dimension size = getPreferredSize();
		
		create = createValue;
		int max = Math.max(size.width, size.height);
		size.width = size.height = (int) (max/2.5);
		setPreferredSize(size);
		setContentAreaFilled(false);
		
		setBorder(null);
	    setFocusPainted(false);
	    setFont(new Font("Arial", Font.PLAIN, 14));
	    setFocusable(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
    	
    	if (create) {
	    	if (getModel().isPressed()) {
	            g.setColor(new Color(50,205,50));
	    	}
	    	else {
	    		g.setColor(new Color(0,255,0));
	    	}
    	} else {
	    	if (getModel().isPressed()) {
	            g.setColor(new Color(205,50,50));
	    	}
	    	else {
	    		g.setColor(new Color(255,0,0));
	    	}
    	}
        
        g.fillOval(0, 0, getSize().width-1, getSize().height-1);
        super.paintComponent(g);
   }
   
   @Override
   protected void paintBorder(Graphics g) {
        g.setColor(getForeground());
        g.drawOval(0, 0, getSize().width-1, getSize().height-1);
   }

   @Override
   public boolean contains(int x, int y) {
        if (shape == null || !shape.getBounds().equals(getBounds())) {
             shape = new Ellipse2D.Float(0, 0, getWidth(), getHeight());
        }
        return shape.contains(x, y);
   }
}
