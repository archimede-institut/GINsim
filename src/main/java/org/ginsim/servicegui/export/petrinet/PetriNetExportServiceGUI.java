package org.ginsim.servicegui.export.petrinet;

import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.SimpleServiceGUI;
import org.ginsim.gui.service.common.ServiceStatus;
import org.ginsim.gui.service.common.StandaloneGUI;
import org.ginsim.service.format.PetriNetFormatService;
import org.mangosdk.spi.ProviderFor;

/**
 * GUI Action to export a LRG into Petri net
 * 
 * @author Aurelien Naldi
 */
@ProviderFor(ServiceGUI.class)
@StandaloneGUI
@ServiceStatus( ServiceStatus.RELEASED)
public class PetriNetExportServiceGUI extends SimpleServiceGUI<PetriNetFormatService> {

	public PetriNetExportServiceGUI() {
		super(PetriNetExportAction.class, W_EXPORT_SPECIFIC+50);
	}
}
