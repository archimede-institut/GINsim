package org.ginsim.service.format;

import org.colomoto.logicalmodel.io.gna.GNAFormat;
import org.ginsim.core.service.*;
import org.mangosdk.spi.ProviderFor;

/**
 * GINsim export service for the GNA (non-xml) format.
 * 
 * @author Pedro T. Monteiro
 */
@ProviderFor(Service.class)
@Alias("GNA")
@ServiceStatus(EStatus.RELEASED)
public class GNAFormatService extends FormatSupportService<GNAFormat> {

	public GNAFormatService() {
		super(new GNAFormat());
	}
}
