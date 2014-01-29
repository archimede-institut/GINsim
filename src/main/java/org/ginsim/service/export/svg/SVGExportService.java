package org.ginsim.service.export.svg;

import java.io.IOException;
import java.util.Collection;

import org.ginsim.core.graph.Edge;
import org.ginsim.core.graph.Graph;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.Service;
import org.mangosdk.spi.ProviderFor;



/**
 * export the graph to SVG
 */
@ProviderFor( Service.class)
@Alias("SVG")
public class SVGExportService implements Service{

	
    /**
     * Export the graph to a SVG file
     * 
     * @param graph the graph to export
	 * @param nodes the list of nodes that must be exported
	 * @param edges the list of edges that must be exported
	 * @param fileName the path to the output file
	 */
	public void export( Graph graph, Collection nodes,  Collection<Edge> edges, String fileName) throws IOException{
		
		SVGEncoder encoder = new SVGEncoder();
	
		encoder.exportSVG( graph, nodes, edges, fileName);
	}
}
