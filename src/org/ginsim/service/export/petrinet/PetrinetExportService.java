package org.ginsim.service.export.petrinet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.service.Service;

public class PetrinetExportService implements Service {

	public static final List<BasePetriNetExport> FORMATS = new ArrayList<BasePetriNetExport>();
	
	static {
		FORMATS.add(new PetriNetExportINA());
		FORMATS.add(new PetriNetExportPNML());
		FORMATS.add(new PetriNetExportAPNN());
	}
	
	public void run(RegulatoryGraph graph, PNConfig config, String filename) throws IOException {
		
	}
	
}
