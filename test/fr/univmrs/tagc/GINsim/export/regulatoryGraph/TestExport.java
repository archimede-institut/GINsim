package fr.univmrs.tagc.GINsim.export.regulatoryGraph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.ginsim.graph.common.Graph;
import org.ginsim.graph.regulatorygraph.GsRegulatoryGraph;

import junit.framework.TestCase;
import fr.univmrs.tagc.GINsim.graph.GsGinmlParser;
import fr.univmrs.tagc.common.TestTools;

public class TestExport extends TestCase {
	GsRegulatoryGraph graph;
	File tmpDir = TestTools.getTempDir();

	public TestExport() throws FileNotFoundException {
		File file = new File(TestTools.getTestDir(), "graph.ginml");
		GsGinmlParser parser = new GsGinmlParser();
		this.graph = (GsRegulatoryGraph)parser.parse(new FileInputStream(file), null);
	}
	public void testGNAML() throws IOException {
		GsGNAMLExport export =  new GsGNAMLExport(graph);
		String filename = tmpDir.getAbsolutePath()+File.separator+"graph.gnaml";
		export.doExport(filename);
	}
	public void testSNAKES() throws IOException {
		SnakesExport export =  new SnakesExport(graph);
		String filename = tmpDir.getAbsolutePath()+File.separator+"graph.py";
		export.doExport(filename);
	}
	public void testCytoscape() throws IOException {
		CytoscapeExport export =  new CytoscapeExport();
		String filename = tmpDir.getAbsolutePath()+File.separator+"graph.xgmml";
		
		super.fail("TODO: finish cytoscape export test");
		// export.doExport(filename);
	}
	
/*	public void testSbml() {
		SBML3Export export = new SBML3Export();
		GsExportConfig config = new GsExportConfig(graph, export, 0);
		config.setFilename(tmpDir.getAbsolutePath()+File.separator+"graph.sbml");
		export.doExport(config);		
	}*/
}
