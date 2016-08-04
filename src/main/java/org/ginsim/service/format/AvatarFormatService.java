package org.ginsim.service.format;

import org.colomoto.logicalmodel.io.avatar.AvatarFormat;
import org.ginsim.core.service.*;
import org.mangosdk.spi.ProviderFor;

/**
 * GINsim export service for the boolsim format.
 * 
 * @author Aurelien Naldi
 */
@ProviderFor(Service.class)
@Alias("avatar")
@ServiceStatus(EStatus.DEVELOPMENT)
public class AvatarFormatService extends FormatSupportService<AvatarFormat> {

	public AvatarFormatService() {
		super(new AvatarFormat());
	}
}
