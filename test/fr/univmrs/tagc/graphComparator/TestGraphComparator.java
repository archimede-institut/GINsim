package fr.univmrs.tagc.graphComparator;

import java.util.HashMap;
import java.util.Iterator;

import junit.framework.TestCase;
import fr.univmrs.tagc.GINsim.dynamicGraph.GsDynamicGraph;
import fr.univmrs.tagc.GINsim.dynamicGraph.GsDynamicNode;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.graph.GsGraphManager;
import fr.univmrs.tagc.GINsim.graphComparator.DynamicGraphComparator;
import fr.univmrs.tagc.GINsim.graphComparator.GraphComparator;
import fr.univmrs.tagc.GINsim.graphComparator.RegulatoryGraphComparator;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;
import fr.univmrs.tagc.common.GsException;

/**
 * Compare two models.
 * @author Duncan Berenguier
 *
 */
public class TestGraphComparator extends TestCase  {
	GsRegulatoryGraph rg1, rg2, rgempty;
	GsDynamicGraph dg1, dg2, dgempty;
	
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
	
	public void testCompareRegulatoryGraphOnTwoEmptyGraph() {
		compareGraph(new RegulatoryGraphComparator(rgempty,rgempty), 0, 0);
	}
	public void testCompareRegulatoryGraphOnOneEmptyGraph() {
		compareGraph(new RegulatoryGraphComparator(rg1,rgempty), 5, 6);
	}
	public void testCompareRegulatoryGraphOnTheSameGraph() {
		compareGraph(new RegulatoryGraphComparator(rg1,rg1), 5, 6);
	}
	public void testCompareRegulatoryGraphOnDifferentGraphs() {
		compareGraph(new RegulatoryGraphComparator(rg1, rg2), 7, 9);
	}
	
	
	public void testCompareDynamicGraphOnTwoEmptyGraph() {
		compareGraph(new DynamicGraphComparator(dgempty,dgempty), 0, 0);
	}
	public void testCompareDynamicGraphOnOneEmptyGraph() {
		compareGraph(new DynamicGraphComparator(dg1, dgempty), 3, 4);
	}
	public void testCompareDynamicGraphOnTheSameGraph() {
		compareGraph(new DynamicGraphComparator(dg1, dg1), 3, 4);
	}
	public void testCompareDynamicGraphOnDifferentGraphs() {
		compareGraph(new DynamicGraphComparator(dg1, dg2), 4, 6);
	}
	
/*
 *   UTILS 
 */

	public void compareGraph(GraphComparator gc, int vertexCount, int edgesCount) {
		GsGraph g1, g2;
		g1 = gc.getG1();
		g2 = gc.getG2();
		System.out.println("\nTested : Compare g1:"+g1.getGraphName()+" and g2:"+g2.getGraphName()+"\n------\n");
		HashMap vm = gc.getStyleMap();

		
		//printVerticesMap(vm);
		assertTrue("Wrong number of vertex in the vertex map. ("+vm.size()+" out of "+vertexCount+")", vm.size() == vertexCount);
		int diffVertexCount = gc.getDiffGraph().getGraphManager().getVertexCount();
		assertTrue("Wrong number of vertex in the diff graph.("+diffVertexCount+" out of "+vertexCount+")", diffVertexCount == vertexCount);
		int diffEdgesCount = countEdges(gc);
		assertTrue("Wrong number of edges in the diff graph.("+diffEdgesCount+" out of "+edgesCount+")", diffEdgesCount == edgesCount);
		
	}
		
	public void printVerticesMap(HashMap vm) {
		System.out.println("\nVertices Map : ");
		for (Iterator it = vm.keySet().iterator(); it.hasNext();) {
			String id = (String) it.next();
			System.out.println("vertex : "+id+" ; color : "+vm.get(id));
		}
	}
	
	public int countEdges(GraphComparator gc) {
		int count = 0;
		for (Iterator it = gc.getDiffGraph().getGraphManager().getEdgeIterator(); it.hasNext(); it.next()) count++; //TODO : another way to get the count of edges ?
		return count;
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
	public static GsRegulatoryGraph rg1() {
		GsRegulatoryGraph g = new GsRegulatoryGraph(); 
		try { g.setGraphName("regulatory_graph_A");} catch (GsException e) {}
		
		g.addNewVertex("A", "A", (byte)1);
		g.addNewVertex("B", "B", (byte)1);
		g.addNewVertex("C", "C", (byte)1);
		g.addNewVertex("D", "D", (byte)1);
		g.addNewVertex("E", "E", (byte)1);
		
		g.addNewEdge("A", "B", (byte)0, GsRegulatoryMultiEdge.SIGN_NEGATIVE);
		g.addNewEdge("A", "C", (byte)0, GsRegulatoryMultiEdge.SIGN_NEGATIVE);  //added
		g.addNewEdge("B", "C", (byte)0, GsRegulatoryMultiEdge.SIGN_POSITIVE);
		g.addNewEdge("C", "D", (byte)0, GsRegulatoryMultiEdge.SIGN_POSITIVE);
		g.addNewEdge("D", "A", (byte)0, GsRegulatoryMultiEdge.SIGN_POSITIVE);
		g.addNewEdge("C", "E", (byte)0, GsRegulatoryMultiEdge.SIGN_POSITIVE);  //added
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
	public static GsRegulatoryGraph rg2() {
		GsRegulatoryGraph g = new GsRegulatoryGraph(); 
		try { g.setGraphName("regulatory_graph_B");} catch (GsException e) {}
		
		g.addNewVertex("A", "A", (byte)1);
		g.addNewVertex("B", "B", (byte)1);
		g.addNewVertex("C", "C", (byte)1);
		g.addNewVertex("D", "D", (byte)2); //different maxValue
		g.addNewVertex("F", "F", (byte)1); //added
		g.addNewVertex("G", "G", (byte)1); //added
		
		g.addNewEdge("A", "B", (byte)0, GsRegulatoryMultiEdge.SIGN_NEGATIVE);
		g.addNewEdge("B", "C", (byte)0, GsRegulatoryMultiEdge.SIGN_NEGATIVE); //different sign
		g.addNewEdge("C", "D", (byte)0, GsRegulatoryMultiEdge.SIGN_POSITIVE); //multiarc
		g.addNewEdge("C", "D", (byte)1, GsRegulatoryMultiEdge.SIGN_NEGATIVE); //multiarc
		g.addNewEdge("D", "A", (byte)1, GsRegulatoryMultiEdge.SIGN_POSITIVE); //different minvalue
		g.addNewEdge("C", "F", (byte)0, GsRegulatoryMultiEdge.SIGN_POSITIVE); //added
		g.addNewEdge("F", "B", (byte)0, GsRegulatoryMultiEdge.SIGN_POSITIVE); //added
		g.addNewEdge("F", "G", (byte)0, GsRegulatoryMultiEdge.SIGN_POSITIVE); //added
		return g;
	}
	
	public static GsRegulatoryGraph rgempty() {
		GsRegulatoryGraph g = new GsRegulatoryGraph(); 
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
	public static  GsDynamicGraph dg1() {
		GsDynamicGraph g = new  GsDynamicGraph(); 
		try { g.setGraphName("dynamic_graph_A");} catch (GsException e) {}
		GsGraphManager gm = g.getGraphManager();
		
		g.addVertex(new GsDynamicNode("a00"));
		g.addVertex(new GsDynamicNode("a01"));
		g.addVertex(new GsDynamicNode("a10"));
		
		g.addEdge(gm.getVertexByName("00"), gm.getVertexByName("01"), false);
		g.addEdge(gm.getVertexByName("10"), gm.getVertexByName("01"), false);
		g.addEdge(gm.getVertexByName("01"), gm.getVertexByName("10"), false);
		g.addEdge(gm.getVertexByName("01"), gm.getVertexByName("00"), false); //added

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
	public static  GsDynamicGraph dg2() {
		GsDynamicGraph g = new  GsDynamicGraph(); 
		try { g.setGraphName("dynamic_graph_B");} catch (GsException e) {}
		GsGraphManager gm = g.getGraphManager();
		
		g.addVertex(new GsDynamicNode("a00"));
		g.addVertex(new GsDynamicNode("a01"));
		g.addVertex(new GsDynamicNode("b10"));//change first letter (should have no effect)
		g.addVertex(new GsDynamicNode("a11"));//added
		
		g.addEdge(gm.getVertexByName("00"), gm.getVertexByName("01"), false);
		g.addEdge(gm.getVertexByName("10"), gm.getVertexByName("01"), false);
		g.addEdge(gm.getVertexByName("01"), gm.getVertexByName("10"), true);//multiple to true //TODO: need to detect that change ? yes mais en fait non
		g.addEdge(gm.getVertexByName("10"), gm.getVertexByName("11"), false);//added
		g.addEdge(gm.getVertexByName("11"), gm.getVertexByName("00"), false);//added

		return g;
	}

	public static GsDynamicGraph dgempty() {
		GsDynamicGraph g = new GsDynamicGraph(); 
		try { g.setGraphName("dynamic_graph_empty");} catch (GsException e) {}
		return g;
	}

}