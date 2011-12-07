package org.ginsim.service.imports.sbml;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.service.Service;
import org.mangosdk.spi.ProviderFor;

@ProviderFor( Service.class)
public class SBMLImportService implements Service {

	/**
	 * Return the graph built from the SBML file at the given path
	 * 
	 * @param filename the path of the SBML file describing the graph
	 * @return the graph built from the SBML file at the given path
	 */
	public Graph run( String filename){
		
		SBMLXpathParser parser = new SBMLXpathParser(filename);
		Graph new_graph = parser.getGraph();
		
		return new_graph;
	}
}
