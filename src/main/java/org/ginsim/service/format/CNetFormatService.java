package org.ginsim.service.format;

import org.colomoto.biolqm.io.cnet.CNetFormat;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.EStatus;
import org.ginsim.core.service.FormatSupportService;
import org.ginsim.core.service.Service;
import org.ginsim.core.service.ServiceStatus;
import org.mangosdk.spi.ProviderFor;

/**
 * GINsim export service for the CNET format used by the BNS tool.
 * 
 * @author Aurelien Naldi
 */
@ProviderFor(Service.class)
@Alias("cnet")
@ServiceStatus(EStatus.DEVELOPMENT)
public class CNetFormatService extends FormatSupportService<CNetFormat> {

	public CNetFormatService() {
		super(new CNetFormat());
	}
}
