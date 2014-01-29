package org.ginsim.servicegui.format.ginml;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.colomoto.logicalmodel.LogicalModel;
import org.ginsim.common.application.Txt;
import org.ginsim.gui.shell.actions.ExportAction;
import org.ginsim.gui.utils.dialog.stackdialog.LogicalModelActionDialog;
import org.ginsim.service.format.ginml.GINMLFormatConfig;

public class GINMLFormatConfigPanel extends LogicalModelActionDialog {
	private static final long serialVersionUID = 7758753788324234716L;

	private final GINMLFormatConfig config;
	private final ExportAction action;

	private JPanel mainPanel;

	public GINMLFormatConfigPanel(GINMLFormatConfig config, ExportAction action) {
		super(config.getGraph(), null, Txt.t("STR_GINML_Title"),
				600, 300);
		this.setTitle(Txt.t("STR_GINML_RunningTitle"));
		setUserID(Txt.t("STR_GINML_Title"));
		this.config = config;
		this.action = action;

		mainPanel = new JPanel(new BorderLayout());
		setMainPanel(mainPanel);
	}

	@Override
	public void run(LogicalModel model) {
		config.updateModel(model);
		action.selectFile();
		cancel();
	}
}
