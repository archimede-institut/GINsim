package fr.univmrs.ibdm.GINsim.regulatoryGraph;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import fr.univmrs.ibdm.GINsim.global.GsEnv;
import fr.univmrs.ibdm.GINsim.global.GsException;
import fr.univmrs.ibdm.GINsim.global.GsMain;
import fr.univmrs.ibdm.GINsim.gui.GsMainFrame;
import fr.univmrs.ibdm.GINsim.gui.GsParameterPanel;
import fr.univmrs.ibdm.GINsim.manageressources.Translator;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.models.GsVertexMinMaxSpinModel;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.GsLogicalFunctionPanel;

/**
 * Panel showing vertex properties
 */
public class GsRegulatoryVertexAttributePanel extends GsParameterPanel {
	private static final long serialVersionUID = 4080699912308903984L;
	private JLabel jLabel = null;
	private JLabel jLabel1 = null;
	private JLabel jLabel2 = null;
	private JLabel jLabel3 = null;
	private JTextField f_id = null;
	private JTextField f_name = null;
	private JSpinner f_base = null;
	private JSpinner f_max = null;
	private ButtonGroup bgroup = null;
	private JToggleButton b_interaction = null;
	private JToggleButton b_function = null;
	private JToggleButton jButton1 = null;
	private JPanel jPanel2 = null;
	private GsInteractionPanel gsInteractionPanel = null;
	private GsLogicalFunctionPanel gsLogicalFunctionPanel = null;
	private GsAnnotationPanel gsAnnotationPanel = null;
	CardLayout card = null;
	
	private GsVertexMinMaxSpinModel minmax = null;
	
	private GsRegulatoryVertex currentVertex = null;
	private GsMainFrame main = null;
	/**
	 * This method initializes 
	 * @param main
	 * 
	 */
	public GsRegulatoryVertexAttributePanel(GsMainFrame main) {
		super();
		this.main = main;
		initialize();
	}
	/**
	 * This method initializes this
	 */
	private void initialize() {
        minmax = new GsVertexMinMaxSpinModel();
        
        this.setLayout(new GridBagLayout());
        GridBagConstraints c;
        
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = java.awt.GridBagConstraints.WEST;
        c.fill = java.awt.GridBagConstraints.HORIZONTAL;
        c.weighty = 1;
        c.gridwidth = 2;
        jLabel2 = new JLabel(Translator.getString("STR_name"));
        this.add(jLabel2, c);
        
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = java.awt.GridBagConstraints.WEST;
        c.fill = java.awt.GridBagConstraints.HORIZONTAL;
        c.weighty = 1;
        jLabel3 = new JLabel(Translator.getString("STR_id"));
        this.add(jLabel3, c);

        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.fill = java.awt.GridBagConstraints.HORIZONTAL;
        c.weighty = 1;
        c.anchor = java.awt.GridBagConstraints.WEST;
        this.add(getF_name(), c);
        
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        c.fill = java.awt.GridBagConstraints.HORIZONTAL;
        c.weighty = 1;
        c.anchor = java.awt.GridBagConstraints.WEST;
        this.add(getF_id(), c);

        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 2;
        c.anchor = java.awt.GridBagConstraints.WEST;
        c.fill = java.awt.GridBagConstraints.HORIZONTAL;
        c.weighty = 1;
        jLabel = new JLabel(Translator.getString("STR_max"));
        this.add(jLabel, c);
        
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 2;
        c.anchor = java.awt.GridBagConstraints.WEST;
        c.fill = java.awt.GridBagConstraints.HORIZONTAL;
        c.weighty = 1;
        this.add(getF_max(), c);
        
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 3;
        c.anchor = java.awt.GridBagConstraints.WEST;
        c.fill = java.awt.GridBagConstraints.HORIZONTAL;
        c.weighty = 1;
        jLabel1 = new JLabel(Translator.getString("STR_base"));
        this.add(jLabel1, c);
        
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 3;
        c.anchor = java.awt.GridBagConstraints.WEST;
        c.fill = java.awt.GridBagConstraints.HORIZONTAL;
        c.weighty = 1;
        this.add(getF_base(), c);
        
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 4;
        c.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        c.fill = java.awt.GridBagConstraints.HORIZONTAL;
        c.weighty = 2;
        this.add(getJButton1(), c);
        
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 4;
        c.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        c.fill = java.awt.GridBagConstraints.HORIZONTAL;
        c.weighty = 2;
        this.add(getB_interaction(), c);
        
        if (GsMain.SHOW_FUNCTION) {
	        c = new GridBagConstraints();
	        c.gridx = 2;
	        c.gridy = 4;
	        c.anchor = java.awt.GridBagConstraints.SOUTHWEST;
	        c.fill = java.awt.GridBagConstraints.HORIZONTAL;
	        c.weighty = 2;
	        this.add(getB_function(), c);
        }
        
        c = new GridBagConstraints();
        c.gridx = 3;
        c.gridy = 0;
        c.gridheight = 8;
        c.fill = java.awt.GridBagConstraints.BOTH;
        c.insets = new Insets(0, 10, 0, 0);
        c.gridwidth = 2;
        c.weightx = 1;
        c.weighty = 5;
        this.add(getJPanel2(), c);

        this.setPreferredSize(new java.awt.Dimension(636,60));
		this.setSize(636, 60);
        
		getBgroup();
		card.show(jPanel2, getGsInteractionPanel().getName());
	}

	/**
     * @return the bGroup
     */
    private ButtonGroup getBgroup() {
        if (bgroup == null) {
            bgroup = new ButtonGroup();
            bgroup.add(getB_interaction());
            bgroup.add(getB_function());
            bgroup.add(getJButton1());
        }
        return bgroup;
    }
    /**
	 * This method initializes f_id	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getF_id() {
		if (f_id == null) {
			f_id = new JTextField();
			f_id.setPreferredSize(new java.awt.Dimension(65,19));
			f_id.addFocusListener(new java.awt.event.FocusAdapter() { 
				public void focusLost(java.awt.event.FocusEvent e) {    
					doChangeId();
				}
			});
		}
		return f_id;
	}
	/**
	 * This method initializes f_name	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getF_name() {
		if (f_name == null) {
			f_name = new JTextField();
			f_name.setPreferredSize(new java.awt.Dimension(65,19));
			f_name.addFocusListener(new java.awt.event.FocusAdapter() { 
				public void focusLost(java.awt.event.FocusEvent e) {    
					doChangeName();
				}
			});
		}
		return f_name;
	}
	/**
	 * This method initializes f_base	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JSpinner getF_base() {
		if (f_base == null) {
            f_base = minmax.getSMin();
		}
		return f_base;
	}
	/**
	 * This method initializes f_max	
	 * 	
	 * @return javax.swing.JTextField	
	 */    
	private JSpinner getF_max() {
		if (f_max == null) {
            f_max = minmax.getSMax();
		}
		return f_max;
	}
	/**
	 * This method initializes b_interaction	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JToggleButton getB_interaction() {
		if (b_interaction == null) {
			b_interaction = new JToggleButton(Translator.getString("STR_parameters"));
			b_interaction.setSelected(true);
			b_interaction.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {
				    interactionselectedChanged();
				}
			});
		}
		return b_interaction;
	}
	
	private JToggleButton getB_function() {
		if (b_function == null) {
			b_function = new JToggleButton(Translator.getString("STR_function"));
			b_function.setSelected(true);
			b_function.addChangeListener(new javax.swing.event.ChangeListener() { 
				public void stateChanged(javax.swing.event.ChangeEvent e) {
				    interactionselectedChanged();
				}
			});
		}
		return b_function;
	}
	
	protected void interactionselectedChanged() {
		if (b_interaction.isSelected()) {
		    card.show(jPanel2, gsInteractionPanel.getName());
		    gsInteractionPanel.setEditedObject(currentVertex);
		} else if (b_function.isSelected()) {
		    card.show(jPanel2, gsLogicalFunctionPanel.getName());
		    gsLogicalFunctionPanel.setEditedObject(currentVertex);
		} else {
		    card.show(jPanel2, gsAnnotationPanel.getName());
		    gsAnnotationPanel.setEditedObject(currentVertex.getAnnotation());
		}
	}
	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */    
	private JToggleButton getJButton1() {
		if (jButton1 == null) {
			jButton1 = new JToggleButton(Translator.getString("STR_notes"));
		}
		return jButton1;
	}
	/**
	 * This method initializes jPanel2	
	 * 	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getJPanel2() {
		if (jPanel2 == null) {
			jPanel2 = new JPanel();
			card = new CardLayout();
			jPanel2.setLayout(card);
			jPanel2.setName("jPanel2");
			jPanel2.setPreferredSize(new java.awt.Dimension(513,60));
			jPanel2.add(getGsAnnotationPanel(), getGsAnnotationPanel().getName());
			jPanel2.add(getGsInteractionPanel(), getGsInteractionPanel().getName());
			jPanel2.add(getLogicalFunctionPanel(), getLogicalFunctionPanel().getName());
		}
		return jPanel2;
	}
	/**
	 * This method initializes gsInteractionPanel	
	 * 	
	 * @return fr.univmrs.ibdm.GINsim.regulatoryGraph.GsInteractionPanel	
	 */    
	private GsInteractionPanel getGsInteractionPanel() {
		if (gsInteractionPanel == null) {
            gsInteractionPanel = new GsInteractionPanel((GsRegulatoryGraph)main.getGraph());
			gsInteractionPanel.setName("gsInteractionPanel");
			gsInteractionPanel.setPreferredSize(new java.awt.Dimension(800,60));
		}
		return gsInteractionPanel;
	}
	private GsLogicalFunctionPanel getLogicalFunctionPanel() {
		if (gsLogicalFunctionPanel == null) {
			gsLogicalFunctionPanel = new GsLogicalFunctionPanel((GsRegulatoryGraph)main.getGraph());
			gsLogicalFunctionPanel.setName("logicalFunctionPanel");
		}
		return gsLogicalFunctionPanel;
	}
	/**
	 * This method initializes gsAnnotationPanel	
	 * 	
	 * @return fr.univmrs.ibdm.GINsim.regulatoryGraph.GsAnnotationPanel	
	 */    
	private GsAnnotationPanel getGsAnnotationPanel() {
		if (gsAnnotationPanel == null) {
			gsAnnotationPanel = new GsAnnotationPanel();
			gsAnnotationPanel.setName("gsAnnotationPanel");
			gsAnnotationPanel.setPreferredSize(new java.awt.Dimension(400,60));
		}
		return gsAnnotationPanel;
	}
    /**
     * @see fr.univmrs.ibdm.GINsim.gui.GsParameterPanel#setEditedObject(java.lang.Object)
     */
    public void setEditedObject(Object obj) {
        if (obj != null && obj instanceof GsRegulatoryVertex) {
            if (currentVertex != null) {
                // apply pending changes!
                if (!f_id.getText().equals(currentVertex.getId())) {
                    doChangeId();
                }
                if (!f_name.getText().equals(currentVertex.getName())) {
                    doChangeName();
                }
            }
            currentVertex = (GsRegulatoryVertex)obj;
            f_id.setText(currentVertex.getId());
            f_name.setText(currentVertex.getName());
            minmax.setVertex(currentVertex, (GsRegulatoryGraph)main.getGraph());
            if (b_interaction.isSelected()) {
                gsInteractionPanel.setEditedObject(obj);
            } else if (b_function.isSelected()) {
                gsLogicalFunctionPanel.setEditedObject(obj);
            } else {
                gsAnnotationPanel.setEditedObject(currentVertex.getAnnotation());
            }
        } else {
            currentVertex = null;
        }
    }
    
    public void setMainFrame(GsMainFrame mainFrame) {
        super.setMainFrame(mainFrame);
        gsAnnotationPanel.setMainFrame(mainFrame);
        gsInteractionPanel.setMainFrame(mainFrame);
    }
    
    protected void doChangeId() {
        if (!graph.isEditAllowed()) {
            return;
        }
        try {
            mainFrame.getGraph().changeVertexId(currentVertex, f_id.getText());
        } catch (GsException e) {
            f_id.setText(currentVertex.getId());
            GsEnv.error(e, main);
        }
    }
    
    protected void doChangeName() {
        if (!graph.isEditAllowed()) {
            return;
        }
        currentVertex.setName(f_name.getText());
        graph.fireMetaChange();
    }
}
