package fr.univmrs.ibdm.GINsim.reg2dyn;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableColumn;

import fr.univmrs.ibdm.GINsim.global.GsEnv;
import fr.univmrs.ibdm.GINsim.global.GsException;
import fr.univmrs.ibdm.GINsim.global.Tools;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.gui.GsJTable;
import fr.univmrs.ibdm.GINsim.manageressources.Translator;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;

/**
 * Frame to configure reg2dyn
 */
public class Reg2dynFrame extends JDialog {

	private static final long serialVersionUID = 5487085168476726804L;
	private javax.swing.JPanel jContentPane = null;

	private javax.swing.JLabel labelMode = null;
	private javax.swing.JRadioButton radioAsynchrone = null;
	private javax.swing.JRadioButton radioSynchrone = null;

	private ButtonGroup syncGrp;
	private ButtonGroup depthGrp;
	private ButtonGroup initGrp;

	private javax.swing.JRadioButton radioFullGraph = null;
	javax.swing.JRadioButton radioSelectedOnly = null;

	private javax.swing.JRadioButton radioDephtFirst = null;
	private javax.swing.JRadioButton radioBreadthFirst = null;
	private javax.swing.JTextField textMaxDepth = null;
	private javax.swing.JTextField textMaxNodes = null;
    private JTextField textSaveName = null;
	private javax.swing.JLabel labelMaxDepth = null;
	private javax.swing.JLabel labelMaxNodes = null;
	private javax.swing.JLabel labelInitState = null;
	private javax.swing.JButton buttonRun = null;
	private javax.swing.JButton buttonCancel = null;
	private javax.swing.JButton buttonBlock = null;
	private javax.swing.JButton buttonResetBlock = null;
	private JButton buttonResetStates = null;
	private JButton buttonDelStateRow = null;
    private JComboBox comboRestore = null;
	private int[] minBlock;
	private int[] maxBlock;
    
    private int[][] pclass;
	
	private GsRegulatoryGraph graph;
	private javax.swing.JScrollPane jScrollPane = null;
	private javax.swing.JTable tableInitStates = null;
	private javax.swing.JLabel labelInfo = null;
	private boolean isrunning = false;
	private Simulation sim;
	private JLabel progressLabel = null;
    private Vector v_priorityClass = new Vector();
    private Map m_priority_elt = new HashMap();
	
	private JFrame frame;
	private Reg2dynTableModel model = null;
	private Vector nodeOrder;
    private long size;
    private JRadioButton radioPriorityClass;
    private JButton buttonCfgPriorityClass;
    
	private GsReg2dynPriorityClassConfig priorityClassConfigDialog = null;
    private GsReg2dynBlockConfig blockConfigDialog = null;
    private Map m_params;
    
    private ParameterComboModel pcmodel;
    
	/**
	 * @param frame
	 * @param graph
	 * @param m_params 
	 */
	public Reg2dynFrame(JFrame frame, GsRegulatoryGraph graph, Map m_params) {
		super(frame);
		this.frame = frame;
        this.m_params = m_params;
		if (graph == null) {
            GsEnv.error(new GsException(GsException.GRAVITY_ERROR, "no graph"), frame);
			return;
		}
		this.graph = graph;
		graph.addBlockEdit(this);
		initialize();
		refreshGraph();
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
		this.setSize(650, 450);
		this.setContentPane(getJContentPane());
		getRadioAsynchrone().setSelected(true);
		getRadioFullGraph().setSelected(true);
		this.setTitle(Translator.getString("STR_reg2dynRunningTitle"));
		this.addWindowListener(new java.awt.event.WindowAdapter() { 
			public void windowClosing(java.awt.event.WindowEvent e) {
			    close();
			}
		});
	}
	/**
     * 
     */
    protected void close() {
		if (isrunning) {
			sim.interrupt();    
		}
		graph.removeBlockEdit(this);
		if (blockConfigDialog != null) {
		    blockConfigDialog.setVisible(false);
		}
        if (priorityClassConfigDialog != null) {
            priorityClassConfigDialog.setVisible(false);
        }
		setVisible(false);
    }

    /**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new javax.swing.JPanel();
			jContentPane.setLayout(new GridBagLayout());

			// all buttonGroups
			syncGrp = new ButtonGroup();
			depthGrp = new ButtonGroup();
			initGrp = new ButtonGroup();
			
			Insets topInset = new Insets(20,0,0,0);
			Insets indentInset = new Insets(0, 30, 0, 0);

			// search mode:
			GridBagConstraints c_l_search = new GridBagConstraints();
			c_l_search.gridx = 0;
			c_l_search.gridy = 0;
            c_l_search.gridwidth = 2;
			c_l_search.anchor = GridBagConstraints.WEST;
			GridBagConstraints c_o_sync = new GridBagConstraints();
			c_o_sync.gridx = 0;
			c_o_sync.gridy = 1;
            c_o_sync.gridwidth = 2;
			c_o_sync.anchor = GridBagConstraints.WEST;
			GridBagConstraints c_o_async = new GridBagConstraints();
			c_o_async.gridx = 0;
			c_o_async.gridy = 2;
            c_o_async.gridwidth = 2;
			c_o_async.anchor = GridBagConstraints.WEST;
			GridBagConstraints c_o_df = new GridBagConstraints();
			c_o_df.gridx = 0;
			c_o_df.gridy = 3;
            c_o_df.gridwidth = 2;
			c_o_df.insets = indentInset;
			c_o_df.anchor = GridBagConstraints.WEST;
			GridBagConstraints c_o_bf = new GridBagConstraints();
			c_o_bf.gridx = 0;
			c_o_bf.gridy = 4;
            c_o_bf.gridwidth = 2;
			c_o_bf.insets = indentInset;
			c_o_bf.anchor = GridBagConstraints.WEST;
			GridBagConstraints c_o_priorityClass = new GridBagConstraints();
			c_o_priorityClass.gridx = 0;
			c_o_priorityClass.gridy = 7;
            c_o_priorityClass.gridwidth = 2;
			c_o_priorityClass.anchor = GridBagConstraints.WEST;
			GridBagConstraints c_b_cfgPriorityClass = new GridBagConstraints();
			c_b_cfgPriorityClass.gridx = 0;
			c_b_cfgPriorityClass.gridy = 8;
            c_b_cfgPriorityClass.gridwidth = 2;
			c_b_cfgPriorityClass.insets = indentInset;
			c_b_cfgPriorityClass.anchor = GridBagConstraints.WEST;

			GridBagConstraints c_b_block = new GridBagConstraints();
			c_b_block.gridx = 0;
			c_b_block.gridy = 9;
			c_b_block.insets = topInset;
			c_b_block.anchor = GridBagConstraints.WEST;
			GridBagConstraints c_b_resetblock = new GridBagConstraints();
			c_b_resetblock.gridx = 1;
			c_b_resetblock.gridy = 9;
			c_b_resetblock.insets = topInset;
			c_b_resetblock.anchor = GridBagConstraints.EAST;
			GridBagConstraints c_l_maxdepth = new GridBagConstraints();
			c_l_maxdepth.gridx = 0;
			c_l_maxdepth.gridy = 10;
			c_l_maxdepth.insets = topInset;
			c_l_maxdepth.anchor = GridBagConstraints.WEST;
			GridBagConstraints c_t_maxdepth = new GridBagConstraints();
			c_t_maxdepth.gridx = 1;
			c_t_maxdepth.gridy = 10;
			c_t_maxdepth.insets = topInset;
			c_t_maxdepth.anchor = GridBagConstraints.WEST;
            c_t_maxdepth.fill = GridBagConstraints.HORIZONTAL;
			GridBagConstraints c_l_maxnode = new GridBagConstraints();
			c_l_maxnode.gridx = 0;
			c_l_maxnode.gridy = 11;
			c_l_maxnode.anchor = GridBagConstraints.WEST;
			GridBagConstraints c_t_maxnode = new GridBagConstraints();
			c_t_maxnode.gridx = 1;
			c_t_maxnode.gridy = 11;
			c_t_maxnode.anchor = GridBagConstraints.WEST;
            c_t_maxnode.fill = GridBagConstraints.HORIZONTAL;

			GridBagConstraints c_l_init = new GridBagConstraints();
			c_l_init.gridx = 2;
			c_l_init.gridy = 0;
			c_l_init.anchor = GridBagConstraints.WEST;
			GridBagConstraints c_o_full = new GridBagConstraints();
			c_o_full.gridx = 2;
			c_o_full.gridy = 1;
			c_o_full.anchor = GridBagConstraints.WEST;
			GridBagConstraints c_o_partial = new GridBagConstraints();
			c_o_partial.gridx = 2;
			c_o_partial.gridy = 2;
			c_o_partial.anchor = GridBagConstraints.WEST;
			GridBagConstraints c_but_delstaterow = new GridBagConstraints();
			c_but_delstaterow.gridx = 3;
			c_but_delstaterow.gridy = 2;
			c_but_delstaterow.anchor = GridBagConstraints.WEST;
			GridBagConstraints c_but_resetstate = new GridBagConstraints();
			c_but_resetstate.gridx = 4;
			c_but_resetstate.gridy = 2;
			c_but_resetstate.anchor = GridBagConstraints.WEST;
			GridBagConstraints c_table = new GridBagConstraints();
			c_table.gridx = 2;
			c_table.gridy = 3;
			c_table.gridwidth = 4;
			c_table.gridheight = 10;
			c_table.weightx = 1;
			c_table.weighty = 1;
			c_table.fill = GridBagConstraints.BOTH;
			c_table.anchor = GridBagConstraints.WEST;
			
            GridBagConstraints c_p_bottom = new GridBagConstraints();
            c_p_bottom.gridx = 0;
            c_p_bottom.gridy = 13;
            c_p_bottom.gridwidth = 7;
            c_p_bottom.weightx = 1;
            c_p_bottom.fill = GridBagConstraints.HORIZONTAL;
            
			jContentPane.add(getLabelMode(), c_l_search);
			jContentPane.add(getRadioSynchrone(), c_o_sync);
			jContentPane.add(getRadioAsynchrone(), c_o_async);
			jContentPane.add(getRadioDephtFirst(), c_o_df);
			jContentPane.add(getRadioBreadthFirst(), c_o_bf);
			
		    jContentPane.add(getRadioPriorityClass(), c_o_priorityClass);
		    jContentPane.add(getButtonCfgPriorityClass(), c_b_cfgPriorityClass);
			
			jContentPane.add(getLabelInitState(), c_l_init);
			jContentPane.add(getRadioFullGraph(), c_o_full);
			jContentPane.add(getRadioSelectedOnly(), c_o_partial);
			jContentPane.add(getButtonDelStateRow(), c_but_delstaterow);
			jContentPane.add(getButtonResetStates(), c_but_resetstate);
			jContentPane.add(getJScrollPane(), c_table);
			
            jContentPane.add(getPanelBottom(), c_p_bottom);

			jContentPane.add(getButtonBlock(), c_b_block);
			jContentPane.add(getButtonResetBlock(), c_b_resetblock);
			jContentPane.add(getLabelMaxDepth(), c_l_maxdepth);
			jContentPane.add(getTextMaxDepth(), c_t_maxdepth);
			jContentPane.add(getLabelMaxNodes(), c_l_maxnode);
			jContentPane.add(getTextMaxNodes(), c_t_maxnode);
			
			// choose default option
			radioSynchrone.setSelected(true);
		}
		return jContentPane;
	}

    protected void deleteParameter() {
        String name = textSaveName.getText();
        if (m_params.containsKey(name)) {
            m_params.remove(name);
            pcmodel.fireContentsChanged(this, 0, 0);
            if (pcmodel.getSize() == 0) {
                comboRestore.setEnabled(false);
            } else {
                comboRestore.setSelectedIndex(0);
            }
        }
    }

    private JPanel getPanelBottom() {
        JPanel panelBottom = new JPanel();
        panelBottom.setLayout(new GridBagLayout());
        
        GridBagConstraints c_b_run = new GridBagConstraints();
        c_b_run.gridx = 2;
        c_b_run.gridy = 0;
        c_b_run.anchor = GridBagConstraints.EAST;
        GridBagConstraints c_b_cancel = new GridBagConstraints();
        c_b_cancel.gridx = 1;
        c_b_cancel.gridy = 0;
        c_b_cancel.anchor = GridBagConstraints.EAST;
        GridBagConstraints c_l_info = new GridBagConstraints();
        c_l_info.gridx = 0;
        c_l_info.gridy = 1;
        c_l_info.weightx = 1;
        c_l_info.fill = GridBagConstraints.HORIZONTAL;
        GridBagConstraints c_progress = new GridBagConstraints();
        c_progress.gridx = 0;
        c_progress.gridy = 2;
        c_progress.anchor = GridBagConstraints.WEST;

        panelBottom.add(getButtonRun(), c_b_run);
        panelBottom.add(getButtonCancel(), c_b_cancel);
        panelBottom.add(getLabelInfo(), c_l_info);
        progressLabel = new JLabel();
        panelBottom.add(progressLabel, c_progress);

        return panelBottom;
    }
    
	private JButton getButtonBlock() {
		if (buttonBlock == null) {
			buttonBlock = new JButton(Translator.getString("STR_b_blocker"));
			buttonBlock.setActionCommand("blocker");
			buttonBlock.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    configureBlocker();
				}
			});
		}
		return buttonBlock;
	}

	private JButton getButtonResetBlock() {
		if (buttonResetBlock == null) {
		    buttonResetBlock = new JButton(Translator.getString("STR_reset"));
		    buttonResetBlock.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    resetBlocker();
				}
			});
		}
		return buttonResetBlock;
	}
	/**
     * 
     */
    protected void configureBlocker() {
        if (blockConfigDialog == null) {
            blockConfigDialog = new GsReg2dynBlockConfig(frame, graph.getNodeOrder(), minBlock, maxBlock);
        } else {
            blockConfigDialog.refresh(nodeOrder, minBlock, maxBlock);
            blockConfigDialog.setVisible(true);
        }
    }
    
    protected void resetBlocker() {
        for (int i=0 ; i< minBlock.length ; i++) {
            minBlock[i] = -1;
            maxBlock[i] = -1;
        }
    }
    
    private JButton getButtonCfgPriorityClass() {
		if (buttonCfgPriorityClass == null) {
		    buttonCfgPriorityClass = new JButton(Translator.getString("STR_configure"));
		    buttonCfgPriorityClass.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    configurePriorityClass();
				}
			});
		}
		return buttonCfgPriorityClass;
	}
	
    protected void configurePriorityClass() {
        if (priorityClassConfigDialog == null) {
            priorityClassConfigDialog = new GsReg2dynPriorityClassConfig(frame, graph.getNodeOrder(), v_priorityClass, m_priority_elt);
        }
        priorityClassConfigDialog.setVisible(true);
    }

    /**
	 * This method initializes labelMode
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getLabelMode() {
		if(labelMode == null) {
			labelMode = new javax.swing.JLabel();
			labelMode.setText(Translator.getString("STR_reg2dyn_mode"));
		}
		return labelMode;
	}
	/**
	 * This method initializes radioAsynchrone
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getRadioAsynchrone() {
		if(radioAsynchrone == null) {
			radioAsynchrone = new javax.swing.JRadioButton(Translator.getString("STR_asynchrone"));
			radioAsynchrone.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
				    asynchroneChanged();
				}
			});
			syncGrp.add(radioAsynchrone);
			radioAsynchrone.setSelected(true);
		}
		return radioAsynchrone;
	}
	/**
     * 
     */
    protected void asynchroneChanged() {
		boolean selected = radioAsynchrone.isSelected();
		getRadioBreadthFirst().setEnabled(selected);
		getRadioDephtFirst().setEnabled(selected);
    }

    /**
	 * This method initializes radioSynchrone
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getRadioSynchrone() {
		if(radioSynchrone == null) {
			radioSynchrone = new javax.swing.JRadioButton(Translator.getString("STR_synchrone"));
			syncGrp.add(radioSynchrone);
			radioSynchrone.setSelected(true);
		}
		return radioSynchrone;
	}
	/**
	 * This method initializes radioPriority
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getRadioPriorityClass() {
		if(radioPriorityClass == null) {
		    radioPriorityClass = new javax.swing.JRadioButton(Translator.getString("STR_bypriorityclass"));
		    radioPriorityClass.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
				    priorityClassChanged();
				}
			});
			syncGrp.add(radioPriorityClass);
			radioPriorityClass.setSelected(true);
		}
		return radioPriorityClass;
	}

    protected void priorityClassChanged() {
        getButtonCfgPriorityClass().setEnabled(radioPriorityClass.isSelected());
    }

    /**
	 * This method initializes radioDephtFirst
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getRadioDephtFirst() {
		if(radioDephtFirst == null) {
			radioDephtFirst = new javax.swing.JRadioButton(Translator.getString("STR_depth_first"));
			radioDephtFirst.setSelected(true);
			depthGrp.add(radioDephtFirst);
		}
		return radioDephtFirst;
	}
	/**
	 * This method initializes radioBreadthFirst
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getRadioBreadthFirst() {
		if(radioBreadthFirst == null) {
			radioBreadthFirst = new javax.swing.JRadioButton(Translator.getString("STR_breadth_first"));
			depthGrp.add(radioBreadthFirst);
		}
		return radioBreadthFirst;
	}
	/**
	 * This method initializes textMaxDepth
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getTextMaxDepth() {
		if(textMaxDepth == null) {
			textMaxDepth = new javax.swing.JTextField();
			textMaxDepth.setMinimumSize(new Dimension(50, 20));
		}
		return textMaxDepth;
	}
	/**
	 * This method initializes labelMaxDepth
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getLabelMaxDepth() {
		if(labelMaxDepth == null) {
			labelMaxDepth = new javax.swing.JLabel(Translator.getString("STR_maximum_depth"));
		}
		return labelMaxDepth;
	}
	/**
	 * This method initializes textMaxNodes
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getTextMaxNodes() {
		if(textMaxNodes == null) {
			textMaxNodes = new javax.swing.JTextField();
			textMaxNodes.setMinimumSize(new Dimension(50, 20));
		}
		return textMaxNodes;
	}
	/**
	 * This method initializes labelMaxNodes
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getLabelMaxNodes() {
		if(labelMaxNodes == null) {
			labelMaxNodes = new javax.swing.JLabel(Translator.getString("STR_maximum_nodes"));
		}
		return labelMaxNodes;
	}

	/**
	 * This method initializes labelInitState
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getLabelInitState() {
		if(labelInitState == null) {
			labelInitState = new javax.swing.JLabel(Translator.getString("STR_Initial_state"));
		}
		return labelInitState;
	}

	/**
	 * This method initializes radioFullGraph
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getRadioFullGraph() {
		if(radioFullGraph == null) {
			radioFullGraph = new javax.swing.JRadioButton(Translator.getString("STR_full_graph"));
			radioFullGraph.setSelected(true);
			initGrp.add(radioFullGraph);
		}
		return radioFullGraph;
	}
	/**
	 * This method initializes radioSelectedOnly
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private javax.swing.JRadioButton getRadioSelectedOnly() {
		if(radioSelectedOnly == null) {
			radioSelectedOnly = new javax.swing.JRadioButton(Translator.getString("STR_selected_only"));
			radioSelectedOnly.setSelected(true);
			radioSelectedOnly.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    initChanged();
                }
            });
			radioSelectedOnly.setSelected(false);
			initGrp.add(radioSelectedOnly);
		}
		return radioSelectedOnly;
	}
	
	protected void initChanged() {
        getTableInitStates().setEnabled(radioSelectedOnly.isSelected());
        getButtonDelStateRow().setEnabled(radioSelectedOnly.isSelected());
        getButtonResetStates().setEnabled(radioSelectedOnly.isSelected());
	}
	/**
	 * This method initializes buttonRun
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getButtonRun() {
		if(buttonRun == null) {
			buttonRun = new javax.swing.JButton(Translator.getString("STR_run"));
			buttonRun.setActionCommand("run");
			buttonRun.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    runSimulation();
				}
			});
		}
		return buttonRun;
	}
	/**
     * 
     */
    protected void runSimulation() {
        GsSimulationParameters params = createSimulationParameters();
        
		getLabelInfo().setText(Translator.getString("STR_wait_msg"));
		buttonCancel.setText(Translator.getString("STR_abort"));

		// nearly everything should be unenabled
		radioSynchrone.setEnabled(false);
		radioAsynchrone.setEnabled(false);
		radioBreadthFirst.setEnabled(false);
		radioDephtFirst.setEnabled(false);
        radioPriorityClass.setEnabled(false);
        buttonCfgPriorityClass.setEnabled(false);
		radioFullGraph.setEnabled(false);
		radioSelectedOnly.setEnabled(false);

		tableInitStates.setEnabled(false);
		buttonRun.setEnabled(false);
		buttonBlock.setEnabled(false);
        buttonDelStateRow.setEnabled(false);
        buttonResetBlock.setEnabled(false);
        buttonResetStates.setEnabled(false);
		textMaxDepth.setEnabled(false);
		textMaxNodes.setEnabled(false);

		isrunning = true;
		
        sim = new Simulation(getGraph(), getFrame(), params);
        m_params.put("_DEFAULT", params);
    }

    protected GsSimulationParameters createSimulationParameters() {
        GsSimulationParameters params = new GsSimulationParameters();
        String[] genes = new String[nodeOrder.size()];
        int[] tailles = new int[genes.length];
        if (minBlock != null) {
            params.block = new int[2][minBlock.length];
            for (int i=0 ; i<genes.length ; i++) {
                GsRegulatoryVertex vertex = (GsRegulatoryVertex)nodeOrder.get(i);
                genes[i] = vertex.toString();
                tailles[i] = vertex.getMaxValue();
                params.block[0][i] = minBlock[i];
                params.block[1][i] = maxBlock[i];
            }
        } else {
            for (int i=0 ; i<genes.length ; i++) {
                GsRegulatoryVertex vertex = (GsRegulatoryVertex)nodeOrder.get(i);
                genes[i] = vertex.toString();
                tailles[i] = vertex.getMaxValue();
            }
        }
        params.genes = genes;
        params.tailleGenes = tailles;
        
        String sMaxdepth,sMaxnodes;

        if (!radioFullGraph.isSelected()){
            
            params.initStates = copyInitStates(((Reg2dynTableModel)tableInitStates.getModel()).getVStates());
        }
        
        if (radioSynchrone.isSelected()) {
            params.mode = Simulation.SEARCH_SYNCHRONE;
        } else if (radioAsynchrone.isSelected()) {
            if (radioDephtFirst.isSelected()) {
                params.mode = Simulation.SEARCH_ASYNCHRONE_DF;
            } else {
                params.mode = Simulation.SEARCH_ASYNCHRONE_BF;
            }
        } else {
            params.mode = Simulation.SEARCH_BYPRIORITYCLASS;
            params.pclass = getPclass();
            params.pclass_fine = m_priority_elt.get(nodeOrder.get(0)) instanceof Object[];
        }
        
        sMaxdepth = textMaxDepth.getText();
        if((Tools.isInteger(sMaxdepth))||(sMaxdepth.equals(""))){
            if(sMaxdepth.equals("")){
                params.maxdepth = 0;
            }
            else {
                params.maxdepth = Integer.parseInt(textMaxDepth.getText());
            } 
        }
        else {
            getLabelInfo().setText(Translator.getString("STR_non_int_msg"));
            getTextMaxDepth().setText("");
        }
        sMaxnodes = textMaxNodes.getText();
        if((Tools.isInteger(sMaxnodes))||(sMaxnodes.equals(""))){
            if(sMaxnodes.equals("")){
                params.maxnodes = 0;
            }
            else {
                params.maxnodes = Integer.parseInt(textMaxNodes.getText());
            }
        }
        else { 
                getLabelInfo().setText(Translator.getString("STR_non_int_msg"));
                getTextMaxNodes().setText("");
        }
        
        return params;
    }

    private Vector copyInitStates(Vector states) {
        Vector ret  = new Vector(states.size());
        for (int i=0 ; i<states.size() ; i++) {
            Vector[] el = (Vector[])states.get(i);
            Vector[] copy_el = new Vector[el.length];
            for (int j=0 ; j<el.length ; j++) {
                copy_el[j] = (Vector)el[j].clone();
            }
            ret.add(copy_el);
        }
        return ret;
    }

    /**
	 * simulation is done (or interrupted), now choose what to do with the new graph.
	 * @param graph the dynamic graph
	 */
	public void endSimu(GsGraph graph) {
		isrunning = false;
		if (null == graph) {
            GsEnv.error(new GsException(GsException.GRAVITY_ERROR, "No state transition graph."), frame);
		} else {
			GsEnv.whatToDoWithGraph(frame, graph);
		}
		close();
		
		// make widgets visible again
		radioSynchrone.setEnabled(true);
		radioAsynchrone.setEnabled(true);
        radioPriorityClass.setEnabled(true);
		radioBreadthFirst.setEnabled(radioAsynchrone.isSelected());
		radioDephtFirst.setEnabled(radioAsynchrone.isSelected());
        buttonCfgPriorityClass.setEnabled(radioPriorityClass.isSelected());
		radioFullGraph.setEnabled(true);
		radioSelectedOnly.setEnabled(true);

		tableInitStates.setEnabled(radioFullGraph.isSelected());
        buttonDelStateRow.setEnabled(radioSelectedOnly.isSelected());
        buttonResetStates.setEnabled(radioSelectedOnly.isSelected());
        buttonResetBlock.setEnabled(true);
        buttonBlock.setEnabled(true);
        textMaxDepth.setEnabled(true);
        textMaxNodes.setEnabled(true);
		getLabelInfo().setText("");
		buttonCancel.setText(Translator.getString("STR_cancel"));
        buttonRun.setEnabled(true);
	}
	
	/**
	 * This method initializes buttonCancel
	 * 
	 * @return javax.swing.JButton
	 */
	private javax.swing.JButton getButtonCancel() {
		if(buttonCancel == null) {
			buttonCancel = new javax.swing.JButton(Translator.getString("STR_cancel"));
			buttonCancel.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
				    close();
				}
			});
		}
		return buttonCancel;
	}
	/**
	 * @return the graph
	 */
	public GsRegulatoryGraph getGraph() {
		return graph;
	}

	/**
	 * @return the frame
	 */
	public Reg2dynFrame getFrame() {
		return this;
	}

	/**
	 * @return true if the simulation is running
	 */
	public boolean isRunning() {
		return isrunning;
	}

	/**
	 * @param graph
	 */
	public void setGraph(GsRegulatoryGraph graph) {
		this.graph = graph;
	}

	/**
	 * This method initializes tableInitStates
	 * 
	 * @return javax.swing.JTable
	 */
	private javax.swing.JTable getTableInitStates() {
		if(tableInitStates == null) {
			Vector nodeNames = graph.getNodeOrder();
			tableInitStates = new GsJTable();
			model = new Reg2dynTableModel(nodeNames, this);
            tableInitStates.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
			tableInitStates.setModel(model);
			tableInitStates.getTableHeader().setReorderingAllowed(false);
			tableInitStates.setEnabled(false);
            tableInitStates.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            tableInitStates.setValueAt("0", 0, 0);
		}
		return tableInitStates;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private javax.swing.JScrollPane getJScrollPane() {
		if(jScrollPane == null) {
			jScrollPane = new javax.swing.JScrollPane();
			jScrollPane.setViewportView(getTableInitStates());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes labelInfo
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getLabelInfo() {
		if(labelInfo == null) {
			labelInfo = new javax.swing.JLabel();
			labelInfo.setText("");
		}
		return labelInfo;
	}

    /**
     * set the progress level, to give the user some feedback
     * @param n
     */
    public void setProgress(int n) {
        if (isrunning) {
            progressLabel.setText(""+n);
        }
    }
    
    /**
     * set the progress level, to give the user some feedback
     * @param s
     */
    public void setMessage(String s) {
        if (progressLabel != null) {
            progressLabel.setText(s);
        }
    }
    
    /**
     * @return the compiled priority class.
     * in the form of an int[][]
     * each int[] represent a priority class: 
     *  - the very first int is the class' priority
     *  - the second int is the class' mode (sync or async)
     *  - and all others are couples: index of vertex in the nodeOrder followed by transition filter.
     *    the "transition filter" is a bit hacky: add it to your transition (which should be either +1 or -1)
     *    and if the result is zero (0), then this transition shouldn't be followed.
     * 
     * shortly: it is 0 for all transitions, 1 for negative transitions and -1 for positive ones
     */
    private int[][] getPclass() {
        
        Integer zaroo = new Integer(0);
        Integer one = new Integer(1);
        Integer minusOne = new Integer(-1);
        
        // it is done by browsing twice the list:
        //   - during the first pass asynchronous classes with the same priority are merged
        //   - then the real int[][] is created from the merged classes
        
        Vector v_class = new Vector();
        for (int i=0 ; i<v_priorityClass.size() ; i++) {
            GsReg2dynPriorityClass pc = (GsReg2dynPriorityClass)v_priorityClass.get(i);
            Vector v_content;
            if (pc.getMode() == GsReg2dynPriorityClass.ASYNCHRONOUS) {
                v_content = new Vector();
                v_content.add(new Integer(pc.rank));
                v_content.add(new Integer(pc.getMode()));
                v_class.add(v_content);
            } else {
                v_content = new Vector();
                v_content.add(new Integer(pc.rank));
                v_content.add(new Integer(pc.getMode()));
                v_class.add(v_content);
            }
            for (int n=0 ; n<nodeOrder.size() ; n++) {
                Object k = nodeOrder.get(n);
                Object target = m_priority_elt.get(k);
                // if +1 and -1 are separated, target is an Object[]
                if (target instanceof Object[]) {
                    Object[] t = (Object[])target;
                    if (t[0] == pc) {
                        // to do it right: if both +1 and -1 are in the same class, add the node only once :)
                        if (t[1] == pc) {
                            v_content.add(new Integer(n));
                            v_content.add(zaroo);
                        } else {
                            v_content.add(new Integer(n));
                            v_content.add(one);
                        }
                    } else if (t[1] == pc) {
                        v_content.add(new Integer(n));
                        v_content.add(minusOne);
                    }
                } else { // +1 and -1 aren't separated, always accept every transitions
                    if (target == pc) {
                        v_content.add(new Integer(n));
                        v_content.add(zaroo);
                    }
                }
            }
        }

        pclass = new int[v_class.size()][];
        for (int i=0 ; i<pclass.length ; i++) {
            Vector v_content = (Vector)v_class.get(i);
            int[] t = new int[v_content.size()];
            t[0] = ((Integer)v_content.get(0)).intValue();
            if (v_content.size() > 1) {
                t[1] = ((Integer)v_content.get(1)).intValue();
            } else {
                // if only one node in the class, async mode is useless!
                t[1] = GsReg2dynPriorityClass.SYNCHRONOUS;
            }
            for (int n=2 ; n<t.length ; n++) {
                t[n] = ((Integer)v_content.get(n)).intValue();
            }
            pclass[i] = t;
        }
        return pclass;
    }
    
    /**
     * reuse an old instance with a new graph: ensure to avoid problems
     */
    public void refreshGraph() {
        refreshGraph("_DEFAULT");
        if (v_priorityClass.size() == 0) {
            GsReg2dynPriorityClass lastClass = new GsReg2dynPriorityClass();
            v_priorityClass.add(lastClass);
            for (int i=0 ; i<nodeOrder.size() ; i++) {
                m_priority_elt.put(nodeOrder.get(i), lastClass);
            }
        }
    }
   /**
     * Apply a saved simulation parameter.
     * @param key 
     */
    public void refreshGraph(String key) {
        GsSimulationParameters params = (GsSimulationParameters)m_params.get(key);
        Vector newOrder = graph.getNodeOrder();
        nodeOrder = newOrder;

        if (params != null && params.genes != null) {
            if (params.genes.length == newOrder.size()) {
                progressLabel.setText("");
                if (newOrder.size() > 0) {
                    size = 1;
                } else {
                    size = 0;
                }
                boolean ok = true;
                for (int i=0 ; i<params.genes.length ; i++) {
                    GsRegulatoryVertex vertex = (GsRegulatoryVertex)newOrder.get(i);
                    size *= vertex.getMaxValue()+1;
                    if (!params.genes[i].equals(vertex.toString())) {
                        ok = false;
                        break;
                    }
                }
                if (ok) {
                    if (params.maxdepth != 0) {
                        getTextMaxDepth().setText(""+params.maxdepth);
                    }
                    if (params.maxnodes != 0) {
                        getTextMaxNodes().setText(""+params.maxnodes);
                    }
                    switch(params.mode) {
                        case Simulation.SEARCH_ASYNCHRONE_BF:
                            radioAsynchrone.setSelected(true);
                            radioBreadthFirst.setSelected(true);
                            break;
                        case Simulation.SEARCH_ASYNCHRONE_DF:
                            radioAsynchrone.setSelected(true);
                            radioDephtFirst.setSelected(true);
                            break;
                        case Simulation.SEARCH_SYNCHRONE:
                            radioSynchrone.setSelected(true);
                            break;
                        case Simulation.SEARCH_BYPRIORITYCLASS:
                            radioPriorityClass.setSelected(true);
                            setPclass(params.pclass, params.pclass_fine);
                            break;
                    }
                    resetState();
                    if (params.initStates == null) {
                        radioFullGraph.setSelected(true);
                    } else {
                        radioSelectedOnly.setSelected(true);
                        ((Reg2dynTableModel)tableInitStates.getModel()).setVStates(copyInitStates(params.initStates));
                    }
                    
                    // check correctness of values in the blockers!
                    minBlock = new int[params.genes.length];
                    maxBlock = new int[params.genes.length];
                    for (int i=0 ; i<minBlock.length ; i++) {
                        if (params.block[1][i] > ((GsRegulatoryVertex)newOrder.get(i)).getMaxValue()) {
                            progressLabel.setText(Translator.getString("STR_cleanedSimulationParameters"));
                            maxBlock[i] = -1;
                            minBlock[i] = -1;
                        } else {
                            maxBlock[i] = params.block[1][i];
                            minBlock[i] = params.block[0][i];
                        }
                    }

                    tableInitStates.setEnabled(radioSelectedOnly.isSelected());
                    model.reset(newOrder);
                    getRadioFullGraph().setText(Translator.getString("STR_full_graph")+"  ( "+size+" )");
                    return;
                }
            }
        }
        
        // FAILED: clean-up and warn the user
        if (params != null) {
            progressLabel.setText(Translator.getString("STR_conflictingSimulationParameters"));
        }
        
        nodeOrder = new Vector(newOrder.size());
        maxBlock = new int[graph.getNodeOrder().size()];
        minBlock = new int[maxBlock.length];
        if (blockConfigDialog != null) {
            blockConfigDialog.refresh(nodeOrder, minBlock, maxBlock);
        }
        if (newOrder.size() > 0) {
            size = 1;
        } else {
            size = 0;
        }
        for (int i=0 ; i<newOrder.size() ; i++) {
            GsRegulatoryVertex vertex = (GsRegulatoryVertex)newOrder.get(i);
            size *= vertex.getMaxValue()+1;
            nodeOrder.add(vertex);
            maxBlock[i] = -1;
            minBlock[i] = -1;
        }
        model.reset();
        getRadioFullGraph().setText(Translator.getString("STR_full_graph")+"  ( "+size+" )");
    }
    
    private void setPclass(int[][] pclass, boolean fine) {
        if (pclass == null) {
            return;
        }
        v_priorityClass = new Vector();
        m_priority_elt = new HashMap();
        for (int i=0 ; i<pclass.length ; i++) {
            GsReg2dynPriorityClass c = new GsReg2dynPriorityClass();
            int[] cl = pclass[i];
            c.rank = cl[0];
            c.setMode(cl[1]);
            v_priorityClass.add(c);
            if (fine) { // +1 and -1 can be separated
                for (int j=2 ; j<cl.length ; j+= 2) {
                    Object[] target = (Object[])m_priority_elt.get(nodeOrder.get(cl[j]));
                    if (target == null) {
                        target = new Object[2];
                        m_priority_elt.put(nodeOrder.get(cl[j]), target);
                    }
                    switch(cl[j+1]) {
                        case 0:
                            target[0] = c;
                            target[1] = c;
                            break;
                        case 1:
                            target[0] = c;
                            break;
                        case -1:
                            target[1] = c;
                            break;
                    }
                }
            } else { // +1 and -1 are not separated here!
                for (int j=2 ; j<cl.length ; j+= 2) {
                        m_priority_elt.put(nodeOrder.get(cl[j]), c);
                }
            }
        }
    }

    protected void resetState() {
        model.reset();
    }
    
    protected void deleteStateRow() {
        model.deleteRow(tableInitStates.getSelectedRow());
    }
    private JButton getButtonDelStateRow() {
        if (buttonDelStateRow == null) {
            buttonDelStateRow = new JButton("X");
            buttonDelStateRow.setForeground(Color.RED);
            buttonDelStateRow.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    deleteStateRow();
                }
            });
        }
        return buttonDelStateRow;
    }
    private JButton getButtonResetStates() {
        if (buttonResetStates == null) {
            buttonResetStates = new JButton(Translator.getString("STR_reset"));
            buttonResetStates.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    resetState();
                }
            });
        }
        return buttonResetStates;
    }
    
    public void dispose() {
        if (blockConfigDialog != null) {
            blockConfigDialog.dispose();
            blockConfigDialog = null;
        }
    }

    /**
     * the table's structure has changed, update it. 
     * note: don't try to implement tablemodellistener: when this one is called, 
     * the table structure change may not be applied yet in the column model
     */
    public void updateTable() {
        Enumeration e_col = tableInitStates.getColumnModel().getColumns();
        int i=-1;
        while (e_col.hasMoreElements()) {
            TableColumn col = (TableColumn)e_col.nextElement();
            i++;
            int w = 15+8*nodeOrder.get(i).toString().length();
            col.setPreferredWidth(w+10);
            col.setMinWidth(w);
        }
    }
}

class ParameterComboModel extends DefaultComboBoxModel {
    /** */
    private static final long serialVersionUID = 6067293119726193354L;
    Map m;
    Vector v;

    /**
     * @param m
     */
    public ParameterComboModel(Map m) {
        this.m = m;
        fireContentsChanged(this, 0,0);
    }

    public int getSize() {
        return v.size();
    }

    public Object getElementAt(int index) {
        return v.get(index);
    }
    
    protected void fireContentsChanged(Object source, int index0, int index1) {
        v = new Vector(m.size());
        Iterator it = m.keySet().iterator();
        while (it.hasNext()) {
            Object k = it.next();
            v.add(m.get(k));
        }
        super.fireContentsChanged(source, index0, index1);
    }
}
