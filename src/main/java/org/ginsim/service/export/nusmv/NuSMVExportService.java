package org.ginsim.service.export.nusmv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.ginsim.common.application.GsException;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.Service;
import org.mangosdk.spi.ProviderFor;

/**
 * GINsim export service capable of encoding the working model into a NuSMV
 * specification. It currently uses NuSMV version 2.5.3.
 * <p>
 * It considers the use of priority classes for every model, whichever the
 * updating policy defined inside GINsim. If a given model considers priority
 * classes, it maps the corresponding classes. However, if a given model
 * considers an asynchronous (synchronous) updating policy, it creates an
 * equivalent mapping using priority classes, one class for each (all the)
 * variable(s).
 * </p>
 * @author Pedro T. Monteiro
 */
@ProviderFor(Service.class)
@Alias("NuSMV")
public class NuSMVExportService implements Service {

	public void run(NuSMVConfig config, String filename) throws IOException, GsException {
		File f = new File(filename);
		export(config, f);
	}
	public void export(NuSMVConfig config, File file) throws IOException, GsException {

		FileWriter writer = new FileWriter(file);

		NuSMVEncoder encoder = new NuSMVEncoder();
		encoder.write(config, writer);

		writer.close();
	}
}