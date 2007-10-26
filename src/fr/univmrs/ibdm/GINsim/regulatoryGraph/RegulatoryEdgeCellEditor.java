package fr.univmrs.ibdm.GINsim.regulatoryGraph;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.table.TableCellEditor;


public class RegulatoryEdgeCellEditor extends AbstractCellEditor implements TableCellEditor {
	private static final long	serialVersionUID	= -2008594909104233122L;

	EdgeEditPanel panel;
	
	public RegulatoryEdgeCellEditor(GsRegulatoryGraph graph) {
		panel = new EdgeEditPanel(graph);
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

class EdgeEditPanel extends JPanel implements ActionListener {
	private static final long	serialVersionUID	= 5147198338786927504L;

	GsRegulatoryEdge edge;
	GsRegulatoryGraph graph;
	
	private EdgeThresholdModel thmodel = null;
	private JComboBox signcombo;

	public EdgeEditPanel(GsRegulatoryGraph graph) {
		this.graph = graph;
        thmodel = new EdgeThresholdModel();
        signcombo = new JComboBox(GsRegulatoryMultiEdge.SIGN_SHORT);
        add(new JSpinner(thmodel));
        add(signcombo);
        signcombo.addActionListener(this);
	}
	public void setEdge(GsRegulatoryEdge edge) {
		this.edge = edge;
		thmodel.setSelection(edge);
		signcombo.setSelectedIndex(edge.sign);
	}
	public void actionPerformed(ActionEvent e) {
		short s = (short)signcombo.getSelectedIndex();
		if (s != edge.sign && s >= 0 && s<GsRegulatoryMultiEdge.SIGN_SHORT.length) {
			edge.me.setSign(edge.index, s, graph);
		}
	}
}

class EdgeThresholdModel extends AbstractSpinnerModel {
	GsRegulatoryEdge edge;
	
	public void setSelection(GsRegulatoryEdge edge) {
		this.edge = edge;
		fireStateChanged();
	}
	
	public Object getNextValue() {
		if (edge != null) {
			edge.me.setMin(edge.index, (short)(edge.getMin()+1));
			fireStateChanged();
		}
		return getValue();
	}

	public Object getPreviousValue() {
		if (edge != null) {
			edge.me.setMin(edge.index, (short)(edge.getMin()-1));
			fireStateChanged();
		}
		return getValue();
	}

	public Object getValue() {
		if (edge != null) {
			return new Integer(edge.getMin());
		}
		return null;
	}

	public void setValue(Object value) {
		if (edge != null) {
			if (value == null) {
				return;
			}
			if (value instanceof Integer) {
				edge.me.setMin(edge.index, ((Integer)value).shortValue());
				fireStateChanged();
			}
		}
	}
}
