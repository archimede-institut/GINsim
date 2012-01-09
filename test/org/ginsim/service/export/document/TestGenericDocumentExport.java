package org.ginsim.service.export.document;

import java.io.File;

import org.ginsim.TestFileUtils;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.service.ServiceManager;
import org.ginsim.service.export.documentation.LRGDocumentationService;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestGenericDocumentExport {
	static RegulatoryGraph graph;
	static File tmpDir = TestFileUtils.getTempTestFileDirectory("document");
	
	@BeforeClass
	public static void init() {
		File file = new File(TestFileUtils.getTestFileDir(), "graph.zginml");
		graph = TestFileUtils.loadGraph(file);
	}
	
	@Test
	public void testGenericDocument() {
		LRGDocumentationService export =  ServiceManager.get(LRGDocumentationService.class);
		String filename = tmpDir.getAbsolutePath()+File.separator+"tmp/graph.html";
		
		// TODO: extract a separate service for the export, then use it
		//Assert.fail("TODO: finish document export test");
		// export.doExport(filename);
	}
}
