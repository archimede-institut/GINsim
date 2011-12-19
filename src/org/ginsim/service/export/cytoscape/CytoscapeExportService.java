package org.ginsim.service.export.cytoscape;

import java.io.IOException;

import org.ginsim.common.exception.GsException;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.service.Service;
import org.mangosdk.spi.ProviderFor;

/**
 * CytoscapeExport is a plugin for GINsim to export a regulatory graph into XGMML format.
 * 
 * @author BERENGUIER duncan - M1BBSG
 * @version 1.0
 * february 2008 - april 2008
 * 
 */
@ProviderFor(Service.class)
public class CytoscapeExportService implements Service{
	
	/**
	 * Run the Cytoscape export by instantiating and calling a CytoscapeEncoder
	 * 
	 * @param graph the graph to export
	 * @param filename the path to the output xgmml file
	 * @throws GsException
	 * @throws IOException
	 */
	public void run( RegulatoryGraph graph, String filename) throws GsException, IOException{
		
		CytoscapeEncoder encoder = new CytoscapeEncoder();
		
		encoder.encode( graph, filename);
	}
}
