package fr.univmrs.tagc.GINsim.export.regulatoryGraph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import junit.framework.TestCase;
import fr.univmrs.tagc.GINsim.export.GsExportConfig;
import fr.univmrs.tagc.GINsim.graph.GsGinmlParser;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.common.TestTools;

public class TestExportNuSMV extends TestCase {
	
	String[] sFiles;
	GsRegulatoryGraph[] regGraph;
	GsNuSMVExport nusmvExport;

	public TestExportNuSMV() throws FileNotFoundException {
		sFiles = TestTools.getAllModels();
		assertEquals(sFiles != null, true);
		
		regGraph = new GsRegulatoryGraph[sFiles.length];
		nusmvExport = new GsNuSMVExport();
	}
	
	public void testLoadModels() throws FileNotFoundException {
		for (int i = 0; i < sFiles.length; i++) {
			System.out.println("[" + sFiles[i] + "]");
			File fModel = new File(sFiles[i]);
			GsGinmlParser parser = new GsGinmlParser();
			regGraph[i] = (GsRegulatoryGraph) parser.parse(new FileInputStream(fModel), null);
			
			GsExportConfig nusmvConfig = new GsExportConfig(regGraph[i], nusmvExport, 0);
			nusmvConfig.setSpecificConfig(new GsNuSMVConfig(regGraph[i]));
			nusmvExport.doExport(nusmvConfig);
		}
	}
}
