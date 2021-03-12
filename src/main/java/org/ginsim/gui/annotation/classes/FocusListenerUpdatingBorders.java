package org.ginsim.gui.annotation.classes;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;

/**
 * Produces a focus listener that changes the borders of its element when it is triggered
 *
 * @author Martin Boutroux
 */
class FocusListenerUpdatingBorders implements FocusListener {
	
	private Border borderInFocus;
	private Border borderOutFocus;
	
	FocusListenerUpdatingBorders(Color colorInFocus, Color colorOutFocus) {
		super();
		
		this.borderInFocus = BorderFactory.createLineBorder(colorInFocus, 2);
		
		if (colorOutFocus == null) {
			this.borderOutFocus = null;
		} else {
			this.borderOutFocus = BorderFactory.createLineBorder(colorOutFocus);
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		JComponent component = (JComponent) e.getSource();
		component.setBorder(borderInFocus);
	}

	@Override
	public void focusLost(FocusEvent e) {
		JComponent component = (JComponent) e.getSource();
		component.setBorder(borderOutFocus);
	}

}
