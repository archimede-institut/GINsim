package fr.univmrs.ibdm.GINsim.gui;

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
        super();
        this.name = name;
        this.descr = descr;
        this.icon = icon;
        this.mode = mode;
        this.submode = submode;
    }    
    
}
