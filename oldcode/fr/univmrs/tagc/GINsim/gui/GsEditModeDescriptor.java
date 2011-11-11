package fr.univmrs.tagc.GINsim.gui;

import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;

/**
 * describes an edit mode
 */
public class GsEditModeDescriptor {

    /** name of the action (menu entry) */
    public final String name;
    /** tooltip text */
    public final String descr;
    /** icon for menu and toolbar */
    public final ImageIcon icon;
    /** The keystroke for the menu*/
    public final int key;
    
    /** 
     * edit mode
     * @see GsActions#MODE_DEFAULT and friends  
     */
    public final int mode;
    /** edit submode (ie option for the edit mode) */
    public final int submode;

    /**
	 * @param name
	 * @param descr
	 * @param icon
	 * @param mode
	 * @param submode
	 */
	public GsEditModeDescriptor(String name, String descr, ImageIcon icon,
	        int mode, int submode) {
				this(name, descr, icon, mode, submode, KeyEvent.VK_UNDEFINED);
			}

	/**
     * @param name
     * @param descr
     * @param icon
     * @param mode
     * @param submode
     * @param key TODO
     */
    public GsEditModeDescriptor(String name, String descr, ImageIcon icon,
            int mode, int submode, int key) {
        super();
        this.name = name;
        this.descr = descr;
        this.icon = icon;
        this.mode = mode;
        this.submode = submode;
        this.key = key;
    }    
    
}
