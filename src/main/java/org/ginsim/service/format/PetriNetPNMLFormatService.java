package org.ginsim.service.format;

import org.colomoto.biolqm.io.petrinet.PNMLFormat;
import org.ginsim.core.service.*;
import org.mangosdk.spi.ProviderFor;

/**
 * GINsim export service for the PNML Petri net format.
 * 
 * @author Aurelien Naldi
 */
@ProviderFor(Service.class)
@Alias("PNML")
@ServiceStatus(EStatus.RELEASED)
public class PetriNetPNMLFormatService extends FormatSupportService<PNMLFormat> {

	public PetriNetPNMLFormatService() {
		super(new PNMLFormat());
	}
}
