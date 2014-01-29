package org.ginsim.gui.graph.hierarchicaltransitiongraph;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.ginsim.common.application.Txt;
import org.ginsim.core.graph.Graph;
import org.ginsim.core.graph.hierarchicaltransitiongraph.HierarchicalNode;
import org.ginsim.gui.shell.editpanel.AbstractParameterPanel;


public class HierarchicalNodeParameterPanel extends AbstractParameterPanel<HierarchicalNode> {
		private static final long serialVersionUID = 3342245591953494375L;

		private HierarchicalNode node;
		private JScrollPane jScrollPane = null;
		private JTable jTable = null;

		private JLabel typeLabel;
		private JLabel nameLabel;
		
		public HierarchicalNodeParameterPanel( Graph g) {
			super(g);

	        this.setLayout(new GridBagLayout());
	        GridBagConstraints c = new GridBagConstraints();
	        c.gridx = 0;
	        c.gridy = 0;
	        c.weightx = 1;
	        c.fill = GridBagConstraints.BOTH;
	        this.add(getNameLabel(), c);
	        
	        c.gridy++;
	        this.add(getTypeLabel(), c);
	        
	        c.gridy++;
	        c.weighty = 1;
	        this.add(getJScrollPane(), c);
	        this.setMinimumSize(new Dimension(20,20));
		}
		
		/*
		 * @see fr.univmrs.tagc.GINsim.gui.GsParameterPanel#setEditedObject(java.lang.Object)
		 */
		@Override
		public void setEditedItem(HierarchicalNode obj) {
			if (obj instanceof HierarchicalNode) {
				node = (HierarchicalNode)obj;
				((HierarchicalTableModel)getJTable().getModel()).setContent(node);
	            jTable.getColumnModel().getColumn(0).setMinWidth(10);
	            jTable.getColumnModel().getColumn(0).setPreferredWidth(10);
	            nameLabel.setText("Name: "+node.toString()+", ID:"+node.getUniqueId());
	            typeLabel.setText("Type: "+ Txt.t("STR_" + node.typeToString()));
			}
		}
		
		private Component getNameLabel() {
			if(nameLabel == null) {
				nameLabel = new JLabel();
			}
			return nameLabel;
		}

		/**
		 * This method initializes jTable
		 * 
		 * @return javax.swing.JTable
		 */
		private JLabel getTypeLabel() {
			if(typeLabel == null) {
				typeLabel = new JLabel();
			}
			return typeLabel;
		}
		/**
		 * This method initializes jTable
		 * 
		 * @return javax.swing.JTable
		 */
		private JTable getJTable() {
			if(jTable == null) {
				jTable = new JTable();
				jTable.setDefaultRenderer(Object.class, new HierarchicalCellRenderer());
				jTable.setModel( new HierarchicalTableModel(graph));
	            jTable.getTableHeader().setReorderingAllowed(false);
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
				jScrollPane = new JScrollPane();
				jScrollPane.setViewportView(getJTable());
				jScrollPane.setSize(88, 104);
				jScrollPane.setLocation(81, 5);
			}
			return jScrollPane;
		}
}
