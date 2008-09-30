package fr.univmrs.tagc.GINsim.reg2dyn;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fr.univmrs.tagc.GINsim.global.GsEnv;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.GsInitialStatePanel;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.MutantSelectionPanel;
import fr.univmrs.tagc.common.Tools;
import fr.univmrs.tagc.common.datastore.ObjectStore;
import fr.univmrs.tagc.common.datastore.gui.GenericListPanel;
import fr.univmrs.tagc.common.datastore.gui.GenericListSelectionPanel;
import fr.univmrs.tagc.common.manageressources.Translator;
import fr.univmrs.tagc.common.widgets.SplitPane;

/**
 * frame to set up the simulation
 */
public class GsReg2dynFrame extends BaseReg2DynFrame implements ListSelectionListener {
    private static final long serialVersionUID = -4386183125281770860L;
    
    GsSimulationParameterList paramList;
    GsSimulationParameters currentParameter;
    MutantSelectionPanel mutantPanel;
    GenericListPanel listPanel;
    Simulation sim;
    boolean isrunning = false;
    boolean refreshing = false;
    
    private JFrame frame;
    
    private ChangeListener radioChangeListener = null;
    
    private ButtonGroup depthGrp = new ButtonGroup();
    private JRadioButton radioDephtFirst = null;
    private JRadioButton radioBreadthFirst = null;

    private JTextField textMaxDepth = null;
    private JTextField textMaxNodes = null;
    private GenericListSelectionPanel selectPriorityClass;
    
    Insets indentInset = new Insets(0, 30, 0, 0);
    GsInitialStatePanel initStatePanel = null;
    private JPanel mainPanel;

    /**
     * @param frame
     * @param paramList
     */
    public GsReg2dynFrame(JFrame frame, GsSimulationParameterList paramList) {
        super(frame, "display.simulation", 800, 400);
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
        SplitPane spane = new SplitPane();
        spane.setName("display.configSimulation");
        spane.setRightComponent(getMainPanel());
        listPanel = new GenericListPanel();
        listPanel.addSelectionListener(this);
        listPanel.setList(paramList);
        listPanel.setTitle(Translator.getString("STR_simulationSettings"));
        spane.setLeftComponent(listPanel);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = c.weighty = 1;
        setMainPanel(spane);
    }
    
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            
            // the simulation strategy part
            JPanel panel = new JPanel();
            panel.setLayout(new GridBagLayout());
            panel.setBorder(BorderFactory.createTitledBorder(Translator.getString("STR_reg2dyn_mode")));
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
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1;
			panel.add(getPriorityClassSelector(), c);

			c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 2;
			c.anchor = GridBagConstraints.WEST;
			c.insets = indentInset;
			panel.add(getRadioBreadthFirst(), c);
			c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 3;
			c.anchor = GridBagConstraints.WEST;
			c.insets = indentInset;
			panel.add(getRadioDephtFirst(), c);

            
            // size limits
            panel = new JPanel();
            panel.setLayout(new GridBagLayout());
            panel.setBorder(BorderFactory.createTitledBorder(Translator.getString("STR_sizeLimits")));
            c = new GridBagConstraints();
            c.gridx = 1;
            c.gridy = 1;
            c.fill = GridBagConstraints.BOTH;
            c.weightx = 0.4;
            mainPanel.add(panel, c);

            c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.anchor = GridBagConstraints.WEST;
            c.insets = indentInset;
            panel.add(new JLabel(Translator.getString("STR_maximum_depth")), c);
            c = new GridBagConstraints();
            c.gridx = 1;
            c.gridy = 0;
            c.anchor = GridBagConstraints.WEST;
            c.insets = indentInset;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1;
            panel.add(getTextMaxDepth(), c);
            c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 1;
            c.anchor = GridBagConstraints.WEST;
            c.insets = indentInset;
            panel.add(new JLabel(Translator.getString("STR_maximum_nodes")), c);
            c = new GridBagConstraints();
            c.gridx = 1;
            c.gridy = 1;
            c.anchor = GridBagConstraints.WEST;
            c.insets = indentInset;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1;
            panel.add(getTextMaxNodes(), c);
            
            
            // bottom-right part with mutants
            c = new GridBagConstraints();
            c.gridx = 1;
            c.gridy = 0;
            c.fill = GridBagConstraints.BOTH;
            c.weightx = 0.6;
            ObjectStore store = currentParameter == null ? null : currentParameter.store;
            mutantPanel = new MutantSelectionPanel(this, paramList.graph, store);
            mainPanel.add(mutantPanel, c);
            
            // initial state
            initStatePanel = new GsInitialStatePanel(this, paramList.graph.getNodeOrder(), paramList.imanager, true);
            c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 2;
            c.gridwidth = 2;
            c.fill = GridBagConstraints.BOTH;
            c.weightx = 1;
            c.weighty = 1;
            mainPanel.add(initStatePanel, c);
        }
        return mainPanel;
    }
    

    /**
     * 
     */
    protected void run() {
        setMessage(Translator.getString("STR_wait_msg"));
        bcancel.setText(Translator.getString("STR_abort"));

        // nearly everything should be disabled
        radioBreadthFirst.setEnabled(false);
        radioDephtFirst.setEnabled(false);
        selectPriorityClass.setEnabled(false);

        initStatePanel.setEnabled(false);
        brun.setEnabled(false);
        mutantPanel.setEnabled(false);
        initStatePanel.setEnabled(false);
        textMaxDepth.setEnabled(false);
        textMaxNodes.setEnabled(false);

        isrunning = true;
        sim = new Simulation(paramList.graph, this, currentParameter);
    }

    /**
     * simulation is done (or interrupted), now choose what to do with the new graph.
     * @param graph the dynamic graph
     */
    public void endSimu(GsGraph graph) {
        isrunning = false;
        if (null == graph) {
            Tools.error("no state transition graph", frame);
        } else {
            GsEnv.whatToDoWithGraph(frame, graph, true);
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
        currentParameter.breadthFirst = radioBreadthFirst.isSelected();
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
    
    private GenericListSelectionPanel getPriorityClassSelector() {
    	if (selectPriorityClass == null) {
    		selectPriorityClass = new PrioritySelectionPanel(this, paramList.pcmanager);
    	}
    	return selectPriorityClass;
    }

    public void valueChanged(ListSelectionEvent e) {
        int[] t_sel = listPanel.getSelection();
        if (t_sel.length != 1) {
            currentParameter = null;
        } else {
            currentParameter = (GsSimulationParameters)paramList.getElement(null, t_sel[0]);
            mutantPanel.setStore(currentParameter.store);
            initStatePanel.setParam(currentParameter);
    		selectPriorityClass.setStore(currentParameter.store, GsSimulationParameters.PCLASS);
    		if (currentParameter.breadthFirst) {
    			radioBreadthFirst.setSelected(true);
    		} else {
    			radioDephtFirst.setSelected(true);
    		}
        }
        refresh();
    }
    
    private void refresh() {
        refreshing = true;
        if (currentParameter == null) {
            mutantPanel.setEnabled(false);
            textMaxDepth.setEnabled(false);
            textMaxNodes.setEnabled(false);
            initStatePanel.setEnabled(false);
        } else {
            mutantPanel.setEnabled(true);
            mutantPanel.refresh();
            textMaxDepth.setText(currentParameter.maxdepth > 0 ? ""+currentParameter.maxdepth : "");
            textMaxNodes.setText(currentParameter.maxnodes > 0 ? ""+currentParameter.maxnodes : "");
            textMaxDepth.setEnabled(true);
            textMaxNodes.setEnabled(true);
        }
        refreshing = false;
    }
}