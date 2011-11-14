package org.ginsim.gui.service.tools.dynamicalhierarchicalsimplifier;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.ginsim.graph.Graph;
import org.ginsim.graph.dynamicalhierarchicalgraph.GsDynamicalHierarchicalNode;

import fr.univmrs.tagc.GINsim.gui.GsParameterPanel;

public class GsDynamicalHierarchicalParameterPanel extends GsParameterPanel {
	private static final long serialVersionUID = 3342245591953494375L;

	private GsDynamicalHierarchicalNode node;
	private JScrollPane jScrollPane = null;
	private JTable jTable = null;
	
	/**
	 */
	public GsDynamicalHierarchicalParameterPanel( Graph g) {
		super(g);
		initialize();
	}
	
	/*
	 * @see fr.univmrs.tagc.GINsim.gui.GsParameterPanel#setEditedObject(java.lang.Object)
	 */
	public void setEditedItem(Object obj) {
		if (obj instanceof GsDynamicalHierarchicalNode) {
			node = (GsDynamicalHierarchicalNode)obj;
			((GsDynamicalHierarchicalTableModel)getJTable().getModel()).setContent(node);
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
			jTable.setDefaultRenderer(Object.class, new GsDynamicalHierarchicalCellRenderer());
			jTable.setModel( new GsDynamicalHierarchicalTableModel(graph));
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
