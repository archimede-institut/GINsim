package org.ginsim.core.graph.dynamicgraph;

import static org.junit.Assert.*;

import java.awt.Color;
import java.util.Set;

import org.ginsim.common.OptionStore;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.EdgeAttributeReaderImpl;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.common.NodeAttributeReaderImpl;
import org.ginsim.core.graph.view.NodeBorder;
import org.ginsim.core.graph.view.NodeShape;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

public class BasicDynamicGraphTest {
	
	@BeforeClass
	public static void beforeAllTests(){
		
		try {
			OptionStore.init( BasicDynamicGraphTest.class.getPackage().getName());
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
	 * Create, register and close graph using specified class method
	 * 
	 */
	@Test
	public void createAndCloseGraphTest() {
		
		// Create a new DynamicGraph
		DynamicGraph graph = GraphManager.getInstance().getNewGraph( DynamicGraph.class);
		assertNotNull( "Create graph : the graph is null.", graph);
		
		int graph_list_count = GraphManager.getInstance().getAllGraphs().size();
		assertEquals( "Registering graph : graph was not unregistered.", 1, graph_list_count);
		
		// Close the graph
		GraphManager.getInstance().close( graph);
		
		graph_list_count = GraphManager.getInstance().getAllGraphs().size();
		assertEquals( "Unregistering graph : graph was not unregistered.", 0, graph_list_count);
		
	}
	
	/**
	 * Create a node using specific node constructor and add/remove it to a graph 
	 * 
	 */
	@Test
	public void addAndRemoveNodeTest() {
		
		// Create a new DynamicGraph
		DynamicGraph graph = GraphManager.getInstance().getNewGraph( DynamicGraph.class);
		assertNotNull( "Create graph : the graph is null", graph);
		
		// Add a node
		DynamicNode node = new DynamicNode( "G1"); 
		graph.addNode( node);
		
		int node_count = graph.getNodeCount();
		assertEquals( "Add node : the graph does not contains the right number of node.", 1, node_count);
		
		// Remove the node
		graph.removeNode( node);
		node_count = graph.getNodeCount();
		assertEquals( "Remove node : the graph does not contains the right number of node.", 0, node_count);
	}
	
	/**
	 * Create a edge from node endpoints and add/remove it from a graph
	 * 
	 */
	@Test public void addAndRemoveEdgeWithEndpointsTest() {
		
		// Create a new RegulatoryGraph
		DynamicGraph graph = GraphManager.getInstance().getNewGraph( DynamicGraph.class);
		assertNotNull( "Create graph : the created graph is null: creation failed.", graph);
		
		// Add nodes
		DynamicNode node1 = new DynamicNode( "G1"); 
		graph.addNode( node1);
		
		DynamicNode node2 = new DynamicNode( "G2"); 
		graph.addNode( node2);
		
		// Add Edge
		Edge<DynamicNode> edge = new Edge<DynamicNode>( node1, node2);
		graph.addEdge( edge);
		
		int edge_count = graph.getEdges().size();
		assertEquals( "Add Edge : the Graph does not contains the right number of edges.", 1, edge_count);			
		
		// Remove Edge
		graph.removeEdge( edge);
		edge_count = graph.getEdges().size();
		assertEquals( "Remove Edge : the graph does not contains the right number of edges.", 0, edge_count);
		
		
	}
	


}
