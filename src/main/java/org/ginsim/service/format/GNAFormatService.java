package org.ginsim.service.format;

import org.colomoto.biolqm.io.gna.GNAFormat;
import org.ginsim.core.service.*;
import org.kohsuke.MetaInfServices;

/**
 * GINsim export service for the GNA (non-xml) format.
 * 
 * @author Pedro T. Monteiro
 */
@MetaInfServices(Service.class)
@Alias("GNA")
@ServiceStatus(EStatus.RELEASED)
public class GNAFormatService extends FormatSupportService<GNAFormat> {

	public GNAFormatService() {
		super(new GNAFormat());
	}
}
