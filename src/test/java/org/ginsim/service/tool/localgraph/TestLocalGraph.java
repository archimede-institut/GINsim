package org.ginsim.service.tool.localgraph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ginsim.TestFileUtils;
import org.ginsim.common.application.GsException;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.service.ServiceManager;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestLocalGraph {

	private static String[] saModel;
	private static RegulatoryGraph[] saGraph;
	private static LocalGraphService service;
	private static File dir;

	@BeforeClass
	public static void setUp() {
		// Translator.pushBundle("org.ginsim.messages");
		dir = TestFileUtils.getTestFileDirectory("models");
		saModel = new String[] { "E2F.ginml", "toymodel4d.ginml" };

		saGraph = new RegulatoryGraph[saModel.length];

		for (int i = 0; i < saModel.length; i++) {
			File file = new File(dir, saModel[i]);
			saGraph[i] = TestFileUtils.loadGraph(file);
			saModel[i] = saModel[i].substring(0, saModel[i].indexOf('.'));
		}

		service = ServiceManager.getManager().getService(
				LocalGraphService.class);
		assertNotNull("LocalGraphService service is not available", service);
	}

	@Test
	public void testE2F() {
		RegulatoryGraph graph = saGraph[0];
		List<RegulatoryNode> nodeOrder = graph.getNodeOrder();
		RegulatoryNode cyclinE1 = nodeOrder.get(11);
		RegulatoryNode p21CIP = nodeOrder.get(17);
		try {
			List<byte[]> alStates = new ArrayList<byte[]>();
			byte[] state = new byte[nodeOrder.size()];
			state[2] = (byte) 1;
			// State: 0010...00
			alStates.add(state);
			Map<RegulatoryMultiEdge, String> funct = service.run(graph,
					alStates);

			// CyclinE1 -> p21CIP
			RegulatoryMultiEdge me = graph.getEdge(cyclinE1, p21CIP);
			assertNotNull("Edge CyclinE1 -> p21CIP should not be null", me);
			assertEquals("Edge ECyclinE1 -> p21CIP should be negative",
					"negative", funct.get(me));

			state[2] = (byte) 0;
			// State: 0010...00
			funct = service.run(graph, alStates);

			// CyclinE1 -> p21CIP
			assertNull("Edge ECyclinE1 -> p21CIP should be non functional",
					funct.get(me));
		} catch (GsException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testToymodel4d() {
		RegulatoryGraph graph = saGraph[1];
		List<RegulatoryNode> nodeOrder = graph.getNodeOrder();
		try {
			List<byte[]> alStates = new ArrayList<byte[]>();
			byte[] state = new byte[nodeOrder.size()];
			state[1] = (byte) 1;
			// State: 0100
			alStates.add(state);
			Map<RegulatoryMultiEdge, String> funct = service.run(graph,
					alStates);
			RegulatoryNode gA = nodeOrder.get(0);
			RegulatoryNode gB = nodeOrder.get(1);

			// gA -> gB
			RegulatoryMultiEdge me = graph.getEdge(gA, gB);
			assertNotNull("Edge gA -> gB should not be null", me);
			assertEquals("Edge gA -> gB should be negative", "negative",
					funct.get(me));

			// gB -> gA
			me = saGraph[1].getEdge(gB, gA);
			assertNotNull("Edge gB -> gA should not be null", me);
			assertEquals("Edge gB -> gA should be positive", "positive",
					funct.get(me));
		} catch (GsException e) {
			fail(e.getMessage());
		}
	}

}
