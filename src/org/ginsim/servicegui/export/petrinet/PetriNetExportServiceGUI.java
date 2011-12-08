package org.ginsim.servicegui.export.petrinet;

import org.ginsim.service.export.petrinet.PetrinetExportService;
import org.ginsim.servicegui.ServiceGUI;
import org.ginsim.servicegui.SimpleServiceGUI;
import org.ginsim.servicegui.common.StandaloneGUI;
import org.mangosdk.spi.ProviderFor;


/**
 * GUI Action to export a LRG into Petri net
 */
@ProviderFor(ServiceGUI.class)
@StandaloneGUI
public class PetriNetExportServiceGUI extends SimpleServiceGUI<PetrinetExportService> {

	public PetriNetExportServiceGUI() {
		super(PetriNetExportAction.class, W_ANALYSIS);
	}
}
