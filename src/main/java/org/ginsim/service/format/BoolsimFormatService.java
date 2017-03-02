package org.ginsim.service.format;

import org.colomoto.biolqm.io.boolsim.BoolSimFormat;
import org.ginsim.core.service.*;
import org.mangosdk.spi.ProviderFor;

/**
 * GINsim export service for the boolsim format.
 * 
 * @author Aurelien Naldi
 */
@ProviderFor(Service.class)
@Alias("boolsim")
@ServiceStatus(EStatus.DEVELOPMENT)
public class BoolsimFormatService extends FormatSupportService<BoolSimFormat> {

	public BoolsimFormatService() {
		super(new BoolSimFormat());
	}
}
