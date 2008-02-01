package fr.univmrs.ibdm.GINsim.global;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.univmrs.ibdm.GINsim.graph.GsActionProvider;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.gui.GsPluggableActionDescriptor;
import fr.univmrs.ibdm.GINsim.manageressources.Translator;

/**
 * This frame offers to the user a choice about it's new graph.
 * He can choose to save or to display it. 
 */
public class GsWhatToDoFrame extends JDialog {

	private static final long serialVersionUID = -5827176719167493130L;
	private javax.swing.JPanel theContentPane = null;
	private ButtonGroup optionGrp;
	private javax.swing.JLabel label = null;
	private javax.swing.JRadioButton radioSave = null;
	private javax.swing.JRadioButton radioDisplay = null;
	private javax.swing.JRadioButton radioExport = null;
	private javax.swing.JRadioButton radioAction = null;
    private javax.swing.JButton buttonOK = null;
    private javax.swing.JButton buttonClose = null;
	private GsGraph graph;
	private javax.swing.JLabel labelInfo = null;
	
	private JCheckBox checkLayout = null;
	private JComboBox comboLayout = null;
	private JComboBox comboExport = null;

	private JFrame frame;
	
	private JComboBox comboActions = null;
	
	private boolean hasLayout = true;
	private JPanel infoPanel = null;
	private int size;
	
	/**
	 * This is the default constructor
	 * @param frame
	 * @param graph
	 * @param needLayout 
	 */
	public GsWhatToDoFrame(JFrame frame, GsGraph graph, boolean needLayout) {
		super(frame);
		this.graph = graph;
		this.frame = frame;
		initialize();

		// propose to display and autolayout by default
		radioDisplay.setSelected(true);
		checkLayout.setSelected(needLayout);
		
		// big graphs: add a warning and save by default (fully disable display if really big)
		if (size > 500) {
			getLabelInfo().setForeground(Color.RED);
			getRadioSave().setSelected(true);
			if (size > 1000) {
				getRadioDisplay().setEnabled(false);
				getLabelInfo().setText(Translator.getString("STR_warning_reallyBigGraph"));
			} else {
				getLabelInfo().setText(Translator.getString("STR_warning_bigGraph"));
			}
		}
	}

	/**
	 * Initialization
	 */
	private void initialize() {
		size = graph.getGraphManager().getVertexCount();
		this.setSize(500, 280);
		this.setContentPane(getTheContentPane());
		this.setVisible(true);
		this.setTitle(Translator.getString("STR_whatToDo_title"));
		this.addWindowListener(new java.awt.event.WindowAdapter() { 
			public void windowClosing(java.awt.event.WindowEvent e) {
			    close();
			}
		});
	}

	/**
	 * This method initializes and returns the JContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getTheContentPane() {
		if (theContentPane == null) {
			theContentPane = new javax.swing.JPanel();
			theContentPane.setLayout(new GridBagLayout());
			
			GridBagConstraints c_question = new GridBagConstraints();
			GridBagConstraints c_label = new GridBagConstraints();
			GridBagConstraints c_infoPanel = new GridBagConstraints();
			GridBagConstraints c_r_display = new GridBagConstraints();
			GridBagConstraints c_checkLayout = new GridBagConstraints();
			GridBagConstraints c_comboLayout = new GridBagConstraints();
			GridBagConstraints c_r_save = new GridBagConstraints();
			GridBagConstraints c_r_export = new GridBagConstraints();
			GridBagConstraints c_comboExport = new GridBagConstraints();
            GridBagConstraints c_b_ok = new GridBagConstraints();
            GridBagConstraints c_b_close = new GridBagConstraints();
			GridBagConstraints c_labelInfo = new GridBagConstraints();
			GridBagConstraints c_r_action = new GridBagConstraints();
			GridBagConstraints c_comboActions = new GridBagConstraints();
			
			c_question.gridx = 0;
			c_question.gridy = 0;
			c_question.insets = new Insets(10, 10, 10, 0);
			c_question.gridwidth = 3;
			c_question.anchor = GridBagConstraints.WEST;
			c_label.gridx = 0;
			c_label.gridy = 1;
			c_label.gridwidth = 2;
			c_label.insets = new Insets(10, 15, 10, 5);
			c_label.anchor = GridBagConstraints.WEST;
			c_infoPanel.gridx = 1;
			c_infoPanel.gridy = 1;
			c_infoPanel.weightx = 1;
			c_infoPanel.weighty = 1;
			c_infoPanel.insets = new Insets(0, 5, 3, 0);
			c_labelInfo.gridx = 0;
			c_labelInfo.gridy = 2;
			c_labelInfo.gridwidth = 2;
			c_labelInfo.anchor = GridBagConstraints.WEST;
			c_r_display.gridx = 0;
			c_r_display.gridy = 3;
			c_r_display.anchor = GridBagConstraints.WEST;
			c_checkLayout.gridx = 0;
			c_checkLayout.gridy = 4;
			c_checkLayout.insets = new Insets(0, 15, 0, 0);
			c_checkLayout.anchor = GridBagConstraints.WEST;
			c_comboLayout.gridx = 1;
			c_comboLayout.gridy = 4;
			c_comboLayout.anchor = GridBagConstraints.WEST;
			c_r_save.gridx = 0;
			c_r_save.gridy = 5;
			c_r_save.anchor = GridBagConstraints.WEST;
			c_r_export.gridx = 0;
			c_r_export.gridy = 6;
			c_r_export.anchor = GridBagConstraints.WEST;
			c_comboExport.gridx = 1;
			c_comboExport.gridy = 6;
			c_comboExport.anchor = GridBagConstraints.WEST;
			c_r_action.gridx = 0;
			c_r_action.gridy = 7;
			c_r_action.anchor = GridBagConstraints.WEST;
			c_comboActions.gridx = 1;
			c_comboActions.gridy = 7;
			c_comboActions.anchor = GridBagConstraints.WEST;

            c_b_close.gridx = 1;
            c_b_close.gridy = 8;
            c_b_close.anchor = GridBagConstraints.EAST;
            c_b_ok.gridx = 2;
            c_b_ok.gridy = 8;
            c_b_ok.anchor = GridBagConstraints.EAST;
			
            theContentPane.add(new JLabel(Translator.getString("STR_whatToDo_question")), c_question);
			label = new JLabel(Translator.getString("STR_graphSize")+" "+size);
			theContentPane.add(label, c_label);
			theContentPane.add(getInfoPanel(), c_infoPanel);
			optionGrp = new ButtonGroup();
			theContentPane.add(getRadioSave(), c_r_save);
			theContentPane.add(getCheckLayout(), c_checkLayout);
			theContentPane.add(getComboLayout(), c_comboLayout);
			theContentPane.add(getRadioDisplay(), c_r_display);
			theContentPane.add(getRadioExport(), c_r_export);
			theContentPane.add(getComboExport(), c_comboExport);
            theContentPane.add(getButtonClose(), c_b_close);
            theContentPane.add(getButtonOK(), c_b_ok);
			theContentPane.add(getLabelInfo(), c_labelInfo);
			
			theContentPane.add(getRadioAction(), c_r_action);
			theContentPane.add(getComboActions(), c_comboActions);
			
			// disable unavaible components:
			if (comboLayout.getItemCount() == 0) {
				hasLayout = false;
				checkLayout.setEnabled(false);
				comboLayout.setEnabled(false);
			}
			if (comboExport.getItemCount() == 0) {
				radioExport.setEnabled(false);
			}
			// comboExport always starts disabled
			comboExport.setEnabled(false);
			
			if (comboActions.getItemCount() == 0) {
			    radioAction.setEnabled(false);
				comboActions.setEnabled(false);
			}
			
		}
		return theContentPane;
	}

	/**
	 * This method initializes the radio button for save
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getRadioSave() {
		if(radioSave == null) {
			radioSave = new javax.swing.JRadioButton(Translator.getString("STR_save"));
			optionGrp.add(radioSave);
		}
		return radioSave;
	}
	/**
	 * This method initializes  the radio button for display
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getRadioDisplay() {
		if(radioDisplay == null) {
			radioDisplay = new javax.swing.JRadioButton(Translator.getString("STR_display"));
			optionGrp.add(radioDisplay);
			radioDisplay.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
				    modeChanged();
				}
			});
		}
		return radioDisplay;
	}
	/**
	 * This method initializes bttonOK
	 * 
	 * @return javax.swing.JButton
	 */
    private javax.swing.JButton getButtonOK() {
        if(buttonOK == null) {
            buttonOK = new javax.swing.JButton(Translator.getString("STR_OK"));
            buttonOK.addActionListener(new java.awt.event.ActionListener() { 
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    run();
                }
            });
        }
        return buttonOK;
    }
    private javax.swing.JButton getButtonClose() {
        if(buttonClose == null) {
            buttonClose = new javax.swing.JButton(Translator.getString("STR_close"));
            buttonClose.addActionListener(new java.awt.event.ActionListener() { 
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    close();
                }
            });
        }
        return buttonClose;
    }
	/**
     * 
     */
    protected void run() {
		if (radioSave.isSelected()){
			try {
				graph.save();
			} catch (GsException e1) {
				GsEnv.error(e1, null);
			}
		} else if (radioDisplay.isSelected()) {
			GsEnv.newMainFrame(graph);
			if (checkLayout.isSelected()) {
				GsPluggableActionDescriptor gpad = (GsPluggableActionDescriptor)comboLayout.getSelectedItem();
				try {
					gpad.ap.runAction(GsActionProvider.ACTION_LAYOUT, gpad.param, graph, frame);
				} catch (GsException e1) {
					GsEnv.error(e1, null);
				}
			}
			close();
		} else if (radioExport.isSelected()) {
			GsPluggableActionDescriptor gpad = (GsPluggableActionDescriptor)comboExport.getSelectedItem();
			try {
				gpad.ap.runAction(GsActionProvider.ACTION_EXPORT, gpad.param, graph, frame);
			} catch (GsException e1) {
				GsEnv.error(e1, null);
			}
		} else if (radioAction.isSelected()) {
			GsPluggableActionDescriptor gpad = (GsPluggableActionDescriptor)comboActions.getSelectedItem();
			try {
				gpad.ap.runAction(GsActionProvider.ACTION_ACTION, gpad.param, graph, frame);
			} catch (GsException e1) {
				GsEnv.error(e1, null);
			}
		}
    }

    /**
     * close the "whattodo" frame: the frame itself was closed or the graph has been displayed.
     * do the appropriate cleanups.
     */
    protected void close() {
        if (!graph.isVisible()) {
            graph.close();
        }
        graph = null;
        getComboActions().removeAll();
        getComboExport().removeAll();
        getComboLayout().removeAll();
        infoPanel = null;
        dispose();
    }

    /**
	 * This method initializes labelInfo
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getLabelInfo() {
		if(labelInfo == null) {
			labelInfo = new javax.swing.JLabel("");
		}
		return labelInfo;
	}

	private JPanel getInfoPanel() {
		if (infoPanel == null) {
		    infoPanel = graph.getInfoPanel();
		    if (infoPanel == null) {
		        infoPanel = new JPanel();
		    }
		}
		return infoPanel;
	}
	
	private JCheckBox getCheckLayout() {
		if (checkLayout == null) {
			checkLayout = new JCheckBox(Translator.getString("STR_applyLayout"));
			checkLayout.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
				    modeChanged();
				}
			});
		}
		return checkLayout;
	}
	
    protected void modeChanged() {
		checkLayout.setEnabled(hasLayout && radioDisplay.isSelected());
		comboLayout.setEnabled(hasLayout && radioDisplay.isSelected() && checkLayout.isSelected());
		comboActions.setEnabled(radioAction.isSelected());
		comboExport.setEnabled(radioExport.isSelected());
    }

    private JComboBox getComboActions() {
		if (comboActions == null) {
			comboActions = new JComboBox();
			fillComboWithList(comboActions, graph.getGraphManager().getAction(), GsActionProvider.ACTION_ACTION, graph);
			fillComboWithList(comboActions, graph.getAction(), GsActionProvider.ACTION_ACTION, graph);
			fillComboWithList(comboActions, graph.getSpecificAction(), GsActionProvider.ACTION_ACTION, graph);
		}
		return comboActions;
	}
	private JComboBox getComboExport() {
		if (comboExport == null) {
			comboExport = new JComboBox();
			fillComboWithList(comboExport, graph.getGraphManager().getExport(), GsActionProvider.ACTION_EXPORT, graph);
			fillComboWithList(comboExport, graph.getExport(), GsActionProvider.ACTION_EXPORT, graph);
			fillComboWithList(comboExport, graph.getSpecificExport(), GsActionProvider.ACTION_EXPORT, graph);
		}
		return comboExport;
	}
	private JComboBox getComboLayout() {
		if (comboLayout == null) {
			comboLayout = new JComboBox();
			fillComboWithList(comboLayout, graph.getLayout(), GsActionProvider.ACTION_LAYOUT, graph);
			fillComboWithList(comboLayout, graph.getGraphManager().getLayout(), GsActionProvider.ACTION_LAYOUT, graph);
			fillComboWithList(comboLayout, graph.getSpecificLayout(), GsActionProvider.ACTION_LAYOUT, graph);
		}
		return comboLayout;
	}
	private javax.swing.JRadioButton getRadioAction() {
		if (radioAction == null) {
			radioAction = new JRadioButton(Translator.getString("STR_runAction"));
			optionGrp.add(radioAction);
			radioAction.setSelected(true);
			radioAction.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
				    modeChanged();
				}
			});
		}
		return radioAction;
	}
	private javax.swing.JRadioButton getRadioExport() {
		if (radioExport == null) {
			radioExport = new JRadioButton(Translator.getString("STR_export"));
			optionGrp.add(radioExport);
			radioExport.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
				    modeChanged();
				}
			});
		}
		return radioExport;
	}
	
	private void fillComboWithList(JComboBox combo, List l, int actionCode, GsGraph graph) {
		if (l == null) {
			return;
		}
		for (int i=0 ; i<l.size() ; i++) {
		    Object obj = l.get(i);
		    if (obj instanceof GsPluggableActionDescriptor) {
		        combo.addItem(obj);
		    } else if (obj instanceof GsActionProvider) {
			    GsPluggableActionDescriptor[] t_action = null;
                t_action = ((GsActionProvider)obj).getT_action(actionCode, graph);
			    if (t_action != null) {
			        for (int j=0 ; j<t_action.length ; j++) {
			            combo.addItem(t_action[j]);
			        }
			    }
		    }
		}
	}
}
