package org.ginsim.core.graph.regulatorygraph;

import static org.junit.jupiter.api.Assertions.*;

import org.colomoto.mddlib.MDDManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ginsim.common.application.GsException;
import org.ginsim.common.application.OptionStore;
import org.ginsim.core.graph.GSGraphManager;
import org.ginsim.core.graph.backend.EdgeAttributeReaderImpl;
import org.ginsim.core.graph.Graph;
import org.ginsim.core.graph.backend.NodeAttributeReaderImpl;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.LogicalParameter;
import org.ginsim.core.graph.view.NodeBorder;
import org.ginsim.core.graph.view.NodeShape;


public class BasicRegulatoryGraphTest {

	
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
	 * Try to remove all the registered graphs from the GraphManager after each test
	 * 
	 */
	@AfterEach
	public void afterEachTest(){
		
		Set<Graph> graph_list = GSGraphManager.getInstance().getAllGraphs();
		
		if( graph_list != null && !graph_list.isEmpty()){
			List<Graph> graphs = new ArrayList<>(graph_list.size());
			graphs.addAll(graph_list);
			for( Graph graph : graphs){
				GSGraphManager.getInstance().close( graph);
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
		int old_graph_list_count = GSGraphManager.getInstance().getAllGraphs().size();
		RegulatoryGraph graph = GSGraphManager.getInstance().getNewGraph();
		assertNotNull( graph, "Create graph : the graph is null.");
		
		int graph_list_count = GSGraphManager.getInstance().getAllGraphs().size();
		assertEquals( old_graph_list_count+1, graph_list_count, "Registering graph : graph was not unregistered.");
		
		// Close the graph
		GSGraphManager.getInstance().close( graph);
		
		graph_list_count = GSGraphManager.getInstance().getAllGraphs().size();
		assertEquals( old_graph_list_count, graph_list_count, "Unregistering graph : graph was not unregistered.");
		
	}
	
	/**
	 * Create, register and close graph using specified class method
	 *
	 */
	@Test
	public void createAndCloseGraphWithClassMethodTest() {
		
		// Create a new RegulatoryGraph
		RegulatoryGraph graph = GSGraphManager.getInstance().getNewGraph( RegulatoryGraph.class);
		assertNotNull( graph, "Create graph : the graph is null.");
		
		int graph_list_count = GSGraphManager.getInstance().getAllGraphs().size();
		assertEquals( 1, graph_list_count, "Registering graph : graph was not unregistered.");
		
		// Close the graph
		GSGraphManager.getInstance().close( graph);
		
		graph_list_count = GSGraphManager.getInstance().getAllGraphs().size();
		assertEquals( 0, graph_list_count, "Unregistering graph : graph was not unregistered.");
		
	}
	
	/**
	 * Create a node using default method and add/remove it to a graph 
	 * 
	 */
	@Test
	public void addAndRemoveNodeWithDefaultMethodTest() {
		
		// Create a new RegulatoryGraph
		RegulatoryGraph graph = GSGraphManager.getInstance().getNewGraph();
		assertNotNull( graph, "Create graph : the graph is null.");
		
		// Add a node
		RegulatoryNode node = graph.addNode();
		assertNotNull( node, "Create node : the node is null.");
		
		int node_count = graph.getNodeCount();
		assertEquals( 1, node_count, "Add node : the graph does not contains the right number of node.");
		int nodeorder_count = graph.getNodeOrderSize();
		assertEquals( 1, nodeorder_count, "Add node : the NodeOrder does not contains the right number of node.");
		
		// Remove the node
		graph.removeNode( node);
		node_count = graph.getNodeCount();
		assertEquals( 0, node_count, "Remove node : the graph does not contains the right number of node.");
	}
	
	/**
	 * Create a node using specific node constructor and add/remove it to a graph 
	 * 
	 */
	@Test
	public void addAndRemoveNodeWithClassMethodTest() {
		
		// Create a new RegulatoryGraph
		RegulatoryGraph graph = GSGraphManager.getInstance().getNewGraph();
		assertNotNull( graph, "Create graph : the graph is null.");
		
		// Add a node
		RegulatoryNode node = new RegulatoryNode( "G1", graph); 
		graph.addNode( node);
		
		int node_count = graph.getNodeCount();
		assertEquals( 1, node_count, "Add node : the graph does not contains the right number of node.");
		int nodeorder_count = graph.getNodeOrderSize();
		assertEquals( 1, nodeorder_count, "Add node : the NodeOrder does not contains the right number of node.");
		
		// Remove the node
		graph.removeNode( node);
		node_count = graph.getNodeCount();
		assertEquals( 0, node_count, "Remove node : the graph does not contains the right number of node.");
	}
	
	/**
	 * Create a Multiedge from node endpoints and add/remove it from a graph
	 * 
	 */
	@Test
	public void addAndRemoveEdgeWithEndpointsTest() {
		
		// Create a new RegulatoryGraph
		RegulatoryGraph graph = GSGraphManager.getInstance().getNewGraph();
		assertNotNull( graph, "Create graph : the created graph is null: creation failed.");
		
		// Add nodes
		RegulatoryNode node1 = new RegulatoryNode( "G1", graph); 
		graph.addNode( node1);
		
		RegulatoryNode node2 = new RegulatoryNode( "G2", graph); 
		graph.addNode( node2);
		
		// Add MultiEdge
		RegulatoryMultiEdge me = graph.addEdge(node1, node2, RegulatoryEdgeSign.POSITIVE);
		assertNotNull( me, "Add MultiEdge : the MultiEdge is null.");

		int multiedge_count = graph.getEdges().size();
		assertEquals( 1, multiedge_count, "Add MultiEdge : the Graph does not contains the right number of multiedges.");

		int edge_count = me.getEdgeCount();
		assertEquals( 1, edge_count, "Add MultiEdge : the MultiEdge does not contains the right number of edges.");
		
		// Add new edge on MultiEdge
		try{
			graph.addNewEdge( "G1", "G2", (byte) 2, RegulatoryEdgeSign.POSITIVE);
		}
		catch( GsException gse){
			fail( "Add edge : Exception catched : " + gse);
		}
		edge_count = me.getEdgeCount();
		assertEquals( 2, edge_count, "Add edge : the MultiEdge does not contains the right number of edges.");
		
		// Remove MultiEdge
		graph.removeEdge( me);
		multiedge_count = graph.getEdges().size();
		assertEquals( 0, multiedge_count, "Remove MultiEdge : the graph does not contains the right number of edges.");
		
		
	}
	
	/**
	 * Create two multivaluated nodes with a Multiedge and some logical parameters.
	 * 
	 */
	@Test public void addLogicalParametersTest() {
		
		// Create a new RegulatoryGraph
		RegulatoryGraph regGraph = GSGraphManager.getInstance().getNewGraph();
		assertNotNull( regGraph, "Create graph : the created graph is null: creation failed.");
		
		// Add nodes G0 and G1
		RegulatoryNode node_g0 = regGraph.addNode();
		RegulatoryNode node_g1 = regGraph.addNode();
		node_g1.setMaxValue((byte) 2, regGraph);

		// Add MultiEdge
		RegulatoryMultiEdge g0_g1 = regGraph.addEdge(node_g0, node_g1, RegulatoryEdgeSign.POSITIVE);
		RegulatoryMultiEdge g0_g0 = regGraph.addEdge(node_g0, node_g0, RegulatoryEdgeSign.POSITIVE);
		RegulatoryMultiEdge g1_g0 = regGraph.addEdge(node_g1, node_g0, RegulatoryEdgeSign.POSITIVE);
		RegulatoryMultiEdge g1_g1 = regGraph.addEdge(node_g1, node_g1, RegulatoryEdgeSign.POSITIVE);
		try {
			regGraph.addNewEdge( "G1", "G0", (byte) 2, RegulatoryEdgeSign.POSITIVE);
			regGraph.addNewEdge( "G1", "G1", (byte) 2, RegulatoryEdgeSign.POSITIVE);
		} catch (GsException e) {
			fail("Cannot add a multiedge");
		}

		//Testing the default parameters added to the G0
        MDDManager ddmanager = regGraph.getMDDFactory();
		int mdd = node_g0.getMDD(regGraph, ddmanager);
		assertEquals( 0, mdd, "Initial parameter is not 0");
		
		
		LogicalParameter lp;
		//Create logical parameters for G0
		lp = new LogicalParameter(1); //G1:2 G0
		lp.addEdge(g1_g0.getEdge(1));
		lp.addEdge(g0_g0.getEdge(0));
		node_g0.addLogicalParameter(lp, true);
		lp = new LogicalParameter(1); //G1
		lp.addEdge(g1_g0.getEdge(0));
		node_g0.addLogicalParameter(lp, true);
        mdd = node_g0.getMDD(regGraph, ddmanager);
        // FIXME: check MDD
        //	((N[0]=0 && ((N[1]=0 && 0) ; (N[1]=1 && 1) ; (N[1]=2 && 0))) ; (N[0]=1 && ((N[1]=0 && 0) ; (N[1]=1 && 0) ; (N[1]=2 && 1))))

		//Create logical parameters for G1
		lp = new LogicalParameter(1); //G1:2 G0
		lp.addEdge(g0_g1.getEdge(0));
		lp.addEdge(g1_g1.getEdge(0));
		node_g1.addLogicalParameter(lp, true);
		lp = new LogicalParameter(1); //G1
		lp.addEdge(g0_g1.getEdge(0));
		node_g1.addLogicalParameter(lp, true);
        mdd = node_g1.getMDD(regGraph, ddmanager);
        // FIXME: check MDD
        // ((N[0]=0 && 0) ; (N[0]=1 && ((N[1]=0 && 1) ; (N[1]=1 && 1) ; (N[1]=2 && 0))))

		
		GSGraphManager.getInstance().close( regGraph);
	}

}
