package org.ginsim.service.export.cytoscape;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;

import org.ginsim.exception.GsException;
import org.ginsim.graph.common.EdgeAttributesReader;
import org.ginsim.graph.common.NodeAttributesReader;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.service.Service;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.common.Tools;
import fr.univmrs.tagc.common.xml.XMLWriter;

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
