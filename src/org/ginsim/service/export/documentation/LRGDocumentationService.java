package org.ginsim.service.export.documentation;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.service.Service;
import org.mangosdk.spi.ProviderFor;

@ProviderFor( Service.class)
public class LRGDocumentationService implements Service {

	public void run(RegulatoryGraph graph, DocumentExportConfig config, String filename) throws Exception {
		LRGDocumentationWriter writer = new LRGDocumentationWriter(graph);
		writer.export(config, filename);
	}
}
