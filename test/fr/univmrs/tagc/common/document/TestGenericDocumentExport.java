package fr.univmrs.tagc.common.document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.ginsim.graph.Graph;

import junit.framework.TestCase;
import fr.univmrs.tagc.GINsim.export.GsExportConfig;
import fr.univmrs.tagc.GINsim.export.regulatoryGraph.GenericDocumentExport;
import fr.univmrs.tagc.GINsim.graph.GsGinmlParser;

import fr.univmrs.tagc.common.TestTools;

public class TestGenericDocumentExport extends TestCase {
	Graph graph;
	File tmpDir = TestTools.getTestDir(); //FIXME: replace TestTools.getTtestDir() by tmpDir
	
	public TestGenericDocumentExport() throws FileNotFoundException {
		File file = new File(TestTools.getTestDir(), "graph.ginml");
		GsGinmlParser parser = new GsGinmlParser();
		this.graph = parser.parse(new FileInputStream(file), null);
	}
	public void testGenericDocument() throws IOException {
		GenericDocumentExport export =  new GenericDocumentExport();
		GsExportConfig config = new GsExportConfig(graph, export, 0);
		config.setFilename(tmpDir.getAbsolutePath()+File.separator+"tmp/graph.html");
		//export.doExport(config);
	}
}


