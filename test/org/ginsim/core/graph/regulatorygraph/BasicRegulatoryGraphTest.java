package org.ginsim.core.graph.regulatorygraph;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.Color;
import java.util.Set;

import org.ginsim.common.OptionStore;
import org.ginsim.common.exception.GsException;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.EdgeAttributeReaderImpl;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.common.NodeAttributeReaderImpl;
import org.ginsim.core.graph.view.NodeBorder;
import org.ginsim.core.graph.view.NodeShape;


public class BasicRegulatoryGraphTest {

	
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
	 * Try to remove all the registered graphs from the GraphManager after each test
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
	 * Create, register and close graph using default method
	 * 
	 */
	@Test
	public void createAndCloseGraphWithDefaultMethodTest() {
		
		// Create a new RegulatoryGraph
		RegulatoryGraph graph = GraphManager.getInstance().getNewGraph();
		assertNotNull( "Create graph : the graph is null.", graph);
		
		int graph_list_count = GraphManager.getInstance().getAllGraphs().size();
		assertEquals( "Registering graph : graph was not unregistered.", 1, graph_list_count);
		
		// Close the graph
		GraphManager.getInstance().close( graph);
		
		graph_list_count = GraphManager.getInstance().getAllGraphs().size();
		assertEquals( "Unregistering graph : graph was not unregistered.", 0, graph_list_count);	
		
	}
	
	/**
	 * Create, register and close graph using specified class method
	 * 
	 */
	@Test
	public void createAndCloseGraphWithClassMethodTest() {
		
		// Create a new RegulatoryGraph
		RegulatoryGraph graph = GraphManager.getInstance().getNewGraph( RegulatoryGraph.class);
		assertNotNull( "Create graph : the graph is null.", graph);
		
		int graph_list_count = GraphManager.getInstance().getAllGraphs().size();
		assertEquals( "Registering graph : graph was not unregistered.", 1, graph_list_count);
		
		// Close the graph
		GraphManager.getInstance().close( graph);
		
		graph_list_count = GraphManager.getInstance().getAllGraphs().size();
		assertEquals( "Unregistering graph : graph was not unregistered.", 0, graph_list_count);
		
	}
	
	/**
	 * Create a node using default method and add/remove it to a graph 
	 * 
	 */
	@Test
	public void addAndRemoveNodeWithDefaultMethodTest() {
		
		// Create a new RegulatoryGraph
		RegulatoryGraph graph = GraphManager.getInstance().getNewGraph();
		assertNotNull( "Create graph : the graph is null.", graph);
		
		// Add a node
		RegulatoryNode node = graph.addNode();
		assertNotNull( "Create node : the node is null.", node);
		
		int node_count = graph.getNodeCount();
		if( node_count != 1){
			assertEquals( "Add node : the graph does not contains the right number of node.", 1, node_count);
		}
		
		// Remove the node
		graph.removeNode( node);
		node_count = graph.getNodeCount();
		if( node_count != 0){
			assertEquals( "Remove node : the graph does not contains the right number of node.", 0, node_count);
		}
	}
	
	/**
	 * Create a node using specific node constructor and add/remove it to a graph 
	 * 
	 */
	@Test
	public void addAndRemoveNodeWithClassMethodTest() {
		
		// Create a new RegulatoryGraph
		RegulatoryGraph graph = GraphManager.getInstance().getNewGraph();
		assertNotNull( "Create graph : the graph is null.", graph);
		
		// Add a node
		RegulatoryNode node = new RegulatoryNode( "G1", graph); 
		graph.addNode( node);
		
		int node_count = graph.getNodeCount();
		assertEquals( "Add node : the graph does not contains the right number of node.", 1, node_count);
		
		// Remove the node
		graph.removeNode( node);
		node_count = graph.getNodeCount();
		assertEquals( "Remove node : the graph does not contains the right number of node.", 0, node_count);
	}
	
	/**
	 * Create a Multiedge from node endpoints and add/remove it from a graph
	 * 
	 */
	@Test public void addAndRemoveEdgeWithEndpointsTest() {
		
		// Create a new RegulatoryGraph
		RegulatoryGraph graph = GraphManager.getInstance().getNewGraph();
		assertNotNull( "Create graph : the created graph is null: creation failed.", graph);
		
		// Add nodes
		RegulatoryNode node1 = new RegulatoryNode( "G1", graph); 
		graph.addNode( node1);
		
		RegulatoryNode node2 = new RegulatoryNode( "G2", graph); 
		graph.addNode( node2);
		
		// Add MultiEdge
		RegulatoryMultiEdge me = graph.addEdge(node1, node2, RegulatoryEdgeSign.POSITIVE);
		assertNotNull( "Add MultiEdge : the MultiEdge is null.", me);

		int multiedge_count = graph.getEdges().size();
		assertEquals( "Add MultiEdge : the Graph does not contains the right number of multiedges.", 1, multiedge_count);			

		int edge_count = me.getEdgeCount();
		assertEquals( "Add MultiEdge : the MultiEdge does not contains the right number of edges.", 1, edge_count);
		
		// Add new edge on MultiEdge
		try{
			graph.addNewEdge( "G1", "G2", (byte) 2, RegulatoryEdgeSign.POSITIVE);
		}
		catch( GsException gse){
			fail( "Add edge : Exception catched : " + gse);
		}
		edge_count = me.getEdgeCount();
		assertEquals( "Add edge : the MultiEdge does not contains the right number of edges.", 2, edge_count);
		
		// Remove MultiEdge
		graph.removeEdge( me);
		multiedge_count = graph.getEdges().size();
		assertEquals( "Remove MultiEdge : the graph does not contains the right number of edges.", 0, multiedge_count);
		
		
	}
	

	

}
