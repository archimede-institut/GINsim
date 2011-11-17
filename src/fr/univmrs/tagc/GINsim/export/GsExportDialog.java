package fr.univmrs.tagc.GINsim.export;

import java.awt.Frame;

import org.ginsim.gui.service.common.GsExportAction;

import fr.univmrs.tagc.common.widgets.StackDialog;

public class GsExportDialog extends StackDialog {
	private static final long serialVersionUID = -6796117147061825176L;

	private final GsExportAction export;
	
	public GsExportDialog( GsExportAction export) {
		// TODO: move to the new service GUI and set the right parent frame
		super((Frame)null, "exportDialog_"+export.getID(), 400, 300);
		this.export = export;
		setMainPanel(export.getConfigPanel());
	}

	protected void run() {
		export.selectFile();
		dispose();
	}
}
