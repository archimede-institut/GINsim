package org.ginsim.service.tool.stablestates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.awt.Color;
import java.util.Set;
import java.util.Vector;

import org.ginsim.common.application.GsException;
import org.ginsim.common.application.OptionStore;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.EdgeAttributeReaderImpl;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.common.NodeAttributeReaderImpl;
import org.ginsim.core.graph.regulatorygraph.BasicRegulatoryGraphTest;
import org.ginsim.core.graph.regulatorygraph.RegulatoryEdgeSign;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.LogicalParameter;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;
import org.ginsim.core.graph.view.NodeBorder;
import org.ginsim.core.graph.view.NodeShape;
import org.ginsim.core.service.ServiceManager;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestStableStates {

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
		
		Vector<Graph> graph_list = new Vector( GraphManager.getInstance().getAllGraphs());
		
		if( graph_list != null && !graph_list.isEmpty()){
			
			for( Graph graph : graph_list){
				GraphManager.getInstance().close( graph);
			}
		}
	}
	
	@Test
	public void SimpleExampleTest() throws Exception {
		// Create a new RegulatoryGraph
		RegulatoryGraph regGraph = GraphManager.getInstance().getNewGraph();
		assertNotNull( "Create graph : the graph is null.", regGraph);

		// Add a node
		RegulatoryNode node_g0 = regGraph.addNode();
		RegulatoryNode node_g1 = regGraph.addNode();
		node_g1.setMaxValue((byte) 2, regGraph);
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
				
		LogicalParameter lp;
		//Create logical parameters for G0
		lp = new LogicalParameter(1); //G1:2 G0
		lp.addEdge(g1_g0.getEdge(1));
		lp.addEdge(g0_g0.getEdge(0));
		node_g0.addLogicalParameter(lp, true);
		lp = new LogicalParameter(1); //G1
		lp.addEdge(g1_g0.getEdge(0));
		node_g0.addLogicalParameter(lp, true);
		
		//Create logical parameters for G1
		lp = new LogicalParameter(1); //G1:2 G0
		lp.addEdge(g0_g1.getEdge(0));
		lp.addEdge(g1_g1.getEdge(0));
		node_g1.addLogicalParameter(lp, true);
		lp = new LogicalParameter(1); //G1
		lp.addEdge(g0_g1.getEdge(0));
		node_g1.addLogicalParameter(lp, true);
		
		//Get the stable states
		StableStateSearcher stableStateSearcher = ServiceManager.get(StableStatesService.class).getSearcher(regGraph);
        assertNotNull("The service didn't return any result", stableStateSearcher);
        
        //Get the OMDD containing the stable states
        OMDDNode root = stableStateSearcher.call();
        assertNotNull("The root of the OMDD is null", root);
        
        //Check the states 
        try {
        	assertEquals("The state 00 was expected to be stable", OMDDNode.TERMINALS[1], root.next[0].next[0]);
        	assertEquals("The state 01 was expected to be unstable", OMDDNode.TERMINALS[0], root.next[0].next[1]);
        	assertEquals("The states 1* was expected to be unstable", OMDDNode.TERMINALS[0], root.next[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
        	fail("Expected stable state(s) not found, one OMDD has no next");
        }
        
		GraphManager.getInstance().close( regGraph);

	}
	
}
