package org.ginsim.service.export.sbml;

import java.io.IOException;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.service.Service;
import org.mangosdk.spi.ProviderFor;

@ProviderFor( Service.class)
public class SBMLQualExportService implements Service{

	/**
	 * Execute the export by instantiating the right encoder
	 * 
	 * @param graph the graph to export
	 * @param config the configuration structure
	 * @param filename the path to the target file
	 * @throws IOException
	 */
	public void run( RegulatoryGraph graph, SBMLQualConfig config, String filename) throws IOException{
		
		SBMLQualEncoder encoder = new SBMLQualEncoder( );
		
		encoder.doExport( graph, config, filename);
	}
}
