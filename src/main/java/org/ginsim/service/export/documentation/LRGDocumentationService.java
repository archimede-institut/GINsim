package org.ginsim.service.export.documentation;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.EStatus;
import org.ginsim.core.service.Service;
import org.ginsim.core.service.ServiceStatus;
import org.mangosdk.spi.ProviderFor;

@ProviderFor( Service.class)
@Alias("documentation")
@ServiceStatus(EStatus.RELEASED)
public class LRGDocumentationService implements Service {

	public void run(RegulatoryGraph graph, DocumentExportConfig config, String filename) throws Exception {
		LRGDocumentationWriter writer = new LRGDocumentationWriter(graph);
		writer.export(config, filename);
	}
	
	public void export(RegulatoryGraph graph, String filename) throws Exception {
		JSONDocumentationWriter writer = new JSONDocumentationWriter(graph);
		writer.exportDocumentation(filename);
	}

}
