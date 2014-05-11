package org.ginsim.servicegui.export.nusmv;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
	private JCheckBox mcCheckBox;
	private boolean hasInputs;

	public NuSMVExportConfigPanel(NuSMVConfig config, NuSMVExportAction action) {
		super(config.getGraph(), null, Txt.t("STR_NuSMV"), 600, 400);
		this.setTitle(Txt.t("STR_NuSMVRunningTitle"));
		setUserID(Txt.t("STR_NuSMV"));
		this.config = config;
		this.action = action;
		this.hasInputs = false;

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
		// initPanel.setMessage(Txt.t("STR_NuSMV_Checked"));
		mainPanel.add(initPanel, BorderLayout.CENTER);

		// SOUTH begin
		JPanel bottomPanel = new JPanel(new BorderLayout());

		for (NodeInfo node : this.config.getModel().getNodeOrder()) {
			if (node.isInput()) {
				this.hasInputs = true;
				break;
			}
		}

		if (this.hasInputs) {
			JPanel stateInputsPanel = new JPanel(new BorderLayout());
			stateInputsPanel.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createTitledBorder("State inputs"),
					stateInputsPanel.getBorder()));

			JTextArea jtaCTL = new JTextArea();
			jtaCTL.setBackground(mainPanel.getBackground());
			jtaCTL.setLineWrap(true);
			jtaCTL.setEditable(false);
			jtaCTL.setFont(new Font("Default", Font.BOLD, 12));
			jtaCTL.setText("Selected inputs will be part of the state, fixed with a user defined initial state.\n"
					+ "Non selected input components will freely vary, unless restricted by ARCTL operators.");
			stateInputsPanel.add(jtaCTL, BorderLayout.NORTH);

			JPanel fixInputsPanel = new JPanel();
			fixInputsPanel.setLayout(new BoxLayout(fixInputsPanel,
					BoxLayout.LINE_AXIS));
			this.listCTLFixedInputs = new ArrayList<JCheckBox>();
			for (NodeInfo node : this.config.getModel().getNodeOrder()) {
				if (node.isInput()) {
					JCheckBox jcb = new JCheckBox(node.getNodeID());
					fixInputsPanel.add(jcb);
					this.listCTLFixedInputs.add(jcb);
				}
			}
			JScrollPane fixInputsScroll = new JScrollPane(fixInputsPanel,
					JScrollPane.VERTICAL_SCROLLBAR_NEVER,
					JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			stateInputsPanel.add(fixInputsScroll, BorderLayout.SOUTH);
			fixInputsScroll.setBorder(BorderFactory.createEmptyBorder());

			bottomPanel.add(stateInputsPanel, BorderLayout.NORTH);
		}

		JPanel ssPanel = new JPanel(new BorderLayout());
		ssPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Stable states"),
				ssPanel.getBorder()));
		mcCheckBox = new JCheckBox(
				"Compute the stable states and include them in the export (can take some time!)");
		ssPanel.add(mcCheckBox, BorderLayout.LINE_START);
		bottomPanel.add(ssPanel, BorderLayout.SOUTH);

		mainPanel.add(bottomPanel, BorderLayout.SOUTH);
		setMainPanel(mainPanel);
	}

	@Override
	public void run(LogicalModel model) {
		config.updateModel(model);
		if (this.hasInputs) {
			for (JCheckBox jcb : this.listCTLFixedInputs) {
				if (jcb.isSelected()) {
					config.addFixedInput(jcb.getText());
				}
			}
		}
		config.setExportStableStates(this.mcCheckBox.isSelected());
		action.selectFile();
		cancel();
	}
}
