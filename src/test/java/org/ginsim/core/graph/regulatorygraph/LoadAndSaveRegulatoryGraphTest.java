package org.ginsim.core.graph.regulatorygraph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.ginsim.TestFileUtils;
import org.ginsim.TestUtils;
import org.ginsim.common.application.GsException;
import org.ginsim.core.graph.GSGraphManager;
import org.ginsim.core.graph.Graph;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LoadAndSaveRegulatoryGraphTest {

	private static final String module = "RegulatoryGraph";

	/**
	 * Initialize the OptionStore is required
	 * 
	 */
	@BeforeAll
	public static void beforeAllTests(){
		
		TestUtils.initOptionStore();
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
	 * Try to remove all the registered graphs from the GraphManager after each test
	 * Remove also the test files temporary directory
	 * 
	 */
	@AfterEach
	public void afterEachTest(){
		
		Set<Graph> graph_list = GSGraphManager.getInstance().getAllGraphs();
		if( graph_list != null && !graph_list.isEmpty()){
			
			List<Graph> graphs = new ArrayList<Graph>(graph_list);
			for( Graph graph : graphs){
				GSGraphManager.getInstance().close( graph);
			}
		}
		
	}
	
	/**
	 * Test to build and save a graph
	 * 
	 */
	@Test
	public void saveGraphTest() {
	
		saveGraph();
	}
	
	/**
	 * Test to load a graph from a predefined file 
	 * 
	 */
	@Test
	public void loadGraphTest(){
		
		File file = new File( TestFileUtils.getTestFileDirectory( module), "loadGraphTest.zginml");
		
		loadGraph( file, 4, 7);
		
	}
	
	/**
	 * Test to build a graph, save it and load it
	 * 
	 */
	@Test
	public void saveAndLoadTest(){
		
		File file = saveGraph();
		loadGraph( file, 2, 1);
	}
	
	/**
	 * Build and save a graph to a file
	 * 
	 * @return the File where the graph was saved
	 */
	private File saveGraph(){
		
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
		
		// Save the file
		try{
			File file = new File( TestFileUtils.getTempTestFileDirectory( module), graph.getGraphName());
			graph.save( file.getPath());
			if( !file.exists()){
				fail( "Save graph : the graph file was not created.");
			}
			
			return file;
		}
		catch ( GsException gse) {
			fail( "Save graph : Exception catched : " + gse);
			return null;
		}
		
	}
	
	/**
	 * Load a graph from the given file
	 * 
	 * @param file the file to load
	 */
	private void loadGraph( File file, int expectedNodes, int expectedEdges){
		
		Graph graph = TestFileUtils.loadGraph(file);
		
		assertNotNull( graph, "Load graph : graph is null");
		assertEquals( expectedNodes, graph.getNodeCount(), "Load graph : Graph node number is not correct");
		assertEquals( expectedEdges, graph.getEdges().size(), "Load graph : Graph edge number is not correct");
	}

}
