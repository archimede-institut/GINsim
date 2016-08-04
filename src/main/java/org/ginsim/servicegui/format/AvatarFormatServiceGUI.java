package org.ginsim.servicegui.format;

import org.ginsim.common.utils.FileFormatDescription;
import org.ginsim.core.service.EStatus;
import org.ginsim.core.service.ServiceManager;
import org.ginsim.gui.service.FormatSupportServiceGUI;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.GUIFor;
import org.ginsim.core.service.ServiceStatus;
import org.ginsim.service.format.AvatarFormatService;
import org.ginsim.service.format.BoolsimFormatService;
import org.mangosdk.spi.ProviderFor;

/**
 * GUI Action to export model to the boolsim format
 * 
 * @author Aurelien Naldi
 */
@ProviderFor(ServiceGUI.class)
@GUIFor(AvatarFormatService.class)
@ServiceStatus( EStatus.RELEASED)
public class AvatarFormatServiceGUI extends FormatSupportServiceGUI<AvatarFormatService> {

	private static AvatarFormatService SERVICE = ServiceManager.getManager().getService(AvatarFormatService.class);
	private static FileFormatDescription FORMAT = new FileFormatDescription("Avatar", "avatar");
	
	public AvatarFormatServiceGUI() {
		super("Avatar", SERVICE, FORMAT);
	}
	
}
