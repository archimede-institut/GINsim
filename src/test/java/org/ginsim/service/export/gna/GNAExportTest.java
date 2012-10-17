package org.ginsim.service.export.gna;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.ginsim.TestFileUtils;
import org.ginsim.common.application.GsException;
import org.ginsim.common.application.Translator;
import org.ginsim.common.utils.IOUtils;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.service.ServiceManager;
import org.junit.BeforeClass;
import org.junit.Test;

public class GNAExportTest {

	private static final String module = "GNAExport";

	private static String[] saModel = null;
	private static RegulatoryGraph[] saGraph = null;
	private static GNAExportService service = null;
	private static File dir = null;

	@BeforeClass
	public static void setUp() {
		// TODO: this should not be here...
		// Should be either unnecessary or done Once before All tests
		Translator.pushBundle("org.ginsim.messages");
		dir = TestFileUtils.getTestFileDirectory(module);
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith("ginml");
			}
		};
		saModel = dir.list(filter);

		saGraph = new RegulatoryGraph[saModel.length];

		for (int i = 0; i < saModel.length; i++) {
			File file = new File(dir, saModel[i]);
			saGraph[i] = TestFileUtils.loadGraph(file);
			saModel[i] = saModel[i].substring(0, saModel[i].indexOf('.'));
		}

		service = ServiceManager.getManager()
				.getService(GNAExportService.class);
		assertNotNull("GNAExportService service is not available", service);
	}

	private void runService(RegulatoryGraph graph, String filename) {
		try {
			service.export(graph, filename);
		} catch (IOException e) {
			fail("Could not export to " + filename);
		}
	}

	private StringBuffer readFile(String filename) {
		StringBuffer sb = null;
		try {
			sb = IOUtils.readFromFile(filename);
		} catch (IOException e) {
			fail("Could not read file: " + filename);
		}
		return sb;
	}

	@Test
	public void testExport() {
		File tmpDir = TestFileUtils.getTempTestFileDirectory(module);

		for (int i = 0; i < saModel.length; i++) {
			String filename = tmpDir.getPath() + "/" + saModel[i];

			runService(saGraph[i], filename);

			StringBuffer sbtmp = readFile(filename);

			String origFile = TestFileUtils.getTestFileDirectory(module) + "/"
					+ saModel[i] + ".gna";
			StringBuffer sbOrig = readFile(origFile);

			assertEquals(sbtmp.toString(), sbOrig.toString());
		}
	}
}
