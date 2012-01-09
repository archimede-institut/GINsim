package org.ginsim.core.graph.regulatorygraph;

import static org.junit.Assert.*;

import java.awt.Color;
import java.io.File;
import java.util.Set;

import org.ginsim.TestFileUtils;
import org.ginsim.common.OptionStore;
import org.ginsim.common.exception.GsException;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.EdgeAttributeReaderImpl;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.common.NodeAttributeReaderImpl;
import org.ginsim.core.graph.view.NodeBorder;
import org.ginsim.core.graph.view.NodeShape;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class LoadAndSaveRegulatoryGraphTest {

	private static final String module = "RegulatoryGraph";

	
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
	 * Try to remove all the registered graphs from the GraphManager after each test
	 * Remove also the test files temporary directory
	 * 
	 */
	@After
	public void afterEachTest(){
		
		Set<Graph> graph_list = GraphManager.getInstance().getAllGraphs();
		
		if( graph_list != null && !graph_list.isEmpty()){
			
			for( Graph graph : graph_list){
				GraphManager.getInstance().close( graph);
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
		
		loadGraph( file);
		
	}
	
	/**
	 * Test to build a graph, save it and load it
	 * 
	 */
	@Test
	public void saveAndLoadTest(){
		
		File file = saveGraph();
		loadGraph( file);
	}
	
	/**
	 * Build and save a graph to a file
	 * 
	 * @return the File where the graph was saved
	 */
	private File saveGraph(){
		
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
	private void loadGraph( File file){
		
		try{
			Graph graph = GraphManager.getInstance().open( file);
			
			assertNotNull( "Load graph : graph is null", graph);
			assertEquals( "Load graph : Graph node number is not correct", 4, graph.getNodeCount());
			assertEquals( "Load graph : Graph edge number is not correct", 7, graph.getEdges().size());
		}
		catch ( GsException gse) {
			fail( "Save graph : Exception catched : " + gse);
		}
	}

}
