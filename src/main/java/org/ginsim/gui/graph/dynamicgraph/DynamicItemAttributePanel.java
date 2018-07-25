package org.ginsim.gui.graph.dynamicgraph;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.ginsim.core.graph.dynamicgraph.DynamicGraph;
import org.ginsim.gui.shell.editpanel.AbstractParameterPanel;
import org.ginsim.gui.utils.widgets.EnhancedJTable;


/**
 * basic info on a vertex of the state transition graph (ie state of the system)
 */
public class DynamicItemAttributePanel extends AbstractParameterPanel {

    private static final long serialVersionUID = 9208992495538557201L;

	private final DynamicItemModel tableModel;
	private final DynamicGraph stg;

	/**
	 * @param graph
	 */
	public DynamicItemAttributePanel(DynamicGraph graph) {
		super(graph);
		this.stg = graph;
		
		tableModel = new DynamicItemModel(stg);

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
	}
	
	public void setEditedItem(Object obj) {
        tableModel.setContent(obj);
        
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
