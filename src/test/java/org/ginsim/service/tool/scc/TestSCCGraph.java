package org.ginsim.service.tool.scc;

import org.ginsim.common.application.OptionStore;
import org.ginsim.core.graph.GSGraphManager;
import org.ginsim.core.graph.backend.EdgeAttributeReaderImpl;
import org.ginsim.core.graph.backend.NodeAttributeReaderImpl;
import org.ginsim.core.graph.reducedgraph.NodeReducedData;
import org.ginsim.core.graph.reducedgraph.ReducedGraph;
import org.ginsim.core.graph.regulatorygraph.BasicRegulatoryGraphTest;
import org.ginsim.core.graph.regulatorygraph.RegulatoryEdgeSign;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.view.NodeBorder;
import org.ginsim.core.graph.view.NodeShape;
import org.ginsim.core.service.GSServiceManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestSCCGraph {
	static RegulatoryGraph regGraph;

	@BeforeAll
	public static void setUp() {
		regGraph = GSGraphManager.getInstance().getNewGraph();
		try {
			OptionStore.init(BasicRegulatoryGraphTest.class.getPackage()
					.getName());
			OptionStore.getOption(EdgeAttributeReaderImpl.EDGE_COLOR, new Integer(-13395457));
			OptionStore.getOption(NodeAttributeReaderImpl.VERTEX_BG, new Integer(-26368));
			OptionStore.getOption(NodeAttributeReaderImpl.VERTEX_FG, new Integer(Color.WHITE.getRGB()));
			OptionStore.getOption(NodeAttributeReaderImpl.VERTEX_HEIGHT, new Integer(30));
			OptionStore.getOption(NodeAttributeReaderImpl.VERTEX_WIDTH, new Integer(55));
			OptionStore.getOption(NodeAttributeReaderImpl.VERTEX_SHAPE, NodeShape.RECTANGLE.name());
			OptionStore.getOption(NodeAttributeReaderImpl.VERTEX_BORDER, NodeBorder.SIMPLE.name());
		} catch (Exception e) {
			fail("Initialisation of OptionStore failed : " + e);
		}
	}

	@AfterAll
	public static void tearDown() {
		GSGraphManager.getInstance().close(regGraph);
	}

	@Test
	public void simpleConnectivityTest() {
		assertNotNull( regGraph, "Create graph : the graph is null.");

		RegulatoryNode nodes[] = new RegulatoryNode[14];
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = regGraph.addNode();
		}
		int edges[][] = { { 0, 0 }, // trivial SCC
				{ 1, 2 }, { 2, 1 }, // isolated cycle
				{ 3, 4 }, { 4, 3 }, // cycle in a garden of eden
				{ 4, 5 }, // path
				{ 5, 6 }, { 6, 7 }, { 7, 5 }, // terminal cycle with 3 nodes
				{ 8, 9 }, { 9, 8 }, { 9, 10 }, { 10, 8 }, // isolated cycle with
															// 3 node and a
															// sub-cycle
				{ 11, 12 }, { 11, 13 }, { 12, 13 } // a tree, making it a cycle
													// only in non oriented
													// graphs
		};

		for (int[] edge : edges) {
			regGraph.addEdge(nodes[edge[0]], nodes[edge[1]],
					RegulatoryEdgeSign.POSITIVE);
		}

		SCCGraphService service = GSServiceManager.get(SCCGraphService.class);
        ReducedGraph<?, ?, ?> reducedGraph = service.getSCCGraph(regGraph);
		assertNotNull( reducedGraph, "The graph is null");

		// Count of SCC
		assertEquals(8, reducedGraph.getNodeCount(), "Wrong number of SCC");

		// Count of Edges
		assertEquals(4, reducedGraph.getEdges().size(), "Wrong number of edges");

		// Count of Trivial SCC
		int total = 0;
		for (NodeReducedData scc : reducedGraph.getNodes()) {
			if (scc.isTrivial())
				total++;
		}
		assertEquals(4, total, "Wrong number of trivial SCC");

		// Count of Transient SCC
		total = 0;
		for (NodeReducedData scc : reducedGraph.getNodes()) {
			if (scc.isTransient(regGraph))
				total++;
		}
		assertEquals( 3, total, "Wrong number of transient SCC");

		// Count of nodes in the SCC
		total = 0;
		for (NodeReducedData scc : reducedGraph.getNodes()) {
			total += scc.getContent().size();
		}
		assertEquals( 14, total, "Wrong number of nodes in the SCC");

	}
}
