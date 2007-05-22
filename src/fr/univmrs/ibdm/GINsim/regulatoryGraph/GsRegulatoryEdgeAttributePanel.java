package fr.univmrs.ibdm.GINsim.regulatoryGraph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;

import fr.univmrs.ibdm.GINsim.data.GsDirectedEdge;
import fr.univmrs.ibdm.GINsim.global.GsEnv;
import fr.univmrs.ibdm.GINsim.global.GsException;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.gui.GsMainFrame;
import fr.univmrs.ibdm.GINsim.gui.GsParameterPanel;
import fr.univmrs.ibdm.GINsim.manageressources.Translator;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.models.GsDirectedEdgeListModel;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.models.GsEdgeMinMaxSpinModel;
/**
 * Panel showing edge properties
 */
public class GsRegulatoryEdgeAttributePanel extends GsParameterPanel {

	private static final long serialVersionUID = 6693691826561475496L;
	private GsRegulatoryMultiEdge currentEdge = null;
	private int selected = -1;
	private JList jList = null;
	private JScrollPane jScrollPane = null;
	private JButton jButton = null;
	private JButton jButton1 = null;
    private JLabel labelFullName = null;
	private JLabel jLabel1 = null;
	private JLabel jLabel2 = null;
	private JSpinner s_min = null;
	private JSpinner s_max = null;
	private JComboBox c_sign = null;
	private GsAnnotationPanel gsAnnotationPanel = null;
	private GsEdgeMinMaxSpinModel minmax = null;
	/**
	 * This method initializes
	 * @param main
	 *
	 */
	public GsRegulatoryEdgeAttributePanel(GsMainFrame main) {
		super();
        setMainFrame(main);
        if (main.getGraph() == null) {
            GsEnv.error("graph null", main);
        }
		initialize();
	}

    public void setGraph(GsGraph graph) {
        super.setGraph(graph);
        getGsAnnotationPanel().setGraph(graph);
    }

	/**
	 * This method initializes this
	 */
	private void initialize() {
        minmax = new GsEdgeMinMaxSpinModel(graph, getJList());
        this.setLayout(new GridBagLayout());
        this.setName("edgeAttr");
        this.setSize(new java.awt.Dimension(426,60));

		jLabel1 = new JLabel(Translator.getString("STR_max"));
        jLabel2 = new JLabel(Translator.getString("STR_min"));
        labelFullName = new JLabel();

        GridBagConstraints c = new java.awt.GridBagConstraints();
        c.fill = java.awt.GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        this.add(labelFullName, c);

        c = new java.awt.GridBagConstraints();
        c.fill = java.awt.GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        c.gridheight = 7;
        c.gridwidth = 1;
        this.add(getJScrollPane(), c);
        c = new java.awt.GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        c.fill = java.awt.GridBagConstraints.BOTH;
        this.add(getJButton(), c);

        c = new java.awt.GridBagConstraints();
        c.gridx = 1;
        c.gridy = 2;
        c.fill = java.awt.GridBagConstraints.BOTH;
        this.add(getJButton1(), c);

        c = new java.awt.GridBagConstraints();
		c.gridx = 1;
		c.gridy = 3;
		c.fill = java.awt.GridBagConstraints.NONE;
        c.anchor = java.awt.GridBagConstraints.WEST;
        this.add(jLabel2, c);

        c = new java.awt.GridBagConstraints();
		c.gridx = 1;
		c.gridy = 4;
		c.fill = java.awt.GridBagConstraints.NONE;
        c.anchor = java.awt.GridBagConstraints.WEST;
        this.add(jLabel1, c);

        c = new java.awt.GridBagConstraints();
        c.gridx = 4;
        c.gridy = 0;
        c.fill = java.awt.GridBagConstraints.BOTH;
        c.weightx = 1.0D;
        c.weighty = 1.0D;
        c.gridheight = 7;
        this.add(getGsAnnotationPanel(), c);

        c = new java.awt.GridBagConstraints();
        c.gridx = 2;
        c.gridy = 3;
        c.anchor = java.awt.GridBagConstraints.WEST;
        c.fill = java.awt.GridBagConstraints.BOTH;
        this.add(getS_min(), c);

        c = new java.awt.GridBagConstraints();
        c.gridx = 2;
        c.gridy = 4;
        c.anchor = java.awt.GridBagConstraints.WEST;
        c.fill = java.awt.GridBagConstraints.BOTH;
        this.add(getS_max(), c);

        c = new java.awt.GridBagConstraints();
        c.gridx = 2;
        c.gridy = 5;
        c.gridwidth = 1;
        c.fill = java.awt.GridBagConstraints.NONE;
        c.anchor = java.awt.GridBagConstraints.WEST;
        c.gridwidth = 1;
        this.add(getC_sign(), c);
	}
    /**
     * @see fr.univmrs.ibdm.GINsim.gui.GsParameterPanel#setEditedObject(java.lang.Object)
     */
    public void setEditedObject(Object obj) {
        if (currentEdge != null) {
            // apply pending changes
        }
        if (obj != null && obj instanceof GsDirectedEdge) {
            currentEdge = (GsRegulatoryMultiEdge)((GsDirectedEdge)obj).getUserObject();
            if (currentEdge != null) {
                ((GsDirectedEdgeListModel)jList.getModel()).setEdge(currentEdge);
                jList.setSelectedIndex(0);
                updateSelection();
                labelFullName.setText(currentEdge.getSource()+" "+Translator.getString("STR_to")+" "+currentEdge.getTarget());
            }
        }
    }
	/**
	 * This method initializes jList
	 *
	 * @return javax.swing.JList
	 */
	private JList getJList() {
		if (jList == null) {
			jList = new JList();
			jList.setModel(new GsDirectedEdgeListModel());
			jList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			jList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
				public void valueChanged(javax.swing.event.ListSelectionEvent e) {
				    listSelectionChanged();
				}
			});
		}
		return jList;
	}

	protected void listSelectionChanged() {
		selected = jList.getSelectedIndex();
		updateSelection();
    }
    /**
	 * This method initializes jButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton("+");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    addEdge();
				}
			});
		}
		return jButton;
	}
	/**
     * the "+" button was activated
     */
    protected void addEdge() {
	    if (!graph.isEditAllowed()) {
	        return;
	    }
		((GsRegulatoryGraph)mainFrame.getGraph()).addToExistingEdge( currentEdge, 0);
		((GsDirectedEdgeListModel)jList.getModel()).update();
		jList.setSelectedIndex(currentEdge.getEdgeCount()-1);
    }
    /**
	 * This method initializes jButton1
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getJButton1() {
		if (jButton1 == null) {
			jButton1 = new JButton("X");
            jButton1.setForeground(Color.RED);
			jButton1.setPreferredSize(new Dimension(10,50));
			jButton1.setSize(new Dimension(10,50));
			jButton1.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    delete();
				}
			});
		}
		return jButton1;
	}
	/**
     *
     */
    protected void delete() {
	    if (!graph.isEditAllowed()) {
	        return;
	    }
	    int index = jList.getSelectedIndex();
	    try {
            ((GsRegulatoryGraph)mainFrame.getGraph()).removeEdgeFromMultiEdge(currentEdge, index);
        } catch (GsException e1) {
            GsEnv.error(e1, mainFrame);
        }

	    ((GsDirectedEdgeListModel)jList.getModel()).update();
	    if (index == currentEdge.getEdgeCount()) {
	        index--;
	    }
	    jList.setSelectedIndex(index);
    }
    /**
	 * This method initializes s_min
	 *
	 * @return javax.swing.JSpinner
	 */
	private JSpinner getS_min() {
		if (s_min == null) {
			s_min = minmax.getSMin();
		}
		return s_min;
	}
	/**
	 * This method initializes s_max
	 *
	 * @return javax.swing.JSpinner
	 */
	private JSpinner getS_max() {
		if (s_max == null) {
            s_max = minmax.getSMax();
		}
		return s_max;
	}
	/**
	 * This method initializes c_sign
	 *
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getC_sign() {
		if (c_sign == null) {
		    c_sign = new JComboBox();
		    c_sign.setPreferredSize(new java.awt.Dimension(60,20));
		    c_sign.setMinimumSize(new java.awt.Dimension(60,25));
		    String[] s_signs = GsRegulatoryEdge.SIGN_SHORT;
		    for (int i=0 ; i<s_signs.length ; i++) {
		    		c_sign.addItem(s_signs[i]);
		    }
		    c_sign.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent arg0) {
				    applySign();
				}
			});
		}
		return c_sign;
	}
	/**
     *
     */
    protected void applySign() {
	    if (graph.isEditAllowed() && currentEdge.getSign() != c_sign.getSelectedIndex()) {
	        currentEdge.setSign(jList.getSelectedIndex(), (short)c_sign.getSelectedIndex(), graph);
            graph.fireMetaChange();
            ((GsDirectedEdgeListModel)jList.getModel()).update();
	    }
    }
    /**
	 * This method initializes gsAnnotationPanel
	 *
	 * @return fr.univmrs.ibdm.GINsim.regulatoryGraph.GsAnnotationPanel
	 */
	private GsAnnotationPanel getGsAnnotationPanel() {
		if (gsAnnotationPanel == null) {
			gsAnnotationPanel = new GsAnnotationPanel();
		}
		return gsAnnotationPanel;
	}

	private void updateSelection() {
	    if (selected == -1) {
	        // unactive all
	        s_min.setEnabled(false);
	        s_max.setEnabled(false);
	        c_sign.setEnabled(false);
	        gsAnnotationPanel.setEditedObject(null);
	    } else {
	        // active and populate
	    	int index = jList.getSelectedIndex();
	        minmax.setMedge(currentEdge);
	        minmax.setEdge(index);
	        s_min.setEnabled(true);
	        s_max.setEnabled(true);
	        c_sign.setEnabled(true);
	        c_sign.setSelectedIndex(currentEdge.getSign(index));
	        gsAnnotationPanel.setEditedObject(currentEdge.getGsAnnotation(index));
	    }
	}

	/**
	 * @return Returns the jScrollPane.
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJList());
			jScrollPane.setPreferredSize(new java.awt.Dimension(150,50));
			jScrollPane.setMinimumSize(new java.awt.Dimension(150,50));

		}
		return jScrollPane;
	}
 }  //  @jve:decl-index=0:visual-constraint="13,19"
