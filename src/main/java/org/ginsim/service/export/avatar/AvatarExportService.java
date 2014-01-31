package org.ginsim.service.export.avatar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.ginsim.common.application.GsException;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.EStatus;
import org.ginsim.core.service.Service;
import org.ginsim.core.service.ServiceStatus;
import org.mangosdk.spi.ProviderFor;

/**
 * GINsim export service capable of encoding the working model into an AVATAR
 * specification.
 * 
 * @author Pedro T. Monteiro
 */
@ProviderFor(Service.class)
@Alias("Avatar")
@ServiceStatus(EStatus.UNKNOWN)
public class AvatarExportService implements Service {

	public void run(AvatarConfig config, String filename) throws IOException, GsException {
		File f = new File(filename);
		export(config, f);
	}
	public void export(AvatarConfig config, File file) throws IOException, GsException {

		FileWriter writer = new FileWriter(file);

		AvatarEncoder encoder = new AvatarEncoder();
		encoder.write(config, writer);

		writer.close();
	}
}
