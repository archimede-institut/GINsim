package org.ginsim.service.format.ginml;

import org.colomoto.biolqm.io.ginml.GINMLFormat;
import org.ginsim.core.service.*;
import org.kohsuke.MetaInfServices;

/**
 * GINsim export service for the GINML (model-only) format.
 * 
 * @author Pedro T. Monteiro
 */
@MetaInfServices(Service.class)
@Alias("GINML")
@ServiceStatus(EStatus.RELEASED)
public class GINMLFormatService extends FormatSupportService<GINMLFormat> {

	public GINMLFormatService() {
		super(new GINMLFormat());
	}

	public void run(GINMLFormatConfig config, String filename) throws Exception {
		super.export(config.getModel(), filename);
	}
}