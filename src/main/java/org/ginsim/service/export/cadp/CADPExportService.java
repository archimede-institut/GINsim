package org.ginsim.service.export.cadp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.ginsim.common.application.GsException;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.Service;
import org.ginsim.service.tool.composition.IntegrationFunctionMapping;
import org.ginsim.service.tool.composition.Topology;
import org.mangosdk.spi.ProviderFor;


/*
 * GINsim export service for CADP including:
 * 
 * 	LOTOS NT specifications (common, module and integration)
 *  EXP specifications (synchronisation vectors)
 *  SVL script
 *  
 *  @author Nuno D. Mendes
 */
@ProviderFor(Service.class)
@Alias("CADP")
public class CADPExportService implements Service {

// TODO: More than one file is generated, so the String only specifies the HEAD name	
	
public void run(RegulatoryGraph graph, Topology topology, IntegrationFunctionMapping mapping, String fileheadname) throws IOException, GsException {
	File common = new File(fileheadname + "_common.lnt");
	File modules = new File(fileheadname + "_modules.lnt");
	File integration = new File(fileheadname + "_integration.lnt");
	File exp = new File(fileheadname + ".exp");
	File svl = new File(fileheadname + ".svl");

	
	
	// TODO: Create directory with all these files and a README file with instructions

}

public void export()  {
	
	
}

/*
	public void export(NuSMVConfig config, File file) throws IOException, GsException {

		FileWriter writer = new FileWriter(file);

		NuSMVEncoder encoder = new NuSMVEncoder();
		encoder.write(config, writer);

		writer.close();
	}*/
	
}
