package org.ginsim.gui.service;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;


public abstract class GsActionAction extends BaseAction{

	public GsActionAction(String name) {
		super(name, null, null, null);
	}

	/**
     * 
     * @param name Entry to insert in the menu
     * @param tooltip Long description of the action
     */
	public GsActionAction(String name, String tooltip) {
		
		this(name, null, tooltip, null);
	}
	
	/**
     * 
     * @param name Entry to insert in the menu
     * @param icon icon image for menu and toolbar
     * @param tooltip Long description of the action
     * @param accelerator the keyboard bytecut
     */
	public GsActionAction(String name, ImageIcon icon, String tooltip, KeyStroke accelerator) {
		
		super(name, icon, tooltip, accelerator, null);
	}
	
}
