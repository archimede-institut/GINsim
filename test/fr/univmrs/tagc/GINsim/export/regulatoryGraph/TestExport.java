package fr.univmrs.tagc.GINsim.export.regulatoryGraph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.TestCase;

import org.ginsim.exception.GsException;
import org.ginsim.graph.GinmlParser;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.service.ServiceManager;
import org.ginsim.service.export.cytoscape.CytoscapeEncoder;
import org.ginsim.service.export.gna.GNAExportService;
import org.ginsim.service.export.snakes.SnakesEncoder;

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
		GNAExportService export = ServiceManager.getManager().getService(GNAExportService.class);
		String filename = tmpDir.getAbsolutePath()+File.separator+"graph.gnaml";
		export.run(graph, filename);
	}
	public void testSNAKES() throws IOException {
		
		String filename = tmpDir.getAbsolutePath()+File.separator+"graph.py";
		SnakesEncoder export =  new SnakesEncoder();
		try{
			export.encode( graph, filename);
		}
		catch( GsException exception){
			super.fail( "Exception occured during testSnakes : " + exception);
		}
	}
	public void testCytoscape() throws IOException {

		String filename = tmpDir.getAbsolutePath()+File.separator+"graph.xgmml";
		CytoscapeEncoder encoder = new CytoscapeEncoder();
		try{
			encoder.encode( graph, filename);
		}
		catch( GsException exception){
			super.fail( "Exception occured during testCytoscape : " + exception);
		}
		super.fail("TODO: finish cytoscape export test");
	}
	
/*	public void testSbml() {
		SBML3Export export = new SBML3Export();
		GsExportConfig config = new GsExportConfig(graph, export, 0);
		config.setFilename(tmpDir.getAbsolutePath()+File.separator+"graph.sbml");
		export.doExport(config);		
	}*/
}
