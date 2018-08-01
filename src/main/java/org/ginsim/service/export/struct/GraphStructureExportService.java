package org.ginsim.service.export.struct;

import java.io.IOException;
import java.util.Collection;

import org.ginsim.common.application.GsException;
import org.ginsim.core.graph.Edge;
import org.ginsim.core.graph.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.EStatus;
import org.ginsim.core.service.Service;
import org.ginsim.core.service.ServiceStatus;
import org.kohsuke.MetaInfServices;

/**
 * Export the structure of a graph to various formats: GraphViz, Biolayout, and cytoscape.
 *
 * @author Aurelien Naldi
 */
@MetaInfServices( Service.class)
@Alias("structure-export")
@ServiceStatus(EStatus.RELEASED)
public class GraphStructureExportService implements Service {

	/**
	 * Encode the graph to struct output
	 * 
	 * @param graph the graph to encode
	 * @param edges the list of edges that must be part of the output
	 * @param filename the path to the output file
	 */
	public void biolayoutExport( Graph graph, Collection<Edge<?>> edges, String filename) throws IOException{
		
		BiolayoutEncoder encoder = new BiolayoutEncoder();
		encoder.encode( graph, edges, filename);
	}

    public void graphvizExport( Graph graph, Collection<Object> nodes,  Collection<Edge<?>> edges, String filename) throws IOException{

        GraphvizEncoder encoder = new GraphvizEncoder();
        encoder.encode(graph, nodes, edges, filename);
    }

    /**
     * Run the Cytoscape export by instantiating and calling a CytoscapeEncoder
     *
     * @param graph the graph to export
     * @param filename the path to the output xgmml file
     * @throws org.ginsim.common.application.GsException
     * @throws IOException
     */
    public void cytoscapeExport( RegulatoryGraph graph, String filename) throws GsException, IOException{

        CytoscapeEncoder encoder = new CytoscapeEncoder();
        encoder.encode( graph, filename);
    }

}
