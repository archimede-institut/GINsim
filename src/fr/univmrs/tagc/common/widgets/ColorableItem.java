package fr.univmrs.tagc.common.widgets;

import java.awt.Color;

/**
 * in a JList or a JTable, this Object can "choose" its background color. 
 *
 */
public interface ColorableItem {

    /**
     * @return the background color to use for this object
     */
    public Color getColor();
}
