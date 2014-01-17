package org.ginsim.gui.utils.data;

import javax.swing.*;

/**
 * Describe a column for use in a ListPanel
 */
public class ColumnDefinition {

    public static final ColumnDefinition SINGLE = new ColumnDefinition(null, String.class, false);
    public static final ColumnDefinition ACTION = new ColumnDefinition(null, JButton.class, false);
    public static final ColumnDefinition EDITME = new ColumnDefinition(null, String.class, true);
    public static final ColumnDefinition SELECT = new ColumnDefinition(null, Boolean.class, true);

    public final String title;
    public final Class type;
    public final boolean editable;

    public ColumnDefinition(String title, Class type, boolean editable) {
        this.title = title;
        this.type = type;
        this.editable = editable;
    }
}
