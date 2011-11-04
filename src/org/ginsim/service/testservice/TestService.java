package org.ginsim.service.testservice;

import org.ginsim.graph.testGraph.TestGraph;
import org.ginsim.service.GsService;
import org.mangosdk.spi.ProviderFor;

@ProviderFor(GsService.class)
public class TestService implements GsService {

	@Override
	public Class getServedGraphClass() {
		
		return TestGraph.class;
	}

}
