package org.ginsim.service.tool.graphcomparator;

import java.util.HashMap;
import java.util.Iterator;

import org.ginsim.TestUtils;
import org.ginsim.common.application.GsException;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.dynamicgraph.DynamicGraph;
import org.ginsim.core.graph.dynamicgraph.DynamicNode;
import org.ginsim.core.graph.regulatorygraph.RegulatoryEdgeSign;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Compare two models.
 * @author Duncan Berenguier
 *
 */
public class TestGraphComparator {
	RegulatoryGraph rg1, rg2, rgempty;
	DynamicGraph dg1, dg2, dgempty;
	
	public TestGraphComparator() {
		this.rg1 = GraphExamples.rg1();
		this.rg2 = GraphExamples.rg2();
		this.rgempty = GraphExamples.rgempty();
		this.dg1 = GraphExamples.dg1();
		this.dg2 = GraphExamples.dg2();
		this.dgempty = GraphExamples.dgempty();
	}
/*
 *   TESTS 
 */
	/**
	 * Initialize the OptionStore is required
	 * 
	 */
	@BeforeClass
	public static void beforeAllTests(){
		
		TestUtils.initOptionStore();
	}
	
	
	@Test
	public void testCompareRegulatoryGraphOnTwoEmptyGraph() {
		compareGraph(new RegulatoryGraphComparator(rgempty,rgempty), 0, 0);
	}
	@Test
	public void testCompareRegulatoryGraphOnOneEmptyGraph() {
		compareGraph(new RegulatoryGraphComparator(rg1,rgempty), 5, 6);
	}
	@Test
	public void testCompareRegulatoryGraphOnTheSameGraph() {
		compareGraph(new RegulatoryGraphComparator(rg1,rg1), 5, 6);
	}
	@Test
	public void testCompareRegulatoryGraphOnDifferentGraphs() {
		compareGraph(new RegulatoryGraphComparator(rg1, rg2), 7, 9);
	}
	
	
	@Test
	public void testCompareDynamicGraphOnTwoEmptyGraph() {
		compareGraph(new DynamicGraphComparator(dgempty,dgempty), 0, 0);
	}
	@Test
	public void testCompareDynamicGraphOnOneEmptyGraph() {
		compareGraph(new DynamicGraphComparator(dg1, dgempty), 3, 4);
	}
	@Test
	public void testCompareDynamicGraphOnTheSameGraph() {
		compareGraph(new DynamicGraphComparator(dg1, dg1), 3, 4);
	}
	@Test
	public void testCompareDynamicGraphOnDifferentGraphs() {
		compareGraph(new DynamicGraphComparator(dg1, dg2), 4, 6);
	}
	
/*
 *   UTILS 
 */

	public void compareGraph(GraphComparator gc, int vertexCount, int edgesCount) {
        try {
            GraphComparatorResult result = (GraphComparatorResult)gc.call();

            int diffNodeCount = result.getDiffGraph().getNodeCount();
            Assert.assertTrue("Wrong number of vertex in the diff graph.("+diffNodeCount+" out of "+vertexCount+")", diffNodeCount == vertexCount);
            int diffEdgesCount = countEdges(gc);
            Assert.assertTrue("Wrong number of edges in the diff graph.("+diffEdgesCount+" out of "+edgesCount+")", diffEdgesCount == edgesCount);
        } catch (Exception e) {
            throw new RuntimeException("Graph comparison failed");
        }
	}
		
	public void printNodesMap(HashMap vm) {
		System.out.println("\nNodes Map : ");
		for (Iterator it = vm.keySet().iterator(); it.hasNext();) {
			String id = (String) it.next();
			System.out.println("vertex : "+id+" ; color : "+vm.get(id));
		}
	}
	
	public int countEdges(GraphComparator gc) {
		return gc.getDiffGraph().getEdges().size();
	}

}


class GraphExamples {
	
	/**
	 * 
	 *        A  -> B
	 *        ^      
	 *        |  \  |
	 *            > >
	 *        D <-  C  -> E
	 * 
	 */
	public static RegulatoryGraph rg1() {
		RegulatoryGraph g = GraphManager.getInstance().getNewGraph(); 
		try { g.setGraphName("regulatory_graph_A");} catch (GsException e) {}
		
		g.addNewNode("A", "A", (byte)1);
		g.addNewNode("B", "B", (byte)1);
		g.addNewNode("C", "C", (byte)1);
		g.addNewNode("D", "D", (byte)1);
		g.addNewNode("E", "E", (byte)1);
		
		try {
			g.addNewEdge("A", "B", (byte)0, RegulatoryEdgeSign.NEGATIVE);
			g.addNewEdge("A", "C", (byte)0, RegulatoryEdgeSign.NEGATIVE);  //added
			g.addNewEdge("B", "C", (byte)0, RegulatoryEdgeSign.POSITIVE);
			g.addNewEdge("C", "D", (byte)0, RegulatoryEdgeSign.POSITIVE);
			g.addNewEdge("D", "A", (byte)0, RegulatoryEdgeSign.POSITIVE);
			g.addNewEdge("C", "E", (byte)0, RegulatoryEdgeSign.POSITIVE);  //added
		} catch (GsException e) {
			e.printStackTrace();
		}

		return g;
	}
	
	/**
	 * 
	 *        A  -> B     G
	 *        ^       ^   ^
	 *        |     |  \  |
	 *              >
	 *        D <-  C  -> F
	 *        
	 */
	public static RegulatoryGraph rg2() {
		RegulatoryGraph g = GraphManager.getInstance().getNewGraph(); 
		try { g.setGraphName("regulatory_graph_B");} catch (GsException e) {}
		
		g.addNewNode("A", "A", (byte)1);
		g.addNewNode("B", "B", (byte)1);
		g.addNewNode("C", "C", (byte)1);
		g.addNewNode("D", "D", (byte)2); //different maxValue
		g.addNewNode("F", "F", (byte)1); //added
		g.addNewNode("G", "G", (byte)1); //added
		
		try {
			g.addNewEdge("A", "B", (byte)0, RegulatoryEdgeSign.NEGATIVE);
			g.addNewEdge("B", "C", (byte)0, RegulatoryEdgeSign.NEGATIVE); //different sign
			g.addNewEdge("C", "D", (byte)0, RegulatoryEdgeSign.POSITIVE); //multiarc
			g.addNewEdge("C", "D", (byte)1, RegulatoryEdgeSign.NEGATIVE); //multiarc
			g.addNewEdge("D", "A", (byte)1, RegulatoryEdgeSign.POSITIVE); //different minvalue
			g.addNewEdge("C", "F", (byte)0, RegulatoryEdgeSign.POSITIVE); //added
			g.addNewEdge("F", "B", (byte)0, RegulatoryEdgeSign.POSITIVE); //added
			g.addNewEdge("F", "G", (byte)0, RegulatoryEdgeSign.POSITIVE); //added
		} catch (GsException e) {
			e.printStackTrace();
		}

		return g;
	}
	
	public static RegulatoryGraph rgempty() {
		RegulatoryGraph g = GraphManager.getInstance().getNewGraph(); 
		try { g.setGraphName("regulatory_graph_empty");} catch (GsException e) {}
		return g;
	}
	
	
	/**
	 * 
	 *       00 <-> 01
	 *            >  
	 *           /  
	 *          <   
	 *       10     11
	 * 
	 */
	public static  DynamicGraph dg1() {
		DynamicGraph g = GraphManager.getInstance().getNewGraph( DynamicGraph.class); 
		try { g.setGraphName("dynamic_graph_A");} catch (GsException e) {}
		
		g.addNode(new DynamicNode("a00"));
		g.addNode(new DynamicNode("a01"));
		g.addNode(new DynamicNode("a10"));
		
		g.addEdge(g.getNodeByName("00"), g.getNodeByName("01"), false);
		g.addEdge(g.getNodeByName("10"), g.getNodeByName("01"), false);
		g.addEdge(g.getNodeByName("01"), g.getNodeByName("10"), false);
		g.addEdge(g.getNodeByName("01"), g.getNodeByName("00"), false); //added

		return g;
	}

	
	/**
	 * 
	 *       00  -> 01
	 *          < >  
	 *           X  
	 *          <   
	 *       10  -> 11
	 * 
	 */
	public static  DynamicGraph dg2() {
		DynamicGraph g = GraphManager.getInstance().getNewGraph(DynamicGraph.class); 
		try { g.setGraphName("dynamic_graph_B");} catch (GsException e) {}
		
		g.addNode(new DynamicNode("a00"));
		g.addNode(new DynamicNode("a01"));
		g.addNode(new DynamicNode("b10"));//change first letter (should have no effect)
		g.addNode(new DynamicNode("a11"));//added
		
		g.addEdge(g.getNodeByName("00"), g.getNodeByName("01"), false);
		g.addEdge(g.getNodeByName("10"), g.getNodeByName("01"), false);
		g.addEdge(g.getNodeByName("01"), g.getNodeByName("10"), true);//multiple to true //TODO: need to detect that change ? yes mais en fait non
		g.addEdge(g.getNodeByName("10"), g.getNodeByName("11"), false);//added
		g.addEdge(g.getNodeByName("11"), g.getNodeByName("00"), false);//added

		return g;
	}

	public static DynamicGraph dgempty() {
		DynamicGraph g = GraphManager.getInstance().getNewGraph(DynamicGraph.class); 
		try { g.setGraphName("dynamic_graph_empty");} catch (GsException e) {}
		return g;
	}

}