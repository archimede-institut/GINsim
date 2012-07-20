package org.ginsim.service.export.sat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.ginsim.core.service.Service;
import org.mangosdk.spi.ProviderFor;

/**
 * GINsim export service capable of encoding the working model into a SAT
 * specification in CNF.
 * @author Pedro T. Monteiro
 */
@ProviderFor(Service.class)
public class SATExportService implements Service {

	public void run(SATConfig config, String filename) throws IOException {
		File f = new File(filename);
		run(config, f);
	}
	public void run(SATConfig config, File file) throws IOException {

		FileWriter writer = new FileWriter(file);

		SATEncoder encoder = new SATEncoder();
		encoder.write(config, writer);

		writer.close();
	}
}
