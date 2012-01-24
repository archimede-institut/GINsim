package org.ginsim.servicegui.export.petrinet;

import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.SimpleServiceGUI;
import org.ginsim.gui.service.common.ServiceStatus;
import org.ginsim.gui.service.common.StandaloneGUI;
import org.ginsim.service.export.petrinet.PetrinetExportService;
import org.mangosdk.spi.ProviderFor;


/**
 * GUI Action to export a LRG into Petri net
 */
@ProviderFor(ServiceGUI.class)
@StandaloneGUI
@ServiceStatus( ServiceStatus.RELEASED)
public class PetriNetExportServiceGUI extends SimpleServiceGUI<PetrinetExportService> {

	public PetriNetExportServiceGUI() {
		super(PetriNetExportAction.class, W_ANALYSIS);
	}
}
