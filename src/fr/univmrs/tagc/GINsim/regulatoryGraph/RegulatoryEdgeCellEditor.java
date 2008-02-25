package fr.univmrs.tagc.GINsim.regulatoryGraph;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;


public class RegulatoryEdgeCellEditor extends AbstractCellEditor implements TableCellEditor {
	private static final long	serialVersionUID	= -2008594909104233122L;

	RegulatoryEdgeEditPanel panel;
	
	public RegulatoryEdgeCellEditor(GsRegulatoryGraph graph) {
		panel = new RegulatoryEdgeEditPanel(graph);
	}
	
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		panel.setEdge((GsRegulatoryEdge)value);
		return panel;
	}

	public Object getCellEditorValue() {
		return panel.edge;
	}
}
