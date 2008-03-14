package fr.univmrs.tagc.GINsim.export.regulatoryGraph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.TestCase;
import fr.univmrs.tagc.GINsim.export.GsExportConfig;
import fr.univmrs.tagc.GINsim.graph.GsGinmlParser;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.common.TestTools;

public class TestExport extends TestCase {
	GsGraph graph;
	File tmpDir = TestTools.getTempDir();
	
	public TestExport() throws FileNotFoundException {
		File file = new File(TestTools.getTestDir(), "graph.ginml");
		GsGinmlParser parser = new GsGinmlParser();
		this.graph = parser.parse(new FileInputStream(file), null);
	}
	public void testGNAML() throws IOException {
		GsGNAMLExport export =  new GsGNAMLExport();
		GsExportConfig config = new GsExportConfig(graph, export, 0);
		config.setFilename(TestTools.getTestDir().getAbsolutePath()+File.separator+"tmp/graph.gnaml"); //FIXME: replace TestTools.getTtestDir() by tmpDir
		export.doExport(config);
	}
}
