package fr.univmrs.tagc.GINsim.export.regulatoryGraph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import junit.framework.TestCase;

import org.ginsim.graph.regulatorygraph.GsRegulatoryGraph;

import fr.univmrs.tagc.GINsim.graph.GsGinmlParser;

public class TestExportNuSMV extends TestCase {
	
	String[] sFiles;
	GsRegulatoryGraph[] regGraph;
	GsNuSMVExport nusmvExport;

	public TestExportNuSMV() throws FileNotFoundException {
		// sFiles = TestTools.getAllModels();
		assertEquals(sFiles != null, true);
		
		regGraph = new GsRegulatoryGraph[sFiles.length];
	}
	
	public void testLoadModels() throws FileNotFoundException {
		for (int i = 0; i < sFiles.length; i++) {
			System.out.println("[" + sFiles[i] + "]");
			File fModel = new File(sFiles[i]);
			GsGinmlParser parser = new GsGinmlParser();
			regGraph[i] = (GsRegulatoryGraph) parser.parse(new FileInputStream(fModel), null);
			
			nusmvExport = new GsNuSMVExport(regGraph[i]);
			GsNuSMVConfig config = new GsNuSMVConfig(regGraph[i]);
			
			super.fail("TODO: finish NuSMV export test");
			// String filename = "";
			// nusmvExport.doExport(filename);
		}
	}
}
