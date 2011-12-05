package org.ginsim.service.export.graphviz;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;

import org.ginsim.exception.GsException;
import org.ginsim.graph.common.Edge;
import org.ginsim.graph.common.Graph;
import org.ginsim.service.Service;
import org.ginsim.service.export.Dotify;
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
