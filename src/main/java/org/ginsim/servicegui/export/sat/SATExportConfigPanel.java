package org.ginsim.servicegui.export.sat;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.colomoto.logicalmodel.LogicalModel;
import org.ginsim.common.application.Txt;
import org.ginsim.gui.graph.regulatorygraph.initialstate.InitialStatePanel;
import org.ginsim.gui.utils.dialog.stackdialog.LogicalModelActionDialog;
import org.ginsim.service.export.sat.SATConfig;

public class SATExportConfigPanel extends LogicalModelActionDialog {
	private static final long serialVersionUID = -7398674287463858306L;

	private final SATConfig config;
	private final SATExportAction action;

	private JPanel mainPanel;
	private InitialStatePanel initPanel;

	public SATExportConfigPanel(SATConfig config, SATExportAction action) {
		super(config.getGraph(), null, Txt.t("STR_SAT"), 600, 400);
		this.setTitle(Txt.t("STR_SATRunningTitle"));
		setUserID(Txt.t("STR_SAT"));
		this.config = config;
		this.action = action;

		// NORTH begin
		mainPanel = new JPanel(new BorderLayout());

		// CENTER begin
		initPanel = new InitialStatePanel(config.getGraph(), false);
		initPanel.setParam(config);
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
