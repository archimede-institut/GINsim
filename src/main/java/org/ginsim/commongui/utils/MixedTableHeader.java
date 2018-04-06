package org.ginsim.commongui.utils;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class MixedTableHeader implements TableCellRenderer {


    private final int min, max;
    private int height = 50;
    private final TableCellRenderer delegateRenderer;
    private final TableCellRenderer verticalRenderer;

    public MixedTableHeader(JTable table, int min) {
        this(table, min, Integer.MAX_VALUE);
    }

    public MixedTableHeader(JTable table, int min, int max) {
        JTableHeader header = table.getTableHeader();
        this.delegateRenderer = header.getDefaultRenderer();
        this.verticalRenderer = new VerticalTableHeaderCellRenderer();
        this.min = min;
        this.max = max;

        header.setDefaultRenderer(this);
    }

    public Component getTableCellRendererComponent(JTable table,  Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component cmp;
        if (column >= min && column <= max) {
            cmp = verticalRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        } else {
            cmp = delegateRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }

        Dimension dim = cmp.getPreferredSize();
        int h = dim.height;
        if (h > height) {
            height = h;
        } else {
            dim.height = height;
            cmp.setPreferredSize(dim);
        }
        return cmp;
    }
}
