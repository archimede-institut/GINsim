package org.ginsim.service.tool.stableregions;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.colomoto.biolqm.LogicalModel;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.EStatus;
import org.ginsim.core.service.Service;
import org.ginsim.core.service.ServiceStatus;
import org.mangosdk.spi.ProviderFor;

@ProviderFor(Service.class)
@Alias("stableregions")
@ServiceStatus(EStatus.DEVELOPMENT)
public class StableRegionsService implements Service {
	public Set<Set<String>> getSCCs(RegulatoryGraph regGraph){
		Set<Set<String>> sccs = new HashSet<Set<String>>();
		LogicalModel model = regGraph.getModel();
		PNtoGraph ptg = new PNtoGraph(model);
		try {
			PNGraph pnGraph = ptg.getPnGraph();
			pnGraph.printPnGraph();
			sccs = pnGraph.getStableMotifs();
			pnGraph.printAttractors(sccs);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sccs;
	}
	
}


	
