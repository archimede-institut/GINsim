package org.ginsim.service.format;

import org.colomoto.biolqm.io.petrinet.INAFormat;
import org.ginsim.core.service.*;
import org.kohsuke.MetaInfServices;

/**
 * GINsim export service for the INA Petri net format.
 * 
 * @author Aurelien Naldi
 */
@MetaInfServices(Service.class)
@Alias("INA")
@ServiceStatus(EStatus.RELEASED)
public class PetriNetINAFormatService extends FormatSupportService<INAFormat> {

	public PetriNetINAFormatService() {
		super(new INAFormat());
	}
}
