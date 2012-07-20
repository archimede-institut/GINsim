package fr.univmrs.tagc.GINsim.export.regulatoryGraph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import org.ginsim.common.application.GsException;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.io.parser.GinmlParser;
import org.ginsim.service.export.nusmv.NuSMVConfig;
import org.ginsim.service.export.nusmv.NuSMVEncoder;
import org.junit.Assert;
import org.junit.Test;


public class TestExportNuSMV {
	
	String[] sFiles;
	RegulatoryGraph[] regGraph;
	NuSMVEncoder nusmvEncoder;

	public TestExportNuSMV() throws FileNotFoundException {
		// sFiles = TestTools.getAllModels();
		Assert.assertEquals(sFiles != null, true);
		
		regGraph = new RegulatoryGraph[sFiles.length];
	}
	
	/**
	 * 
	 * @throws FileNotFoundException
	 */
	@Test
	public void testLoadModels() throws FileNotFoundException, GsException {
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
				Assert.fail("TODO: finish NuSMV export test");
			}
		}
	}
}
