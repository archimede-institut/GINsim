package org.ginsim.service.export.nusmv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.ginsim.TestFileUtils;
import org.ginsim.common.application.GsException;
import org.ginsim.common.application.Translator;
import org.ginsim.common.utils.IOUtils;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.service.ServiceManager;
import org.junit.BeforeClass;
import org.junit.Test;

public class NuSMVExportTest {

	private static final String module = "NuSMVExport";

	private static String[] saModel = null;
	private static RegulatoryGraph[] saGraph = null;
	private static NuSMVExportService service = null;
	private static File dir = null;

	@BeforeClass
	public static void setUp() {
		// TODO: this should not be here...
		// Should be either unnecessary or done Once before All tests
		Translator.pushBundle("org.ginsim.messages");
		dir = TestFileUtils.getTestFileDirectory("models");
		saModel = new String[] { "E2F.ginml", "toymodel4d.ginml",
				"SP-2x18vars.ginml" };

		saGraph = new RegulatoryGraph[saModel.length];

		for (int i = 0; i < saModel.length; i++) {
			File file = new File(dir, saModel[i]);
			saGraph[i] = TestFileUtils.loadGraph(file);
			saModel[i] = saModel[i].substring(0, saModel[i].indexOf('.'));
		}

		service = ServiceManager.getManager().getService(
				NuSMVExportService.class);
		assertNotNull("NuSMVExportService service is not available", service);
	}

	private void runService(NuSMVConfig config, File tmpFile) {
		try {
			service.export(config, tmpFile);
		} catch (IOException e) {
			fail("Could not export to " + tmpFile);
		} catch (GsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private StringBuffer readFile(File file) {
		StringBuffer sb = null;
		try {
			sb = IOUtils.readFromFile(file.toString());
		} catch (IOException e) {
			fail("Could not read file: " + file.toString());
		}
		return sb;
	}

	@Test
	public void testExportAsync() {
		File tmpDir = TestFileUtils.getTempTestFileDirectory(module);

		for (int i = 0; i < saModel.length; i++) {
			File tmpFile = new File(tmpDir, saModel[i]);

			NuSMVConfig config = new NuSMVConfig(saGraph[i]);
			config.setUpdatePolicy(NuSMVConfig.CFG_ASYNC);

			runService(config, tmpFile);

			StringBuffer sbtmp = readFile(tmpFile);

			File origFile = new File(
					TestFileUtils.getTestFileDirectory(module), saModel[i]
							+ ".async.smv");
			StringBuffer sbOrig = readFile(origFile);

			// Discard first line, containing the generation date
			assertEquals(sbtmp.substring(sbtmp.indexOf("\n")),
					sbOrig.substring(sbOrig.indexOf("\n")));
		}
	}

	@Test
	public void testExportSync() {
		File tmpDir = TestFileUtils.getTempTestFileDirectory(module);

		for (int i = 0; i < saModel.length; i++) {
			File tmpFile = new File(tmpDir, saModel[i]);

			NuSMVConfig config = new NuSMVConfig(saGraph[i]);
			config.setUpdatePolicy(NuSMVConfig.CFG_SYNC);

			runService(config, tmpFile);

			StringBuffer sbtmp = readFile(tmpFile);

			File origFile = new File(
					TestFileUtils.getTestFileDirectory(module), saModel[i]
							+ ".sync.smv");
			StringBuffer sbOrig = readFile(origFile);

			// Discard first line, containing the generation date
			assertEquals(sbtmp.substring(sbtmp.indexOf("\n")),
					sbOrig.substring(sbOrig.indexOf("\n")));
		}
	}
}
