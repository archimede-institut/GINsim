package org.ginsim.service.export.svg;

import java.io.IOException;
import java.util.Collection;

import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.service.Service;
import org.mangosdk.spi.ProviderFor;



/**
 * export the graph to SVG
 */
@ProviderFor( Service.class)
public class SVGExportService implements Service{

	
    /**
     * Export the graph to a SVG file
     * 
     * @param graph the graph to export
	 * @param nodes the list of nodes that must be exported
	 * @param edges the list of edges that must be exported
	 * @param fileName the path to the output file
	 */
	public void run( Graph graph, Collection nodes,  Collection<Edge> edges, String fileName) throws IOException{
		
		SVGEncoder encoder = new SVGEncoder();
	
		encoder.exportSVG( graph, nodes, edges, fileName);
	}
}
