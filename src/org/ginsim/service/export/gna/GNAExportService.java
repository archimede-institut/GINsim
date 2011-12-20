package org.ginsim.service.export.gna;

import java.io.FileWriter;
import java.io.IOException;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.service.Service;
import org.mangosdk.spi.ProviderFor;

/**
 * GINsim export service capable of encoding the working model into the old (non
 * xml based) GNA specification.
 * <p>
 * It only writes the variables and the regulatory interactions between them,
 * leaving out the set of initial states.
 * </p>
 * 
 * @author Pedro T. Monteiro
 */
@ProviderFor(Service.class)
public class GNAExportService implements Service {

	public void run(RegulatoryGraph graph, String filename) throws IOException {

		FileWriter writer = new FileWriter(filename);

		GNAEncoder encoder = new GNAEncoder();
		encoder.write(graph, writer);

		writer.close();
	}
}
