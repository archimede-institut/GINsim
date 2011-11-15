package org.ginsim;

import java.util.ArrayList;
import java.util.List;

import org.ginsim.graph.common.Graph;
import org.ginsim.graph.hierachicaltransitiongraph.GsHierarchicalTransitionGraph;
import org.ginsim.graph.reducedgraph.GsReducedGraph;
import org.ginsim.graph.testGraph.TestGraph;
import org.ginsim.graph.testGraph.TestGraphImpl;
import org.ginsim.graph.testGraph.TestVertex;
import org.ginsim.gui.GUIManager;

/**
 * Simple, stupid launcher to test the ongoing refactoring
 * 
 * @author Aurelien Naldi
 */
public class TestRefactor {

	/**
	 * @param args
	 * @throws InstantiationException 
	 * @throws IllegalAccessException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
		TestGraph lrg = new TestGraphImpl();

		TestVertex v1 = lrg.addVertex();
		TestVertex v2 = lrg.addVertex();
		TestVertex v3 = lrg.addVertex();

		lrg.addEdge(v1, v2);
		lrg.addEdge(v1, v3);
		
		Class liste = getGraphInterface( GsReducedGraph.class);
		System.out.println( "Liste = " + liste);
		Class liste1 = getGraphInterface( GsHierarchicalTransitionGraph.class);
		System.out.println( "Liste = " + liste1);
		
		GUIManager.getInstance().newFrame(lrg);
	}

	/**
	 * Test method to detect the current directory.
	 * It will be needed for plugins, dynamiic classpath and such
	 */
	private static void init() {
		Class<?> cl = TestRefactor.class;
		String clname = cl.getName().replace(".",	"/") + ".class";
		System.out.println(cl.getClassLoader().getResource(clname));
	}

}
