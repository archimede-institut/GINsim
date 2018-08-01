package org.ginsim.service.export.document;

import java.io.File;

import org.ginsim.TestFileUtils;
import org.ginsim.common.document.GenericDocumentFormat;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.service.GSServiceManager;
import org.ginsim.service.export.documentation.LRGDocumentationService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class TestGenericDocumentExport {
	private static final String module = "DocumentationExport";
	static RegulatoryGraph graph;
	static File tmpDir = TestFileUtils.getTempTestFileDirectory(module);
	
	@BeforeAll
	public static void init() {
		File file = new File(TestFileUtils.getTestFileDirectory(module), "graph.ginml");
		graph = TestFileUtils.loadGraph(file);
	}
	
	@Test
	public void testGenericDocument() {
		LRGDocumentationService export =  GSServiceManager.get(LRGDocumentationService.class);
		String filename = tmpDir.getAbsolutePath()+File.separator+"graph.html";
		
		try {
			export.run(graph, GenericDocumentFormat.getAllFormats().get(0), filename);
		} catch (Exception e) {
			fail("Error during export: "+e.getMessage());
		}
	}
}
