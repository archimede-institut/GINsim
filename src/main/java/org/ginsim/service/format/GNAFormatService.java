package org.ginsim.service.format;

import org.colomoto.logicalmodel.io.gna.GNAFormat;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.FormatSupportService;
import org.ginsim.core.service.Service;
import org.mangosdk.spi.ProviderFor;

/**
 * GINsim export service for the GNA (non-xml) format.
 * 
 * @author Pedro T. Monteiro
 */
@ProviderFor(Service.class)
@Alias("GNA")
public class GNAFormatService extends FormatSupportService<GNAFormat> {

	public GNAFormatService() {
		super(new GNAFormat());
	}
}
