package org.ginsim.servicegui.export.nusmv;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.NodeInfo;
import org.ginsim.common.application.Txt;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
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
	private List<JCheckBox> listCTLFixedInputs;

	public NuSMVExportConfigPanel(NuSMVConfig config, NuSMVExportAction action) {
		super(config.getGraph(), null, Txt.t("STR_NuSMV"), 600, 400);
		this.setTitle(Txt.t("STR_NuSMVRunningTitle"));
		setUserID(Txt.t("STR_NuSMV"));
		this.config = config;
		this.action = action;

		// NORTH begin
		mainPanel = new JPanel(new BorderLayout());
		SimulationParameterList paramList = (SimulationParameterList) ObjectAssociationManager
				.getInstance().getObject(config.getGraph(),
						SimulationParametersManager.KEY, true);
		priorityPanel = new PrioritySelectionPanel(this, paramList.pcmanager);
		priorityPanel.setStore(config);
		mainPanel.add(priorityPanel, BorderLayout.NORTH);

		// CENTER begin
		initPanel = new InitialStatePanel(config.getGraph(), true);
		initPanel.setParam(config);
		initPanel.setMessage(Txt.t("STR_NuSMV_Checked"));
		mainPanel.add(initPanel, BorderLayout.CENTER);

		// SOUTH begin
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.PAGE_AXIS));

		JTextArea jtaCTL = new JTextArea();
		jtaCTL.setBackground(mainPanel.getBackground());
		jtaCTL.setLineWrap(true);
		jtaCTL.setEditable(false);
		jtaCTL.setFont(new Font("Default", Font.BOLD, 12));
		jtaCTL.setText("Input components selected below will be: fixed during the verification; "
				+ "part of the state description; have an initial state.\n"
				+ "Non selected input components can only be restricted by ARCTL operators, "
				+ "otherwise they will freely vary.");
		bottomPanel.add(jtaCTL);

		JPanel inputsPanel = new JPanel(new FlowLayout());
		JLabel label = new JLabel("Inputs: ");
		inputsPanel.add(label);
		this.listCTLFixedInputs = new ArrayList<JCheckBox>();
		for (NodeInfo node : this.config.getModel().getNodeOrder()) {
			if (node.isInput()) {
				JCheckBox jcb = new JCheckBox(node.getNodeID());
				inputsPanel.add(jcb);
				this.listCTLFixedInputs.add(jcb);
			}
		}
		bottomPanel.add(inputsPanel);

		mainPanel.add(bottomPanel, BorderLayout.SOUTH);
		setMainPanel(mainPanel);
	}

	@Override
	public void run(LogicalModel model) {
		config.updateModel(model);
		for (JCheckBox jcb : this.listCTLFixedInputs) {
			if (jcb.isSelected()) {
				config.addFixedInput(jcb.getText());
			}
		}
		action.selectFile();
		cancel();
	}
}
