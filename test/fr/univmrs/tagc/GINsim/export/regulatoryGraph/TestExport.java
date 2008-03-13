package fr.univmrs.tagc.GINsim.export.regulatoryGraph;

import java.io.File;

import junit.framework.TestCase;
import fr.univmrs.tagc.GINsim.export.GsExportConfig;
import fr.univmrs.tagc.GINsim.graph.GsGinsimGraphDescriptor;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.graph.GsGraphDescriptor;

public class TestExport extends TestCase {
	GsGraph graph;
	static String root = "testCase/";
	
	public void textExport() {
        GsGraphDescriptor gd = new GsGinsimGraphDescriptor();
        graph = gd.open(new File(root+"graph.ginml"));
	}

	public void testGNAML() {
		GsGNAMLExport export =  new GsGNAMLExport();
		GsExportConfig config = new GsExportConfig(graph, export, 0);
		config.setFilename(root+"graph.gnaml");
		export.doExport(config);
	}
}
