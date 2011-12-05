package org.ginsim.service.export.graphviz;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;

import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.service.export.Dotify;

public class GraphvizEncoder {

	
	/**
	 * Encode the graph to graphviz output
	 * 
	 * @param graph the graph to encode
	 * @param fileName
	 */
	/**
	 * Encode the graph to graphviz output
	 * 
	 * @param graph the graph to encode
	 * @param nodes the list of nodes that must be part of the export
	 * @param edges the list of edges that must be part of the export
	 * @param filename the path to the output file
	 */
	public void encode( Graph graph, Collection<Object> nodes,  Collection<Edge<?>> edges, String filename) throws IOException{
		DateFormat dateformat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
		String date = dateformat.format(new Date());
        FileWriter out = new FileWriter(filename);

        out.write("/* Graphviz file generated by " +
    			"GINsim - " + date + " */" +
    			"\n\ndigraph G {");
        
		// Create external keys for nodes
        if (nodes == null) {
        	nodes = graph.getNodes();
        }

        // Process Nodes
        for (Object vertex: nodes) {
        	if (vertex instanceof Dotify) {
        		out.write(((Dotify) vertex).toDot());
        	} else {
	        	out.write("\n\t"+vertex+" [label=\"" + vertex + "\", shape=\"box\"];");
        	}

        }

        // Process Edges
        if (edges == null) {
        	edges = graph.getEdges();
        }
        for (Edge edge: edges) {
			Object from = edge.getSource();
        	Object to = edge.getTarget();	        			
    		if (from instanceof Dotify) {
                out.write("\n\t"+((Dotify) from).toDot(to));
    		} else {
                out.write("\n\t" + from + " -> " + to + ";");
    		}
        }

		// Close main tags
		out.write("\n}");
		out.close();
	}
}
