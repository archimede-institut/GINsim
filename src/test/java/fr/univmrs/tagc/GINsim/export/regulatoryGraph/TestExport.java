package fr.univmrs.tagc.GINsim.export.regulatoryGraph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.ginsim.TestFileUtils;
import org.ginsim.common.application.GsException;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.io.parser.GinmlParser;
import org.ginsim.service.export.struct.CytoscapeEncoder;
import org.junit.Assert;
import org.junit.Test;

public class TestExport {
	RegulatoryGraph graph;
	File tmpDir = TestFileUtils.getTempTestFileDirectory("exports");

	public TestExport() throws FileNotFoundException, GsException{
		File file = new File(TestFileUtils.getTestFileDir(), "graph.ginml");
		GinmlParser parser = new GinmlParser();
		this.graph = (RegulatoryGraph)parser.parse(new FileInputStream(file), null);
	}
	
	@Test
	public void testCytoscape() throws IOException {

		String filename = tmpDir.getAbsolutePath()+File.separator+"graph.xgmml";
		CytoscapeEncoder encoder = new CytoscapeEncoder();
		try{
			encoder.encode( graph, filename);
		}
		catch( GsException exception){
			Assert.fail( "Exception occured during testCytoscape : " + exception);
		}
		Assert.fail("TODO: finish cytoscape export test");
	}
	
}
