package org.ginsim.gui.annotation.classes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;

/**
 * Produces a small button used to create/remove stuff (annotations...)
 *
 * @author Martin Boutroux
 */
class CircleButton extends JButton {
	private static final long serialVersionUID = 1L;

	private static final Color CREATE_NORMAL = new Color(40,130,70);
	private static final Color DELETE_NORMAL = new Color(190,20,20);
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
	    setFont(new Font("Arial", Font.BOLD, 14));
		setFocusable(false);

		if (create) {
			setForeground(CREATE_NORMAL);
			setToolTipText("Create");
		} else {
			setForeground(DELETE_NORMAL);
			setToolTipText("Delete");
		}
    }
}
