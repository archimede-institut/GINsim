package fr.univmrs.ibdm.GINsim.regulatoryGraph;

import java.awt.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;

import fr.univmrs.ibdm.GINsim.gui.GsParameterPanel;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.GsTreeInteractionsModel;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeElement;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.param2function.GsFunctionsCreator;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.models.GsIncomingEdgeListModel;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.models.GsTableInteractionsModel;
import fr.univmrs.tagc.common.datastore.GenericPropertyInfo;
import fr.univmrs.tagc.common.datastore.ObjectPropertyEditorUI;
import fr.univmrs.tagc.common.datastore.gui.GenericPropertyHolder;
import fr.univmrs.tagc.common.widgets.EnhancedJTable;
import fr.univmrs.tagc.common.widgets.SplitPane;
import fr.univmrs.tagc.common.widgets.StockButton;

/**
 * Panel to edit interaction of a gene
 */
public class GsInteractionPanel extends GsParameterPanel
	implements ObjectPropertyEditorUI {

	private static final long serialVersionUID = 8583991719735516132L;

	private GsRegulatoryVertex currentVertex = null;

	protected JTable jTable = null;
	private JScrollPane jScrollPane = null;
	private JList jList = null;
	private JScrollPane jScrollPane1 = null;
    private JButton but_remove = null;
    private JButton but_addParameter = null;
    private JButton upButton = null;
    private JButton downButton = null;

	private GsIncomingEdgeListModel edgeList = null;
	private GsTableInteractionsModel interactionList = null;
	private GsRegulatoryGraph graph;
	private LogicalParameterCellRenderer cellRenderer;

	private JSplitPane jSplitPane = null;
	private JPanel jPanel = null;

	private JButton chaosButton;

	private GenericPropertyInfo	pinfo;

    public GsInteractionPanel() {
    }
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
        setLayout(new GridBagLayout());

        GridBagConstraints c_split = new GridBagConstraints();
        c_split.gridx = 0;
        c_split.gridy = 0;
        c_split.gridwidth = 3;
        c_split.fill = GridBagConstraints.BOTH;
        c_split.weightx = 1;
        c_split.weighty = 1;
        add(getJSplitPane(), c_split);
        edgeList = new GsIncomingEdgeListModel();
        jList.setModel(edgeList);
	}

    /**
	 * @return the jSplitPane
	 */
	private Component getJSplitPane() {
		if (jSplitPane == null) {
			jSplitPane = new SplitPane();
			jSplitPane.setResizeWeight(1);
			jSplitPane.setLeftComponent(getJPanel());
			jSplitPane.setRightComponent(getJScrollPane1());
			jSplitPane.setName("logicalParametersPanel");
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
            gridBagConstraints7.gridwidth = 2;
			gridBagConstraints7.weightx = 1;
			gridBagConstraints7.weighty = 1;
			gridBagConstraints8.gridx = 2;
			gridBagConstraints8.gridy = 0;
			gridBagConstraints8.weightx = 0;
			gridBagConstraints8.weighty = 0;
			gridBagConstraints10.gridx = 2;
			gridBagConstraints10.gridy = 2;
			gridBagConstraints10.weightx = 0;
			gridBagConstraints10.weighty = 0;

            Insets insets = new Insets(0, 5, 3, 5);
            gridBagConstraints7.insets = insets;
            gridBagConstraints8.insets = insets;
            gridBagConstraints10.insets = insets;

            c_up.gridx = 2;
            c_up.gridy = 3;
            c_down.gridx = 2;
            c_down.gridy = 4;

			jPanel.add(getJScrollPane(), gridBagConstraints7);
            jPanel.add(getButAddParameter(), gridBagConstraints8);
            jPanel.add(getButRemove(), gridBagConstraints10);
            jPanel.add(getUpButton(), c_up);
            jPanel.add(getDownButton(), c_down);


            GridBagConstraints c_chaos = new GridBagConstraints();
            c_chaos.gridx = 2;
            c_chaos.gridy = 5;
            c_chaos.insets = new Insets(10, 5, 3, 5);
            jPanel.add(getChaosButton(), c_chaos);
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
            edgeList.setEdge(graph.getGraphManager().getIncomingEdges(currentVertex));
            interactionList.setNode(currentVertex);
            cellRenderer.setVertex(currentVertex);
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
			jTable = new EnhancedJTable(interactionList);
            jTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
            jTable.getColumn(jTable.getColumnName(0)).setMaxWidth(50);
            jTable.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            this.cellRenderer = new LogicalParameterCellRenderer(graph, interactionList);
            jTable.setDefaultRenderer(Object.class, cellRenderer);
            jTable.setDefaultRenderer(Integer.class, cellRenderer);
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
	private JButton getButRemove() {
		if (but_remove == null) {
			but_remove = new StockButton("list-remove.png", true);
			but_remove.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					deleteParameter();
				}
			});
		}
		return but_remove;
	}
	/**
	 * This method initializes jButton2
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getButAddParameter() {
		if (but_addParameter == null) {
			but_addParameter = new StockButton("go-previous.png", true);
			but_addParameter.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					insertRight2Left();
				}
			});
		}
		return but_addParameter;
	}

    /**
     * This method initializes upButton
     *
     * @return javax.swing.JButton
     */
    private javax.swing.JButton getUpButton() {
        if(upButton == null) {
			upButton = new StockButton("go-up.png", true);
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
            } else {
				return;
			}
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
			downButton = new StockButton("go-down.png", true);
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
            if (a<model.getInteractions().getManualSize()-1) {
                model.moveElementAt(a, a+1);
                index[i]=a+1;
            } else {
				return;
			}
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
		int sepIndex = interactionList.getInteractions().getManualSize();
        int[]t_selection = jTable.getSelectedRows();
        boolean add = t_selection != null && t_selection.length == 1 ;
        boolean edit = t_selection != null && t_selection.length > 0 &&
        	t_selection[0] < sepIndex && t_selection[t_selection.length-1] < sepIndex;
        boolean chaos = true;

    	getButRemove().setEnabled(edit);
    	getUpButton().setEnabled(edit);
    	getDownButton().setEnabled(edit);
    	getChaosButton().setEnabled(chaos);
    	if (add) {
            getButAddParameter().setEnabled(true);
            jList.setEnabled(true);
    		List edges = interactionList.getActivesEdges(t_selection[0]);
    		if (edges != null) {
    			int[] indices = new int[edges.size()];
    			for (int i=0 ; i<edges.size() ; i++) {
    				indices[i] = edgeList.getIndex(((GsRegulatoryEdge)edges.get(i)));
    			}
    			jList.setSelectedIndices(indices);
    		}
    	} else {
            getButAddParameter().setEnabled(false);
            jList.setEnabled(false);
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
            if (selectedrow >= interactionList.getInteractions().getManualSize()-1) {
                selectedrow = -1;
            }
			int[] indices=jList.getSelectedIndices();
			Vector edgeindex = new Vector();
			Object[] objs = jList.getSelectedValues();
			for (int i=0;i<indices.length;i++) {
				if (objs[i] != null) {
                    edgeindex.add(objs[i]);
                }
			}
            graph.fireMetaChange();
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

	/**
	 * delete the selected row in the table
	 */
	protected void deleteParameter() {
	    if (!graph.isEditAllowed()) {
	        return;
	    }
		int[] selectedrows = jTable.getSelectedRows();
        if (selectedrows.length != 0) {
    		    interactionList.removeInteractions(selectedrows);
            graph.fireMetaChange();
        }
	}

	private javax.swing.JButton getChaosButton() {
      if(chaosButton == null) {
        chaosButton = new StockButton("chaos.png", false);
        chaosButton.setName("chaosButton");
        chaosButton.setToolTipText("Are you sure ?");
        chaosButton.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent e) {
            doChaos();
          }
        });
      }
      return chaosButton;
    }

    protected void doChaos() {
      // TODO: allow to "copy" a set of function-generated parameters in the manual section
      GsFunctionsCreator c = null;
      Vector v = new Vector();
      List interactions = ((GsTableInteractionsModel)jTable.getModel()).getInteractions();
      //int[] sel;

      //if (jTable.getSelectionModel().isSelectionEmpty())
        c = new GsFunctionsCreator(graph, interactions, currentVertex);
      //else {
      //  sel = jTable.getSelectedRows();
      //  for (int i = 0; i < sel.length; i++) v.addElement(interactions.elementAt(sel[i]));
      //  c = new GsFunctionsCreator(graph.getGraphManager(), v, currentVertex);
      //}

      Hashtable h = c.doIt();

      Enumeration enu = h.keys();
      Integer key;
      String s;

      GsTreeInteractionsModel interactionsModel = currentVertex.getInteractionsModel();
      interactionsModel.clear();
      while (enu.hasMoreElements()) {
        key = (Integer)enu.nextElement();
        v = (Vector)h.get(key);
        for (Enumeration enu2 = v.elements(); enu2.hasMoreElements(); ) {
          s = (String)enu2.nextElement();
          try {
            interactionsModel.addExpression(null, key.shortValue(), currentVertex, s);
          }
          catch (Exception ex) {
            ex.printStackTrace();
          }
        }
      }
      interactionsModel.setRootInfos();
      interactionsModel.fireTreeStructureChanged((GsTreeElement)interactionsModel.getRoot());
    }
	public void apply() {
	}
	public void refresh(boolean force) {
		setEditedObject(pinfo.getRawValue());
	}
	public void setEditedProperty(GenericPropertyInfo pinfo,
			GenericPropertyHolder panel) {
		this.pinfo = pinfo;
		this.graph = (GsRegulatoryGraph)pinfo.data;
		initialize();
		panel.addField(this, pinfo, 0);
	}
}

class LogicalParameterCellRenderer extends DefaultTableCellRenderer {
	private static final long	serialVersionUID	= -1799999900862412151L;

	private GsTableInteractionsModel model;
	private GsRegulatoryGraph graph;
	private GsRegulatoryVertex	vertex;
	
	public LogicalParameterCellRenderer(GsRegulatoryGraph graph, GsTableInteractionsModel model) {
		this.model = model;
		this.graph = graph;
	}

	public void setVertex(GsRegulatoryVertex vertex) {
		this.vertex = vertex;
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        Component cmp = super.getTableCellRendererComponent( table , value , isSelected , hasFocus , row , column );
        setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
    	if (column == 0) {
    		if (row >= model.getInteractions().getManualSize() && row < model.getInteractions().size() ) {
    			setBackground(isSelected ? Color.GRAY : Color.LIGHT_GRAY);
    		}
    	} else {
	        GsLogicalParameter param = model.getParameter(row);
	        if (param != null) {
		        if (param.isDup) {
		        	setBackground(isSelected ? Color.GRAY : Color.LIGHT_GRAY);
		        } else if (param.hasConflict) {
		        	setBackground(isSelected ? Color.PINK : Color.RED);
 		        } else if (!param.activable(graph, vertex)) {
		        	setBackground(isSelected ? Color.ORANGE : Color.ORANGE);
		        }
	        }
    	}
        return cmp;
    }
}
