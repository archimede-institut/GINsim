package org.ginsim.commongui.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * A default cell renderer for a JTableHeader.
 * <P>
 * DefaultTableHeaderCellRenderer attempts to provide identical behavior to the
 * renderer which the Swing subsystem uses by default, the Sun proprietary
 * class sun.swing.table.DefaultTableCellHeaderRenderer.
 * <P>
 * To apply any desired customization, DefaultTableHeaderCellRenderer may be
 * suitably extended.
 * 
 * @author Darryl
 */
@SuppressWarnings("serial")
public class DefaultTableHeaderCellRenderer extends DefaultTableCellRenderer {
	private boolean horizontalTextPositionSet;
	
    /**
     * Constructs a <code>DefaultTableHeaderCellRenderer</code>.
     * <P>
     * The horizontal alignment and text position are set as appropriate to a
     * table header cell, and the opaque property is set to false.
     */
    public DefaultTableHeaderCellRenderer() {
    	setHorizontalAlignment(CENTER);
	    setHorizontalTextPosition(LEFT);
	    setVerticalAlignment(BOTTOM);
	    setOpaque(false);
//	    super.setBorder(new BevelBorder(BevelBorder.RAISED));
	  }

    public Component getTableCellRendererComponent(JTable table, Object value,
    		boolean isSelected, boolean hasFocus, int row, int column) {
    	Icon sortIcon = null;
    	boolean isPaintingForPrint = false;
    	
    	if (table != null) {
    		JTableHeader header = table.getTableHeader();
    		if (header != null) {
    			Color fgColor = null;
    			Color bgColor = null;
    			if (hasFocus) {
    				fgColor = UIManager.getColor("TableHeader.focusCellForeground");
    				bgColor = UIManager.getColor("TableHeader.focusCellBackground");
    			}
    			if (fgColor == null) {
    				fgColor = header.getForeground();
    			}
    			if (bgColor == null) {
    				bgColor = header.getBackground();
    			}
    			setForeground(fgColor);
    			setBackground(bgColor);

    			setFont(header.getFont());

    			isPaintingForPrint = header.isPaintingForPrint();
    		}

    		if (!isPaintingForPrint && table.getRowSorter() != null) {
    			if (!horizontalTextPositionSet) {
    				// There is a row sorter, and the developer hasn't
    				// set a text position, change to leading.
    				setHorizontalTextPosition(JLabel.LEADING);
    			}
    			sortIcon = getIcon(table, column);
    		}
    	}

    	setText(value == null ? "" : value.toString());
    	setIcon(sortIcon);

//    	Border border = null;
//    	if (hasFocus) {
//    		border = UIManager.getBorder("TableHeader.focusCellBorder");
//    	}
//    	if (border == null) {
//    		border = UIManager.getBorder("TableHeader.cellBorder");
//    	}
//    	setBorder(border);

    	return this;
    }

    public static SortOrder getColumnSortOrder(JTable table, int column) {
    	SortOrder rv = null;
        if (table.getRowSorter() == null) {
            return rv;
        }
        java.util.List<? extends RowSorter.SortKey> sortKeys =
            table.getRowSorter().getSortKeys();
        if (sortKeys.size() > 0 && sortKeys.get(0).getColumn() ==
            table.convertColumnIndexToModel(column)) {
            rv = sortKeys.get(0).getSortOrder();
        }
        return rv;
    }

//    @Override
//    public void paintComponent(Graphics g) {
//        boolean b = UIManager.getBoolean("TableHeader.rightAlignSortArrow");
//        if (b && sortArrow != null) {
//            //emptyIcon is used so that if the text in the header is right
//            //aligned, or if the column is too narrow, then the text will
//            //be sized appropriately to make room for the icon that is about
//            //to be painted manually here.
//            emptyIcon.width = sortArrow.getIconWidth();
//            emptyIcon.height = sortArrow.getIconHeight();
//            setIcon(emptyIcon);
//            super.paintComponent(g);
//            Point position = computeIconPosition(g);
//            sortArrow.paintIcon(this, g, position.x, position.y);
//        } else {
//            super.paintComponent(g);
//        }
//    }

//    private Point computeIconPosition(Graphics g) {
//        FontMetrics fontMetrics = g.getFontMetrics();
//        Rectangle viewR = new Rectangle();
//        Rectangle textR = new Rectangle();
//        Rectangle iconR = new Rectangle();
//        Insets i = getInsets();
//        viewR.x = i.left;
//        viewR.y = i.top;
//        viewR.width = getWidth() - (i.left + i.right);
//        viewR.height = getHeight() - (i.top + i.bottom);
//        SwingUtilities.layoutCompoundLabel(
//            this,
//            fontMetrics,
//            getText(),
//            sortArrow,
//            getVerticalAlignment(),
//            getHorizontalAlignment(),
//            getVerticalTextPosition(),
//            getHorizontalTextPosition(),
//            viewR,
//            iconR,
//            textR,
//            getIconTextGap());
//        int x = getWidth() - i.right - sortArrow.getIconWidth();
//        int y = iconR.y;
//        return new Point(x, y);
//    }

	  /**
	   * Overloaded to return an icon suitable to the primary sorted column, or null if
	   * the column is not the primary sort key.
	   *
	   * @param table the <code>JTable</code>.
	   * @param column the column index.
	   * @return the sort icon, or null if the column is unsorted.
	   */
	  protected Icon getIcon(JTable table, int column) {
	    SortKey sortKey = getSortKey(table, column);
	    if (sortKey != null && table.convertColumnIndexToView(sortKey.getColumn()) == column) {
	      switch (sortKey.getSortOrder()) {
	        case ASCENDING:
	          return UIManager.getIcon("Table.ascendingSortIcon");
	        case DESCENDING:
	          return UIManager.getIcon("Table.descendingSortIcon");
	      }
	    }
	    return null;
	  }


	  /**
	   * Returns the current sort key, or null if the column is unsorted.
	   *
	   * @param table the table
	   * @param column the column index
	   * @return the SortKey, or null if the column is unsorted
	   */
	  protected SortKey getSortKey(JTable table, int column) {
	    RowSorter<?> rowSorter = table.getRowSorter();
	    if (rowSorter == null) {
	      return null;
	    }

	    List<?> sortedColumns = rowSorter.getSortKeys();
	    if (sortedColumns.size() > 0) {
	      return (SortKey) sortedColumns.get(0);
	    }
	    return null;
	  }

		  
//	  /**
//	   * Returns the default table header cell renderer.
//	   * <P>
//	   * If the column is sorted, the appropriate icon is retrieved from the
//	   * current Look and Feel, and a border appropriate to a table header cell
//	   * is applied.
//	   * <P>
//	   * Subclasses may override this method to provide custom content or
//	   * formatting.
//	   *
//	   * @param table the <code>JTable</code>.
//	   * @param value the value to assign to the header cell
//	   * @param isSelected This parameter is ignored.
//	   * @param hasFocus This parameter is ignored.
//	   * @param row This parameter is ignored.
//	   * @param column the column of the header cell to render
//	   * @return the default table header cell renderer
//	   */
//	  @Override
//	  public Component getTableCellRendererComponent(JTable table, Object value,
//	          boolean isSelected, boolean hasFocus, int row, int column) {
//	    super.getTableCellRendererComponent(table, value,
//	            isSelected, hasFocus, row, column);
//	    setIcon(getIcon(table, column));
//	    setBorder(UIManager.getBorder("TableHeader.cellBorder"));
//	    return this;
//	  }


	  private class EmptyIcon implements Icon {
		  int width = 0;
		  int height = 0;
		  public void paintIcon(Component c, Graphics g, int x, int y) {}
		  public int getIconWidth() { return width; }
		  public int getIconHeight() { return height; }
	  }
}
