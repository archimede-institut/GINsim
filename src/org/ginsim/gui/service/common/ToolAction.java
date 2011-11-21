package org.ginsim.gui.service.common;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;


public abstract class ToolAction extends BaseAction{

	public ToolAction(String name) {
		super(name, null, null, null);
	}

	/**
     * 
     * @param name Entry to insert in the menu
     * @param tooltip Long description of the action
     */
	public ToolAction(String name, String tooltip) {
		
		this(name, null, tooltip, null);
	}
	
	/**
     * 
     * @param name Entry to insert in the menu
     * @param icon icon image for menu and toolbar
     * @param tooltip Long description of the action
     * @param accelerator the keyboard bytecut
     */
	public ToolAction(String name, ImageIcon icon, String tooltip, KeyStroke accelerator) {
		
		super(name, icon, tooltip, accelerator, null);
	}
	
}
