package org.ginsim.servicegui.export.cadp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import org.ginsim.gui.utils.widgets.EnhancedJTable;
import org.ginsim.servicegui.tool.composition.CompositionSpecificationDialog;

/**
 * Widget to specify the initial states of each module
 * 
 * @author Nuno D. Mendes
 */
public class InitialStatesWidget extends JPanel {

	private static final long serialVersionUID = -2054629059623751148L;
	private CompositionSpecificationDialog dialog = null;
	private JScrollPane scrollPane = null;
	private EnhancedJTable tableInitStates = null;
	private JLabel messageLabel = new JLabel();

	public InitialStatesWidget(final CompositionSpecificationDialog dialog) {
		super();
		this.dialog = dialog;
		setLayout(new BorderLayout());

		// TODO: replace with STR_s
		setBorder(BorderFactory.createTitledBorder("Specify Initial States"));

		add(messageLabel, BorderLayout.NORTH);
		messageLabel.setForeground(Color.RED);
		add(getScrollPane(), BorderLayout.CENTER);
		setSize(getPreferredSize());

	}

	public void setMessage(String message) {
		this.messageLabel.setText(message);

	}

	private EnhancedJTable getTableInitialStates() {
		if (tableInitStates == null) {
			LRMInitialStateModel model = new LRMInitialStateModel(this, dialog);
			tableInitStates = new EnhancedJTable();
			tableInitStates.addCellRenderer(String.class,
					ShowNonEditableStringCellRenderer.class);
			tableInitStates.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			tableInitStates.setModel(model);
			tableInitStates.getTableHeader().setReorderingAllowed(false);
			tableInitStates.setRowSelectionAllowed(true);
			tableInitStates.setColumnSelectionAllowed(true);

			model.setTable(tableInitStates);

			tableInitStates.setSize(tableInitStates.getPreferredSize());
		}

		return tableInitStates;
	}

	private JScrollPane getScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane();

			EnhancedJTable tableInitialStates = getTableInitialStates();

			DefaultTableModel rowHeaderTableModel = new DefaultTableModel(0, 1) {
				private static final long serialVersionUID = 6486499169039037077L;

				@Override
				public boolean isCellEditable(int rowIndex, int colIndex) {
					return false;
				}

				@Override
				public String getColumnName(int columnIndex) {
					return "";
				}
			};
			for (int i = 0; i < this.dialog.getNumberInstances(); i++)
				rowHeaderTableModel
						.addRow(new Object[] { "Module " + (i + 1) });

			EnhancedJTable dispTableRowHeader = new EnhancedJTable();
			dispTableRowHeader.setModel(rowHeaderTableModel);
			dispTableRowHeader.setDefaultRenderer(Object.class,
					tableInitialStates.getTableHeader().getDefaultRenderer());

			scrollPane.setViewportView(tableInitialStates);
			scrollPane.setRowHeaderView(dispTableRowHeader);
			scrollPane.getRowHeader().setPreferredSize(
					dispTableRowHeader.getPreferredSize());

			JTableHeader corner = dispTableRowHeader.getTableHeader();
			corner.setReorderingAllowed(false);
			corner.setResizingAllowed(false);
			scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, corner);

		}

		return scrollPane;
	}

	public List<byte[]> getInitialStates() {
		return ((LRMInitialStateModel) getTableInitialStates().getModel())
				.getInitialStates();
	}

	public void fireInitialStatesUpdate() {
		((LRMInitialStateModel) this.getTableInitialStates().getModel())
				.fireTableDataChanged();
	}

}
