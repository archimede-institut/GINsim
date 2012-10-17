package org.ginsim.service.imports.boolsim;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.io.boolsim.BoolSimFormat;
import org.ginsim.core.graph.regulatorygraph.LogicalModel2RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.Service;
import org.mangosdk.spi.ProviderFor;

/**
 * GINsim export service for the boolsim format.
 * 
 * @author Aurelien Naldi
 */
@ProviderFor(Service.class)
@Alias("boolsim_i")
public class BoolsimImportService implements Service {

	private final BoolSimFormat support = new BoolSimFormat();
	
	public RegulatoryGraph importFile( String filename) throws IOException {
		LogicalModel model = support.importFile(new File(filename));
		RegulatoryGraph lrg = LogicalModel2RegulatoryGraph.importModel(model);
		
		return lrg;
	}

}
