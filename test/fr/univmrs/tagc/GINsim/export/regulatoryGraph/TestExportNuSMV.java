package fr.univmrs.tagc.GINsim.export.regulatoryGraph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import junit.framework.TestCase;

import org.ginsim.graph.GsGinmlParser;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.service.export.nusmv.GsNuSMVConfig;
import org.ginsim.service.export.nusmv.GsNuSMVExport;


public class TestExportNuSMV extends TestCase {
	
	String[] sFiles;
	RegulatoryGraph[] regGraph;
	GsNuSMVExport nusmvExport;

	public TestExportNuSMV() throws FileNotFoundException {
		// sFiles = TestTools.getAllModels();
		assertEquals(sFiles != null, true);
		
		regGraph = new RegulatoryGraph[sFiles.length];
	}
	
	public void testLoadModels() throws FileNotFoundException {
		for (int i = 0; i < sFiles.length; i++) {
			System.out.println("[" + sFiles[i] + "]");
			File fModel = new File(sFiles[i]);
			GsGinmlParser parser = new GsGinmlParser();
			regGraph[i] = (RegulatoryGraph) parser.parse(new FileInputStream(fModel), null);
			
			nusmvExport = new GsNuSMVExport(regGraph[i]);
			GsNuSMVConfig config = new GsNuSMVConfig(regGraph[i]);
			
			super.fail("TODO: finish NuSMV export test");
			// String filename = "";
			// nusmvExport.doExport(filename);
		}
	}
}
