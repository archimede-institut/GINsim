package org.ginsim.servicegui.export.nusmv;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.colomoto.biolqm.LogicalModel;
import org.colomoto.biolqm.NodeInfo;
import org.ginsim.common.application.Txt;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.graph.regulatorygraph.initialstate.InitialStatePanel;
import org.ginsim.gui.utils.dialog.stackdialog.LogicalModelActionDialog;
import org.ginsim.service.export.nusmv.NuSMVConfig;
import org.ginsim.service.tool.reg2dyn.SimulationParameterList;
import org.ginsim.service.tool.reg2dyn.SimulationParametersManager;
import org.ginsim.servicegui.tool.reg2dyn.PrioritySelectionPanel;

public class NuSMVExportConfigPanel extends LogicalModelActionDialog {
	private static final long serialVersionUID = -7398674287463858306L;

	private final NuSMVConfig config;
	private final NuSMVExportAction action;

	private JPanel mainPanel;
	private PrioritySelectionPanel priorityPanel;
	private InitialStatePanel initPanel;
	private JRadioButton jrbCTL;
	private JRadioButton jrbARCTL;
	private boolean hasInputs;

	public NuSMVExportConfigPanel(RegulatoryGraph graph, NuSMVConfig config, NuSMVExportAction action) {
		super(graph, null, Txt.t("STR_NuSMV"), 600, 400);
		this.setTitle(Txt.t("STR_NuSMVRunningTitle"));
		setUserID(Txt.t("STR_NuSMV"));
		this.config = config;
		this.action = action;
		this.hasInputs = false;

		// NORTH begin
		mainPanel = new JPanel(new BorderLayout());
		SimulationParameterList paramList = (SimulationParameterList) ObjectAssociationManager.getInstance()
				.getObject(graph, SimulationParametersManager.KEY, true);
		priorityPanel = new PrioritySelectionPanel(this, paramList.pcmanager);
		priorityPanel.setStore(config);
		mainPanel.add(priorityPanel, BorderLayout.NORTH);

		// CENTER begin
		initPanel = new InitialStatePanel(graph, true);
		initPanel.setParam(config);
		// initPanel.setMessage(Txt.t("STR_NuSMV_Checked"));
		mainPanel.add(initPanel, BorderLayout.CENTER);

		// SOUTH begin
		JPanel bottomPanel = new JPanel(new BorderLayout());

		for (NodeInfo node : this.config.getModel().getComponents()) {
			if (node.isInput()) {
				this.hasInputs = true;
				break;
			}
		}
		
		if (this.hasInputs) {
			JPanel stateInputsPanel = new JPanel(new BorderLayout());
			stateInputsPanel.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder("State inputs"), stateInputsPanel.getBorder()));

			this.jrbCTL   = new JRadioButton("Fix - All inputs are fix and part of the state representation");
			this.jrbARCTL = new JRadioButton("Vary - All inputs freely vary unless restricted by ARCTL operators");

			stateInputsPanel.add(this.jrbCTL, BorderLayout.NORTH);
			stateInputsPanel.add(this.jrbARCTL, BorderLayout.SOUTH);
			ButtonGroup bg = new ButtonGroup();
			bg.add(jrbCTL);
			bg.add(jrbARCTL);
			this.jrbCTL.setSelected(true);
			bottomPanel.add(stateInputsPanel, BorderLayout.NORTH);
		}

		mainPanel.add(bottomPanel, BorderLayout.SOUTH);
		setMainPanel(mainPanel);
	}

	@Override
	public void run(LogicalModel model) {
		if (this.getPerturbation() != null) {
			model = this.getPerturbation().apply(model);
		}
		if (this.getReduction() != null) {
			model = this.getReduction().apply(model);
		}
		config.updateModel(model);
		if (this.hasInputs) {
			config.setFixedInputs(this.jrbCTL.isSelected());
		}
		action.selectFile();
		cancel();
	}
}
