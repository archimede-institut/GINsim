package fr.univmrs.tagc.GINsim.hierachicalTransitionGraph;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableColumn;

import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.gui.GsParameterPanel;

public class GsHierarchicalParameterPanel extends GsParameterPanel {
		private static final long serialVersionUID = 3342245591953494375L;

		private GsHierarchicalNode node;
		private JScrollPane jScrollPane = null;
		private JTable jTable = null;

		private JTextField sigmaTextField;
		
		/**
		 */
		public GsHierarchicalParameterPanel(GsGraph g) {
			super();
			this.graph = g;
			initialize();
		}
		
		/*
		 * @see fr.univmrs.tagc.GINsim.gui.GsParameterPanel#setEditedObject(java.lang.Object)
		 */
		public void setEditedObject(Object obj) {
			if (obj instanceof GsHierarchicalNode) {
				node = (GsHierarchicalNode)obj;
				((GsHierarchicalTableModel)getJTable().getModel()).setContent(node);
	            jTable.getColumnModel().getColumn(0).setMinWidth(10);
	            jTable.getColumnModel().getColumn(0).setPreferredWidth(10);
	            
	            Set sigma = node.getSigma();
	            StringBuffer s = new StringBuffer();
	            for (Iterator iterator = sigma.iterator(); iterator.hasNext();) {
					GsHierarchicalNode node = (GsHierarchicalNode) iterator.next();
					s.append(node.getLongId()+", ");
				}
	            sigmaTextField.setText(s.toString());
			}
		}

		/**
		 * This method initializes this
		 */
		private void initialize() {
	        this.setLayout(new GridBagLayout());
	        GridBagConstraints c = new GridBagConstraints();
	        c.fill = GridBagConstraints.BOTH;
	        c.weightx = 1;
	        c.weighty = 1;
	        this.add(getSigmaTextField(), c);
	        c.gridy++;
	        this.add(getJScrollPane(), c);
	        this.setMinimumSize(new Dimension(20,20));
		}

		/**
		 * This method initializes jTable
		 * 
		 * @return javax.swing.JTable
		 */
		private JTextField getSigmaTextField() {
			if(sigmaTextField == null) {
				sigmaTextField = new JTextField();
			}
			return sigmaTextField;
		}
		/**
		 * This method initializes jTable
		 * 
		 * @return javax.swing.JTable
		 */
		private JTable getJTable() {
			if(jTable == null) {
				jTable = new JTable();
				jTable.setDefaultRenderer(Object.class, new GsHierarchicalCellRenderer());
				jTable.setModel( new GsHierarchicalTableModel(graph));
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
