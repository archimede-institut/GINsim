package fr.univmrs.tagc.GINsim.export.regulatoryGraph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import junit.framework.TestCase;

import org.ginsim.graph.GinmlParser;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.service.export.nusmv.NuSMVConfig;
import org.ginsim.service.export.nusmv.NuSMVExport;


public class TestExportNuSMV extends TestCase {
	
	String[] sFiles;
	RegulatoryGraph[] regGraph;
	NuSMVExport nusmvExport;

	public TestExportNuSMV() throws FileNotFoundException {
		// sFiles = TestTools.getAllModels();
		assertEquals(sFiles != null, true);
		
		regGraph = new RegulatoryGraph[sFiles.length];
	}
	
	public void testLoadModels() throws FileNotFoundException {
		for (int i = 0; i < sFiles.length; i++) {
			System.out.println("[" + sFiles[i] + "]");
			File fModel = new File(sFiles[i]);
			GinmlParser parser = new GinmlParser();
			regGraph[i] = (RegulatoryGraph) parser.parse(new FileInputStream(fModel), null);
			
			nusmvExport = new NuSMVExport(regGraph[i]);
			NuSMVConfig config = new NuSMVConfig(regGraph[i]);
			
			super.fail("TODO: finish NuSMV export test");
			// String filename = "";
			// nusmvExport.doExport(filename);
		}
	}
}
