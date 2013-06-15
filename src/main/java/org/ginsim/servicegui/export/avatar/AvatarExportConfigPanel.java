package org.ginsim.servicegui.export.avatar;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.colomoto.logicalmodel.LogicalModel;
import org.ginsim.common.application.Translator;
import org.ginsim.gui.graph.regulatorygraph.initialstate.InitialStatePanel;
import org.ginsim.gui.utils.dialog.stackdialog.LogicalModelActionDialog;
import org.ginsim.service.export.avatar.AvatarConfig;

public class AvatarExportConfigPanel extends LogicalModelActionDialog {

	private static final long serialVersionUID = -8297993789802561249L;
	private final AvatarConfig config;
	private final AvatarExportAction action;

	private JPanel mainPanel;
	private InitialStatePanel initPanel;

	public AvatarExportConfigPanel(AvatarConfig config, AvatarExportAction action) {
		super(config.getGraph(), null, Translator.getString("STR_AVATAR"), 600,
				400);
		this.setTitle(Translator.getString("STR_AVATARRunningTitle"));
		setUserID(Translator.getString("STR_AVATAR"));
		this.config = config;
		this.action = action;

		mainPanel = new JPanel(new BorderLayout());

		initPanel = new InitialStatePanel(config.getGraph(), true);
		initPanel.setParam(config);
//		initPanel.setMessage(Translator.getString("STR_AVATAR_Checked"));
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
