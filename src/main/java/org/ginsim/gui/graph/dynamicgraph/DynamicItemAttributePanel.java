package org.ginsim.gui.graph.dynamicgraph;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.ginsim.core.graph.dynamicgraph.DynamicGraph;
import org.ginsim.core.graph.dynamicgraph.DynamicNode;
import org.ginsim.gui.shell.editpanel.AbstractParameterPanel;
import org.ginsim.gui.utils.widgets.EnhancedJTable;


/**
 * basic info on a vertex of the state transition graph (ie state of the system)
 */
public class DynamicItemAttributePanel extends AbstractParameterPanel {

    private static final long serialVersionUID = 9208992495538557201L;

	private final DynamicItemModel tableModel;
	private final ExtraTableModel extraTableModel;
	private final DynamicGraph stg;
	private final byte[] extraValues;
	
	/**
	 * @param graph
	 */
	public DynamicItemAttributePanel(DynamicGraph graph) {
		super(graph);
		this.stg = graph;
		
		tableModel = new DynamicItemModel(stg);
		String[] extra = stg.getExtraNames();
		
		// assemble the panel content
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        this.add(new StateActionPanel(tableModel), c);
        
        c.gridy++;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        this.add(getScrollPane(tableModel), c);

        if (extra != null && extra.length > 0) {
			// FIXME: create extra table model!
			extraTableModel = new ExtraTableModel(extra);
			extraValues = new byte[extra.length];
			
            c.gridy++;
	        c.weighty = 1;
	        c.fill = GridBagConstraints.BOTH;
            this.add(getScrollPane(extraTableModel), c);
		} else {
			extraValues = null;
			extraTableModel = null;
		}
	}
	
	public void setEditedItem(Object obj) {
        tableModel.setContent(obj);
        
        if (extraTableModel != null) {
        	if (obj instanceof DynamicNode) {
        		DynamicNode node = (DynamicNode)obj;
        		extraTableModel.setValues( stg.fillExtraValues(node.state, extraValues) );
        	} else {
        		extraTableModel.setValues(null);
        	}
        }
	}

	/**
	 * Create a Scroll pane containing a JTable for the given table model.
	 * 
	 * @param tmodel
	 * @return
	 */
	private JScrollPane getScrollPane(TableModel tmodel) {
		JTable table = new EnhancedJTable(tmodel);
		table.setDefaultRenderer(Object.class, new DynamicItemCellRenderer());
		table.getTableHeader().setReorderingAllowed(false);
		
		JScrollPane jScrollPane = new JScrollPane();
		jScrollPane.setViewportView(table);
		jScrollPane.setSize(88, 104);
		return jScrollPane;
	}
}


class ExtraTableModel extends AbstractTableModel implements StateTableModel {

	private final String[] titles;
	private byte[] values = null;
	
	public ExtraTableModel(String[] titles) {
		this.titles = titles;
	}

	public void setValues(byte[] values) {
		this.values = values;
		fireTableDataChanged();
	}
	
	@Override
	public int getColumnCount() {
		return titles.length;
	}
	
	@Override
	public String getColumnName(int column) {
		return titles[column];
	}

	@Override
	public int getRowCount() {
		if (values == null) {
			return 0;
		}
		return 1;
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return values[columnIndex];
	}

	@Override
	public byte[] getState(int index) {
		return values;
	}

	@Override
	public int getComponentCount() {
		return titles.length;
	}

	@Override
	public String getComponentName(int index) {
		return titles[index];
	}
	
}