package org.ginsim.servicegui.format;

import org.ginsim.common.utils.FileFormatDescription;
import org.ginsim.core.service.ServiceManager;
import org.ginsim.gui.service.FormatSupportServiceGUI;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.ServiceStatus;
import org.ginsim.service.format.BoolsimFormatService;
import org.mangosdk.spi.ProviderFor;

/**
 * GUI Action to export model to the boolsim format
 * 
 * @author Aurelien Naldi
 */
@ProviderFor(ServiceGUI.class)
@GUIFor(BoolsimFormatService.class)
@ServiceStatus( ServiceStatus.RELEASED)
public class BoolsimFormatServiceGUI extends FormatSupportServiceGUI<BoolsimFormatService> {

	private static BoolsimFormatService SERVICE = ServiceManager.getManager().getService(BoolsimFormatService.class);
	private static FileFormatDescription FORMAT = new FileFormatDescription("Boolsim", "net");
	
	public BoolsimFormatServiceGUI() {
		super("Boolsim", SERVICE, FORMAT);
	}
	
}
