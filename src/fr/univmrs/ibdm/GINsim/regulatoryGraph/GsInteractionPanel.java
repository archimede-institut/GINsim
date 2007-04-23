package fr.univmrs.ibdm.GINsim.regulatoryGraph;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fr.univmrs.ibdm.GINsim.data.GsDirectedEdge;
import fr.univmrs.ibdm.GINsim.dynamicGraph.GsDynamicPathItemCellRenderer;
import fr.univmrs.ibdm.GINsim.global.GsEnv;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.graph.GsGraphManager;
import fr.univmrs.ibdm.GINsim.graph.GsGraphNotificationAction;
import fr.univmrs.ibdm.GINsim.graph.GsGraphNotificationMessage;
import fr.univmrs.ibdm.GINsim.gui.GsJTable;
import fr.univmrs.ibdm.GINsim.gui.GsParameterPanel;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.models.GsIncomingEdgeListModel;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.models.GsTableInteractionsModel;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.GsBooleanParser;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.parser.TBooleanParser;
import java.util.Iterator;
import java.util.Hashtable;
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

    private JTextField manualEntry = null;
    private JTextField manualLevel = null;
    private JButton manualHelp = null;
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
//        this.setPreferredSize(new java.awt.Dimension(700,60));
//        this.setMinimumSize(new java.awt.Dimension(300,60));
//        this.setSize(704, 60);

        setLayout(new GridBagLayout());

        GridBagConstraints c_manualEntry = new GridBagConstraints();
        GridBagConstraints c_manualLevel = new GridBagConstraints();
        GridBagConstraints c_manualHelp = new GridBagConstraints();
        GridBagConstraints c_split = new GridBagConstraints();

        c_split.gridx = 0;
        c_split.gridy = 0;
        c_split.gridwidth = 3;
        c_split.fill = GridBagConstraints.BOTH;
        c_split.weightx = 1;
        c_split.weighty = 1;

        c_manualLevel.gridx = 0;
        c_manualLevel.gridy = 1;
        c_manualLevel.fill = GridBagConstraints.BOTH;
        c_manualEntry.gridx = 1;
        c_manualEntry.gridy = 1;
        c_manualEntry.fill = GridBagConstraints.BOTH;
        c_manualEntry.weightx = 1;
        c_manualHelp.gridx = 2;
        c_manualHelp.gridy = 1;

        add(getManualEntry(), c_manualEntry);
        add(getManualLevel(), c_manualLevel);
        add(getManualHelp(), c_manualHelp);
        add(getJSplitPane(), c_split);

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
            jPanel.add(getJButton2(), gridBagConstraints8);
            jPanel.add(getJButton(), gridBagConstraints10);
            jPanel.add(getUpButton(), c_up);
            jPanel.add(getDownButton(), c_down);

		}
		return jPanel;
	}
    private JTextField getManualLevel() {
        if (manualLevel == null) {
            manualLevel = new JTextField("1");
            manualLevel.setMinimumSize(new Dimension(35, 18));
        }
        return manualLevel;
    }
    private JTextField getManualEntry() {
        if (manualEntry == null) {
            manualEntry = new JTextField();
            manualEntry.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    manualActivate();
                }
            });
        }
        return manualEntry;
    }

    private JButton getManualHelp() {
        if (manualHelp == null) {
            manualHelp = new JButton("?");
            manualHelp.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    manualHelp();
                }
            });
        }
        return manualHelp;
    }

    protected void manualHelp() {
        // TODO: help for formula
    }

    protected void manualActivate() {
        // check the entered formula and build the corresponding tree
        Vector nodeOrder = graph.getNodeOrder();
        GsGraphManager manager = graph.getGraphManager();
        String s = manualEntry.getText().trim();
        StringTokenizer stok = new StringTokenizer(s);
        List l_edge = edgeList.getEdge();
        short[][] t_cst = new short[l_edge.size()][];
        int[] t_edgeRef = new int[nodeOrder.size()];
        short value = (short)(currentVertex.getMaxValue() + 1);
        try {
            value = (short)Integer.parseInt(manualLevel.getText().trim());
        } catch (Exception e) {
        }
        if (value > currentVertex.getMaxValue()) {
            graph.addNotificationMessage(new GsGraphNotificationMessage(graph, "invalid value: "+manualLevel.getText().trim(), GsGraphNotificationMessage.NOTIFICATION_WARNING));
            return;
        }

        for (int i=0 ; i<nodeOrder.size() ; i++) {
            Object o = manager.getEdge(nodeOrder.get(i), currentVertex);
            if (o == null) {
                t_edgeRef[i] = -1;
            } else {
                int ref = l_edge.indexOf(o);
                t_edgeRef[i] = ref;
                t_cst[ref] = new short[ ((GsRegulatoryMultiEdge)((GsDirectedEdge)o).getUserObject()).getEdgeCount() ];
                for (int j=0 ; j<t_cst[ref].length ; j++) {
                    t_cst[ref][j] = (short)-1;
                }
            }
        }

        while(stok.hasMoreTokens()) {
            String token = stok.nextToken();
            boolean isnot = false;
            if (token.startsWith("!")) {
                isnot = true;
                if (token.length() == 1) {
                    if (stok.hasMoreTokens()) {
                        token = stok.nextToken();
                    } else {
                        graph.addNotificationMessage(new GsGraphNotificationMessage(graph, "invalid formula", GsGraphNotificationMessage.NOTIFICATION_WARNING));
                        return;
                    }
                } else {
                    token = token.substring(1);
                }
            }
            int delim = token.lastIndexOf("#");
            int mindex = 0;
            if (delim != -1) {
                try {
                    mindex = Integer.parseInt(token.substring(delim+1));
                } catch (Exception e) {
                    mindex = -1;
                    graph.addNotificationMessage(new GsGraphNotificationMessage(graph, "\""+token.substring(delim+1)+"\" is not a valid interaction index", GsGraphNotificationMessage.NOTIFICATION_WARNING));
                    return;
                }
                token = token.substring(0,delim);
            }
            Object o = manager.getVertexByName(token);
            int index = nodeOrder.indexOf(o);
            if (index == -1) {
                graph.addNotificationMessage(new GsGraphNotificationMessage(graph, "\""+token+"\" is not a valid gene", GsGraphNotificationMessage.NOTIFICATION_WARNING));
                return;
            }
            // check that the token IS a regulator
            index = t_edgeRef[index];
            if (index == -1) {
                GsGraphNotificationAction action = new GsGraphNotificationAction() {

                    public String[] getActionName() {
                        return new String[] {"add and submit", "add and edit", "edit"};
                    }
                    public boolean timeout(GsGraph graph, Object data) {
                        return true;
                    }
                    public boolean perform(GsGraph graph, Object data, int ref) {
                        if (!(data instanceof Object[])) {
                            return true;
                        }
                        Object source = ((Object[])data)[0];
                        Object target = ((Object[])data)[1];
                        if (ref < 2) {
                            Object isnot = ((Object[])data)[2];
                            ((GsRegulatoryGraph)graph).interactiveAddEdge(source, target,
                                    isnot == Boolean.FALSE ? GsRegulatoryEdge.SIGN_POSITIVE : GsRegulatoryEdge.SIGN_NEGATIVE);
                        }
                        graph.getGraphManager().select(target);
                        ((JTextField)((Object[])data)[3]).setText(""+((Object[])data)[4]);
                        ((JTextField)((Object[])data)[5]).setText(""+((Object[])data)[6]);
                        if (ref == 0) {
                            ((GsInteractionPanel)((Object[])data)[7]).manualActivate();
                        } else {
                            ((JTextField)((Object[])data)[3]).requestFocusInWindow();
                        }
                        return true;
                    }
                };
                Object[] data = new Object[] {o, currentVertex,
                        isnot ? Boolean.TRUE : Boolean.FALSE,
                        manualEntry, manualEntry.getText(),
                        manualLevel, manualLevel.getText(),
                        this};
                graph.addNotificationMessage(new GsGraphNotificationMessage(graph, "\""+token+"\" is not a regulator of "+currentVertex, action, data, GsGraphNotificationMessage.NOTIFICATION_WARNING));
                return;
            }

            if (mindex < 0 || mindex >= t_cst[index].length) {
                graph.addNotificationMessage(new GsGraphNotificationMessage(graph, "\""+mindex+"\" is not a valid interaction index", GsGraphNotificationMessage.NOTIFICATION_WARNING));
                return;
            }

            o = manager.getEdge(o, currentVertex);

            boolean hasTrue = true;
            t_cst[index][mindex] = isnot ? (short)0 : (short)1;
            if (!hasTrue) {
                graph.addNotificationMessage(new GsGraphNotificationMessage(graph, "broken formula: never happens", GsGraphNotificationMessage.NOTIFICATION_WARNING));
                return;
            }
        }
        // build and add all logical parameters corresponding to this formula
        boolean[][] t_pos = new boolean [t_cst.length][];
        for (int i=0 ; i<t_pos.length ; i++) {
            t_pos[i] = new boolean[t_cst[i].length];
            for (int j=0 ; j<t_cst[i].length ; j++) {
                t_pos[i][j] = t_cst[i][j] == (short)1;
            }
        }
        boolean canUpdate = true;
        do {
            canUpdate = false;
            Vector edgeindex = new Vector();
            for (int i=0 ; i<t_pos.length ; i++) {
                for (int j=0 ; j<t_pos[i].length ; j++) {
                    if (t_pos[i][j]) {
                        edgeindex.add(new GsEdgeIndex( (GsRegulatoryMultiEdge)((GsDirectedEdge)l_edge.get(i)).getUserObject(),j));
                    }

                    // if necessary, move to the next state
                    if (canUpdate) {
                        // reset all the next ones
                        if (t_pos[i][j] && t_cst[i][j] == -1) {
                            t_pos[i][j] = false;
                        }
                    } else {
                        // test if an update is possible here
                        if (!t_pos[i][j] && t_cst[i][j] == -1) {
                            t_pos[i][j] = true;
                            canUpdate = true;
                        }
                    }

                }
            }
            interactionList.setActivesEdges(interactionList.getRowCount()-1,edgeindex, value);
        } while (canUpdate);
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
            //cplModel.setNode(currentVertex, graph);
            manualEntry.setText("");
            manualLevel.setText("1");
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
      else {
        System.err.println("OK");
        Vector nodeOrder = graph.getNodeOrder();
        GsGraphManager manager = graph.getGraphManager();
        Vector allowedEdges = new Vector();
        for (int i = 0 ; i < nodeOrder.size() ; i++) {
          GsDirectedEdge o = (GsDirectedEdge)manager.getEdge(nodeOrder.get(i), currentVertex);
          if (o != null) allowedEdges.addElement(o);
        }
        Iterator it = allowedEdges.iterator();
        Hashtable v2 = new Hashtable();
        Hashtable v3 = new Hashtable();
        int nb_1 = 0, N = 0;
        while (it.hasNext()) {
          GsDirectedEdge e = (GsDirectedEdge)it.next();
          GsRegulatoryMultiEdge me = (GsRegulatoryMultiEdge)e.getUserObject();
          for (int i = 0; i < me.getEdgeCount(); i++) {
            v2.put(me.getId(i), new Integer(N));
            v3.put(new Integer(N++), me.getId(i));
          }
        }

        Vector pattern_1 = new Vector();
        Vector inter = ((GsTableInteractionsModel)jTable.getModel()).getInteractions();
        for (int i = 0; i < inter.size(); i++) {
          GsLogicalParameter p = (GsLogicalParameter)inter.elementAt(i);
          N = 0;
          for (int k = 0; k < p.EdgeCount(); k++)
            N += 1 << ((Integer) v2.get(p.getEdge(k).data.getId(p.getEdge(k).index))).intValue();
          pattern_1.addElement(new Integer(N));
        }
        if (currentVertex.getBaseValue() == 1) pattern_1.addElement(new Integer(0));
        Vector pattern_0 = new Vector();
        for (int i = 0; i < Math.pow(2, v2.size()); i++)
          if (!pattern_1.contains(new Integer(i))) {
            pattern_0.addElement(new Integer(i));

          }
        int mask = 0, value = 0, n = 0, h, k = 0;
        String s, s2;
        for (int y = 0; y < 3; y++) {
        //while (!pattern_1.isEmpty()) {
          for (int i = 1; i <= v2.size(); i++) {
            for (int j = 0; j < Math.pow(2, i); j++) {
              for (k = 0; k <= (v2.size() - i); k++) {
                n = 0;
                mask = ((int)Math.pow(2, i) - 1) << k;
                value = j << k;
                for (int i_0 = 0; i_0 < pattern_0.size(); i_0++) {
                  h = (~((((Integer)pattern_0.get(i_0)).intValue() & mask) ^ value) & mask);
                  if (h == mask) n++;
                }
                if (n == pattern_0.size()) {
                  System.err.println("nb bits = " + i + "   value = " + j + "   pos = " + k + "   N = " + n);
                  System.err.println("mask = " + mask);
                  break;
                }
              }
              if (n == pattern_0.size()) {
                s = "";
                for (int m = 0; m < i; m++) {
                  s = (String)v3.get(new Integer(k + m));
                  s = s.substring(0, s.lastIndexOf("_")) + "#" + s.substring(s.lastIndexOf("_") + 1);
                  if ((value & (int)Math.pow(2, m + k)) == value)
                    s = "!" + s;
                  if (m < (i - 1)) s += " & ";
                }
                System.err.println("---> " + s);
                it = pattern_1.iterator();
                while (it.hasNext()) {
                  Integer p = (Integer)it.next();
                  int v = (~(value & mask) & mask);
                  h = (~((p.intValue() & mask) ^ v) & mask);
                  if (h == mask) {
                    System.err.println("A virer : " + p);
                    pattern_1.remove(p);
                    it = pattern_1.iterator();
                  }
                }
              }
            }
          }
          System.err.println(pattern_1);
          //break;
        }
        System.err.println(pattern_0);
        System.err.println(v2);
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
