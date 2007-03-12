package fr.univmrs.ibdm.GINsim.reg2dyn;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import fr.univmrs.ibdm.GINsim.global.GsEnv;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.gui.GsJTable;
import fr.univmrs.ibdm.GINsim.gui.GsListPanel;
import fr.univmrs.ibdm.GINsim.gui.GsStackDialog;
import fr.univmrs.ibdm.GINsim.manageressources.Translator;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsMutantListManager;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryMutantDef;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryMutants;
import fr.univmrs.ibdm.GINsim.util.widget.MSplitPane;

/**
 * frame to set up the simulation
 */
public class GsReg2dynFrame extends GsStackDialog implements ListSelectionListener {
    private static final long serialVersionUID = -4386183125281770860L;
    
    GsSimulationParameterList paramList;
    GsSimulationParameters currentParameter;
    GsMutantModel mutantModel;
    GsListPanel listPanel;
    Simulation sim;
    boolean isrunning = false;
    boolean refreshing = false;
    
    private JFrame frame;
    
    private ButtonGroup syncGrp = new ButtonGroup();
    private JRadioButton radioAsynchrone = null;
    private JRadioButton radioSynchrone = null;
    private JRadioButton radioPriorityClass;
    private ChangeListener radioChangeListener = null;
    
    private ButtonGroup depthGrp = new ButtonGroup();
    private JRadioButton radioDephtFirst = null;
    private JRadioButton radioBreadthFirst = null;

    private JTextField textMaxDepth = null;
    private JTextField textMaxNodes = null;
    private JButton buttonConfigMutants = null;
    private JButton buttonCfgPriorityClass;
    
    private JScrollPane jScrollPane = null;
    private GsJTable tableInitStates = null;
    private Reg2dynTableModel model = null;
    private JButton buttonDelStateRow = null;
    private JButton buttonResetStateRow = null;

    Insets topInset = new Insets(20,0,0,0);
    Insets indentInset = new Insets(0, 30, 0, 0);

    private JPanel mainPanel;

    /**
     * @param frame
     * @param paramList
     */
    public GsReg2dynFrame(JFrame frame, GsSimulationParameterList paramList) {
        super(frame);
        this.frame = frame;
        this.paramList = paramList;
        paramList.graph.addBlockEdit(this);
        initialize();
        this.setTitle(Translator.getString("STR_reg2dynRunningTitle"));
        this.addWindowListener(new java.awt.event.WindowAdapter() { 
            public void windowClosing(java.awt.event.WindowEvent e) {
                cancel();
            }
        });
    }

    private void initialize() {
        setSize(800, 400);
        JSplitPane spane = new MSplitPane("display.configSimulation");
        spane.setRightComponent(getMainPanel());
        listPanel = new GsListPanel();
        listPanel.addSelectionListener(this);
        listPanel.setList(paramList);
        listPanel.setTitle(Translator.getString("STR_simulationSettings"));
        spane.setLeftComponent(listPanel);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = c.weighty = 1;
        setMainPanel(spane, "display.simulation", 800, 400);
    }
    
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            
            // the simulation strategy part
            JPanel panel = new JPanel();
            panel.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.gridheight = 2;
            c.fill = GridBagConstraints.BOTH;
            c.weightx = 0.4;
            mainPanel.add(panel, c);

            panel.setLayout(new GridBagLayout());
            c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.anchor = GridBagConstraints.WEST;
            c.insets = topInset;
            panel.add(new JLabel(Translator.getString("STR_reg2dyn_mode")), c);
            c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 1;
            c.anchor = GridBagConstraints.WEST;
            panel.add(getRadioSynchrone(), c);
            c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 2;
            c.anchor = GridBagConstraints.WEST;
            panel.add(getRadioAsynchrone(), c);
            c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 3;
            c.anchor = GridBagConstraints.WEST;
            c.insets = indentInset;
            panel.add(getRadioBreadthFirst(), c);
            c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 4;
            c.anchor = GridBagConstraints.WEST;
            c.insets = indentInset;
            panel.add(getRadioDephtFirst(), c);
            c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 5;
            c.anchor = GridBagConstraints.WEST;
            panel.add(getRadioPriorityClass(), c);
            c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 6;
            c.anchor = GridBagConstraints.WEST;
            c.insets = indentInset;
            panel.add(getButtonCfgPriorityClass(), c);
            
            // the top-right part with number limit
            panel = new JPanel();
            panel.setLayout(new GridBagLayout());
            c = new GridBagConstraints();
            c.gridx = 1;
            c.gridy = 0;
            c.fill = GridBagConstraints.BOTH;
            c.weightx = 0.4;
            mainPanel.add(panel, c);

            c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.anchor = GridBagConstraints.WEST;
            c.insets = topInset;
            panel.add(new JLabel(Translator.getString("STR_sizeLimits")), c);
            c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 1;
            c.anchor = GridBagConstraints.WEST;
            c.insets = indentInset;
            panel.add(new JLabel(Translator.getString("STR_maximum_depth")), c);
            c = new GridBagConstraints();
            c.gridx = 1;
            c.gridy = 1;
            c.anchor = GridBagConstraints.WEST;
            c.insets = indentInset;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1;
            panel.add(getTextMaxDepth(), c);
            c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 2;
            c.anchor = GridBagConstraints.WEST;
            c.insets = indentInset;
            panel.add(new JLabel(Translator.getString("STR_maximum_nodes")), c);
            c = new GridBagConstraints();
            c.gridx = 1;
            c.gridy = 2;
            c.anchor = GridBagConstraints.WEST;
            c.insets = indentInset;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1;
            panel.add(getTextMaxNodes(), c);
            
            
            // bottom-right part with mutants
            panel = new JPanel();
            panel.setLayout(new GridBagLayout());
            c = new GridBagConstraints();
            c.gridx = 1;
            c.gridy = 1;
            c.fill = GridBagConstraints.BOTH;
            c.weightx = 0.6;
            mainPanel.add(panel, c);

            c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.anchor = GridBagConstraints.WEST;
            c.insets = new Insets(20, 30, 0, 0);
            panel.add(new JLabel(Translator.getString("STR_mutants")), c);
            c = new GridBagConstraints();
            c.gridx = 1;
            c.gridy = 1;
            c.anchor = GridBagConstraints.EAST;
            panel.add(getButtonConfigMutants(), c);
            c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 1;
            c.gridwidth = 1;
            c.weightx = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.anchor = GridBagConstraints.WEST;
            c.insets = indentInset;
            mutantModel = new GsMutantModel((GsRegulatoryMutants)paramList.graph.getObject(GsMutantListManager.key, true));
            panel.add(new JComboBox(mutantModel), c);
            
            // initial state
            panel = new JPanel();
            panel.setLayout(new GridBagLayout());
            c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 2;
            c.gridwidth = 2;
            c.fill = GridBagConstraints.BOTH;
            c.weightx = 1;
            c.weighty = 1;
            mainPanel.add(panel, c);

            c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.anchor = GridBagConstraints.WEST;
            c.insets = topInset;
            panel.add(new JLabel(Translator.getString("STR_Initial_state")), c);
            c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 1;
            c.gridwidth = 4;
            c.fill = GridBagConstraints.BOTH;
            c.weightx = 1;
            c.weighty = 1;
            panel.add(getJScrollPane(), c);
            c = new GridBagConstraints();
            c.gridx = 1;
            c.gridy = 0;
            panel.add(getButtonDelStateRow(), c);
            c = new GridBagConstraints();
            c.gridx = 2;
            c.gridy = 0;
            panel.add(getButtonResetStateRow(), c);
        }
        return mainPanel;
    }
    
    /**
     * This method initializes tableInitStates
     * 
     * @return javax.swing.JTable
     */
    private javax.swing.JTable getTableInitStates() {
        if(tableInitStates == null) {
            Vector nodeNames = paramList.graph.getNodeOrder();
            tableInitStates = new GsJTable();
            model = new Reg2dynTableModel(nodeNames, this, paramList.imanager);
            tableInitStates.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
            tableInitStates.setModel(model);
            tableInitStates.getTableHeader().setReorderingAllowed(false);
            tableInitStates.setEnabled(false);
            tableInitStates.setRowSelectionAllowed(true);
            tableInitStates.setColumnSelectionAllowed(true);

            model.setTable(tableInitStates);
        }
        return tableInitStates;
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
            int w = 15+8*5;
            col.setPreferredWidth(w+10);
            col.setMinWidth(w);
        }
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
    protected void deleteStateRow() {
    	int[] t = tableInitStates.getSelectedRows();
    	for (int i=t.length-1 ; i>=0 ; i--) {
    		model.deleteRow(t[i]);
    	}
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

    protected void resetStateRow() {
        model.reset();
    }
    private JButton getButtonResetStateRow() {
        if (buttonResetStateRow == null) {
            buttonResetStateRow = new JButton(Translator.getString("STR_reset"));
            buttonResetStateRow.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    resetStateRow();
                }
            });
        }
        return buttonResetStateRow;
    }

    /**
     * 
     */
    protected void run() {
        setMessage(Translator.getString("STR_wait_msg"));
        bcancel.setText(Translator.getString("STR_abort"));

        // nearly everything should be unenabled
        radioSynchrone.setEnabled(false);
        radioAsynchrone.setEnabled(false);
        radioBreadthFirst.setEnabled(false);
        radioDephtFirst.setEnabled(false);
        radioPriorityClass.setEnabled(false);
        buttonCfgPriorityClass.setEnabled(false);

        tableInitStates.setEnabled(false);
        brun.setEnabled(false);
        buttonConfigMutants.setEnabled(false);
        buttonDelStateRow.setEnabled(false);
        textMaxDepth.setEnabled(false);
        textMaxNodes.setEnabled(false);

        isrunning = true;
        sim = new Simulation(paramList.graph, this, currentParameter);
    }

    /**
     * set the progress level, to give the user some feedback
     * @param n
     */
    public void setProgress(int n) {
        if (isrunning) {
            setMessage(""+n);
        }
    }

    /**
     * simulation is done (or interrupted), now choose what to do with the new graph.
     * @param graph the dynamic graph
     */
    public void endSimu(GsGraph graph) {
        isrunning = false;
        if (null == graph) {
            GsEnv.error("no state transition graph", frame);
        } else {
            GsEnv.whatToDoWithGraph(frame, graph);
        }
        cancel();
    }

    /**
     * close the frame, eventually end the simulation first 
     */
    protected void cancel() {
        if (isrunning) {
            sim.interrupt();    
        }
        paramList.graph.removeBlockEdit(this);
        super.cancel();
    }

    /**
     * This method initializes radioAsynchrone
     * 
     * @return javax.swing.JRadioButton
     */
    private javax.swing.JRadioButton getRadioAsynchrone() {
        if(radioAsynchrone == null) {
            radioAsynchrone = new javax.swing.JRadioButton(Translator.getString("STR_asynchrone"));
            radioAsynchrone.addChangeListener(getRadioChangeListener());
            syncGrp.add(radioAsynchrone);
            radioAsynchrone.setSelected(true);
        }
        return radioAsynchrone;
    }
    private ChangeListener getRadioChangeListener() {
        if (radioChangeListener == null) {
            radioChangeListener = new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    radioChanged(e);
                }
            };
        }
        return radioChangeListener;
    }
    /**
     * @param e the original event 
     */
    protected void radioChanged(ChangeEvent e) {
        setMessage("");
        if (refreshing || currentParameter == null) {
            return;
        }
        if (!(e.getSource() instanceof JRadioButton)) {
            return;
        }
        if (!((JRadioButton)e.getSource()).isSelected()) {
            return;
        }
        if (radioAsynchrone.isSelected()) {
            currentParameter.mode = radioBreadthFirst.isSelected() ? Simulation.SEARCH_ASYNCHRONE_BF : Simulation.SEARCH_ASYNCHRONE_DF;
            getRadioBreadthFirst().setEnabled(true);
            getRadioDephtFirst().setEnabled(true);
            buttonCfgPriorityClass.setEnabled(false);
        } else {
            getRadioBreadthFirst().setEnabled(false);
            getRadioDephtFirst().setEnabled(false);
            if (radioPriorityClass.isSelected()) {
                currentParameter.mode = Simulation.SEARCH_BYPRIORITYCLASS;
                buttonCfgPriorityClass.setEnabled(true);
            } else {
                buttonCfgPriorityClass.setEnabled(false);
                currentParameter.mode = Simulation.SEARCH_SYNCHRONE;
            }
        }
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
            radioSynchrone.addChangeListener(getRadioChangeListener());
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
            radioPriorityClass.addChangeListener(getRadioChangeListener());
            syncGrp.add(radioPriorityClass);
            radioPriorityClass.setSelected(true);
        }
        return radioPriorityClass;
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
            radioDephtFirst.addChangeListener(getRadioChangeListener());

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
            radioBreadthFirst.addChangeListener(getRadioChangeListener());
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
            textMaxDepth.addFocusListener(new FocusListener() {
                public void focusLost(FocusEvent e) {
                    updateMaxDepth();
                }
                public void focusGained(FocusEvent e) {
                }
            });
            textMaxDepth.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    updateMaxDepth();
                }
            });
        }
        return textMaxDepth;
    }
    protected void updateMaxDepth() {
        setMessage("");
        if (refreshing || currentParameter == null) {
            return;
        }
        try {
            String s = textMaxDepth.getText().trim();
            if ("".equals(s)) {
                currentParameter.maxdepth = 0;
            } else {
                currentParameter.maxdepth = Integer.parseInt(s);
            }
        } catch (NumberFormatException e) {
            setMessage("should be an integer");
        }
        textMaxDepth.setText(currentParameter.maxdepth > 0 ? ""+currentParameter.maxdepth : "");
    }
    
    protected void updateMaxNodes() {
        setMessage("");
        if (refreshing || currentParameter == null) {
            return;
        }
        try {
            String s = textMaxNodes.getText().trim();
            if ("".equals(s)) {
                currentParameter.maxnodes = 0;
            } else {
                currentParameter.maxnodes = Integer.parseInt(s);
            }
        } catch (NumberFormatException e) {
            setMessage("should be an integer");
        }
        textMaxNodes.setText(currentParameter.maxnodes > 0 ? ""+currentParameter.maxnodes : "");
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
            textMaxNodes.addFocusListener(new FocusListener() {
                public void focusLost(FocusEvent e) {
                    updateMaxNodes();
                }
                public void focusGained(FocusEvent e) {
                }
            });
            textMaxNodes.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    updateMaxNodes();
                }
            });
        }
        return textMaxNodes;
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
        addTempPanel(new GsReg2dynPriorityClassConfig(paramList.graph.getNodeOrder(), currentParameter));
    }
    
    private JButton getButtonConfigMutants() {
        if (buttonConfigMutants == null) {
            buttonConfigMutants = new JButton(Translator.getString("STR_configure"));
            buttonConfigMutants.addActionListener(new java.awt.event.ActionListener() { 
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    addTempPanel(GsRegulatoryMutants.getMutantConfigPanel(paramList.graph));
                    mutantModel.setMutantList((GsRegulatoryMutants)paramList.graph.getObject(GsMutantListManager.key, true));
                }
            });
        }
        return buttonConfigMutants;
    }

    public void valueChanged(ListSelectionEvent e) {
        int[] t_sel = listPanel.getSelection();
        if (t_sel.length != 1) {
            currentParameter = null;
        } else {
            currentParameter = (GsSimulationParameters)paramList.getElement(t_sel[0]);
            mutantModel.setParam(currentParameter);
            model.setParam(currentParameter);
        }
        refresh();
    }
    
    private void refresh() {
        refreshing = true;
        if (currentParameter == null) {
            // disable everything
            radioAsynchrone.setEnabled(false);
            radioSynchrone.setEnabled(false);
            radioPriorityClass.setEnabled(false);
            radioBreadthFirst.setEnabled(false);
            radioDephtFirst.setEnabled(false);
            buttonCfgPriorityClass.setEnabled(false);

            buttonConfigMutants.setEnabled(false);
            textMaxDepth.setEnabled(false);
            textMaxNodes.setEnabled(false);
            tableInitStates.setEnabled(false);
        } else {
            // enable and refresh everything
            radioAsynchrone.setEnabled(true);
            radioSynchrone.setEnabled(true);
            radioPriorityClass.setEnabled(true);
            tableInitStates.setEnabled(true);
            switch (currentParameter.mode) {
            case Simulation.SEARCH_ASYNCHRONE_BF:
                radioAsynchrone.setSelected(true);
                radioBreadthFirst.setEnabled(true);
                radioDephtFirst.setEnabled(true);
                radioBreadthFirst.setSelected(true);
                buttonCfgPriorityClass.setEnabled(false);
                break;
            case Simulation.SEARCH_ASYNCHRONE_DF:
                radioAsynchrone.setSelected(true);
                radioBreadthFirst.setEnabled(true);
                radioDephtFirst.setEnabled(true);
                radioDephtFirst.setSelected(true);
                buttonCfgPriorityClass.setEnabled(false);
                break;
            case Simulation.SEARCH_SYNCHRONE:
                radioSynchrone.setSelected(true);
                radioBreadthFirst.setEnabled(false);
                radioDephtFirst.setEnabled(false);
                buttonCfgPriorityClass.setEnabled(false);
                break;
            case Simulation.SEARCH_BYPRIORITYCLASS:
                radioPriorityClass.setSelected(true);
                radioBreadthFirst.setEnabled(false);
                radioDephtFirst.setEnabled(false);
                buttonCfgPriorityClass.setEnabled(true);
                break;
            }
            buttonConfigMutants.setEnabled(true);
            textMaxDepth.setText(currentParameter.maxdepth > 0 ? ""+currentParameter.maxdepth : "");
            textMaxNodes.setText(currentParameter.maxnodes > 0 ? ""+currentParameter.maxnodes : "");
            textMaxDepth.setEnabled(true);
            textMaxNodes.setEnabled(true);
        }
        refreshing = false;
    }
}

class GsMutantModel extends DefaultComboBoxModel implements ComboBoxModel {
    private static final long serialVersionUID = 2348678706086666489L;
    
    GsRegulatoryMutants listMutants;
    GsSimulationParameters currentParam = null;
    
    GsMutantModel(GsRegulatoryMutants listMutants) {
        this.listMutants = listMutants;
    }
    
    void setMutantList(GsRegulatoryMutants mutants) {
            this.listMutants = mutants;
            fireContentsChanged(this, 0, getSize());
    }

    void setParam(GsSimulationParameters param) {
        currentParam = param;
        setSelectedItem(currentParam.mutant);
        fireContentsChanged(this, 0, getSize());
    }
    
    public Object getSelectedItem() {
        if (currentParam == null || currentParam.mutant == null) {
            return "--";
        }
        return currentParam.mutant;
    }

    public void setSelectedItem(Object anItem) {
        if (anItem instanceof GsRegulatoryMutantDef) {
            currentParam.mutant = (GsRegulatoryMutantDef)anItem;
        } else {
            currentParam.mutant = null;
        }
    }

    public Object getElementAt(int index) {
        if (index == 0 || listMutants == null) {
            return "--";
        }
        return listMutants.getElement(index-1);
    }

    public int getSize() {
        if (listMutants == null) {
            return 1;
        }
        return listMutants.getNbElements()+1;
    }
}