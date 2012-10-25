package org.ginsim.servicegui.format;

import org.ginsim.common.utils.FileFormatDescription;
import org.ginsim.core.service.ServiceManager;
import org.ginsim.gui.service.FormatSupportServiceGUI;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.ServiceStatus;
import org.ginsim.service.format.GNAFormatService;
import org.mangosdk.spi.ProviderFor;

/**
 * GUI Action to export model to the GNA (non-xml) format
 * 
 * @author Pedro T. Monteiro
 */
@ProviderFor(ServiceGUI.class)
@GUIFor(GNAFormatService.class)
@ServiceStatus(ServiceStatus.RELEASED)
public class GNAFormatServiceGUI extends
		FormatSupportServiceGUI<GNAFormatService> {

	private static GNAFormatService SERVICE = ServiceManager.getManager()
			.getService(GNAFormatService.class);
	private static FileFormatDescription FORMAT = new FileFormatDescription(
			"GNA", "gna");

	public GNAFormatServiceGUI() {
		super("GNA (non-xml)", SERVICE, FORMAT);
	}

}
