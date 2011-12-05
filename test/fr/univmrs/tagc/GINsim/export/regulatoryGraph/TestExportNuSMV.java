package fr.univmrs.tagc.GINsim.export.regulatoryGraph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import junit.framework.TestCase;

import org.ginsim.core.graph.GinmlParser;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.service.export.nusmv.NuSMVConfig;
import org.ginsim.service.export.nusmv.NuSMVEncoder;


public class TestExportNuSMV extends TestCase {
	
	String[] sFiles;
	RegulatoryGraph[] regGraph;
	NuSMVEncoder nusmvEncoder;

	public TestExportNuSMV() throws FileNotFoundException {
		// sFiles = TestTools.getAllModels();
		assertEquals(sFiles != null, true);
		
		regGraph = new RegulatoryGraph[sFiles.length];
	}
	
	/**
	 * 
	 * @throws FileNotFoundException
	 */
	public void testLoadModels() throws FileNotFoundException {
		for (int i = 0; i < sFiles.length; i++) {
			System.out.println("[" + sFiles[i] + "]");
			File fModel = new File(sFiles[i]);
			GinmlParser parser = new GinmlParser();
			regGraph[i] = (RegulatoryGraph) parser.parse( new FileInputStream(fModel), null);
			
			try{
				nusmvEncoder = new NuSMVEncoder();
				NuSMVConfig config = new NuSMVConfig( regGraph[i]);
				String filename = "";
				FileWriter writer = new FileWriter( filename);
				nusmvEncoder.write( config, writer);
			}
			catch( IOException ioe){
				super.fail("TODO: finish NuSMV export test");
			}
		}
	}
}
