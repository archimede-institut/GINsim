package org.ginsim.service.export.graphviz;

import java.io.IOException;
import java.util.Collection;

import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.service.Service;
import org.mangosdk.spi.ProviderFor;



/**
 * Encode a graph to graphviz format.
 * It is based on org.jgraph.util.JGraphGraphvizEncoder
 * but isn't jgraph specific and writes the result to a File instead of putting it in a String.
 * (basically no original code remains..)
 */
@ProviderFor( Service.class)
public class GraphvizExportService implements Service {

	public void run( Graph graph, Collection<Object> nodes,  Collection<Edge<?>> edges, String filename) throws IOException{
		
		GraphvizEncoder encoder = new GraphvizEncoder();
		
		encoder.encode(graph, nodes, edges, filename);
	}
}
