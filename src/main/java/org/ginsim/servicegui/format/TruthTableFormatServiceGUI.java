package org.ginsim.servicegui.format;

import org.ginsim.common.utils.FileFormatDescription;
import org.ginsim.core.service.ServiceManager;
import org.ginsim.gui.service.FormatSupportServiceGUI;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.GUIFor;
import org.ginsim.gui.service.ServiceStatus;
import org.ginsim.service.format.TruthTableFormatService;
import org.mangosdk.spi.ProviderFor;

/**
 * GUI Action to import create a model specified in the TruthTable format
 * 
 * @author Pedro T. Monteiro
 */
@ProviderFor(ServiceGUI.class)
@GUIFor(TruthTableFormatService.class)
@ServiceStatus(ServiceStatus.RELEASED)
public class TruthTableFormatServiceGUI extends
		FormatSupportServiceGUI<TruthTableFormatService> {

	private static TruthTableFormatService SERVICE = ServiceManager
			.getManager().getService(TruthTableFormatService.class);
	private static FileFormatDescription FORMAT = new FileFormatDescription(
			"TruthTable", "tt");

	public TruthTableFormatServiceGUI() {
		super("Truth table", SERVICE, FORMAT);
	}

}
