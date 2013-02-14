package org.ginsim.service.export.cadp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.ginsim.common.application.GsException;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.Service;
import org.mangosdk.spi.ProviderFor;

/**
 * GINsim export service for CADP including:
 * 
 * LOTOS NT specifications, (common, module and integration), EXP specifications
 * (synchronization vectors), SVL script, MCL properties and README file
 * 
 * @author Nuno D. Mendes
 */
@ProviderFor(Service.class)
@Alias("CADP")
public class CADPExportService implements Service {

	public void run(CADPExportConfig config, String fileheadname)
			throws IOException, GsException {
		export(config, fileheadname);
	}

	/**
	 * 
	 * Exports the necessary files for CADP
	 * 
	 * @param config
	 *            the configuration of the composition
	 * @param fileheadname
	 *            the name of the bundle (zip) file
	 * @throws GsException
	 * @throws IOException
	 */
	public void export(CADPExportConfig config, String fileheadname)
			throws GsException, IOException {

		File bundle = new File(fileheadname);
		FileOutputStream stream = new FileOutputStream(bundle);
		ZipOutputStream zos = new ZipOutputStream(stream);
		ZipEntry ze = null;

		ze = new ZipEntry("common.lnt"); // TODO: make this a config value
		CADPCommonWriter commonWriter = new CADPCommonWriter(config);
		String common = commonWriter.toString();
		ze.setSize((long) common.getBytes().length);
		zos.setLevel(9);
		zos.putNextEntry(ze);
		zos.write(common.getBytes(), 0, common.getBytes().length);
		zos.closeEntry();
		// System.err.print(common);

		ze = new ZipEntry(config.getLNTModelFilename());
		CADPModuleWriter moduleWriter = new CADPModuleWriter(config);
		String modules = moduleWriter.toString();
		ze.setSize((long) modules.getBytes().length);
		zos.setLevel(9);
		zos.putNextEntry(ze);
		zos.write(modules.getBytes(), 0, modules.getBytes().length);
		zos.closeEntry();
		// System.err.print(modules);

		ze = new ZipEntry(config.getLNTIntegrationFilename());
		CADPIntegrationWriter integrationWriter = new CADPIntegrationWriter(
				config);
		String integration = integrationWriter.toString();
		ze.setSize((long) integration.getBytes().length);
		zos.setLevel(9);
		zos.putNextEntry(ze);
		zos.write(integration.getBytes());
		zos.closeEntry();
		System.err.print(integration);

		ze = new ZipEntry(config.getExpFilename());
		CADPExpWriter expWriter = new CADPExpWriter(config);
		String exp = expWriter.toString();
		ze.setSize((long) exp.getBytes().length);
		zos.setLevel(9);
		zos.putNextEntry(ze);
		zos.write(exp.getBytes());
		zos.closeEntry();
		System.err.print(exp);

		ze = new ZipEntry("file.svl"); // TODO: make it a config value
		CADPSvlWriter svlWriter = new CADPSvlWriter(config);
		String svl = svlWriter.toString();
		ze.setSize(svl.getBytes().length);
		zos.setLevel(9);
		zos.putNextEntry(ze);
		zos.write(svl.getBytes());
		zos.closeEntry();
		System.err.print(svl);

		for (List<byte[]> globalStableState : config.getCompatibleStableStates()){
			ze = new ZipEntry(config.getMCLPropertyFileName(globalStableState));
			CADPMclWriter mclWriter = new CADPMclWriter(config,globalStableState);
			String mcl = mclWriter.toString();
			ze.setSize(mcl.getBytes().length);
			zos.setLevel(9);
			zos.putNextEntry(ze);
			zos.write(svl.getBytes());
			zos.closeEntry();
			System.err.println(mcl);
		}		
					
		// TODO add readme file
		
		zos.finish();
		zos.close();

	}

}
