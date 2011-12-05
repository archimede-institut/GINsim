package org.ginsim.service.export.snakes;

import java.io.IOException;

import org.ginsim.core.exception.GsException;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.service.Service;
import org.mangosdk.spi.ProviderFor;

/**
 * Export the logical functions from regulatory graphs to python for use with the Snakes python library.
 * http://lacl.univ-paris12.fr/pommereau/soft/snakes/ 
 */
@ProviderFor(Service.class)
public class SnakesExportService implements Service{
	
	/**
	 * Run the Snakes export by instantiating and calling a SnakesEncoder
	 * 
	 * @param graph the graph to export
	 * @param filename the path to the output snakes file
	 * @throws GsException
	 * @throws IOException
	 */
	public void run( RegulatoryGraph graph, String filename) throws GsException, IOException{
		
		SnakesEncoder encoder = new SnakesEncoder();
		
		encoder.encode( graph, filename);
	}
}
