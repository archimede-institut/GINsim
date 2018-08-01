package org.ginsim.service.tool.stablestates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.awt.Color;
import java.util.Iterator;
import java.util.Vector;

import org.colomoto.biolqm.tool.fixpoints.FixpointSearcher;
import org.colomoto.mddlib.PathSearcher;
import org.ginsim.common.application.GsException;
import org.ginsim.common.application.OptionStore;
import org.ginsim.core.graph.GSGraphManager;
import org.ginsim.core.graph.backend.EdgeAttributeReaderImpl;
import org.ginsim.core.graph.Graph;
import org.ginsim.core.graph.backend.NodeAttributeReaderImpl;
import org.ginsim.core.graph.regulatorygraph.BasicRegulatoryGraphTest;
import org.ginsim.core.graph.regulatorygraph.RegulatoryEdgeSign;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.LogicalParameter;
import org.ginsim.core.graph.view.NodeBorder;
import org.ginsim.core.graph.view.NodeShape;
import org.ginsim.core.service.GSServiceManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TestStableStates {

	@BeforeAll
	public static void beforeAllTests() {

		try {
			OptionStore.init(BasicRegulatoryGraphTest.class.getPackage()
					.getName());
			OptionStore.getOption(EdgeAttributeReaderImpl.EDGE_COLOR,
					new Integer(-13395457));
			OptionStore.getOption(NodeAttributeReaderImpl.VERTEX_BG,
					new Integer(-26368));
			OptionStore.getOption(NodeAttributeReaderImpl.VERTEX_FG,
					new Integer(Color.WHITE.getRGB()));
			OptionStore.getOption(NodeAttributeReaderImpl.VERTEX_HEIGHT,
					new Integer(30));
			OptionStore.getOption(NodeAttributeReaderImpl.VERTEX_WIDTH,
					new Integer(55));
			OptionStore.getOption(NodeAttributeReaderImpl.VERTEX_SHAPE,
					NodeShape.RECTANGLE.name());
			OptionStore.getOption(NodeAttributeReaderImpl.VERTEX_BORDER,
					NodeBorder.SIMPLE.name());
		} catch (Exception e) {
			fail("Initialisation of OptionStore failed : " + e);
		}
	}

	/**
	 * Try to remove all the registered graphs from the GraphManager after each
	 * test
	 * 
	 */
	@AfterEach
	public void afterEachTest() {

		Vector<Graph> graph_list = new Vector(GSGraphManager.getInstance()
				.getAllGraphs());

		if (graph_list != null && !graph_list.isEmpty()) {

			for (Graph graph : graph_list) {
				GSGraphManager.getInstance().close(graph);
			}
		}
	}

	@Test
	public void SimpleExampleTest() throws Exception {
		// Create a new RegulatoryGraph
		RegulatoryGraph regGraph = GSGraphManager.getInstance().getNewGraph();
		assertNotNull( regGraph, "Create graph : the graph is null.");

		// Add a node
		RegulatoryNode node_g0 = regGraph.addNode();
		RegulatoryNode node_g1 = regGraph.addNode();
		node_g1.setMaxValue((byte) 2, regGraph);
		RegulatoryMultiEdge g0_g1 = regGraph.addEdge(node_g0, node_g1,
				RegulatoryEdgeSign.POSITIVE);
		RegulatoryMultiEdge g0_g0 = regGraph.addEdge(node_g0, node_g0,
				RegulatoryEdgeSign.POSITIVE);
		RegulatoryMultiEdge g1_g0 = regGraph.addEdge(node_g1, node_g0,
				RegulatoryEdgeSign.POSITIVE);
		RegulatoryMultiEdge g1_g1 = regGraph.addEdge(node_g1, node_g1,
				RegulatoryEdgeSign.POSITIVE);
		try {
			regGraph.addNewEdge("G1", "G0", (byte) 2,
					RegulatoryEdgeSign.POSITIVE);
			regGraph.addNewEdge("G1", "G1", (byte) 2,
					RegulatoryEdgeSign.POSITIVE);
		} catch (GsException e) {
			fail("Cannot add a multiedge");
		}

		LogicalParameter lp;
		// Create logical parameters for G0
		lp = new LogicalParameter(1); // G1:2 G0
		lp.addEdge(g1_g0.getEdge(1));
		lp.addEdge(g0_g0.getEdge(0));
		node_g0.addLogicalParameter(lp, true);
		lp = new LogicalParameter(1); // G1
		lp.addEdge(g1_g0.getEdge(0));
		node_g0.addLogicalParameter(lp, true);

		// Create logical parameters for G1
		lp = new LogicalParameter(1); // G1:2 G0
		lp.addEdge(g0_g1.getEdge(0));
		lp.addEdge(g1_g1.getEdge(0));
		node_g1.addLogicalParameter(lp, true);
		lp = new LogicalParameter(1); // G1
		lp.addEdge(g0_g1.getEdge(0));
		node_g1.addLogicalParameter(lp, true);

		// Get the stable states
		FixpointSearcher stableStateSearcher = GSServiceManager.get(
				StableStatesService.class).getSearcher(regGraph);
		assertNotNull( stableStateSearcher, "The service didn't return any result");

		// Get the OMDD containing the stable states
		int root = stableStateSearcher.call();
		assertEquals(false, (root < 0));

		// Check the states
		PathSearcher ps = new PathSearcher(stableStateSearcher.getMDDManager(),
				1);
		int[] path = ps.setNode(root);
		assertEquals(2, path.length);
		System.out.println(root);
		Iterator<Integer> it = ps.iterator();

		int v = it.next();
		assertEquals(1, v);
		assertEquals(0, path[0]);
		assertEquals(0, path[0]);

		// no other path leads to 1
		assertEquals(false, it.hasNext());

		GSGraphManager.getInstance().close(regGraph);
	}

}
