package org.ginsim.service.format;

import org.colomoto.biolqm.io.petrinet.INAFormat;
import org.ginsim.core.service.*;
import org.mangosdk.spi.ProviderFor;

/**
 * GINsim export service for the INA Petri net format.
 * 
 * @author Aurelien Naldi
 */
@ProviderFor(Service.class)
@Alias("INA")
@ServiceStatus(EStatus.RELEASED)
public class PetriNetINAFormatService extends FormatSupportService<INAFormat> {

	public PetriNetINAFormatService() {
		super(new INAFormat());
	}
}
