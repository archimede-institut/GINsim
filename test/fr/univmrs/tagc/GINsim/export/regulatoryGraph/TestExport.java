package fr.univmrs.tagc.GINsim.export.regulatoryGraph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.ginsim.graph.GinmlParser;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.service.export.cytoscape.CytoscapeExportService;
import org.ginsim.service.export.gna.GNAMLExport;
import org.ginsim.service.export.snakes.SnakesEncoder;

import junit.framework.TestCase;
import fr.univmrs.tagc.common.TestTools;

public class TestExport extends TestCase {
	RegulatoryGraph graph;
	File tmpDir = TestTools.getTempDir();

	public TestExport() throws FileNotFoundException {
		File file = new File(TestTools.getTestDir(), "graph.ginml");
		GinmlParser parser = new GinmlParser();
		this.graph = (RegulatoryGraph)parser.parse(new FileInputStream(file), null);
	}
	public void testGNAML() throws IOException {
		GNAMLExport export =  new GNAMLExport(graph);
		String filename = tmpDir.getAbsolutePath()+File.separator+"graph.gnaml";
		// TODO : REFACTORING ACTION
		// TODO : Restore this test
		//export.doExport(filename);
	}
	public void testSNAKES() throws IOException {
		SnakesEncoder export =  new SnakesEncoder(graph);
		String filename = tmpDir.getAbsolutePath()+File.separator+"graph.py";
		// TODO : REFACTORING ACTION
		// TODO : Restore this test
		//export.doExport(filename);
	}
	public void testCytoscape() throws IOException {
		CytoscapeExportService export =  new CytoscapeExportService();
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
