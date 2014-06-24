package org.ginsim.servicegui.export.prism;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.colomoto.logicalmodel.LogicalModel;
import org.ginsim.common.application.Txt;
import org.ginsim.gui.graph.regulatorygraph.initialstate.InitialStatePanel;
import org.ginsim.gui.utils.dialog.stackdialog.LogicalModelActionDialog;
import org.ginsim.service.export.prism.PRISMConfig;

public class PRISMExportConfigPanel extends LogicalModelActionDialog {
	private static final long serialVersionUID = -7398674287463858306L;

	private final PRISMConfig config;
	private final PRISMExportAction action;

	private JPanel mainPanel;
	private InitialStatePanel initPanel;

	public PRISMExportConfigPanel(PRISMConfig config, PRISMExportAction action) {
		super(config.getGraph(), null, Txt.t("STR_PRISM"), 600, 400);
		this.setTitle(Txt.t("STR_PRISMRunningTitle"));
		setUserID(Txt.t("STR_PRISM"));
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
