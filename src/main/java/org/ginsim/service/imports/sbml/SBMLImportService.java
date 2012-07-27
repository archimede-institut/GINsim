package org.ginsim.service.imports.sbml;

import java.io.File;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.io.sbml.SBMLqualImport;
import org.ginsim.core.graph.regulatorygraph.LogicalModel2RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.Service;
import org.mangosdk.spi.ProviderFor;

@ProviderFor( Service.class)
@Alias("SBMLi")
public class SBMLImportService implements Service {

	private static boolean USEJSBML = false;
	
	/**
	 * Return the graph built from the SBML file at the given path
	 * 
	 * @param filename the path of the SBML file describing the graph
	 * @return the graph built from the SBML file at the given path
	 */
	public RegulatoryGraph run( String filename) {
		if (USEJSBML) {
			return runJSBML(filename);
		}
		
		return runLegacy(filename);
	}

	/**
	 * SBML import based on a custom parser.
	 * 
	 * @param filename
	 * @return
	 */
	@Deprecated
	public RegulatoryGraph runLegacy( String filename){
		
		SBMLXpathParser parser = new SBMLXpathParser(filename);
		RegulatoryGraph new_graph = parser.getGraph();
		
		return new_graph;
	}

	/**
	 * SBML import using The JSBML-based parser in LogicalModel.
	 * 
	 * @param filename
	 * @return
	 */
	public RegulatoryGraph runJSBML( String filename) {

		try {
			SBMLqualImport simport = new SBMLqualImport(new File(filename));
			LogicalModel model = simport.getModel();
			return LogicalModel2RegulatoryGraph.importModel(model);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
