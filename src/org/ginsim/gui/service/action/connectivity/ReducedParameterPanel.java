package org.ginsim.gui.service.action.connectivity;

import java.awt.BorderLayout;
import java.awt.Dimension;

import fr.univmrs.tagc.GINsim.gui.GsParameterPanel;

/**
 * this panel display some info about a strong component node:
 * it'll mainly display the list of "real nodes" present in this node
 */
public class ReducedParameterPanel extends GsParameterPanel{

	private static final long serialVersionUID = 3085972711359179082L;
	private javax.swing.JScrollPane jScrollPane = null;
	private javax.swing.JTable jTable = null;

	/**
	 */
	public ReducedParameterPanel() {
		super();
		initialize();
	}
	
	/*
	 * @see fr.univmrs.tagc.GINsim.gui.GsParameterPanel#setEditedObject(java.lang.Object)
	 */
	public void setEditedItem(Object obj) {
		if (obj instanceof GsNodeReducedData) {
			((ConnectivityTableModel)getJTable().getModel()).setContent( ((GsNodeReducedData)obj).getContent() );
		}
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
