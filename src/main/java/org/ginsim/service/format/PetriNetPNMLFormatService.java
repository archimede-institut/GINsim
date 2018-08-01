package org.ginsim.service.format;

import org.colomoto.biolqm.io.petrinet.PNMLFormat;
import org.ginsim.core.service.*;
import org.kohsuke.MetaInfServices;

/**
 * GINsim export service for the PNML Petri net format.
 * 
 * @author Aurelien Naldi
 */
@MetaInfServices(Service.class)
@Alias("PNML")
@ServiceStatus(EStatus.RELEASED)
public class PetriNetPNMLFormatService extends FormatSupportService<PNMLFormat> {

	public PetriNetPNMLFormatService() {
		super(new PNMLFormat());
	}
}
