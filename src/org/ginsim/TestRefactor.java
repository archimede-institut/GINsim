package org.ginsim;

import org.ginsim.graph.testGraph.TestEdge;
import org.ginsim.graph.testGraph.TestGraph;
import org.ginsim.graph.testGraph.TestGraphImpl;
import org.ginsim.graph.testGraph.TestVertex;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.shell.MainFrame;

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
		
		GUIManager.getInstance().newFrame(lrg);
	}
	
}
