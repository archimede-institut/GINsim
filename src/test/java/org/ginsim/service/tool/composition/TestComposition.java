package org.ginsim.service.tool.composition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.ginsim.TestFileUtils;
import org.ginsim.common.application.GsException;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.service.ServiceManager;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestComposition {

	private static String[] saModel;
	private static RegulatoryGraph[] saGraph;
	private static CompositionService service;
	private static File dir;

	@BeforeClass
	public static void setUp() {
		// Txt.pushBundle("org.ginsim.messages");
		dir = TestFileUtils.getTestFileDirectory("models");
		saModel = new String[] { "toy_composition.ginml" }; // TODO: the
															// currently
															// available model
															// is in zginml
															// format (can I
															// obtain a ginml
															// version?)

		saGraph = new RegulatoryGraph[saModel.length];

		for (int i = 0; i < saModel.length; i++) {
			File file = new File(dir, saModel[i]);
			saGraph[i] = TestFileUtils.loadGraph(file);
			saModel[i] = saModel[i].substring(0, saModel[i].indexOf('.'));
		}

		service = ServiceManager.getManager().getService(
				CompositionService.class);
		assertNotNull("CompositionService service is not available", service);
	}

	@Test
	public void test2_4OR() throws Exception {
		RegulatoryGraph graph = saGraph[0];
		RegulatoryGraph composedGraph = null;

		try {

			Topology topology = new GenericTopology(4);
			topology.addNeighbour(0, 1);
			topology.addNeighbour(0, 2);
			topology.addNeighbour(1, 0);
			topology.addNeighbour(1, 3);
			topology.addNeighbour(2, 0);
			topology.addNeighbour(2, 3);
			topology.addNeighbour(3, 1);
			topology.addNeighbour(3, 2);

			List<RegulatoryNode> properComponents = new ArrayList<RegulatoryNode>();
			properComponents.add(graph.getNodeByName("G2"));

			IntegrationFunctionMapping mapping = new IntegrationFunctionMapping();
			mapping.addMapping(graph.getNodeByName("U0"), properComponents,
					IntegrationFunction.OR);

			CompositionConfig config = new CompositionConfig();
			config.setReduce(false);
			config.setTopology(topology);
			config.setMapping(mapping);

			composedGraph = service.run(graph, config);

			assertEquals(
					"The composed graph should have has many components as the sum of the components of the instances",
					composedGraph.getNodeOrderSize(),
					4 * graph.getNodeOrderSize());
			List<RegulatoryNode> inputs = new ArrayList<RegulatoryNode>();
			List<RegulatoryNode> oldInputs = new ArrayList<RegulatoryNode>();
			for (RegulatoryNode node : composedGraph.getNodeOrder()) {
				if (node.isInput())
					inputs.add(node);
				if (node.getNodeInfo().getNodeID().startsWith("U0"))
					oldInputs.add(node);

			}

			assertEquals(
					"The composed graph (without reduction) should have as many old inputs as the inputs of the instances",
					oldInputs.size(), 4);

			assertEquals(
					"The composed graph should not have any inputs, since all are mapped",
					inputs.size(), 0);

			// other tests ??

			config.setReduce(true);

			composedGraph = service.run(graph, config);

			assertEquals(
					"The composed graph should have 4 components less than the sum of the components of the instances",
					composedGraph.getNodeOrderSize(),
					4 * (graph.getNodeOrderSize() - 1));

			inputs = new ArrayList<RegulatoryNode>();
			oldInputs = new ArrayList<RegulatoryNode>();
			for (RegulatoryNode node : composedGraph.getNodeOrder()) {
				if (node.isInput())
					inputs.add(node);
				if (node.getNodeInfo().getNodeID().startsWith("U0"))
					oldInputs.add(node);

			}

			assertEquals(
					"The composed graph (with reduction) should not have any old inputs",
					oldInputs.size(), 0);

			assertEquals(
					"The composed graph should not have any inputs, since all are mapped",
					inputs.size(), 0);

		} catch (GsException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void test2_4AND() throws Exception {
		RegulatoryGraph graph = saGraph[0];
		RegulatoryGraph composedGraph = null;

		try {

			Topology topology = new GenericTopology(4);
			topology.addNeighbour(0, 1);
			topology.addNeighbour(0, 2);
			topology.addNeighbour(1, 0);
			topology.addNeighbour(1, 3);
			topology.addNeighbour(2, 0);
			topology.addNeighbour(2, 3);
			topology.addNeighbour(3, 1);
			topology.addNeighbour(3, 2);

			List<RegulatoryNode> properComponents = new ArrayList<RegulatoryNode>();
			properComponents.add(graph.getNodeByName("G2"));

			IntegrationFunctionMapping mapping = new IntegrationFunctionMapping();
			mapping.addMapping(graph.getNodeByName("U0"), properComponents,
					IntegrationFunction.AND);

			CompositionConfig config = new CompositionConfig();
			config.setReduce(false);
			config.setTopology(topology);
			config.setMapping(mapping);

			composedGraph = service.run(graph, config);

			assertEquals(
					"The composed graph should have has many components as the sum of the components of the instances",
					composedGraph.getNodeOrderSize(),
					4 * graph.getNodeOrderSize());
			List<RegulatoryNode> inputs = new ArrayList<RegulatoryNode>();
			List<RegulatoryNode> oldInputs = new ArrayList<RegulatoryNode>();
			for (RegulatoryNode node : composedGraph.getNodeOrder()) {
				if (node.isInput())
					inputs.add(node);
				if (node.getNodeInfo().getNodeID().startsWith("U0"))
					oldInputs.add(node);

			}

			assertEquals(
					"The composed graph (without reduction) should have as many old inputs as the inputs of the instances",
					oldInputs.size(), 4);

			assertEquals(
					"The composed graph should not have any inputs, since all are mapped",
					inputs.size(), 0);

			// other tests ??

			config.setReduce(true);

			composedGraph = service.run(graph, config);

			assertEquals(
					"The composed graph should have 4 components less than the sum of the components of the instances",
					composedGraph.getNodeOrderSize(),
					4 * (graph.getNodeOrderSize() - 1));

			inputs = new ArrayList<RegulatoryNode>();
			oldInputs = new ArrayList<RegulatoryNode>();
			for (RegulatoryNode node : composedGraph.getNodeOrder()) {
				if (node.isInput())
					inputs.add(node);
				if (node.getNodeInfo().getNodeID().startsWith("U0"))
					oldInputs.add(node);

			}

			assertEquals(
					"The composed graph (with reduction) should not have any old inputs",
					oldInputs.size(), 0);

			assertEquals(
					"The composed graph should not have any inputs, since all are mapped",
					inputs.size(), 0);

		} catch (GsException e) {
			fail(e.getMessage());
		}

	}

	@Test
	public void test2_8OR() throws Exception {
		RegulatoryGraph graph = saGraph[0];
		RegulatoryGraph composedGraph = null;

		try {

			Topology topology = new GenericTopology(4);
			topology.addNeighbour(0, 1);
			topology.addNeighbour(0, 2);
			topology.addNeighbour(0, 3);
			topology.addNeighbour(1, 0);
			topology.addNeighbour(1, 3);
			topology.addNeighbour(1, 2);
			topology.addNeighbour(2, 0);
			topology.addNeighbour(2, 3);
			topology.addNeighbour(2, 1);
			topology.addNeighbour(3, 1);
			topology.addNeighbour(3, 2);
			topology.addNeighbour(3, 0);

			List<RegulatoryNode> properComponents = new ArrayList<RegulatoryNode>();
			properComponents.add(graph.getNodeByName("G2"));

			IntegrationFunctionMapping mapping = new IntegrationFunctionMapping();
			mapping.addMapping(graph.getNodeByName("U0"), properComponents,
					IntegrationFunction.OR);

			CompositionConfig config = new CompositionConfig();
			config.setReduce(false);
			config.setTopology(topology);
			config.setMapping(mapping);

			composedGraph = service.run(graph, config);

			assertEquals(
					"The composed graph should have has many components as the sum of the components of the instances",
					composedGraph.getNodeOrderSize(),
					4 * graph.getNodeOrderSize());
			List<RegulatoryNode> inputs = new ArrayList<RegulatoryNode>();
			List<RegulatoryNode> oldInputs = new ArrayList<RegulatoryNode>();
			for (RegulatoryNode node : composedGraph.getNodeOrder()) {
				if (node.isInput())
					inputs.add(node);
				if (node.getNodeInfo().getNodeID().startsWith("U0"))
					oldInputs.add(node);

			}

			assertEquals(
					"The composed graph (without reduction) should have as many old inputs as the inputs of the instances",
					oldInputs.size(), 4);

			assertEquals(
					"The composed graph should not have any inputs, since all are mapped",
					inputs.size(), 0);

			// other tests ??

			config.setReduce(true);

			composedGraph = service.run(graph, config);

			assertEquals(
					"The composed graph should have 4 components less than the sum of the components of the instances",
					composedGraph.getNodeOrderSize(),
					4 * (graph.getNodeOrderSize() - 1));

			inputs = new ArrayList<RegulatoryNode>();
			oldInputs = new ArrayList<RegulatoryNode>();
			for (RegulatoryNode node : composedGraph.getNodeOrder()) {
				if (node.isInput())
					inputs.add(node);
				if (node.getNodeInfo().getNodeID().startsWith("U0"))
					oldInputs.add(node);

			}

			assertEquals(
					"The composed graph (with reduction) should not have any old inputs",
					oldInputs.size(), 0);

			assertEquals(
					"The composed graph should not have any inputs, since all are mapped",
					inputs.size(), 0);

		} catch (GsException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void test2_8AND() throws Exception {
		RegulatoryGraph graph = saGraph[0];
		RegulatoryGraph composedGraph = null;

		try {

			Topology topology = new GenericTopology(4);
			topology.addNeighbour(0, 1);
			topology.addNeighbour(0, 2);
			topology.addNeighbour(0, 3);
			topology.addNeighbour(1, 0);
			topology.addNeighbour(1, 3);
			topology.addNeighbour(1, 2);
			topology.addNeighbour(2, 0);
			topology.addNeighbour(2, 3);
			topology.addNeighbour(2, 1);
			topology.addNeighbour(3, 1);
			topology.addNeighbour(3, 2);
			topology.addNeighbour(3, 0);

			List<RegulatoryNode> properComponents = new ArrayList<RegulatoryNode>();
			properComponents.add(graph.getNodeByName("G2"));

			IntegrationFunctionMapping mapping = new IntegrationFunctionMapping();
			mapping.addMapping(graph.getNodeByName("U0"), properComponents,
					IntegrationFunction.AND);

			CompositionConfig config = new CompositionConfig();
			config.setReduce(false);
			config.setTopology(topology);
			config.setMapping(mapping);

			composedGraph = service.run(graph, config);

			assertEquals(
					"The composed graph should have has many components as the sum of the components of the instances",
					composedGraph.getNodeOrderSize(),
					4 * graph.getNodeOrderSize());
			List<RegulatoryNode> inputs = new ArrayList<RegulatoryNode>();
			List<RegulatoryNode> oldInputs = new ArrayList<RegulatoryNode>();
			for (RegulatoryNode node : composedGraph.getNodeOrder()) {
				if (node.isInput())
					inputs.add(node);
				if (node.getNodeInfo().getNodeID().startsWith("U0"))
					oldInputs.add(node);

			}

			assertEquals(
					"The composed graph (without reduction) should have as many old inputs as the inputs of the instances",
					oldInputs.size(), 4);

			assertEquals(
					"The composed graph should not have any inputs, since all are mapped",
					inputs.size(), 0);

			// other tests ??

			config.setReduce(true);

			composedGraph = service.run(graph, config);

			assertEquals(
					"The composed graph should have 4 components less than the sum of the components of the instances",
					composedGraph.getNodeOrderSize(),
					4 * (graph.getNodeOrderSize() - 1));

			inputs = new ArrayList<RegulatoryNode>();
			oldInputs = new ArrayList<RegulatoryNode>();
			for (RegulatoryNode node : composedGraph.getNodeOrder()) {
				if (node.isInput())
					inputs.add(node);
				if (node.getNodeInfo().getNodeID().startsWith("U0"))
					oldInputs.add(node);

			}

			assertEquals(
					"The composed graph (with reduction) should not have any old inputs",
					oldInputs.size(), 0);

			assertEquals(
					"The composed graph should not have any inputs, since all are mapped",
					inputs.size(), 0);

		} catch (GsException e) {
			fail(e.getMessage());
		}

	}

}
