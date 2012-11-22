package org.ginsim.service.format;

import org.colomoto.logicalmodel.io.petrinet.PetriNetFormat;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.FormatSupportService;
import org.ginsim.core.service.Service;
import org.mangosdk.spi.ProviderFor;

/**
 * GINsim export service for some Petri net formats.
 * 
 * @author Aurelien Naldi
 */
@ProviderFor(Service.class)
@Alias("PN")
public class PetriNetFormatService extends FormatSupportService<PetriNetFormat> {

	public PetriNetFormatService() {
		super(new PetriNetFormat());
	}
}
