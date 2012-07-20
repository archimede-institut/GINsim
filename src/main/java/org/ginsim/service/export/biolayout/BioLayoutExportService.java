package org.ginsim.service.export.biolayout;

import java.io.IOException;
import java.util.Collection;

import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.Service;
import org.mangosdk.spi.ProviderFor;

/**
 * Encode a graph to biolayout format.
 */
@ProviderFor( Service.class)
@Alias("biolayout")
public class BioLayoutExportService implements Service{

	
	/**
	 * Encode the graph to biolayout output
	 * 
	 * @param graph the graph to encode
	 * @param edges the list of edges that must be part of the output
	 * @param filename the path to the output file
	 */
	public void run( Graph graph, Collection<Edge<?>> edges, String filename) throws IOException{
		
		BiolayoutEncoder encoder = new BiolayoutEncoder();
		
		encoder.encode( graph, edges, filename);
	}
}
