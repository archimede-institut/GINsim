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

    public int fixedSize;

    public ColumnDefinition(String title, Class type, boolean editable, int fixedSize) {
        this(title, type, editable);
        this.fixedSize = fixedSize;
    }

    public ColumnDefinition(String title, Class type, boolean editable) {
        this.title = title;
        this.editable = editable;
        this.type = type;
        if (type == Boolean.class) {
            fixedSize = 30;
        } else {
            fixedSize = -1;
        }
    }
}
