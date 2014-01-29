package org.ginsim.gui.shell.actions;

import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import org.ginsim.gui.service.ServiceGUI;


public abstract class ToolkitAction extends BaseAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6752180058479316207L;

	public ToolkitAction(String name, ServiceGUI serviceGUI) {
		super(name, null, null, null, serviceGUI);
	}

	/**
     * 
     * @param name Entry to insert in the menu
     * @param tooltip Long description of the action
     */
	public ToolkitAction(String name, String tooltip, ServiceGUI serviceGUI) {
		
		this(name, null, tooltip, null, serviceGUI);
	}
	
	/**
     * 
     * @param name Entry to insert in the menu
     * @param icon icon image for menu and toolbar
     * @param tooltip Long description of the action
     * @param accelerator the keyboard bytecut
     */
	public ToolkitAction(String name, ImageIcon icon, String tooltip, KeyStroke accelerator, ServiceGUI serviceGUI) {
		
		super(name, icon, tooltip, accelerator, null, serviceGUI);
	}
	
}