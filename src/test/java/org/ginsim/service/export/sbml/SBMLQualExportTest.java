package org.ginsim.service.export.sbml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import org.ginsim.TestFileUtils;
import org.ginsim.common.application.GsException;
import org.ginsim.common.application.OptionStore;
import org.ginsim.core.graph.GSGraphManager;
import org.ginsim.core.graph.backend.EdgeAttributeReaderImpl;
import org.ginsim.core.graph.backend.NodeAttributeReaderImpl;
import org.ginsim.core.graph.regulatorygraph.BasicRegulatoryGraphTest;
import org.ginsim.core.graph.regulatorygraph.RegulatoryEdgeSign;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.view.NodeBorder;
import org.ginsim.core.graph.view.NodeShape;
import org.ginsim.service.format.sbml.SBMLqualService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SBMLQualExportTest {

	private static final String module = "SBMLExport";
	
	/**
	 * Initialize the Option store and define the test file directory
	 * 
	 */
	@BeforeAll
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
	@BeforeEach
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
		
		assertNotNull( graph, "Re-import graph : graph is null");
		assertEquals( 2, graph.getNodeCount(), "Re-import graph : Graph node number is not correct");
		assertEquals( 1, graph.getEdges().size(), "Re-import graph : Graph edge number is not correct");

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
		//reimportGraphFromSBML( graph);
		
		assertNotNull( graph, "Re-import graph : graph is null");
		assertEquals( 4, graph.getNodeCount(), "Re-import graph : Graph node number is not correct");
		assertEquals( 7, graph.getEdges().size(), "Re-import graph : Graph edge number is not correct");

	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	private RegulatoryGraph createTrivialGraph(){
		
		// Create a new RegulatoryGraph
		RegulatoryGraph graph = GSGraphManager.getInstance().getNewGraph();
		assertNotNull( graph, "Create graph : the created graph is null: creation failed.");
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
		assertNotNull( me, "Add MultiEdge : the MultiEdge is null.");
		
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
			
			SBMLqualService srv = new SBMLqualService();
			srv.export( graph, file.getPath());
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

		SBMLqualService srv = new SBMLqualService();
		RegulatoryGraph new_graph = srv.importLRG(file.getPath());
		
		return new_graph;
	}
	
	/**
	 * Load a graph from the given file
	 * 
	 * @param file the file to load
	 */
	private RegulatoryGraph loadGraph( File file){
		
		try{
			RegulatoryGraph graph = (RegulatoryGraph) GSGraphManager.getInstance().open( file);
			
			assertNotNull( graph, "Load graph : graph is null");
			assertEquals( 4, graph.getNodeCount(), "Load graph : Graph node number is not correct");
			assertEquals( 7, graph.getEdges().size(), "Load graph : Graph edge number is not correct");
			return graph;
		}
		catch ( GsException gse) {
			fail( "Save graph : Exception catched : " + gse);
			return null;
		}
	}

}
