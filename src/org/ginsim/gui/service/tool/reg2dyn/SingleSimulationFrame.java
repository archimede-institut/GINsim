package org.ginsim.gui.service.tool.reg2dyn;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.ginsim.graph.common.Graph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.regulatorygraph.initialstate.InitialStatePanel;
import org.ginsim.gui.graph.regulatorygraph.mutant.MutantSelectionPanel;
import org.ginsim.gui.resource.Translator;
import org.ginsim.gui.utils.data.GenericListPanel;
import org.ginsim.gui.utils.data.GenericListSelectionPanel;
import org.ginsim.gui.utils.widgets.SplitPane;
import org.ginsim.utils.data.ObjectStore;

import fr.univmrs.tagc.common.OptionStore;
import fr.univmrs.tagc.common.utils.GUIMessageUtils;



/**
 * The frame displayed to the user when he want to run a simulation
 */
public class SingleSimulationFrame extends BaseSimulationFrame implements ListSelectionListener{
	private static final long serialVersionUID = 8687415239702718705L;
	
	private static final String[] simulationMethodsNames = {Translator.getString("STR_STG"), Translator.getString("STR_SCCG"), Translator.getString("STR_HTG")};


/* *************** SIMULATION RELATED PARAMETERS **********************/
	/**
	 * The regulatoryGraph frame
	 */
	private Frame regGraphFrame;
	private SimulationParameterList paramList;
	private SimulationParameters currentParameter;
	private Simulation sim;
	boolean isrunning = false;
	boolean refreshing = false;
 
/* *************** GUI RELATED PARAMETERS **********************/
	/* ****** PANELS **********/
	private JPanel mainPanel;
	private JPanel simulationStrategyPanel;
	private JPanel graphSizeLimits;
	private MutantSelectionPanel mutantPanel;
	private GenericListPanel listPanel;
	private InitialStatePanel initStatePanel = null;
	
	private ChangeListener radioChangeListener = null;
	
	private JComboBox simulationMethodsComboBox;

	private ButtonGroup depthGrp = new ButtonGroup();
	private JRadioButton radioDephtFirst = null;
	private JRadioButton radioBreadthFirst = null;

  

	private JTextField textMaxDepth = null;
	private JTextField textMaxNodes = null;
	private GenericListSelectionPanel selectPriorityClass;
	
	private Insets indentInset = new Insets(0, 30, 0, 0);

	
	
	public SingleSimulationFrame(Frame regGraphFrame, SimulationParameterList paramList) {
		super(regGraphFrame, "display.simulation", 800, 400);
		this.regGraphFrame = regGraphFrame;
		this.paramList = paramList;
		GUIManager.getInstance().addBlockEdit( paramList.graph, this);
		initialize();
		this.setTitle(Translator.getString("STR_reg2dynRunningTitle"));
		this.addWindowListener(new java.awt.event.WindowAdapter() { 
			public void windowClosing(java.awt.event.WindowEvent e) {
				cancel();
			}
		});
	}

/* *************** FILLING THE PANELS **********************/
   
	public void initialize() {
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
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			//c.gridheight = 2;
			c.fill = GridBagConstraints.BOTH;
			c.weightx = 0.4;
			mainPanel.add(getSimulationStrategyPanel(), c);
			
		
			// bottom-right part with mutants
			c.gridx++;
			c.fill = GridBagConstraints.BOTH;
			c.weightx = 0.6;
			c.gridheight = 1;
			ObjectStore store = currentParameter == null ? null : currentParameter.store;
			mutantPanel = new MutantSelectionPanel(this, paramList.graph, store);
			mainPanel.add(mutantPanel, c);

			// size limits
			c.gridy++;
			mainPanel.add(getGraphSizeLimitsPanel(), c);
			
			c.gridx--;
			mainPanel.add(getPriorityClassSelector(), c);
			
			// initial state
			initStatePanel = new InitialStatePanel(this, paramList.graph, true);
			c.gridx = 0;
			c.gridy++;
			c.gridwidth = 2;
			c.fill = GridBagConstraints.BOTH;
			c.weightx = 1;
			c.weighty = 1;
			mainPanel.add(initStatePanel, c);
		}
		return mainPanel;
	}

	
	public JPanel getSimulationStrategyPanel() {
		if (simulationStrategyPanel == null) {
		   	simulationStrategyPanel = new JPanel();
			simulationStrategyPanel.setLayout(new GridBagLayout());
			simulationStrategyPanel.setBorder(BorderFactory.createTitledBorder(Translator.getString("STR_reg2dyn_mode")));
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.insets = indentInset;
			c.fill = GridBagConstraints.BOTH;
			
			c.gridx = 0;
			c.gridy++;
			c.gridwidth = 2;
			simulationStrategyPanel.add(getSimulationMethodsComboBox(), c);
			
			c.gridx = 0;
			c.gridy++;
			c.gridwidth = 1;
			simulationStrategyPanel.add(getRadioBreadthFirst(), c);
			c.gridx++;
			simulationStrategyPanel.add(getRadioDephtFirst(), c);
   		}	
		return simulationStrategyPanel;
	}
  
	
	public JPanel getGraphSizeLimitsPanel() {
		if (graphSizeLimits == null) {
			graphSizeLimits = new JPanel();
			graphSizeLimits.setLayout(new GridBagLayout());
			graphSizeLimits.setBorder(BorderFactory.createTitledBorder(Translator.getString("STR_sizeLimits")));
			GridBagConstraints c = new GridBagConstraints();
	
			c.gridx = 0;
			c.gridy = 0;
			c.anchor = GridBagConstraints.WEST;
			c.insets = indentInset;
			graphSizeLimits.add(new JLabel(Translator.getString("STR_maximum_depth")), c);
	   	
			c.gridx++;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1;
			graphSizeLimits.add(getTextMaxDepth(), c);
			
			c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 1;
			c.anchor = GridBagConstraints.WEST;
			c.insets = indentInset;
			graphSizeLimits.add(new JLabel(Translator.getString("STR_maximum_nodes")), c);
			
			c.gridx++;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1;
			graphSizeLimits.add(getTextMaxNodes(), c);
		}
		return graphSizeLimits;
	}

/* *************** INITILISING THE WIDGETS (RADIO, COMBOBOX...) **********************/

	/**
	 * This method initializes radioDephtFirst
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getRadioDephtFirst() {
		if (radioDephtFirst == null) {
			radioDephtFirst = new JRadioButton(Translator.getString("STR_depth_first"));
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
	private JRadioButton getRadioBreadthFirst() {
		if (radioBreadthFirst == null) {
			radioBreadthFirst = new JRadioButton(Translator.getString("STR_breadth_first"));
			depthGrp.add(radioBreadthFirst);
			radioBreadthFirst.addChangeListener(getRadioChangeListener());
		}
		return radioBreadthFirst;
	}
	
	private JComboBox getSimulationMethodsComboBox() {
		if (simulationMethodsComboBox == null) {
			simulationMethodsComboBox = new JComboBox(simulationMethodsNames);
			Integer selectedIndex = (Integer)OptionStore.getOption("simulation.defaultMethod");
			if (selectedIndex != null) {
				int v = selectedIndex.intValue();
				simulationMethodsComboBox.setSelectedIndex(v);
			}
			simulationMethodsComboBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					currentParameter.simulationStrategy = simulationMethodsComboBox.getSelectedIndex();
					if (currentParameter.simulationStrategy != SimulationParameters.STRATEGY_STG) {
						radioBreadthFirst.setEnabled(false);
						radioDephtFirst.setEnabled(false);
					} else {
						radioBreadthFirst.setEnabled(true);
						radioDephtFirst.setEnabled(true);
					}
				}
			});		
		}
		return simulationMethodsComboBox;
	}

  
	
	private GenericListSelectionPanel getPriorityClassSelector() {
		if (selectPriorityClass == null) {
			selectPriorityClass = new PrioritySelectionPanel(this, paramList.pcmanager);
		}
		return selectPriorityClass;
	}

	/**
	 * This method initializes textMaxDepth
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getTextMaxDepth() {
		if (textMaxDepth == null) {
			textMaxDepth = new JTextField();
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
	
	/**
	 * This method initializes textMaxNodes
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getTextMaxNodes() {
		if (textMaxNodes == null) {
			textMaxNodes = new JTextField();
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

	
/* *************** RUN AND SIMULATION **********************/

	/**
	 * simulation is done (or interrupted), now choose what to do with the new graph.
	 * @param graph the dynamic graph
	 */
	public void endSimu( Graph graph) {
		isrunning = false;
		if (null == graph) {
			GUIMessageUtils.openErrorDialog("no graph generated", regGraphFrame);
		} else {
			GUIManager.getInstance().whatToDoWithGraph( graph, true);
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
		GUIManager.getInstance().removeBlockEdit( paramList.graph, this);
		OptionStore.setOption("simulation.defaultMethod", new Integer(simulationMethodsComboBox.getSelectedIndex()));
		OptionStore.setOption(id+".width", new Integer(getWidth()));
		OptionStore.setOption(id+".height", new Integer(getHeight()));
		super.cancel();
	}
	protected void run() {
		if (currentParameter == null) return;
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
		currentParameter.simulationStrategy = simulationMethodsComboBox.getSelectedIndex();
		OptionStore.setOption("simulation.defaultMethod", new Integer(simulationMethodsComboBox.getSelectedIndex()));
		if (currentParameter.simulationStrategy == SimulationParameters.STRATEGY_STG) {
			sim = new Simulation(paramList.graph, this, currentParameter);
		} else {
			sim = new HTGSimulation(paramList.graph, this, currentParameter);
		}
		
	}

/* *************** LISTENERS **********************/

	/**
	 * Update the <b>currentParameter.maxdepth</b> value when the corresponding widget is changed.
	 */
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
	
	
	/**
	 * Update the <b>currentParameter.maxnodes</b> value when the corresponding widget is changed.
	 */
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

	private ChangeListener getRadioChangeListener() {
		if (radioChangeListener == null) {
			radioChangeListener = new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
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
			};
		}
		return radioChangeListener;
	}

	public void valueChanged(ListSelectionEvent e) {
		int[] t_sel = listPanel.getSelection();
		if (t_sel.length != 1) {
			currentParameter = null;
		} else {
			currentParameter = (SimulationParameters)paramList.getElement(null, t_sel[0]);
			mutantPanel.setStore(currentParameter.store);
			initStatePanel.setParam(currentParameter);
			selectPriorityClass.setStore(currentParameter.store, SimulationParameters.PCLASS);
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
