package org.ginsim.service.export.biolayout;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import org.ginsim.exception.GsException;
import org.ginsim.graph.common.Edge;
import org.ginsim.graph.common.Graph;
import org.ginsim.service.GsService;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.common.Tools;

/**
 * Encode a graph to biolayout format.
 */
@ProviderFor( GsService.class)
public class BioLayoutExportService implements GsService{
	static transient Hashtable hash;

	/**
	 * @param graph
	 * @param selectedOnly
	 * @param fileName
	 */
	public static void encode( Graph graph, Collection<Edge<?>> edges, String fileName) {
		hash = new Hashtable();
		try {
	        FileWriter out = new FileWriter(fileName);
	
	        // out.write("// Biolayout file generated by GINsim - " + 
	        // DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(new Date()) +"\n");
	        Iterator it;
	        
	        // Process Edges
	        if (edges == null) {
	        	edges = graph.getEdges();
	        }
	        for (Edge edge: edges) {
        		Object from = edge.getSource();
        		Object to = edge.getTarget();
	        	out.write(from + "\t" + to + "\n");
	        }
	
			// Close main tags
			out.close();
		} catch (IOException e) {
			Tools.error(new GsException(GsException.GRAVITY_ERROR, e.getLocalizedMessage()), null);
		}
	}
}
