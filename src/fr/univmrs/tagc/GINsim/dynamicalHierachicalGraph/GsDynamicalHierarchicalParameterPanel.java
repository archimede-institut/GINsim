package fr.univmrs.tagc.GINsim.dynamicalHierachicalGraph;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.gui.GsParameterPanel;
import fr.univmrs.tagc.common.Tools;
import fr.univmrs.tagc.common.manageressources.Translator;

public class GsDynamicalHierarchicalParameterPanel extends GsParameterPanel {
	private static final long serialVersionUID = 3342245591953494375L;

	private GsDynamicalHierarchicalNode node;
	private JScrollPane jScrollPane = null;
	private JTable jTable = null;
	
	/**
	 */
	public GsDynamicalHierarchicalParameterPanel(GsGraph g) {
		super();
		this.graph = g;
		initialize();
	}
	
	/*
	 * @see fr.univmrs.tagc.GINsim.gui.GsParameterPanel#setEditedObject(java.lang.Object)
	 */
	public void setEditedObject(Object obj) {
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
