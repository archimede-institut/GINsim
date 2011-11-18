package org.ginsim.gui.service.tools.connectivity;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.ginsim.graph.reducedgraph.GsNodeReducedData;
import org.ginsim.graph.reducedgraph.GsReducedGraph;
import org.ginsim.gui.shell.editpanel.AbstractParameterPanel;


/**
 * this panel display some info about a strong component node:
 * it'll mainly display the list of "real nodes" present in this node
 */
public class ReducedParameterPanel extends AbstractParameterPanel<GsNodeReducedData> {

	private static final long serialVersionUID = 3085972711359179082L;
	private javax.swing.JScrollPane jScrollPane = null;
	private javax.swing.JTable jTable = null;

	/**
	 * @param graph 
	 */
	public ReducedParameterPanel(GsReducedGraph graph) {
		super(graph);
		initialize();
	}
	
	@Override
	public void setEditedItem(GsNodeReducedData obj) {
		((ConnectivityTableModel)getJTable().getModel()).setContent( obj.getContent() );
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
	private JTable getJTable() {
		if(jTable == null) {
			jTable = new javax.swing.JTable();
			jTable.setModel( new ConnectivityTableModel());
		}
		return jTable;
	}
	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if(jScrollPane == null) {
			jScrollPane = new javax.swing.JScrollPane();
			jScrollPane.setViewportView(getJTable());
			jScrollPane.setSize(88, 104);
			jScrollPane.setLocation(81, 5);
		}
		return jScrollPane;
	}
}
