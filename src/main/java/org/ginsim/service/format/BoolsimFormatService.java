package org.ginsim.service.format;

import org.colomoto.logicalmodel.io.boolsim.BoolSimFormat;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.FormatSupportService;
import org.ginsim.core.service.Service;
import org.mangosdk.spi.ProviderFor;

/**
 * GINsim export service for the boolsim format.
 * 
 * @author Aurelien Naldi
 */
@ProviderFor(Service.class)
@Alias("boolsim")
public class BoolsimFormatService extends FormatSupportService<BoolSimFormat> {

	public BoolsimFormatService() {
		super(new BoolSimFormat());
	}
}
