package org.ginsim.gui.graph.regulatorygraph;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import org.ginsim.core.graph.regulatorygraph.RegulatoryEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;


public class RegulatoryEdgeCellEditor extends AbstractCellEditor implements TableCellEditor {
	private static final long	serialVersionUID	= -2008594909104233122L;

	RegulatoryEdgeEditPanel panel;
	
	public RegulatoryEdgeCellEditor(RegulatoryGraph graph) {
		panel = new RegulatoryEdgeEditPanel(graph);
	}
	
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		panel.setEdge((RegulatoryEdge)value);
		return panel;
	}

	public Object getCellEditorValue() {
		return panel.edge;
	}
}
