package org.ginsim.service.imports.sbml;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
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
	public RegulatoryGraph run( String filename){
		
		SBMLXpathParser parser = new SBMLXpathParser(filename);
		RegulatoryGraph new_graph = parser.getGraph();
		
		return new_graph;
	}
}
