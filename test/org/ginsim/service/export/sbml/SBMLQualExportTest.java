package org.ginsim.service.export.sbml;

import static org.junit.Assert.*;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import org.ginsim.TestFileUtils;
import org.ginsim.common.OptionStore;
import org.ginsim.common.exception.GsException;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.EdgeAttributeReaderImpl;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.common.NodeAttributeReaderImpl;
import org.ginsim.core.graph.regulatorygraph.BasicRegulatoryGraphTest;
import org.ginsim.core.graph.regulatorygraph.RegulatoryEdgeSign;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.view.NodeBorder;
import org.ginsim.core.graph.view.NodeShape;
import org.ginsim.service.imports.sbml.SBMLXpathParser;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SBMLQualExportTest {

	private static final String module = "SBMLExport";
	
	/**
	 * Initialize the Option store and define the test file directory
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
	 * Clean the test file temp directory
	 * 
	 */
	@Before
	public void beforeEachTest(){
		
		TestFileUtils.cleanTempTestFileDirectory( module);
	}
	
	/**
	 * Test to export an re-import a trivial graph builded from commands
	 * 
	 */
	@Test
	public void exportAndImportTrivialSBMLGraphTest(){
	
		RegulatoryGraph graph = createTrivialGraph();
		exportGraphToSBML( graph);
		reimportGraphFromSBML( graph);
		
		assertNotNull( "Re-import graph : graph is null", graph);
		assertEquals( "Re-import graph : Graph node number is not correct", 2, graph.getNodeCount());
		assertEquals( "Re-import graph : Graph edge number is not correct", 1, graph.getEdges().size());

	}
	
	/**
	 * Test to export an re-import a graph loaded from a ginml file
	 * 
	 */
	@Test
	public void exportAndImportSBMLGraphTest(){
	
		File file = new File( TestFileUtils.getTestFileDirectory( module), "exportGraphTest.zginml");
		RegulatoryGraph graph = loadGraph( file);
		exportGraphToSBML( graph);
		reimportGraphFromSBML( graph);
		
		assertNotNull( "Re-import graph : graph is null", graph);
		assertEquals( "Re-import graph : Graph node number is not correct", 4, graph.getNodeCount());
		assertEquals( "Re-import graph : Graph edge number is not correct", 7, graph.getEdges().size());

	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	private RegulatoryGraph createTrivialGraph(){
		
		// Create a new RegulatoryGraph
		RegulatoryGraph graph = GraphManager.getInstance().getNewGraph();
		assertNotNull( "Create graph : the created graph is null: creation failed.", graph);
		try {
			graph.setGraphName( "saveGraphTest");
		} catch( GsException gse) {
			fail( "Create graph : Exception catched : " + gse);
		}
		
		// Add nodes
		RegulatoryNode node1 = new RegulatoryNode( "G0", graph); 
		graph.addNode( node1);
		
		RegulatoryNode node2 = new RegulatoryNode( "G1", graph); 
		graph.addNode( node2);
		
		// Add MultiEdge
		RegulatoryMultiEdge me = graph.addEdge(node1, node2, RegulatoryEdgeSign.POSITIVE);
		assertNotNull( "Add MultiEdge : the MultiEdge is null.", me);
		
		// Add new edge on MultiEdge
		try{
			graph.addNewEdge( "G0", "G1", (byte) 2, RegulatoryEdgeSign.POSITIVE);
		}
		catch( GsException gse){
			fail( "Add edge : Exception catched : " + gse);
		}
		
		return graph;
	}
	
	
	/**
	 * Export the given graph to SBML file
	 * 
	 * @param graph
	 */
	private void exportGraphToSBML( RegulatoryGraph graph){
		
		try{
			File file = new File( TestFileUtils.getTempTestFileDirectory( module), graph.getGraphName());
			
			SBMLQualEncoder encoder = new SBMLQualEncoder( );
			encoder.doExport( graph, new SBMLQualConfig(graph), file.getPath());
			if( !file.exists()){
				fail( "Export graph : the graph file was not created.");
			}
			
		}
		catch ( IOException ioe) {
			fail( "Export graph : Exception catched : " + ioe);
		}
	}
	
	/**
	 * Re-import the given graph that was previously exported 
	 * 
	 * @param graph the graph to re-import
	 */
	private RegulatoryGraph reimportGraphFromSBML( RegulatoryGraph graph){
		
		// import the graph
		File file = new File( TestFileUtils.getTempTestFileDirectory( module), graph.getGraphName());
		
		SBMLXpathParser parser = new SBMLXpathParser( file.getPath());
		RegulatoryGraph new_graph = parser.getGraph();
		
		return new_graph;
	}
	
	/**
	 * Load a graph from the given file
	 * 
	 * @param file the file to load
	 */
	private RegulatoryGraph loadGraph( File file){
		
		try{
			RegulatoryGraph graph = (RegulatoryGraph) GraphManager.getInstance().open( file);
			
			assertNotNull( "Load graph : graph is null", graph);
			assertEquals( "Load graph : Graph node number is not correct", 4, graph.getNodeCount());
			assertEquals( "Load graph : Graph edge number is not correct", 7, graph.getEdges().size());
			return graph;
		}
		catch ( GsException gse) {
			fail( "Save graph : Exception catched : " + gse);
			return null;
		}
	}

}
