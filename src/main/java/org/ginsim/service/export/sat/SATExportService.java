package org.ginsim.service.export.sat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.ginsim.common.application.GsException;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.EStatus;
import org.ginsim.core.service.Service;
import org.ginsim.core.service.ServiceStatus;
import org.kohsuke.MetaInfServices;

/**
 * GINsim export service capable of encoding the model properties into a SAT
 * specification in CNF.
 * 
 * @author Pedro T. Monteiro
 */
@MetaInfServices(Service.class)
@Alias("SAT")
@ServiceStatus(EStatus.RELEASED)
public class SATExportService implements Service {

	public void run(SATConfig config, String filename) throws Exception {
		File f = new File(filename);
		export(config, f);
	}

	public void export(SATConfig config, File file) throws Exception {
		FileWriter writer = new FileWriter(file);

		SATEncoder encoder = new SATEncoder();
		encoder.write(config, writer);

		writer.close();
	}
}
