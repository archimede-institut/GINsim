package org.ginsim.service.imports.sbml;

import java.io.File;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.io.sbml.SBMLqualImport;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.Service;
import org.mangosdk.spi.ProviderFor;

@ProviderFor( Service.class)
@Alias("SBMLi")
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

	
	public LogicalModel runJSBML( String filename) throws IOException {

		try {
			SBMLqualImport simport = new SBMLqualImport(new File(filename));
			LogicalModel model = simport.getModel();
			
			// TODO: import layout information
			
			// TODO: turn into a regulatory graph
			
			return model;
			
		} catch (XMLStreamException e) {
			throw new IOException(e);
		}
	}
}
