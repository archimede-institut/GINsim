package org.ginsim.service.imports.truthtable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collection;
import java.util.Iterator;

import org.ginsim.TestFileUtils;
import org.ginsim.common.application.GsException;
import org.ginsim.common.application.Translator;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.junit.BeforeClass;
import org.junit.Test;

public class TruthTableImportTest {

	private static final String module = "TruthTableImport";

	@BeforeClass
	public static void setUp() {
		Translator.pushBundle("org.ginsim.messages");
	}

	@Test
	public void importTruthTableTest1() {
		String table = "table.1.3d.tt";
		File file = new File(TestFileUtils.getTestFileDirectory(module), table);
		FileReader fr;
		try {
			fr = new FileReader(file);
			TruthTableParser parser = new TruthTableParser(fr);
			RegulatoryGraph graph = parser.buildCompactLRG();

			assertNotNull(module + ": graph is null", graph);
			assertEquals(module + ": Graph node number is incorrect", 3,
					graph.getNodeCount());
			assertEquals(module + ": Graph edge number is incorrect", 5, graph
					.getEdges().size());
			for (RegulatoryNode node : graph.getNodeOrder()) {
				Collection<RegulatoryMultiEdge> mEdges = graph
						.getIncomingEdges(node);
				for (RegulatoryMultiEdge me : mEdges) {
					assertEquals(module + ": " + node.getId()
							+ " edge count is incorrect", 1, me.getEdgeCount());
				}
			}

		} catch (FileNotFoundException e) {
			fail("File not found: " + module + "/" + table);
		} catch (GsException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void importTruthTableTest1Incomplete() {
		String table = "table.1.3d.incomplete.tt";
		File file = new File(TestFileUtils.getTestFileDirectory(module), table);
		FileReader fr;
		try {
			fr = new FileReader(file);
			new TruthTableParser(fr);
			fail("Should have failed since the table " + table
					+ " is incomplete!");
		} catch (FileNotFoundException e) {
			fail("File not found: " + module + "/" + table);
		} catch (GsException e) {
			assertEquals(e.getMessage(),
					Translator.getString("STR_TruthTable_incomplete")
							+ ": found 7 lines, expecting at least 8:");
		}
	}

	@Test
	public void importTruthTableTest2() {
		String table = "table.2.3d.tt";
		File file = new File(TestFileUtils.getTestFileDirectory(module), table);
		FileReader fr;
		try {
			fr = new FileReader(file);
			TruthTableParser parser = new TruthTableParser(fr);
			RegulatoryGraph graph = parser.buildCompactLRG();

			assertNotNull(module + ": graph is null", graph);
			assertEquals(module + ": Graph node number is incorrect", 3,
					graph.getNodeCount());
			assertEquals(module + ": Graph edge number is incorrect", 3, graph
					.getEdges().size());
			for (RegulatoryNode node : graph.getNodeOrder()) {
				Collection<RegulatoryMultiEdge> mEdges = graph
						.getIncomingEdges(node);
				for (RegulatoryMultiEdge me : mEdges) {
					assertEquals(module + ": " + node.getId()
							+ " expecting an auto-regulation", me.getSource(),
							me.getTarget());
					assertEquals(module + ": " + node.getId()
							+ " edge count is incorrect", 1, me.getEdgeCount());
				}
			}

		} catch (FileNotFoundException e) {
			fail("File not found: " + module + "/" + table);
		} catch (GsException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void importTruthTableTest3Multivalued() {
		String table = "table.3.2d.multivalue.tt";
		File file = new File(TestFileUtils.getTestFileDirectory(module), table);
		FileReader fr;
		try {
			fr = new FileReader(file);
			TruthTableParser parser = new TruthTableParser(fr);
			RegulatoryGraph graph = parser.buildCompactLRG();

			assertNotNull(module + ": graph is null", graph);
			assertEquals(module + ": Graph node number is incorrect", 2,
					graph.getNodeCount());
			assertEquals(module + ": Graph edge number is incorrect", 4, graph
					.getEdges().size());

			RegulatoryNode nodeG0 = graph.getNodeByName("g0");
			RegulatoryNode nodeG1 = graph.getNodeByName("g1");

			Iterator<RegulatoryMultiEdge> iter = graph.getIncomingEdges(nodeG0)
					.iterator();
			RegulatoryMultiEdge me = iter.next();
			assertEquals(module + ": " + nodeG0.getId()
					+ " expecting an auto-regulation", me.getSource(),
					me.getTarget());
			assertEquals(module + ": " + nodeG0.getId()
					+ " expecting source to be ", me.getSource(), nodeG0);
			me = iter.next();
			assertEquals(module + ": " + nodeG0.getId()
					+ " expecting source to be ", me.getSource(), nodeG1);

			iter = graph.getIncomingEdges(nodeG1).iterator();
			me = iter.next();
			assertEquals(module + ": " + nodeG1.getId()
					+ " expecting source to be ", me.getSource(), nodeG0);
			me = iter.next();
			assertEquals(module + ": " + nodeG1.getId()
					+ " expecting an auto-regulation", me.getSource(),
					me.getTarget());

		} catch (FileNotFoundException e) {
			fail("File not found: " + module + "/" + table);
		} catch (GsException e) {
			fail(e.getMessage());
		}
	}
}
