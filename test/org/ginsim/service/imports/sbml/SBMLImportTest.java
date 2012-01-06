package org.ginsim.service.imports.sbml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.awt.Color;
import java.io.File;

import org.ginsim.common.OptionStore;
import org.ginsim.core.TestFileUtils;
import org.ginsim.core.graph.common.EdgeAttributeReaderImpl;
import org.ginsim.core.graph.common.NodeAttributeReaderImpl;
import org.ginsim.core.graph.regulatorygraph.BasicRegulatoryGraphTest;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.view.NodeBorder;
import org.ginsim.core.graph.view.NodeShape;
import org.junit.BeforeClass;
import org.junit.Test;

public class SBMLImportTest {

	private static final String module = "SBMLImport";
	
	
	/**
	 * Initialize the OPtion store and define the test file directory
	 * 
	 */
	@BeforeClass
	public static void beforeAllTests(){
		
		try {
			OptionStore.init( BasicRegulatoryGraphTest.class.getPackage().getName());
	    	OptionStore.getOption( EdgeAttributeReaderImpl.EDGE_COLOR, new Integer(-13395457));
	    	OptionStore.getOption( NodeAttributeReaderImpl.VERTEX_BG, new Integer(-26368));
	    	OptionStore.getOption( NodeAttributeReaderImpl.VERTEX_FG, new Integer(Color.WHITE.getRGB()));
	    	OptionStore.getOption( NodeAttributeReaderImpl.VERTEX_HEIGHT, new Integer(30));
	    	OptionStore.getOption( NodeAttributeReaderImpl.VERTEX_WIDTH, new Integer(55));
	    	OptionStore.getOption( NodeAttributeReaderImpl.VERTEX_SHAPE, NodeShape.RECTANGLE.name());
	    	OptionStore.getOption( NodeAttributeReaderImpl.VERTEX_BORDER, NodeBorder.SIMPLE.name());
		} catch (Exception e) {
			fail( "Initialisation of OptionStore failed : " + e);
		}
		
	}
	
	/**
	 * Test to import a graph from a predefined SBML file 
	 * 
	 */
	@Test
	public void importSBMLGraphTest(){
		
		File file = new File( TestFileUtils.getTestFileDirectory( module), "importGraphTest.sbml");
		
		SBMLXpathParser parser = new SBMLXpathParser( file.getPath());
		RegulatoryGraph graph = parser.getGraph();
		
		assertNotNull( "Import graph : graph is null", graph);
		assertEquals( "Import graph : Graph node number is not correct", 4, graph.getNodeCount());
		assertEquals( "Import graph : Graph edge number is not correct", 7, graph.getEdges().size());
		
	}

}
