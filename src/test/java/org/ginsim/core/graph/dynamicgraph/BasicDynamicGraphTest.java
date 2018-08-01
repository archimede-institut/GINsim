package org.ginsim.core.graph.dynamicgraph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.awt.Color;
import java.util.Set;

import org.ginsim.common.application.OptionStore;
import org.ginsim.core.graph.GSGraphManager;
import org.ginsim.core.graph.backend.EdgeAttributeReaderImpl;
import org.ginsim.core.graph.backend.NodeAttributeReaderImpl;
import org.ginsim.core.graph.view.NodeBorder;
import org.ginsim.core.graph.view.NodeShape;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class BasicDynamicGraphTest {
	
	@BeforeAll
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
	 * Create, register and close graph using specified class method
	 * 
	 */
	@Test
	public void createAndCloseGraphTest() {
		
		// Create a new DynamicGraph
		DynamicGraph graph = GSGraphManager.getInstance().getNewGraph( DynamicGraph.class);
		assertNotNull( graph,  "Create graph : the graph is null.");
		
		Set graph_list = GSGraphManager.getInstance().getAllGraphs();
		if( !graph_list.contains( graph)){
			fail( "Registering graph : graph was not registered.");
		}
		
		// Close the graph
		GSGraphManager.getInstance().close( graph);
		
		graph_list = GSGraphManager.getInstance().getAllGraphs();
		if( graph_list.contains( graph)){
			fail( "Unregistering graph : graph was not unregistered.");
		}
		
	}
	
	/**
	 * Create a node using specific node constructor and add/remove it to a graph 
	 * 
	 */
	@Test
	public void addAndRemoveNodeTest() {
		
		// Create a new DynamicGraph
		DynamicGraph graph = GSGraphManager.getInstance().getNewGraph( DynamicGraph.class);
		assertNotNull( graph, "Create graph : the graph is null");
		
		// Add a node
		DynamicNode node = new DynamicNode( "G1"); 
		graph.addNode( node);
		
		int node_count = graph.getNodeCount();
		assertEquals( 1, node_count, "Add node : the graph does not contains the right number of node.");
		
		// Remove the node
		graph.removeNode( node);
		node_count = graph.getNodeCount();
		assertEquals( 0, node_count, "Remove node : the graph does not contains the right number of node.");
	}
	
	/**
	 * Create a edge from node endpoints and add/remove it from a graph
	 * Note : edge removing is not tested because it is not implemented on DynamicGraph
	 * 
	 */
	@Test
	public void addEdgeWithEndpointsTest() {
		
		// Create a new RegulatoryGraph
		DynamicGraph graph = GSGraphManager.getInstance().getNewGraph( DynamicGraph.class);
		assertNotNull( graph, "Create graph : the created graph is null: creation failed.");
		
		// Add nodes
		DynamicNode node1 = new DynamicNode( "G1"); 
		graph.addNode( node1);
		
		DynamicNode node2 = new DynamicNode( "G2"); 
		graph.addNode( node2);
		
		// Add Edge
		graph.addEdge(node1, node2, false);
		
		int edge_count = graph.getEdges().size();
		assertEquals( 1, edge_count, "Add Edge : the Graph does not contains the right number of edges.");
		
	}
	


}
