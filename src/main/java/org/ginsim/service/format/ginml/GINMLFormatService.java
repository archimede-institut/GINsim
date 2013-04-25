package org.ginsim.service.format.ginml;

import java.io.IOException;

import org.colomoto.logicalmodel.io.ginml.GINMLFormat;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.FormatSupportService;
import org.ginsim.core.service.Service;
import org.mangosdk.spi.ProviderFor;

/**
 * GINsim export service for the GINML (model-only) format.
 * 
 * @author Pedro T. Monteiro
 */
@ProviderFor(Service.class)
@Alias("GINML")
public class GINMLFormatService extends FormatSupportService<GINMLFormat> {

	public GINMLFormatService() {
		super(new GINMLFormat());
	}

	public void run(GINMLFormatConfig config, String filename)
			throws IOException {
		super.export(config.getModel(), filename);
	}
}