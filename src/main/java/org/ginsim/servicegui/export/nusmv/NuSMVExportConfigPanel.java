package org.ginsim.servicegui.export.nusmv;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import org.colomoto.logicalmodel.LogicalModel;
import org.ginsim.common.application.Translator;
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

	public NuSMVExportConfigPanel(NuSMVConfig config, NuSMVExportAction action) {
		super(config.getGraph(), null, Translator.getString("STR_NuSMV"), 600,
				400);
		this.setTitle(Translator.getString("STR_NuSMVRunningTitle"));
		setUserID(Translator.getString("STR_NuSMV"));
		this.config = config;
		this.action = action;

		mainPanel = new JPanel(new BorderLayout());

		SimulationParameterList paramList = (SimulationParameterList) ObjectAssociationManager
				.getInstance().getObject(config.getGraph(),
						SimulationParametersManager.KEY, true);
		priorityPanel = new PrioritySelectionPanel(this, paramList.pcmanager);
		priorityPanel.setStore(config);
		mainPanel.add(priorityPanel, BorderLayout.NORTH);

		initPanel = new InitialStatePanel(config.getGraph(), true);
		initPanel.setParam(config);
		initPanel.setMessage(Translator.getString("STR_NuSMV_Checked"));
		mainPanel.add(initPanel, BorderLayout.CENTER);
		setMainPanel(mainPanel);
	}

	@Override
	public void run(LogicalModel model) {
		config.updateModel(model);
		action.selectFile();
		cancel();
	}
}
