package org.ginsim.service.export.reggraph;

import java.io.FileWriter;
import java.io.IOException;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.EStatus;
import org.ginsim.core.service.Service;
import org.ginsim.core.service.ServiceStatus;
import org.mangosdk.spi.ProviderFor;

/**
 * GINsim export service for the Regulatory Graph format.
 * 
 * @author Pedro T. Monteiro
 */
@ProviderFor(Service.class)
@Alias("reggraph")
@ServiceStatus(EStatus.RELEASED)
public class RegGraphExportService implements Service {

	public void export(RegulatoryGraph graph, String filename)
			throws IOException {

		RegGraphEncoder encoder = new RegGraphEncoder(graph);

		FileWriter writer = new FileWriter(filename);
		encoder.write(writer);
		writer.close();
	}
}