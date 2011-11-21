package org.ginsim.gui.service.common;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

public abstract class LayoutAction extends BaseAction {

	public LayoutAction(String name) {
		super(name, null, null, null);
	}

	/**
     * 
     * @param name Entry to insert in the menu
     * @param tooltip Long description of the action
     */
	public LayoutAction(String name, String tooltip) {
		
		this(name, null, tooltip, null);
	}
	
	/**
     * 
     * @param name Entry to insert in the menu
     * @param icon icon image for menu and toolbar
     * @param tooltip Long description of the action
     * @param accelerator the keyboard bytecut
     */
	public LayoutAction(String name, ImageIcon icon, String tooltip, KeyStroke accelerator) {
		
		super(name, icon, tooltip, accelerator, null);
	}
}
