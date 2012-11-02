package org.ginsim.service.export.cadp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.ginsim.common.application.GsException;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.Service;

import org.mangosdk.spi.ProviderFor;

/**
 * GINsim export service for CADP including:
 * 
 * 	LOTOS NT specifications (common, module and integration)
 *  EXP specifications (synchronization vectors)
 *  SVL script
 *  
 *  @author Nuno D. Mendes
 */
@ProviderFor(Service.class)
@Alias("CADP")
public class CADPExportService implements Service {

	// TODO: More than one file is generated, so the String only specifies the
	// HEAD name

	// TODO: Make EncoderUtils package to make the naming of gates, commaLists, decoratedLists
	// processNames, etc for all the exporters. also the naming of files etc
	
	public void run(CADPExportConfig config, String fileheadname)
			throws IOException, GsException {
		// File common = new File(fileheadname + "_common.lnt");
		// File modules = new File(fileheadname + "_modules.lnt");
		// File integration = new File(fileheadname + "_integration.lnt");
		// File exp = new File(fileheadname + ".exp");
		// File svl = new File(fileheadname + ".svl");
		// mcl?

		// TODO: Create directory with all these files and a README file with
		// instructions

	}

	public void export(CADPExportConfig config) throws GsException {

		CADPCommonWriter common = new CADPCommonWriter(config);
		CADPModuleWriter module = new CADPModuleWriter(config);
		CADPIntegrationWriter integration = new CADPIntegrationWriter(config);
		CADPExpWriter exp = new CADPExpWriter(config);
		CADPSvlWriter svl = new CADPSvlWriter(config);

		// common
		// modules
		// integration
		// exp
		// svl
		// mcl

	}

	/*
	 * public void export(NuSMVConfig config, File file) {
	 * 
	 * FileWriter writer = new FileWriter(file);
	 * 
	 * NuSMVEncoder encoder = new NuSMVEncoder(); encoder.write(config, writer);
	 * 
	 * writer.close(); }
	 */

}
