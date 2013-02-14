package org.ginsim.servicegui.export.cadp;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellRenderer;

/**
 * 
 * Class changing the TableCellRenderer in order to place visually identify
 * non-editable cells
 * 
 * @author Nuno D. Mendes
 * 
 */
public class ShowNonEditableStringCellRenderer implements TableCellRenderer {

	private TableCellRenderer _defaultRenderer;

	public ShowNonEditableStringCellRenderer(TableCellRenderer renderer) {
		_defaultRenderer = renderer;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		if (value instanceof Component) {
			return (Component) value;
		}

		JComponent c = (JComponent) _defaultRenderer
				.getTableCellRendererComponent(table, value, isSelected,
						hasFocus, row, column);

		if (table.getModel().isCellEditable(row, column)) {
			c.setBorder(null);

		} else {
			c.setBorder(new LineBorder(Color.RED));

		}
		return c;

	}

}