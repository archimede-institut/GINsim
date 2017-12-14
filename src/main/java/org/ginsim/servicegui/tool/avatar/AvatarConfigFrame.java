package org.ginsim.servicegui.tool.avatar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.ToolTipManager;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.colomoto.biolqm.LogicalModel;
import org.colomoto.biolqm.NodeInfo;
import org.colomoto.biolqm.StatefulLogicalModel;
import org.colomoto.biolqm.StatefulLogicalModelImpl;
import org.ginsim.common.application.Txt;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedState;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedStateList;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedStatesHandler;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedStatesManager;
import org.ginsim.gui.graph.regulatorygraph.initialstate.CompleteStatePanel;
import org.ginsim.service.tool.avatar.params.AvatarParameterList;
import org.ginsim.service.tool.avatar.params.AvatarParameters;
import org.ginsim.service.tool.avatar.params.AvatarParametersManager;
import org.ginsim.service.tool.avatar.params.AvatarStateStore;
import org.ginsim.service.tool.avatar.service.EnumAlgorithm;
import org.ginsim.service.tool.avatar.simulation.Simulation;
import org.ginsim.servicegui.tool.avatar.algopanels.AvatarPanel;
import org.ginsim.servicegui.tool.avatar.algopanels.FirefrontPanel;
import org.ginsim.servicegui.tool.avatar.algopanels.MonteCarloPanel;
import org.ginsim.servicegui.tool.avatar.algopanels.SimulationPanel;
import org.ginsim.servicegui.tool.avatar.others.TitleToolTipPanel;
import org.ginsim.servicegui.tool.avatar.parameters.AvaParameterEditionPanel;
import org.ginsim.servicegui.tool.avatar.parameters.AvatarParametersHelper;

/**
 * Main panel for displaying the all of the context associated with avatar
 * simulations
 * 
 * @author Pedro T. Monteiro
 * @author Rui Henriques
 * @version 1.0
 */
public class AvatarConfigFrame extends AvatarLogicalModelActionDialog {

	private static final int W = 1020, H = 600;
	private static final String ID = "avatar_gui";
	private static final long serialVersionUID = 1L;
	private Color purple = new Color(204, 153, 255), blue = new Color(130, 180, 246), marine = new Color(204, 204, 255);

	/****************/
	/** PARAMETERS **/
	/****************/

	/** pointer to the panel with the parameters for an avatar simulation */
	public SimulationPanel panelAvatar;
	/** pointer to the panel with the parameters for a firefront simulation */
	public SimulationPanel panelFF;
	/** pointer to the panel with the parameters for a Monte Carlo simulation */
	public SimulationPanel panelMC;
	/** pointer to the panel with the initial states and oracles */
	public CompleteStatePanel states;
	/** selected simulation: Avatar, Firefront or Monte Carlo */
	public JComboBox<EnumAlgorithm> jcbAlgorithm = new JComboBox<EnumAlgorithm>(new DefaultComboBoxModel<EnumAlgorithm>(
			new EnumAlgorithm[] { EnumAlgorithm.AVATAR, EnumAlgorithm.FIREFRONT, EnumAlgorithm.MONTE_CARLO }));
	/** whether charts should be created and plotted */
	public JCheckBox plots = new JCheckBox("Plot statistics");
	/**
	 * whether detailed logs should be printed (not advisable for complex models)
	 */
	public JCheckBox quiet = new JCheckBox("Quiet mode");
	/** named store with the initial states and oracles */
	public AvatarStateStore statestore;

	private JPanel progressBar;
	private JSplitPane horizontalPanel;
	private boolean first = true;
	public JTextPane progress = new JTextPane();
	private JButton forceStop = new JButton("Force exit");
	private AvatarResults results;
	private File memorizedFile = new File("chart.png"), logFile = new File("log.txt"),
			resFile = new File("result.html"), csvFile = new File("result.csv");
	private AvaParameterEditionPanel editionPanel;

	private String open = "<html><div style=\"width:265px;\">", end = "</div></html>";
	private String statesVar = open + "Specification of (sets of) initial states (* means all possible values)" + end;
	private String algoVar = open + "Select the algorithm:"
			+ "<br>1) AVATAR, an adapted Monte Carlo simulation, for attractor identification and approximation of reachability probabilities"
			+ "<br>2) FIREFRONT for a quasi-exact reachability probabilities of stable states and small complex attractors"
			+ "<br>3) MONTECARLO for an approximation of reachability probabilities of stable states" + end;

	/**
	 * Creates the panel with avatar-based simulations from the current graph
	 * 
	 * @param graph
	 *            regulatory graph (with possibly contextual information) on which
	 *            to apply the services
	 * @param _parent
	 *            pointer to the parent panel (to return upon closing the panel for
	 *            the analysis of attractors)
	 */
	public AvatarConfigFrame(RegulatoryGraph graph, JFrame _parent) {
		super(graph, _parent, ID, W, H);
		this.setTitle(Txt.t("STR_avatar"));
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		ToolTipManager.sharedInstance().setInitialDelay(0);
		ToolTipManager.sharedInstance().setDismissDelay(40000);

		/** A: initial states panel **/
		List<byte[]> istates = lrg.isStateful() ? lrg.getStates() : new ArrayList<byte[]>();
		// for(byte[] istate : istates)
		// System.out.println("IState="+AvatarUtils.toString(istate));

		NamedStatesHandler nstatesHandler = (NamedStatesHandler) ObjectAssociationManager.getInstance().getObject(lrg,
				NamedStatesManager.KEY, true);
		if (nstatesHandler.getInitialStates().size() == 0)
			statestore = new AvatarStateStore(istates, lrg);
		else
			statestore = new AvatarStateStore(lrg);

		statestore.addStateList(nstatesHandler.getInitialStates(), nstatesHandler.getInputConfigs(),
				new NamedStateList(graph.getNodeOrder(), false));
		states = new CompleteStatePanel(statestore.nstates, statestore.instates, statestore.oracles, true);
		states.setParam(statestore);

		int i = 0;
		List<List<byte[]>> ioracles = lrg.hasOracles() ? lrg.getOracles() : new ArrayList<List<byte[]>>();
		Map<String, List<byte[]>> oracles = new HashMap<String, List<byte[]>>();
		for (List<byte[]> o : ioracles) {
			// System.out.println("IOracle=" + AvatarUtils.toString(o));
			oracles.put("Att_" + (i++), o);
		}
		statestore.addOracle(oracles);
		states.updateParam(statestore);

		Icon img = new ImageIcon(getClass().getResource("/greyQuestionMark.png"));
		panelAvatar = new AvatarPanel(img);
		panelFF = new FirefrontPanel(img);
		panelFF.setBorder(new TitledBorder(new LineBorder(purple, 2), EnumAlgorithm.FIREFRONT + " Parameters",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panelMC = new MonteCarloPanel(img);
		panelMC.setBorder(new TitledBorder(new LineBorder(purple, 2), EnumAlgorithm.MONTE_CARLO + " Parameters",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		plots.setSelected(true);
		quiet.setSelected(true);
		quiet.setToolTipText(
				"Check this box to enable logs production (may lead to significant computational time overhead)");
		jcbAlgorithm.setSelectedIndex(0);

		AvatarParameterList paramList = (AvatarParameterList) ObjectAssociationManager.getInstance().getObject(lrg,
				AvatarParametersManager.KEY, false);
		AvatarParameters param;
		if (paramList == null || paramList.size() == 0) {
			param = AvatarParametersHelper.load(this);
			paramList = new AvatarParameterList(graph, param);
		} else {
			param = paramList.get(0);
			statestore.addOracle(param.statestore.oracles);
			param.statestore = statestore;
		}
		editionPanel = new AvaParameterEditionPanel(this, lrg, paramList);

		refresh(param);

		/** H: CLOSING **/
		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				ObjectAssociationManager.getInstance().addObject(lrg, AvatarParametersManager.KEY,
						editionPanel.paramList);
				copyStates();
				dispose();
				// parent.dispose();
			}
		});
	}

	private void copyStates() {
		NamedStatesHandler imanager = (NamedStatesHandler) ObjectAssociationManager.getInstance().getObject(lrg,
				NamedStatesManager.KEY, true);
		NamedStateList nstates = new NamedStateList(statestore.nodes, false);// imanager.getInitialStates();
		NamedStateList instates = new NamedStateList(statestore.inodes, true);// imanager.getInputConfigs();
		for (AvatarParameters p : editionPanel.paramList) {
			AvatarStateStore store = p.statestore;
			for (NamedState s : store.nstates)
				if (nstates.getByName(s.getName()) == null && s.getMap().size() > 0)
					nstates.add(s);
			for (NamedState s : store.instates)
				if (instates.getByName(s.getName()) == null && s.getMap().size() > 0)
					instates.add(s);
		}
		imanager.setNormalStates(nstates);
		imanager.setInputStates(instates);
		ObjectAssociationManager.getInstance().addObject(lrg, NamedStatesManager.KEY, imanager);
	}

	/**
	 * Updates the parameters of the simulation panel given the new context
	 * 
	 * @param param
	 *            the simulation context (parameters) to use to update the fields of
	 *            the main panel
	 */
	public void refresh(AvatarParameters param) {
		mainPanel.removeAll();

		/** LOAD PARAMS **/
		AvatarParametersHelper.unload(param, this);
		JPanel rightPanel = new JPanel(new GridBagLayout());
		JPanel topPanel = getTopPanel();
		rightPanel.removeAll();
		rightPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.weightx = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 2;
		gbc.fill = GridBagConstraints.BOTH;
		rightPanel.add(topPanel, gbc);

		Icon img = new ImageIcon(getClass().getResource("/greyQuestionMark.png"));
		int width = 200, widthstates = 430, startX = 5, startY = 10, heightStates = 300, shiftX = 5;

		/*
		 * if(statestore.getInputState().size()==0){ this.setResizable(true);
		 * //this.setSize(getWidth(),getHeight()-70); heightStates=heightStates-70; }
		 */
		JPanel statesPanel = new TitleToolTipPanel();
		statesPanel.setToolTipText(statesVar);
		statesPanel.setBorder(BorderFactory.createTitledBorder("States and oracles"));
		gbc.gridx = 2;
		gbc.gridy = 2;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.gridwidth = 1;
		gbc.gridheight = 2;
		gbc.fill = GridBagConstraints.BOTH;
		statesPanel.setLayout(new GridLayout(1, 1));
		// states.setPreferredSize(new Dimension(500,200));
		// states.scrollRectToVisible(states.getBounds());
		states.setMinimumSize(new Dimension(widthstates - 10, heightStates - 15));
		// System.out.println("Size:"+param.states.getAllStateList().size()+"<->"+param.states.getAllIStateList().size());
		statesPanel.add(states);
		rightPanel.add(statesPanel, gbc);
		gbc.gridheight = 1;

		/** A: select algorithm **/
		jcbAlgorithm.setSelectedIndex(param.algorithm);
		jcbAlgorithm.setVisible(true);
		quiet.setSelected(param.quiet);
		plots.setSelected(param.plots);
		JPanel panelOutput = new TitleToolTipPanel();
		panelOutput.setLayout(new GridLayout(1, 1));
		panelOutput.setBorder(BorderFactory.createTitledBorder("Output"));
		panelOutput.add(quiet);

		JPanel panelAlgo = new TitleToolTipPanel();
		panelAlgo.setLayout(new GridLayout(2, 1));
		panelAlgo.setBorder(BorderFactory.createTitledBorder("Simulation"));

		JPanel panelSelAlgo = new TitleToolTipPanel();
		panelSelAlgo.setMinimumSize(new Dimension(0, 50));
		panelSelAlgo.setToolTipText(algoVar);
		panelSelAlgo.setBorder(BorderFactory.createTitledBorder("Algorithm"));
		panelSelAlgo.setLayout(new GridLayout(1, 1));
		panelSelAlgo.add(jcbAlgorithm);

		panelAlgo.add(panelSelAlgo);
		panelAlgo.add(panelOutput);
		// algorithm.setPreferredSize(new Dimension(widthstates-10, heightStates-22));
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.BOTH;
		rightPanel.add(panelAlgo, gbc);

		jcbAlgorithm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				EnumAlgorithm algo = (EnumAlgorithm) jcbAlgorithm.getSelectedItem();
				panelAvatar.setVisible(false);
				panelFF.setVisible(false);
				panelMC.setVisible(false);
				if (algo == EnumAlgorithm.FIREFRONT) {
					panelFF.setVisible(true);
				} else if (algo == EnumAlgorithm.MONTE_CARLO) {
					panelMC.setVisible(true);
				} else {
					panelAvatar.setVisible(true);
				}
			}
		});

		/** B: Output **/
		/*
		 * panelOutput.add(plots); rightPanel.add(panelOutput);
		 */

		/** D: Side Panels **/
		// gbc = new GridBagConstraints();
		gbc.gridy = 2;
		gbc.gridx = 1;
		gbc.weightx = 0;
		gbc.weighty = 1;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		rightPanel.add(panelAvatar, gbc);
		rightPanel.add(panelFF, gbc);
		rightPanel.add(panelMC, gbc);
		gbc.gridy = 3;
		gbc.fill = GridBagConstraints.NORTH;
		/*
		 * JButton dynamicUpdate = new JButton("Data-driven Params");
		 * dynamicUpdate.setMinimumSize(new Dimension(160,20));
		 * rightPanel.add(dynamicUpdate,gbc); dynamicUpdate.addActionListener(new
		 * ParamActionListener(param,this,lrg));
		 */

		panelAlgo.setVisible(true);
		panelOutput.setVisible(true);
		panelAvatar.setVisible(false);
		panelFF.setVisible(false);
		panelMC.setVisible(false);
		if (jcbAlgorithm.getSelectedIndex() == 0)
			panelAvatar.setVisible(true);
		else if (jcbAlgorithm.getSelectedIndex() == 1)
			panelFF.setVisible(true);
		else
			panelMC.setVisible(true);

		/** PARAM PANEL **/
		JSplitPane paramPanel = editionPanel.getEditionPanel();
		paramPanel.setRightComponent(rightPanel);
		// paramPanel.setContinuousLayout(true);
		paramPanel.setEnabled(true);
		paramPanel.setIgnoreRepaint(false);
		paramPanel.setOneTouchExpandable(true);
		paramPanel.setDividerLocation(0.3);
		// paramPanel.setMinimumSize(new Dimension(300,100));
		paramPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		paramPanel.setBorder(null);

		/** G: Progress bar **/
		progress.setContentType("text/html");
		progress.setOpaque(false);
		progress.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
		progress.setBackground(new Color(0, 0, 0, 0));
		progress.setFont(new Font(Font.MONOSPACED, 3, 5));
		JScrollPane jsp = new JScrollPane(progress);
		// jsp.setsetMargin(new Insets(20,20,20,20));
		jsp.setBorder(null);
		progressBar = new JPanel();
		progressBar.setBorder(new TitledBorder(new LineBorder(marine, 4), "Running progress... ", TitledBorder.LEADING,
				TitledBorder.TOP, null, new Color(0, 0, 0)));
		progressBar.setLayout(new GridBagLayout());
		gbc.gridy = 3;
		gbc.gridx = 1;
		gbc.weightx = 1;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.BOTH;
		progressBar.add(jsp, gbc);
		gbc.gridy = 3;
		gbc.gridx = 3;
		gbc.weightx = 0;
		gbc.gridwidth = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		progressBar.add(forceStop, gbc);

		forceStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (results != null)
					results.kill(true);
			}
		});
		horizontalPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, paramPanel, progressBar);
		horizontalPanel.setEnabled(true);
		horizontalPanel.setOneTouchExpandable(true);
		paramPanel.setMinimumSize(new Dimension(400, 400));
		progressBar.setMaximumSize(new Dimension(400, 50));
		if (first) {
			horizontalPanel.getRightComponent().setVisible(false);
			first = false;
		} else
			horizontalPanel.setDividerLocation(0.7);
		// paramPanel.setRightComponent(rightPanel);
		// paramPanel.setContinuousLayout(true);

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		mainPanel.add(horizontalPanel, gbc);

		mainPanel.repaint();
		mainPanel.validate();
	}

	@Override
	public void run(LogicalModel _model) {

		brun.setEnabled(false);
		forceStop.setEnabled(true);
		progress.setText("Initializing simulation");
		progressBar.setVisible(true);
		horizontalPanel.getRightComponent().setVisible(true);
		horizontalPanel.setDividerLocation(0.8);

		/** ARGUMENTS **/
		StatefulLogicalModel model = null;
		try {

			/** A: extract selected states */
			List<byte[]> initialStates = new ArrayList<byte[]>();
			int nstates = _model.getComponents().size();
			if (states.getStateList().size() == 0) {
				for (NamedState state2 : states.getIStateList()) {
					byte[] state = new byte[nstates];
					for (int i = 0; i < nstates; i++) {
						NodeInfo node = _model.getComponents().get(i);
						List<Integer> values = node.isInput() ? state2.getMap().get(node) : null;
						if (values == null || values.size() > 1)
							state[i] = -1;
						else
							state[i] = (byte) ((int) values.get(0));
					}
					// System.out.println("Final istate="+AvatarUtils.toString(state));
					initialStates.add(state);
				}
			} else {
				for (NamedState state1 : states.getStateList()) {
					if (states.getIStateList().size() == 0) {
						byte[] state = new byte[nstates];
						for (int i = 0; i < nstates; i++) {
							NodeInfo node = _model.getComponents().get(i);
							List<Integer> values = state1.getMap().get(node);
							if (values == null || values.size() > 1)
								state[i] = -1;
							else
								state[i] = (byte) ((int) values.get(0));
						}
						// System.out.println("Final istate="+AvatarUtils.toString(state));
						initialStates.add(state);
					}
					for (NamedState state2 : states.getIStateList()) {
						byte[] state = new byte[nstates];
						for (int i = 0; i < nstates; i++) {
							NodeInfo node = _model.getComponents().get(i);
							List<Integer> values = node.isInput() ? state2.getMap().get(node)
									: state1.getMap().get(node);
							if (values == null || values.size() > 1)
								state[i] = -1;
							else
								state[i] = (byte) ((int) values.get(0));
						}
						// System.out.println("Final istate="+AvatarUtils.toString(state));
						initialStates.add(state);
					}
				}
			}
			if (initialStates.size() == 0) {
				byte[] state = new byte[nstates];
				for (int i = 0; i < nstates; i++)
					state[i] = -1;
				initialStates.add(state);
			}
			model = new StatefulLogicalModelImpl(_model, initialStates);

			/** B: extract selected oracles */
			List<List<byte[]>> oracles = new ArrayList<List<byte[]>>();
			String name = "";
			for (NamedState ostate : states.getOracles()) {
				if (!ostate.getName().equals(name)) {
					oracles.add(new ArrayList<byte[]>());
					name = ostate.getName();
				}
				byte[] state = new byte[nstates];
				for (int i = 0; i < nstates; i++) {
					NodeInfo node = _model.getComponents().get(i);
					List<Integer> values = ostate.getMap().get(node);
					if (values == null || values.size() > 1)
						state[i] = -1;
					else
						state[i] = (byte) ((int) values.get(0));
				}
				oracles.get(oracles.size() - 1).add(state);
			}
			// for(List<byte[]> o : oracles) System.out.println("Oracle
			// entry:"+AvatarUtils.toString(o));
			((StatefulLogicalModelImpl) model).setOracles(oracles);

		} catch (Exception e) {
			progress.setEnabled(false);
			brun.setEnabled(true);
			String fileErrorMessage = "Unfortunately we were not able to finish your request.<br><em>Reason:</em> Exception while reading the input states and parsing the model.";
			AvatarResults.errorDisplay(fileErrorMessage, e);
			e.printStackTrace();
			return;
		}
		Simulation sim = null;
		try {
			/** B: extract algo-specific parameters and run */
			EnumAlgorithm algo = (EnumAlgorithm) jcbAlgorithm.getSelectedItem();
			if (algo == EnumAlgorithm.FIREFRONT)
				sim = ((FirefrontPanel) panelFF).getSimulation(model, plots.isSelected(), quiet.isSelected());
			else if (algo == EnumAlgorithm.AVATAR)
				sim = ((AvatarPanel) panelAvatar).getSimulation(model, plots.isSelected(), quiet.isSelected());
			else if (algo == EnumAlgorithm.MONTE_CARLO)
				sim = ((MonteCarloPanel) panelMC).getSimulation(model, false, quiet.isSelected());
		} catch (Exception e) {
			progress.setEnabled(false);
			brun.setEnabled(true);
			String fileErrorMessage = "Unfortunately we were not able to finish your request.<br><em>Reason:</em> Exception while parameterizing the algorithm!";
			AvatarResults.errorDisplay(fileErrorMessage, e);
			e.printStackTrace();
			return;
		}
		results = new AvatarResults(sim, progress, this, quiet.isSelected(), model, memorizedFile, logFile, resFile,
				csvFile, brun, forceStop);
		results.runAvatarResults();
	}

	@Override
	public void doClose() {
		if (results != null)
			results.kill(false);
		// System.out.println(AvatarUtils.toString(editionPanel.paramList.get(0).statesSelected));
		editionPanel.selectionUpdated(new int[] {});
		// System.out.println(AvatarUtils.toString(editionPanel.paramList.get(0).statesSelected));
		ObjectAssociationManager.getInstance().addObject(lrg, AvatarParametersManager.KEY, editionPanel.paramList);
		copyStates();
		dispose();
		// parent.dispose();
	}

	public void setCurrent(AvatarParameters p) {
		editionPanel.setCurrent(p);
	}
}

/*
 * class ParamActionListener implements ActionListener { AvatarParameters param;
 * AvatarConfigFrame frame; RegulatoryGraph graph; public
 * ParamActionListener(AvatarParameters p, AvatarConfigFrame f, RegulatoryGraph
 * lrg){ param=p; frame=f; graph=lrg; }
 * 
 * @Override public void actionPerformed(ActionEvent arg0) { param =
 * AvatarParamDynamicUpdate.complete(param,graph); frame.refresh(param); } }
 */
