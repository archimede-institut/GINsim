package fr.univmrs.ibdm.GINsim.gui;

import java.awt.Color;

/**
 * in a JList or a JTable, this Object can "choose" its background color. 
 *
 */
public interface GsColorable {

    /**
     * @return the background color to use for this object
     */
    public Color getColor();
}
