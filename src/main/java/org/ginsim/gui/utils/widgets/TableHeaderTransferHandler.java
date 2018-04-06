package org.ginsim.gui.utils.widgets;

import org.ginsim.gui.utils.data.MyBasicTransferable;

import javax.swing.*;
import javax.swing.plaf.UIResource;
import java.awt.datatransfer.Transferable;

public class TableHeaderTransferHandler extends TransferHandler implements UIResource {

    /**
     * Create a Transferable to use as the source for a data transfer.
     *
     * @param c  The component holding the data to be transfered.  This
     *  argument is provided to enable sharing of TransferHandlers by
     *  multiple components.
     * @return  The representation of the data to be transfered.
     *
     */
    protected Transferable createTransferable(JComponent c) {
        if (c instanceof JTable) {
            JTable table = (JTable) c;
            int[] rows;
            int[] cols;

            if (!table.getRowSelectionAllowed() && !table.getColumnSelectionAllowed()) {
                return null;
            }

            if (!table.getRowSelectionAllowed()) {
                int rowCount = table.getRowCount();

                rows = new int[rowCount];
                for (int counter = 0; counter < rowCount; counter++) {
                    rows[counter] = counter;
                }
            } else {
                rows = table.getSelectedRows();
            }

            if (!table.getColumnSelectionAllowed()) {
                int colCount = table.getColumnCount();

                cols = new int[colCount];
                for (int counter = 0; counter < colCount; counter++) {
                    cols[counter] = counter;
                }
            } else {
                cols = table.getSelectedColumns();
            }

            if (rows == null || cols == null || rows.length == 0 || cols.length == 0) {
                return null;
            }

            StringBuffer plainBuf = new StringBuffer();
            StringBuffer htmlBuf = new StringBuffer();

            htmlBuf.append("<html>\n<body>\n<table>\n");

            // Add the header
            htmlBuf.append("<tr>\n");
            for (int col = 0; col < cols.length; col++) {
                String name = table.getColumnName(col);
                plainBuf.append(name + "\t");
                htmlBuf.append("  <th>" + name + "</th>\n");
            }

            plainBuf.deleteCharAt(plainBuf.length() - 1).append("\n");
            htmlBuf.append("</tr>\n");
            for (int row = 0; row < rows.length; row++) {
                htmlBuf.append("<tr>\n");
                for (int col = 0; col < cols.length; col++) {
                    Object obj = table.getValueAt(rows[row], cols[col]);
                    String val = ((obj == null) ? "" : obj.toString());
                    plainBuf.append(val + "\t");
                    htmlBuf.append("  <td>" + val + "</td>\n");
                }
                // we want a newline at the end of each line and not a tab
                plainBuf.deleteCharAt(plainBuf.length() - 1).append("\n");
                htmlBuf.append("</tr>\n");
            }

            // remove the last newline
            plainBuf.deleteCharAt(plainBuf.length() - 1);
            htmlBuf.append("</table>\n</body>\n</html>");

            return new MyBasicTransferable(plainBuf.toString(), htmlBuf.toString());
        }

        return null;
    }

    public int getSourceActions(JComponent c) {
        return COPY;
    }
}

