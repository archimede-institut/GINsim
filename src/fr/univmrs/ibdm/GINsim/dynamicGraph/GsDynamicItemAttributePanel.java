package fr.univmrs.ibdm.GINsim.dynamicGraph;

import java.awt.BorderLayout;
import java.awt.Dimension;

import fr.univmrs.ibdm.GINsim.gui.GsParameterPanel;
import fr.univmrs.tagc.widgets.EnhancedJTable;

/**
 * basic info on a vertex of the state transition graph (ie state of the system)
 */
public class GsDynamicItemAttributePanel extends GsParameterPanel {

    private static final long serialVersionUID = 9208992495538557201L;
	private javax.swing.JScrollPane jScrollPane = null;  //  @jve:visual-info  decl-index=0 visual-constraint="204,-174"
	private javax.swing.JTable jTable = null;

	/**
	 * @param graph
	 */
	public GsDynamicItemAttributePanel(GsDynamicGraph graph) {
		super();
		this.graph = graph;
		initialize();
	}
	
	public void setEditedObject(Object obj) {
        ((GsDynamicItemModel)getJTable().getModel()).setContent(obj);
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
        this.setLayout(new BorderLayout());
        this.add(getJScrollPane(), null);
        this.setMinimumSize(new Dimension(20,20));
	}
	/**
	 * This method initializes jTable
	 * 
	 * @return javax.swing.JTable
	 */
	private javax.swing.JTable getJTable() {
		if(jTable == null) {
			GsDynamicItemModel model = new GsDynamicItemModel((GsDynamicGraph)graph);
            jTable = new EnhancedJTable(model);
			jTable.setDefaultRenderer(Object.class, new GsDynamicItemCellRenderer());
			jTable.setModel(model);
            jTable.getTableHeader().setReorderingAllowed(false);
		}
		return jTable;
	}
	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private javax.swing.JScrollPane getJScrollPane() {
		if(jScrollPane == null) {
			jScrollPane = new javax.swing.JScrollPane();
			jScrollPane.setViewportView(getJTable());
			jScrollPane.setSize(88, 104);
			jScrollPane.setLocation(81, 5);
		}
		return jScrollPane;
	}
}
