package fr.univmrs.tagc.common.document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.TestCase;

import org.ginsim.graph.GinmlParser;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.service.export.documentation.GenericDocumentExport;

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
		GenericDocumentExport export =  new GenericDocumentExport(graph);
		String filename = tmpDir.getAbsolutePath()+File.separator+"tmp/graph.html";
		
		// TODO: extract a separate service for the export, then use it
		fail("TODO: finish document export test");
		// export.doExport(filename);
	}
}
