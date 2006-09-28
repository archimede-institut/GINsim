package fr.univmrs.ibdm.GINsim.regulatoryGraph;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import fr.univmrs.ibdm.GINsim.global.GsEnv;
import fr.univmrs.ibdm.GINsim.global.GsException;
import fr.univmrs.ibdm.GINsim.gui.GsMainFrame;
import fr.univmrs.ibdm.GINsim.gui.GsParameterPanel;
import fr.univmrs.ibdm.GINsim.manageressources.Translator;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.models.GsVertexMinMaxSpinModel;
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
	private JToggleButton jButton1 = null;
	private JPanel jPanel2 = null;
	private GsInteractionPanel gsInteractionPanel = null;
	private GsAnnotationPanel gsAnnotationPanel = null;
	CardLayout card = null;  //  @jve:decl-index=0:visual-constraint="715,206"
	
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
        
        GridBagConstraints gridBagConstraints29 = new GridBagConstraints();
        GridBagConstraints gridBagConstraints28 = new GridBagConstraints();
        GridBagConstraints gridBagConstraints27 = new GridBagConstraints();
        GridBagConstraints gridBagConstraints25 = new GridBagConstraints();
        GridBagConstraints gridBagConstraints24 = new GridBagConstraints();
        GridBagConstraints gridBagConstraints23 = new GridBagConstraints();
        GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
        GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
        GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
        GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
        GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
        this.setLayout(new GridBagLayout());
        gridBagConstraints18.gridx = 0;
        gridBagConstraints18.gridy = 2;
        gridBagConstraints19.gridx = 1;
        gridBagConstraints19.gridy = 2;
        gridBagConstraints19.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints20.gridx = 0;
        gridBagConstraints20.gridy = 0;
        gridBagConstraints21.gridx = 0;
        gridBagConstraints21.gridy = 1;
        gridBagConstraints21.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints22.gridx = 0;
        gridBagConstraints22.gridy = 3;
        gridBagConstraints23.gridx = 0;
        gridBagConstraints23.gridy = 4;
        gridBagConstraints24.gridx = 1;
        gridBagConstraints24.gridy = 3;
        gridBagConstraints25.gridx = 1;
        gridBagConstraints25.gridy = 4;
        gridBagConstraints27.gridx = 0;
        gridBagConstraints27.gridy = 5;
        gridBagConstraints28.gridx = 1;
        gridBagConstraints28.gridy = 5;
        gridBagConstraints29.gridx = 3;
        gridBagConstraints29.gridy = 0;
		jLabel3 = new JLabel(Translator.getString("STR_id"));
		jLabel2 = new JLabel(Translator.getString("STR_name"));
		jLabel1 = new JLabel(Translator.getString("STR_base"));
		jLabel = new JLabel(Translator.getString("STR_max"));
		gridBagConstraints29.gridheight = 8;
		gridBagConstraints29.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints29.gridwidth = 2;
		gridBagConstraints29.weightx = 1;
		gridBagConstraints29.weighty = 1;
		this.add(getF_id(), gridBagConstraints19);
		this.setPreferredSize(new java.awt.Dimension(636,60));
		this.setSize(636, 60);
		gridBagConstraints18.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints18.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints20.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints20.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints20.gridwidth = 2;
		gridBagConstraints22.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints22.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints23.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints23.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints27.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints27.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints28.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints28.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints25.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints25.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints24.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints24.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints21.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints21.gridwidth = 2;
		gridBagConstraints19.anchor = java.awt.GridBagConstraints.WEST;
		this.add(jLabel2, gridBagConstraints20);
		this.add(jLabel, gridBagConstraints22);
		this.add(jLabel1, gridBagConstraints23);
		this.add(getF_max(), gridBagConstraints24);
		this.add(getF_base(), gridBagConstraints25);
		this.add(getB_interaction(), gridBagConstraints28);
		this.add(getJButton1(), gridBagConstraints27);
		this.add(getF_name(), gridBagConstraints21);
		this.add(jLabel3, gridBagConstraints18);
		this.add(getJPanel2(), gridBagConstraints29);
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
	
	protected void interactionselectedChanged() {
		if (b_interaction.isSelected()) {
		    card.show(jPanel2, gsInteractionPanel.getName());
		    gsInteractionPanel.setEditedObject(currentVertex);
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
