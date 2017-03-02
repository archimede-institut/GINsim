package org.ginsim.service.format;

import org.colomoto.biolqm.io.petrinet.INAFormat;
import org.ginsim.core.service.*;
import org.mangosdk.spi.ProviderFor;

/**
 * GINsim export service for some Petri net formats.
 * 
 * @author Aurelien Naldi
 */
@ProviderFor(Service.class)
@Alias("PN")
@ServiceStatus(EStatus.RELEASED)
public class PetriNetFormatService extends FormatSupportService<INAFormat> {

	public PetriNetFormatService() {
		super(new INAFormat());
	}
}
