package fr.univmrs.ibdm.GINsim.regulatoryGraph;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Vector;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fr.univmrs.ibdm.GINsim.dynamicGraph.GsDynamicPathItemCellRenderer;
import fr.univmrs.ibdm.GINsim.global.GsEnv;
import fr.univmrs.ibdm.GINsim.gui.GsJTable;
import fr.univmrs.ibdm.GINsim.gui.GsParameterPanel;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.models.GsIncomingEdgeListModel;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.models.GsTableInteractionsModel;
/**
 * Panel to edit interaction of a gene
 */
public class GsInteractionPanel extends GsParameterPanel {

	private static final long serialVersionUID = 8583991719735516132L;

	private GsRegulatoryVertex currentVertex = null;
	
	protected JTable jTable = null;
	private JScrollPane jScrollPane = null;
	private JList jList = null;
	private JScrollPane jScrollPane1 = null;
    private JButton jButton = null;
    private JButton jButton2 = null;
    private JButton upButton = null;
    private JButton downButton = null;
	
	private GsIncomingEdgeListModel edgeList = null;
	private GsTableInteractionsModel interactionList = null;
	private GsRegulatoryGraph graph;
    
	private JSplitPane jSplitPane = null;
	private JPanel jPanel = null;
	/**
	 * This method initializes 
	 * @param graph 
	 * 
	 */
    public GsInteractionPanel(GsRegulatoryGraph graph) {
		super();
        this.graph = graph;
		initialize();
	}
	/**
	 * This method initializes this
	 */
	private void initialize() {
        this.setLayout(new CardLayout());
        this.setPreferredSize(new java.awt.Dimension(700,60));
        this.setMinimumSize(new java.awt.Dimension(300,60));
        this.setSize(704, 60);
        this.add(getJSplitPane(), getJSplitPane().getName());
        edgeList = new GsIncomingEdgeListModel();
        jList.setModel(edgeList);
	}
	
    /**
	 * @return the jSplitPane
	 */
	private Component getJSplitPane() {
		if (jSplitPane == null) {
			jSplitPane = new JSplitPane();
			jSplitPane.setName("jSplitPane");
			jSplitPane.setDividerSize(2);
			jSplitPane.setResizeWeight(1);
			jSplitPane.setLeftComponent(getJPanel());
			jSplitPane.setRightComponent(getJScrollPane1());
			jSplitPane.setDividerLocation(380);
		}
		return jSplitPane;
	}
	/**
	 * @return the jPanel
	 */
	private Component getJPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            GridBagConstraints c_up = new GridBagConstraints();
            GridBagConstraints c_down = new GridBagConstraints();

            jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			gridBagConstraints7.weightx = 1.0;
			gridBagConstraints7.weighty = 4.0D;
			gridBagConstraints7.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints8.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints10.fill = java.awt.GridBagConstraints.NONE;
			gridBagConstraints7.gridx = 0;
			gridBagConstraints7.gridy = 0;
			gridBagConstraints7.gridheight = 6;
			gridBagConstraints7.weightx = 1;
			gridBagConstraints7.weighty = 1;
			gridBagConstraints8.gridx = 1;
			gridBagConstraints8.gridy = 0;
			gridBagConstraints8.weightx = 0;
			gridBagConstraints8.weighty = 0;
			gridBagConstraints10.gridx = 1;
			gridBagConstraints10.gridy = 2;
			gridBagConstraints10.weightx = 0;
			gridBagConstraints10.weighty = 0;
            c_up.gridx = 1;
            c_up.gridy = 3;
            c_down.gridx = 1;
            c_down.gridy = 4;
			jPanel.add(getJScrollPane(), gridBagConstraints7);
            jPanel.add(getJButton2(), gridBagConstraints8);
            jPanel.add(getJButton(), gridBagConstraints10);
            jPanel.add(getUpButton(), c_up);
            jPanel.add(getDownButton(), c_down);
		}
		return jPanel;
	}
	/**
     * @see fr.univmrs.ibdm.GINsim.gui.GsParameterPanel#setEditedObject(java.lang.Object)
     */
    public void setEditedObject(Object obj) {
        
        if (currentVertex != null) {
            // apply pending changes
        }
        if (obj != null && obj instanceof GsRegulatoryVertex) {
            currentVertex = (GsRegulatoryVertex)obj;
            edgeList.setEdge(mainFrame.getGraph().getGraphManager().getIncomingEdges(currentVertex));
            interactionList.setNode(currentVertex);
            if (jTable.getSelectedRow() == -1) {
                int i = interactionList.getRowCount();
                jTable.getSelectionModel().setSelectionInterval(i, i);
            }
        }
    }

	/**
	 * This method initializes jTable	
	 * 	
	 * @return javax.swing.JTable	
	 */    
	private JTable getJTable() {
		if (jTable == null) {
            Vector v_ok = new Vector();
	        interactionList = new GsTableInteractionsModel(graph, v_ok);
			jTable = new GsJTable(interactionList);
            jTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
            jTable.getColumn(jTable.getColumnName(0)).setMaxWidth(50);
            jTable.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            jTable.setDefaultRenderer(Object.class, new GsDynamicPathItemCellRenderer(v_ok));
            jTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    selectLeft2Right();
                }
            });
		}
		return jTable;
	}
	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */    
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJTable());
			jScrollPane.setName("jScrollPane");
		}
		return jScrollPane;
	}
	/**
	 * This method initializes jScrollPane1	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */    
	private JScrollPane getJScrollPane1() {
		if (jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setViewportView(getJList());
			jScrollPane1.setName("jScrollPane1");
			jList.setMinimumSize(new java.awt.Dimension(120,50));
			jList.setSize(new java.awt.Dimension(120,50));
		}
		return jScrollPane1;
	}
	/**
	 * This method initializes jList	
	 * 	
	 * @return javax.swing.JList	
	 */    
	private JList getJList() {
		if (jList == null) {
			jList = new JList();
			jList.setModel(new GsIncomingEdgeListModel());
			jList.setName("jList");
		}
		return jList;
	}
	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton("X");
            jButton.setForeground(Color.RED);
			jButton.setPreferredSize(new java.awt.Dimension(54,25));
			jButton.setName("jButton");
			jButton.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					deleteInteraction();
				}
			});
		}
		return jButton;
	}
	/**
	 * This method initializes jButton2	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JButton getJButton2() {
		if (jButton2 == null) {
			jButton2 = new JButton("<<");
			jButton2.setName("jButton2");
			jButton2.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					insertRight2Left();
				}
			});
		}
		return jButton2;
	}

    /**
     * This method initializes upButton
     * 
     * @return javax.swing.JButton
     */
    private javax.swing.JButton getUpButton() {
        if(upButton == null) {
            upButton = new javax.swing.JButton(GsEnv.getIcon("upArrow.gif"));
            upButton.setName("upButton");
            upButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            upButton.addActionListener(new java.awt.event.ActionListener() { 
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    moveUp();
                }
            });
        }
        return upButton;
    }
    /**
     * move the selected items up 
     */
    protected void moveUp() {
        int[] index=jTable.getSelectedRows();
        GsTableInteractionsModel model = (GsTableInteractionsModel)jTable.getModel();
        for (int i=0;i<index.length;i++) {
            int a = index[i];
            if (a>0) {
                model.moveElementAt(a, a-1);
                index[i]=a-1;
            } else return;
        }
        DefaultListSelectionModel selectionModel = (DefaultListSelectionModel)jTable.getSelectionModel();
        selectionModel.clearSelection();
        int min, max;
        int i=0;
        while (i<index.length) {
            min = index[i++];
            max = min;
            while (i<index.length) {
                if (index[i] == max+1) {
                    i++;
                    max++;
                } else {
                    break;
                }
            }
            selectionModel.addSelectionInterval(min, max);
        }
        graph.fireMetaChange();
    }
    /**
     * This method initializes downButton
     * 
     * @return javax.swing.JButton
     */
    private javax.swing.JButton getDownButton() {
        if(downButton == null) {
            downButton = new javax.swing.JButton(GsEnv.getIcon("downArrow.gif"));
            downButton.setName("downButton");
            downButton.addActionListener(new java.awt.event.ActionListener() { 
                public void actionPerformed(java.awt.event.ActionEvent e) {    
                    moveDown();
                }
            });
        }
        return downButton;
    }
    /**
     * move the selected items down
     */
    protected void moveDown() {
        int[] index=jTable.getSelectedRows();
        GsTableInteractionsModel model = (GsTableInteractionsModel)jTable.getModel();
        for (int i=index.length-1;i>=0;i--) {
            int a = index[i];
            if (a<jTable.getRowCount()-2) {
                model.moveElementAt(a, a+1);
                index[i]=a+1;
            } else return;
        }
        DefaultListSelectionModel selectionModel = (DefaultListSelectionModel)jTable.getSelectionModel();
        selectionModel.clearSelection();
        int min, max;
        int i=0;
        while (i<index.length) {
            min = index[i++];
            max = min;
            while (i<index.length) {
                if (index[i] == max+1) {
                    i++;
                    max++;
                } else {
                    break;
                }
            }
            selectionModel.addSelectionInterval(min, max);
        }
        graph.fireMetaChange();
    }

    
	/**
	 * Get the selection in the left table (jTable), 
	 * and select the corresponding rows in the right list (jList)
	 */
	protected void selectLeft2Right() {
        int[]t_selection = jTable.getSelectedRows();
        if (t_selection == null || t_selection.length > 1 || t_selection.length == 0) {
            getJButton2().setEnabled(false);
            jList.setEnabled(false);
            return;
        }
        getJButton2().setEnabled(true);
        jList.setEnabled(true);
		Vector edge = interactionList.getActivesEdges(t_selection[0]);
		if (edge != null) {
			int[] indices = new int[edge.size()];
			for (int i=0 ; i<edge.size() ; i++) {
				indices[i] = edgeList.getIndex(((GsEdgeIndex)edge.get(i)));
			}
			jList.setSelectedIndices(indices);
		}
	}

	/**
	 * Get the selected element in the right list (jL_IncomEdge),
	 * and insert them in the selected row in the left table (jSP_Table_Value)
	 */
	protected void insertRight2Left() {
	    if (!graph.isEditAllowed()) {
	        return;
	    }
		if (jTable.getSelectedRowCount()<=1) {
			int selectedrow = jTable.getSelectedRow();
            if (selectedrow == interactionList.getRowCount()-1) {
                selectedrow = -1;
            }
			int[] indices=jList.getSelectedIndices();
			if (indices.length>0) {
				Vector edgeindex = new Vector();
				Object[] objs = jList.getSelectedValues();
				for (int i=0;i<indices.length;i++) {
					if (objs[i] != null) {
                        edgeindex.add( ((GsEdgeIndex)objs[i]).clone() );
                        graph.fireMetaChange();
                    }
				}
                jTable.clearSelection();
				if (selectedrow>=0) {
                    interactionList.setActivesEdges(selectedrow,edgeindex);
                    jTable.getSelectionModel().addSelectionInterval(selectedrow, selectedrow);
                }
				else {
                    interactionList.setActivesEdges(interactionList.getRowCount()-1, edgeindex);
                    selectedrow = interactionList.getRowCount()-1;
                    jTable.getSelectionModel().addSelectionInterval(selectedrow, selectedrow);
                }
			}
		}
	}

	/**
	 * delete the selected row in the table
	 */
	protected void deleteInteraction() {
	    if (!graph.isEditAllowed()) {
	        return;
	    }
		int[] selectedrows = jTable.getSelectedRows();
        if (selectedrows.length != 0) {
    		    interactionList.removeInteractions(selectedrows);
            graph.fireMetaChange();
        }
	}

	
}
