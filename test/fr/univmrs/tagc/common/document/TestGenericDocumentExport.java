package fr.univmrs.tagc.common.document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.TestCase;

import org.ginsim.core.graph.GinmlParser;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.service.ServiceManager;
import org.ginsim.service.export.documentation.LRGDocumentationService;

import fr.univmrs.tagc.common.TestTools;

public class TestGenericDocumentExport extends TestCase {
	RegulatoryGraph graph;
	File tmpDir = TestTools.getTestDir(); //TODO: replace TestTools.getTtestDir() by tmpDir
	
	public TestGenericDocumentExport() throws FileNotFoundException {
		File file = new File(TestTools.getTestDir(), "graph.ginml");
		GinmlParser parser = new GinmlParser();
		this.graph = (RegulatoryGraph)parser.parse(new FileInputStream(file), null);
	}
	public void testGenericDocument() throws IOException {
		LRGDocumentationService export =  ServiceManager.get(LRGDocumentationService.class);
		String filename = tmpDir.getAbsolutePath()+File.separator+"tmp/graph.html";
		
		// TODO: extract a separate service for the export, then use it
		fail("TODO: finish document export test");
		// export.doExport(filename);
	}
}
