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

	public static final int CREATE = 0;
	public static final int DELETE = 1;
	public static final int OPEN = 2;
	private static final Font FONT = new Font("Sans", Font.BOLD, 11);
	private static final Color CREATE_NORMAL = new Color(40,130,70);
	private static final Color OPEN_NORMAL = new Color(40,50,150);
	private static final Color DELETE_NORMAL = new Color(190,20,20);

    CircleButton(String label, int type) {
		super(label);
		Dimension size = getPreferredSize();
		
		int max = Math.max(size.width, size.height);
		size.width = size.height = (int) (max/2.5);
		setPreferredSize(size);
		setContentAreaFilled(false);
		
		setBorder(null);
	    setFocusPainted(false);
	    setFont(FONT);
		setFocusable(false);

		switch (type) {
			case CREATE:
				setForeground(CREATE_NORMAL);
				setToolTipText("Create");
				break;
			case DELETE:
				setForeground(DELETE_NORMAL);
				setToolTipText("Delete");
				break;
			case OPEN:
				setForeground(OPEN_NORMAL);
				setToolTipText("Open");
				break;
		}
    }
}
