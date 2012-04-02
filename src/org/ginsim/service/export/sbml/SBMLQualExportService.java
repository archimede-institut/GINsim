package org.ginsim.service.export.sbml;

import java.io.IOException;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.Service;
import org.mangosdk.spi.ProviderFor;

@ProviderFor( Service.class)
@Alias("SBMLe")
public class SBMLQualExportService implements Service{

	/**
	 * Execute the export by instantiating the right encoder
	 * 
	 * @param graph the graph to export
	 * @param config the configuration structure
	 * @param filename the path to the target file
	 * @throws IOException
	 */
	public void export( RegulatoryGraph graph, SBMLQualConfig config, String filename) throws IOException{
		
		SBMLQualEncoder encoder = new SBMLQualEncoder( );
		
		encoder.doExport( graph, config, filename);
	}
	
	/**
	 * Convenience method to export without having to configure anything
	 * @param graph
	 * @param filename
	 * @throws IOException
	 */
	public void export( RegulatoryGraph graph, String filename) throws IOException {
		SBMLQualConfig cfg = new SBMLQualConfig(graph);
		export(graph, cfg, filename);
	}
}
