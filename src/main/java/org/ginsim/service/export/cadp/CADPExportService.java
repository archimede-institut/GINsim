package org.ginsim.service.export.cadp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
	//	File common = new File(fileheadname + "_common.lnt");
		//File modules = new File(fileheadname + "_modules.lnt");
	//	File integration = new File(fileheadname + "_integration.lnt");
	//	File exp = new File(fileheadname + ".exp");
	//	File svl = new File(fileheadname + ".svl");
		export(config,fileheadname);
		
		// mcl?
		
		// TODO: Create directory with all these files and a README file with
		// instructions

	}

	public void export(CADPExportConfig config, String fileheadname) throws GsException, IOException {

		File bundle = new File(fileheadname);
		FileOutputStream stream = new FileOutputStream(bundle);
		ZipOutputStream zos = new ZipOutputStream(stream);
		ZipEntry ze = null;
		
		ze = new ZipEntry("common.lnt");
		CADPCommonWriter commonWriter = new CADPCommonWriter(config);
		String common = commonWriter.toString();
		ze.setSize((long) common.getBytes().length);
		zos.setLevel(9);
		zos.putNextEntry(ze);
		zos.write(common.getBytes());
		zos.closeEntry();
		
		ze = new ZipEntry(config.getLNTModelFilename());
		CADPModuleWriter moduleWriter = new CADPModuleWriter(config);
		String modules = moduleWriter.toString();
		ze.setSize((long) modules.getBytes().length);
		zos.setLevel(9);
		zos.putNextEntry(ze);
		zos.write(modules.getBytes());
		zos.closeEntry();
		
		ze = new ZipEntry(config.getLNTIntegrationFilename());
		CADPIntegrationWriter integrationWriter = new CADPIntegrationWriter(config);
		String integration = integrationWriter.toString();
		ze.setSize((long) integration.getBytes().length);
		zos.setLevel(9);
		zos.putNextEntry(ze);
		zos.write(integration.getBytes());
		zos.closeEntry();
		
		ze = new ZipEntry(config.getExpFilename());
		CADPExpWriter expWriter = new CADPExpWriter(config);
		String exp = expWriter.toString();
		ze.setSize((long) exp.getBytes().length);
		zos.setLevel(9);
		zos.putNextEntry(ze);
		zos.write(exp.getBytes());
		zos.closeEntry();
		
		ze = new ZipEntry("file.svl");
		CADPSvlWriter svlWriter = new CADPSvlWriter(config);
		String svl = svlWriter.toString();
		ze.setSize(svl.getBytes().length);
		zos.setLevel(9);
		zos.putNextEntry(ze);
		zos.write(svl.getBytes());
		zos.closeEntry();


		zos.finish();
		zos.close();

		// mcl

		// readme
	}

}
